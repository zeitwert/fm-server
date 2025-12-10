
import { API, Building, BuildingStoreModel, BUILDING_API, Config, Enumerated, session, UUID } from "@zeitwert/ui-model";
import { action } from "mobx";
import { getSnapshot } from "mobx-state-tree";

export enum StratusBuildingStatus {
  OK = "OK",
  Warning = "Warning",
  Error = "Error",
}

export enum ImportState {
  INITIAL = "initial",
  PARSE_META = "parseMeta",
  PARSE_BUILDINGS = "parseBuildings",
  VALIDATE_BUILDINGS = "validateBuildings",
  IMPORT_BUILDINGS = "importBuildings",
  DONE = "done",
  ERROR = "error",
}

export interface StratusMeta {
  application: string;
  version: string;
  timestamp: string;
}

export interface StratusBuildingElement {
  partId?: string | undefined;
  part?: Enumerated | undefined;
  weight?: number | undefined;
  condition?: number | undefined;
  conditionYear?: number | undefined;
  description?: string | undefined;
  strain?: string | undefined;
  strength?: string | undefined;
}

export interface StratusBuilding {
  fields: string[];
  theoreticalFieldCount?: number;
  //
  status: StratusBuildingStatus;
  messages: string[];
  //
  id: number;
  isSelected: boolean;
  isImported: boolean;
  //
  address?: string | undefined;
  buildingTypeDisplay?: string | undefined;
  conditionYear?: number | undefined;
  //
  buildingNr?: string | undefined;
  identification?: string | undefined;
  name?: string | undefined;
  buildingYear?: number | undefined;
  street?: string | undefined;
  zip?: string | undefined;
  city?: string | undefined;
  countryId?: string | undefined;
  // BUILDING_MANAGER_ID?: string | undefined;
  // PORTFOLIO_MANAGER_ID?: string | undefined;
  currencyId?: string | undefined;
  insuredValue?: number | undefined;
  insuredValueYear?: number | undefined;
  correctionFactor?: string | undefined;
  notInsuredValue?: number | undefined;
  notInsuredValueYear?: number | undefined;
  thirdPartyValue?: number | undefined;
  thirdPartyValueYear?: number | undefined;
  volume?: number | undefined;
  area?: number | undefined;
  buildingTypeId?: string | undefined;
  buildingType?: Enumerated | undefined;
  buildingSubTypeId?: string | undefined;
  buildingSubType?: Enumerated | undefined;
  department?: string | undefined;
  // EXTRA1?: string | undefined;
  // EXTRA2?: string | undefined;
  // EXTRA3?: string | undefined;
  // EXTRA4?: string | undefined;
  maintenanceStrategyId?: string | undefined;
  doCalc?: string | undefined;
  doNotCalcFromYear?: string | undefined;
  doForceRestauration?: string | undefined;
  // REGISTERED_BY_ID?: string | undefined;
  // REGISTERED_AT?: string | undefined;
  // MODIFIED_BY?: string | undefined;
  // MODIFIED_AT?: string | undefined;
  description?: string | undefined;
  partCatalogId?: string | undefined;
  partCatalog?: Enumerated | undefined;
  partCount?: number | undefined;
  //
  elements: StratusBuildingElement[];
}

export type StratusBuildings = StratusBuilding[];

const BUILDING_NR = 0;
const IDENTIFICATION = 1;
const NAME = 2;
const BUILDING_YEAR = 3;
const STREET = 4;
const ZIP = 5;
const CITY = 6;
const COUNTRY_ID = 7;
// const BUILDING_MANAGER_ID = 8;
// const PORTFOLIO_MANAGER_ID = 9;
const CURRENCY_ID = 10;
const INSURED_VALUE = 11;
const INSURED_VALUE_YEAR = 12;
const CORRECTION_FACTOR = 13;
const NOT_INSURED_VALUE = 14;
const NOT_INSURED_YEAR = 15;
const THIRD_PARTY_VALUE = 16;
const THIRD_PARTY_VALUE_YEAR = 17;
const VOLUME = 18;
const AREA = 19;
const BUILDING_TYPE_ID = 20;
const BUILDING_SUB_TYPE_ID = 21;
const DEPARTMENT = 22;
// const EXTRA1 = 23;
// const EXTRA2 = 24;
// const EXTRA3 = 25;
// const EXTRA4 = 26;
const MAINTENANCE_STRATEGY_ID = 27;
const DO_CALC = 28;
const DO_NOT_CALC_FROM_YEAR = 29;
const DO_FORCE_RESTORATION = 30;
// const REGISTERED_BY_ID = 31;
// const REGISTERED_AT = 32;
// const MODIFIED_BY = 33;
// const MODIFIED_AT = 34;
const DESCRIPTION = 35;
const PART_CATALOG_ID = 36;
const PART_COUNT = 37;

const PART_ID = 0;
const PART_DESCRIPTION = 2;
const PART_CONDITION_YEAR = 3;
const PART_STRAIN = 5;
const PART_STRENGTH = 6;
const PART_CONDITION = 7;
const PART_WEIGHT = 8;

export default class StratusImport {

  importState: ImportState = ImportState.INITIAL;
  importError: string | undefined = undefined;

  stratusMeta: StratusMeta | undefined = undefined;

  private buildingPartCatalogs: Map<string, Enumerated> = new Map();
  private buildingParts: Map<string, Enumerated> = new Map();
  private buildingTypes: Map<string, Enumerated> = new Map();
  private buildingSubTypes: Map<string, Enumerated> = new Map();
  private zeitwertBuildings: Building[] = [];

  public loadReferenceData = async () => {
    await this.loadBuildingPartCatalogs();
    await this.loadBuildingParts();
    await this.loadBuildingTypes();
    await this.loadBuildingSubTypes();
    await this.loadBuildings();
  }

  private asEnumerated = (obj: any): Enumerated => {
    return { id: obj.id, name: obj.name };
  }

  private loadBuildingPartCatalogs = async () => {
    const rsp = await API.get(Config.getEnumUrl("building", "codeBuildingPartCatalog"));
    rsp.data.forEach((catalog: Enumerated) => {
      this.buildingPartCatalogs.set(catalog.id, this.asEnumerated(catalog));
    });
  }

  private loadBuildingParts = async () => {
    const rsp = await API.get(Config.getEnumUrl("building", "codeBuildingPart"));
    rsp.data.forEach((part: Enumerated) => {
      this.buildingParts.set(part.id, this.asEnumerated(part));
    });
  }

  private loadBuildingTypes = async () => {
    const rsp = await API.get(Config.getEnumUrl("building", "codeBuildingType"));
    rsp.data.forEach((type: Enumerated) => {
      this.buildingTypes.set(type.id, this.asEnumerated(type));
    });
  }

  private loadBuildingSubTypes = async () => {
    const rsp = await API.get(Config.getEnumUrl("building", "codeBuildingSubType"));
    rsp.data.forEach((subType: Enumerated) => {
      this.buildingSubTypes.set(subType.name, this.asEnumerated(subType));
    });
  }

  private loadBuildings = async () => {
    const entityTypeRepository = await BUILDING_API.getAggregates();
    const buildingsRepo = entityTypeRepository["building"];
    Object.keys(buildingsRepo).forEach(k => this.zeitwertBuildings.push(buildingsRepo[k]));
  }

  private setImportState(state: ImportState, message?: string) {
    this.importState = state;
    this.importError = message;
  }

  public parseHeader = (content: string): string[] => {
    this.setImportState(ImportState.PARSE_META);
    try {
      const lines = content.split("\n") ?? [];
      if (lines?.length < 7) {
        this.setImportState(ImportState.ERROR, "Ungültiges Dateiformat, zu wenig Zeilen");
      } else {
        const line1 = lines[1].split("\t");
        this.stratusMeta = {
          application: line1[1],
          version: line1[2],
          timestamp: line1[3],
        };
        return lines.splice(7);
      }
    } catch (e) {
      this.setImportState(ImportState.ERROR, "Ungültiges Dateiformat, Fehler beim Parsen der Metadaten, " + e);
    }
    return [];
  }

  public parseBuildings = (lines: string[]): StratusBuildings => {
    this.setImportState(ImportState.PARSE_BUILDINGS);
    const buildings: StratusBuilding[] = [];
    try {
      lines.forEach((buildingLine, index) => {
        if (buildingLine !== "") {
          const building: StratusBuilding = {
            id: index + 1,
            fields: buildingLine.split("\t"),
            status: StratusBuildingStatus.OK,
            messages: [],
            isSelected: false,
            isImported: false,
            elements: [],
          };
          // put the fields into the corresponding building fields (indexed by constants above)
          building.buildingNr = building.fields[BUILDING_NR]?.trim().replaceAll(" ", ".");
          building.identification = building.fields[IDENTIFICATION];
          building.name = building.fields[NAME];
          building.buildingYear = Number(building.fields[BUILDING_YEAR]);
          building.street = building.fields[STREET];
          building.zip = building.fields[ZIP];
          building.city = building.fields[CITY];
          building.address = building.street + " " + building.zip + " " + building.city;
          building.countryId = building.fields[COUNTRY_ID];
          building.currencyId = building.fields[CURRENCY_ID];
          building.insuredValue = Number(building.fields[INSURED_VALUE]);
          building.insuredValueYear = Number(building.fields[INSURED_VALUE_YEAR]);
          building.correctionFactor = building.fields[CORRECTION_FACTOR];
          building.notInsuredValue = Number(building.fields[NOT_INSURED_VALUE]);
          building.notInsuredValueYear = Number(building.fields[NOT_INSURED_YEAR]);
          building.thirdPartyValue = Number(building.fields[THIRD_PARTY_VALUE]);
          building.thirdPartyValueYear = Number(building.fields[THIRD_PARTY_VALUE_YEAR]);
          building.volume = Number(building.fields[VOLUME]);
          building.area = Number(building.fields[AREA]);

          building.buildingTypeId = building.fields[BUILDING_TYPE_ID];
          building.buildingTypeId = building.buildingTypeId ? "T" + building.buildingTypeId.substring(0, 2) : undefined;
          building.buildingSubTypeId = building.fields[BUILDING_SUB_TYPE_ID];

          building.department = building.fields[DEPARTMENT];
          building.maintenanceStrategyId = building.fields[MAINTENANCE_STRATEGY_ID];
          building.doCalc = building.fields[DO_CALC];
          building.doNotCalcFromYear = building.fields[DO_NOT_CALC_FROM_YEAR];
          building.doForceRestauration = building.fields[DO_FORCE_RESTORATION];
          building.description = building.fields[DESCRIPTION];
          building.description = building.description?.replaceAll("\\n", "\n");

          building.partCatalogId = building.fields[PART_CATALOG_ID];
          building.partCatalogId = building.partCatalogId ? "C" + building.partCatalogId : undefined;

          building.partCount = Number(building.fields[PART_COUNT]);

          building.buildingType = building.buildingTypeId && this.buildingTypes.has(building.buildingTypeId) ? this.buildingTypes.get(building.buildingTypeId) : undefined;
          building.buildingSubType = this.buildingSubTypes.has(building.buildingSubTypeId) ? this.buildingSubTypes.get(building.buildingSubTypeId) : undefined;
          const buildingTypeName = building.buildingType?.name ?? building.buildingTypeId ?? "";
          const buildingSubTypeName = building.buildingSubType?.name ?? building.buildingSubTypeId ?? "";
          building.buildingTypeDisplay = buildingTypeName + (buildingTypeName && buildingSubTypeName ? " | " : "") + buildingSubTypeName;
          building.partCatalog = building.partCatalogId && this.buildingPartCatalogs.has(building.partCatalogId) ? this.buildingPartCatalogs.get(building.partCatalogId) : undefined;

          building.theoreticalFieldCount = 38 + building.partCount * 9;

          if (building.theoreticalFieldCount === building.fields.length) {
            for (let i = 0; i < building.partCount; i++) {
              const baseIndex = 38 + i * 9;
              const partId = "P" + building.fields[baseIndex + PART_ID];
              const element: StratusBuildingElement = {
                partId: partId,
                part: this.buildingParts.has(partId) ? this.buildingParts.get(partId) : undefined,
                weight: Number(building.fields[baseIndex + PART_WEIGHT]),
                condition: 100 * Number(building.fields[baseIndex + PART_CONDITION]),
                conditionYear: Number(building.fields[baseIndex + PART_CONDITION_YEAR]),
                description: building.fields[baseIndex + PART_DESCRIPTION]?.replaceAll("\\n", "\n"),
                strain: building.fields[baseIndex + PART_STRAIN],
                strength: building.fields[baseIndex + PART_STRENGTH],
              }
              building.elements.push(element);
            }
          }
          building.conditionYear = building.elements.reduce((acc, cur) => Math.max(acc, cur.conditionYear ?? 0), 0);

          buildings.push(building);
        }
      });
    } catch (e) {
      this.setImportState(ImportState.ERROR, "Ungültiges Dateiformat, Fehler beim Parsen der Gebäude, " + e);
    }
    return buildings;
  }

  public validateBuildings = (buildings: StratusBuildings) => {
    try {
      buildings.forEach((building) => {
        building.status =
          !!building.buildingNr &&
            !!building.name &&
            !!building.street &&
            !!building.zip &&
            !!building.city &&
            (building.insuredValue ?? 0) > 0 &&
            (building.insuredValueYear ?? 0) > 0 &&
            (building.conditionYear ?? 0) > 0 &&
            !!building.buildingTypeId &&
            !!building.buildingSubTypeId &&
            !!building.maintenanceStrategyId &&
            !!building.partCatalogId &&
            (building.theoreticalFieldCount === building.fields.length)
            ? StratusBuildingStatus.OK
            : StratusBuildingStatus.Error;
      });
      buildings
        .filter((building) => building.status === StratusBuildingStatus.OK)
        .forEach((building) => {
          if (!this.buildingPartCatalogs.has(building.partCatalogId!)) {
            building.status = StratusBuildingStatus.Error;
            building.messages.push("Katalog nicht gefunden (" + building.partCatalogId + ")");
          }
          if (!this.buildingTypes.has(building.buildingTypeId!)) {
            building.status = StratusBuildingStatus.Error;
            building.messages.push("Gebäudetyp nicht gefunden (" + building.buildingTypeId + ")");
          }
          if (!this.buildingSubTypes.has(building.buildingSubTypeId!)) {
            building.status = StratusBuildingStatus.Error;
            building.messages.push("Gebäudesubtyp nicht gefunden (" + building.buildingSubTypeId + ")");
          }
          if (building.maintenanceStrategyId !== "Normal") {
            building.status = StratusBuildingStatus.Warning;
            building.messages.push("Unterhaltsplanung ist nicht 'Normal'");
          }
          if (!building.elements.every((element) => element.conditionYear === building.conditionYear)) {
            building.status = StratusBuildingStatus.Warning;
            building.messages.push("Bauteile mit unterschiedlichen Zustandsbewertungsjahren");
          }
          building.elements.forEach((element) => {
            if (!element.part) {
              building.status = StratusBuildingStatus.Error;
              building.messages.push("Bauteil nicht gefunden " + element.partId);
            }
          });
          this.zeitwertBuildings.forEach((existingBuilding) => {
            if (existingBuilding.buildingNr === building.buildingNr) {
              building.status = StratusBuildingStatus.Warning;
              building.messages.push("Gebäude mit gleicher Nummer existiert bereits");
            }
            if (existingBuilding.name === building.name) {
              building.status = StratusBuildingStatus.Warning;
              building.messages.push("Gebäude mit gleichem Namen existiert bereits");
            }
            if (existingBuilding.street === building.street) {
              building.status = StratusBuildingStatus.Warning;
              building.messages.push("Gebäude mit gleicher Adresse existiert bereits");
            }
          })
        });
    } catch (e) {
      this.setImportState(ImportState.ERROR, "Ungültiges Dateiformat, Fehler beim Validieren der Gebäude, " + e);
    }
  }

  @action
  public importBuildings = async (buildings: StratusBuildings, onProgress: (progress: number) => void) => {
    this.setImportState(ImportState.IMPORT_BUILDINGS);
    const TotalSteps = 5 * buildings.length;
    try {
      let i = 0;
      const onLocalProgress = (progress: number) => {
        onProgress(100 * (i * 5 + progress) / TotalSteps);
      }
      for (; i < buildings.length; i++) {
        await this.importBuilding(buildings[i], onLocalProgress);
      }
      this.setImportState(ImportState.DONE);
    } catch (e) {
      this.setImportState(ImportState.ERROR, "Fehler beim generieren der Gebäude, " + e);
    }
  }

  private importBuilding = async (stratusBuilding: StratusBuilding, onProgress: (progress: number) => void) => {
    onProgress(0);
    const store = BuildingStoreModel.create({});
    try {
      store.create({
        name: stratusBuilding.name,
        description: stratusBuilding.description,
        buildingNr: stratusBuilding.buildingNr,
        //
        buildingType: stratusBuilding.buildingType,
        buildingSubType: stratusBuilding.buildingSubType,
        buildingYear: stratusBuilding.buildingYear,
        currency: { id: "chf" },
        //
        street: stratusBuilding.street,
        zip: stratusBuilding.zip,
        city: stratusBuilding.city,
        country: { id: "ch" },
        //
        volume: stratusBuilding.volume,
        areaGross: stratusBuilding.area,
        //
        insuredValue: stratusBuilding.insuredValue,
        insuredValueYear: stratusBuilding.insuredValueYear,
        notInsuredValue: stratusBuilding.notInsuredValue,
        notInsuredValueYear: stratusBuilding.notInsuredValueYear,
        thirdPartyValue: stratusBuilding.thirdPartyValue,
        thirdPartyValueYear: stratusBuilding.thirdPartyValueYear,
        //
        partCatalog: stratusBuilding.partCatalog,
        maintenanceStrategy: { id: "normal" },
      });
      await store.building!.setAccount(session.sessionInfo?.account?.id!);
      await store.store();
      onProgress(1);
      await store.building?.addRating();
      onProgress(2);
      store.building!.setField("ratingDate", new Date().setFullYear(stratusBuilding.conditionYear!));
      await store.building!.setPartCatalog({ id: "C99", name: "Stratus" }/*stratusBuilding.partCatalog*/);
      onProgress(3);
      const changes = Object.assign({}, getSnapshot(store.building!) as any);
      changes.elements = [];
      stratusBuilding.elements.forEach((element) => {
        const buildingElement: any = {
          id: "New:" + UUID(),
          obj: undefined,
          buildingPart: element.part,
          weight: element.weight,
          condition: element.condition,
          conditionYear: stratusBuilding.conditionYear,
          strain: element.strain === "-" ? -1 : element.strain === "+" ? 1 : undefined,
          strength: element.strength === "-" ? -1 : element.strength === "+" ? 1 : undefined,
          description: element.description,
          lifeTime20: 0,
          lifeTime50: 0,
          lifeTime70: 0,
          lifeTime85: 0,
          lifeTime95: 0,
          lifeTime100: 0,
        };
        changes.elements.push(buildingElement);
      });
      changes.meta = { clientVersion: changes.meta.version };
      await BUILDING_API.storeAggregate(changes);
      onProgress(5);
    } catch (e) {
      console.error("Fehler beim generieren von Gebäude", e, stratusBuilding);
      throw new Error("could not create building");
    }
  }

}
