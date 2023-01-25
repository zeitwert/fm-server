
import { Button, Card, Icon, MediaObject, Modal, ProgressBar, Tabs, TabsPanel } from "@salesforce/design-system-react";
import { FieldGroup, FieldRow, Input, TextArea } from "@zeitwert/ui-forms";
import { EntityType, EntityTypes } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import classNames from "classnames";
import { makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import StratusImport, { StratusBuilding, StratusBuildings, StratusBuildingStatus } from "./StratusImport";

export interface StratusImportFormProps {
	onCancel: () => void;
	onImport: (buildings: StratusBuildings) => void;
}

@observer
export default class StratusImportForm extends React.Component<StratusImportFormProps> {

	stratusImport: StratusImport = new StratusImport();
	@observable buildings: StratusBuildings = [];
	@observable selectedBuilding: StratusBuilding | undefined = undefined;
	@observable doImport: boolean = false;
	@observable importProgress: number = 0;

	constructor(props: StratusImportFormProps) {
		super(props);
		console.clear();
		makeObservable(this);
	}

	async componentDidMount() {
		await this.stratusImport.loadReferenceData();
	}

	render() {
		const entityType = EntityTypes[EntityType.BUILDING];
		const { stratusMeta, importState, importError } = this.stratusImport;
		const heading = (
			<MediaObject
				body={<>Immobilienimport aus Stratus</>}
				figure={<Icon category={entityType.iconCategory} name={entityType.iconName} size="small" />}
				verticalCenter
			/>
		);
		const allowImport = !!this.buildings.find((b) => b.isSelected);
		const buttons = (
			<>
				<Button onClick={this.props.onCancel}>Cancel</Button>
				<Button variant="brand" onClick={this.onImport} disabled={!allowImport}>
					Import
				</Button>
			</>
		);
		return (
			<Modal
				heading={heading}
				onRequestClose={this.props.onCancel}
				dismissOnClickOutside={false}
				footer={buttons}
				ariaHideApp={false}
				size="large"
				isOpen
			>
				<Card hasNoHeader>
					<div className="slds-card__body slds-card__body_inner">
						<Grid isVertical={false}>
							<Col cols={1} totalCols={3} className="slds-m-left_small slds-m-right_small">
								<div style={{ float: "left", width: "60px" }}>
									<Icon category="utility" name="warning" size="large" style={{ fill: "darkorange" }} />
								</div>
								<div style={{ float: "right", width: "calc(100% - 60px)" }}>
									<p>
										Damit Umlaute richtig übernommen werden, muss die Exportdatei <code>object.txt</code> von Stratus im UTF-8 Format gespeichert werden.<br />
										<strong>Vorgehen: </strong>
										Im Windows-Explorer <code>object.txt</code> via rechte Maustaste und Kontextmenü <code>Öffnen mit &gt; Editor</code> öffnen.<br />
										Danach <code>Speichern unter</code>, im Dateidialog die Codierung <code>UTF-8</code> auswählen und die Datei mit der Endung <code>.utf8.txt</code> speichern.
									</p>
								</div>
							</Col>
							<Col cols={1} totalCols={3} className="slds-m-left_small slds-m-right_small">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<input type="file" accept=".utf8.txt" onChange={this.onFileChange} />
										</FieldRow>
									</FieldGroup>
								</div>
							</Col>
							<Col cols={1} totalCols={3} className="slds-m-left_small slds-m-right_small">
								<dl className="slds-dl_horizontal">
									<dt className="slds-dl_horizontal__label">Applikation:</dt>	<dd className="slds-dl_horizontal__detail">{stratusMeta?.application}</dd>
									<dt className="slds-dl_horizontal__label">Version:</dt>			<dd className="slds-dl_horizontal__detail">{stratusMeta?.version}</dd>
									<dt className="slds-dl_horizontal__label">Timestamp:</dt>		<dd className="slds-dl_horizontal__detail">{stratusMeta?.timestamp}</dd>
									<dt className="slds-dl_horizontal__label">Status:</dt>			<dd className="slds-dl_horizontal__detail">{importState}{importError ? ": " + importError : ""}</dd>
								</dl>
							</Col>
						</Grid>
					</div>
				</Card>
				<Card hasNoHeader>
					<div style={{ minHeight: "200px", maxHeight: "300px", overflow: "auto", cursor: "default" }}>
						<BuildingList
							buildings={this.buildings}
							selectedBuilding={this.selectedBuilding}
							onSelect={(building) => this.selectedBuilding = building}
						/>
					</div>
				</Card>
				{
					<Card hasNoHeader>
						<div>
							<BuildingInfo building={this.selectedBuilding} />
						</div>
					</Card>
				}
				{
					<Card hasNoHeader>
						<div>
							<ImportProgress progress={this.importProgress} />
						</div>
					</Card>
				}
			</Modal>
		);
	}

	private onFileChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
		e.preventDefault();
		const reader = new FileReader();
		reader.onload = async (e) => {
			if (typeof e.target?.result === "string") {
				this.parseFile(e.target?.result);
			}
		};
		e.target?.files?.[0] && reader.readAsText(e.target?.files?.[0]);
	}

	private parseFile = async (content: string) => {
		const lines = this.stratusImport.parseHeader(content);
		const buildings = this.stratusImport.parseBuildings(lines);
		buildings.sort((a, b) => (a.buildingNr ?? "").localeCompare(b.buildingNr ?? ""));
		this.stratusImport.validateBuildings(buildings);
		this.buildings = buildings;
	}

	private onImport = async () => {
		this.doImport = true;
		try {
			this.stratusImport.importBuildings(toJS(this.buildings).filter(b => b.isSelected), this.onImportProgress);
		} finally {
			this.doImport = false;
		}
	}

	private onImportProgress = (progress: number) => {
		this.importProgress = progress;
	}

}

interface BuildingListProps {
	buildings: StratusBuildings;
	selectedBuilding: StratusBuilding | undefined;
	onSelect: (bulding: StratusBuilding) => void;
}

@observer
class BuildingList extends React.Component<BuildingListProps> {

	@observable showDetails: boolean = false;
	@observable allSelected: boolean = false;

	constructor(props: BuildingListProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		return (
			<table className="slds-table slds-table_cell-buffer slds-table_bordered">
				<thead>
					<tr className="slds-line-height_reset">
						<th>
							<div>
								<input
									type="checkbox"
									className="slds-checkbox"
									checked={this.allSelected}
									onChange={() => this.selectAll(!this.allSelected)}
								/>
							</div>
						</th>
						<th><div className="slds-truncate">Nr</div></th>
						<th><div className="slds-truncate">Name</div></th>
						<th><div className="slds-truncate">Status</div></th>
						<th><div className="slds-truncate">Adresse</div></th>
						<th><div className="slds-truncate">PartCatalog</div></th>
						<th><div className="slds-truncate">BuildingType</div></th>
						<th><div className="slds-truncate">Versicherungswert</div></th>
						<th><div className="slds-truncate">PartCount</div></th>
						{
							this.showDetails &&
							<>
								<th><div className="slds-truncate">BuildingYear</div></th>
								<th><div className="slds-truncate">Volume</div></th>
								<th><div className="slds-truncate">Area</div></th>
								<th><div className="slds-truncate">Description</div></th>
								<th><div className="slds-truncate">notInsuredValue</div></th>
								<th><div className="slds-truncate">notInsuredValueYear</div></th>
								<th><div className="slds-truncate">thirdPartyValue</div></th>
								<th><div className="slds-truncate">thirdPartyValueYear</div></th>
								<th><div className="slds-truncate">maintenanceStrategyId</div></th>
								<th><div className="slds-truncate">correctionFactor</div></th>
								<th><div className="slds-truncate">countryId</div></th>
								<th><div className="slds-truncate">currencyId</div></th>
								<th><div className="slds-truncate">department</div></th>
								<th><div className="slds-truncate">doCalc</div></th>
								<th><div className="slds-truncate">doNotCalcFromYear</div></th>
								<th><div className="slds-truncate">doForceRestauration</div></th>
							</>
						}
					</tr>
				</thead>
				<tbody>
					{
						this.props.buildings.map(building => (
							<tr
								className={classNames("slds-hint-parent", { "slds-is-selected": building.id === this.props.selectedBuilding?.id })}
								key={"bldg-" + building.buildingNr}
								onClick={() => this.props.onSelect(building)}
							>
								<td>
									<div>
										<input
											type="checkbox"
											className="slds-checkbox"
											disabled={building.status === StratusBuildingStatus.Error}
											checked={building.isSelected}
											onChange={() => building.isSelected = !building.isSelected}
										/>
									</div>
								</td>
								<td><div className="slds-truncate">{building.buildingNr}</div></td>
								<td><div className="slds-truncate">{building.name}</div></td>
								<td><div className="slds-truncate">{this.getStatusIcon(building)}</div></td>
								<td><div className="slds-truncate">{building.address}</div></td>
								<td><div className="slds-truncate">{building.partCatalog?.name ?? building.partCatalogId}</div></td>
								<td><div className="slds-truncate">{building.buildingTypeDisplay}</div></td>
								<td><div className="slds-truncate" style={{ float: "right" }}>{building.insuredValue + (building.insuredValueYear ? ` (${building.insuredValueYear})` : "")}</div></td>
								<td><div className="slds-truncate">{building.partCount} | {building.fields.length} | {building.theoreticalFieldCount}</div></td>
								{
									this.showDetails &&
									<>
										<td><div className="slds-truncate">{building.buildingYear}</div></td>
										<td><div className="slds-truncate">{building.volume}</div></td>
										<td><div className="slds-truncate">{building.area}</div></td>
										<td><div className="slds-truncate">{building.description}</div></td>
										<td><div className="slds-truncate">{building.notInsuredValue}</div></td>
										<td><div className="slds-truncate">{building.notInsuredValueYear}</div></td>
										<td><div className="slds-truncate">{building.thirdPartyValue}</div></td>
										<td><div className="slds-truncate">{building.thirdPartyValueYear}</div></td>
										<td><div className="slds-truncate">{building.maintenanceStrategyId}</div></td>
										<td><div className="slds-truncate">{building.correctionFactor}</div></td>
										<td><div className="slds-truncate">{building.countryId}</div></td>
										<td><div className="slds-truncate">{building.currencyId}</div></td>
										<td><div className="slds-truncate">{building.department}</div></td>
										<td><div className="slds-truncate">{building.doCalc}</div></td>
										<td><div className="slds-truncate">{building.doNotCalcFromYear}</div></td>
										<td><div className="slds-truncate">{building.doForceRestauration}</div></td>
									</>
								}
							</tr>
						))
					}
				</tbody>
			</table>
		);
	}

	private getStatusIcon = (building: StratusBuilding) => {
		switch (building.status) {
			case StratusBuildingStatus.Error:
				return <Icon category="utility" name="error" size="small" colorVariant="error" />;
			case StratusBuildingStatus.Warning:
				return <Icon category="utility" name="warning" size="small" colorVariant="warning" />;
			default:
				return <Icon category="utility" name="check" size="small" colorVariant="success" />;
		}
	}

	private selectAll = (allSelected: boolean) => {
		this.allSelected = allSelected;
		this.props.buildings.forEach((building) => {
			building.isSelected = allSelected && (building.status !== StratusBuildingStatus.Error);
		});
	}

}

interface BuildingInfoProps {
	building: StratusBuilding | undefined;
}

enum TABS {
	MAIN = "main",
	ELEMENTS = "elements",
	VALIDATIONS = "validations",
}
const TAB_VALUES = Object.values(TABS);

@observer
class BuildingInfo extends React.Component<BuildingInfoProps> {

	@observable activeLeftTabId = TABS.MAIN;

	constructor(props: BuildingInfoProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const building = this.props.building;
		return (
			<div style={{ minHeight: "640px", maxHeight: "640px", overflowY: "auto" }}>
				{
					building &&
					<Tabs
						className="full-height"
						selectedIndex={TAB_VALUES.indexOf(this.activeLeftTabId)}
						onSelect={(tabId: number) => (this.activeLeftTabId = TAB_VALUES[tabId])}
					>
						<TabsPanel label="Details">
							{
								this.activeLeftTabId === TABS.MAIN &&
								<Card hasNoHeader><BuildingDetails building={building} /></Card>
							}
						</TabsPanel>
						<TabsPanel label="Bauteile">
							{
								this.activeLeftTabId === TABS.ELEMENTS &&
								<Card hasNoHeader><ElementList building={building} /></Card>
							}
						</TabsPanel>
						<TabsPanel label={`Validierungen (${building?.messages.length})`}>
							{
								!!building?.messages.length && this.activeLeftTabId === TABS.VALIDATIONS &&
								<Card hasNoHeader><ValidationList building={building} /></Card>
							}
						</TabsPanel>
					</Tabs>
				}
			</div>
		);
	}

}

class BuildingDetails extends React.Component<BuildingInfoProps> {

	render() {
		const building = this.props.building;
		return (
			<>
				<Grid className="slds-wrap slds-m-top_small" isVertical={false}>
					<Col cols={1} totalCols={3}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Grunddaten">
								<FieldRow>
									<Input label="Nr" value={building?.buildingNr ?? " "} size={3} disabled />
									<Input label="Name" value={building?.name ?? " "} size={9} disabled />
								</FieldRow>
							</FieldGroup>
							<FieldGroup legend="Addresse" isAddress className="slds-m-top_medium">
								<FieldRow>
									<Input label="Strasse" value={building?.street ?? " "} disabled />
								</FieldRow>
								<FieldRow>
									<Input label="PLZ" value={building?.zip ?? " "} size={3} disabled />
									<Input label="Ort" value={building?.city ?? " "} size={9} disabled />
								</FieldRow>
							</FieldGroup>
							<FieldGroup legend="Beschreibung">
								<FieldRow>
									<TextArea value={building?.description ?? " "} rows={12} disabled />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
					<Col cols={1} totalCols={3}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Klassifizierung">
								<FieldRow>
									<Input label="Gebäudekategorie" value={building?.partCatalog?.name ?? building?.partCatalogId ?? " "} disabled />
								</FieldRow>
								<FieldRow>
									<Input label="Unterhaltsplanung" value={building?.maintenanceStrategyId ?? " "} disabled />
								</FieldRow>
								<FieldRow>
									<Input label="Bauwerksart SIA I" value={building?.buildingType?.name ?? building?.buildingTypeId ?? "-"} disabled />
								</FieldRow>
								<FieldRow>
									<Input label="Bauwerksart SIA II" value={building?.buildingSubType?.name ?? building?.buildingSubTypeId ?? "-"} disabled />
								</FieldRow>
							</FieldGroup>
							<FieldGroup legend="Dimensionen" className="slds-m-top_medium">
								<FieldRow>
									<Input label="Volumen RI (m³)" value={building?.volume?.toString() ?? " "} size={6} disabled />
									<Input label="Fläche GF (m²)" value={building?.area?.toString() ?? " "} size={6} disabled />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
					<Col cols={1} totalCols={3}>
						<Card hasNoHeader={true} bodyClassName="slds-card__body_inner">
							<FieldGroup legend="Bewertung">
								<FieldRow>
									<Input label="Baujahr" value={building?.buildingYear?.toString() ?? " "} size={6} disabled />
									<Input label="Bewertungsjahr" value={building?.conditionYear?.toString() ?? " "} size={6} disabled />
								</FieldRow>
								<FieldRow>
									<Input label="Versicherungswert (kCHF)" value={building?.insuredValue?.toString() ?? " "} size={8} disabled />
									<Input label="Jahr" value={building?.insuredValueYear?.toString() ?? " "} size={4} disabled />
								</FieldRow>
								<FieldRow>
									<Input label="Nicht versicherter Wert (kCHF)" value={building?.notInsuredValue?.toString() ?? " "} size={8} disabled />
									<Input label="Jahr" value={building?.notInsuredValueYear?.toString() ?? " "} size={4} disabled />
								</FieldRow>
								<FieldRow>
									<Input label="Wert Fremdeigentum (kCHF)" value={building?.thirdPartyValue?.toString() ?? " "} size={8} disabled />
									<Input label="Jahr" value={building?.thirdPartyValueYear?.toString() ?? " "} size={4} disabled />
								</FieldRow>
							</FieldGroup>
						</Card>
					</Col>
				</Grid>
			</>
		);
	}

}

@observer
class ElementList extends React.Component<BuildingInfoProps> {

	partId?: string | undefined;
	description?: string | undefined;
	conditionYear?: string | undefined;
	strain?: string | undefined;
	strength?: string | undefined;
	condition?: string | undefined;
	weight?: string | undefined;

	render() {
		return (
			<table className="slds-table slds-table_cell-buffer slds-table_bordered">
				<thead>
					<tr className="slds-line-height_reset">
						<th style={{ minWidth: "100px", maxWidth: "100px" }}><div className="slds-truncate">Id</div></th>
						<th style={{ minWidth: "40px", maxWidth: "40px" }}><div className="slds-truncate">Weight</div></th>
						<th style={{ minWidth: "40px", maxWidth: "40px" }}><div className="slds-truncate">Condition</div></th>
						<th style={{ minWidth: "40px", maxWidth: "40px" }}><div className="slds-truncate">Year</div></th>
						<th><div style={{ minWidth: "400px", maxWidth: "600px" }} className="slds-truncate">Description</div></th>
						<th><div className="slds-truncate">Strain</div></th>
						<th><div className="slds-truncate">Strength</div></th>
					</tr>
				</thead>
				<tbody>
					{
						this.props.building?.elements.map(element => (
							<tr
								className="slds-hint-parent"
								key={"bldg-" + element.partId}
							>
								<td><div className="slds-truncate">{element.part?.name ?? element.partId}</div></td>
								<td><div className="slds-truncate">{element.weight}</div></td>
								<td><div className="slds-truncate">{element.condition}</div></td>
								<td><div className="slds-truncate">{element.conditionYear}</div></td>
								<td><div className="slds-truncate" style={{ minWidth: "400px", maxWidth: "600px" }}>{element.description ?? "&nbsp;"}</div></td>
								<td><div className="slds-truncate">{element.strain ?? "&nbsp;"}</div></td>
								<td><div className="slds-truncate">{element.strength ?? "&nbsp;"}</div></td>
							</tr>
						))
					}
				</tbody>
			</table>
		);
	}

}

@observer
class ValidationList extends React.Component<BuildingInfoProps> {

	render() {
		return (
			<div className="slds-m-around_medium">
				<ul className="slds-list_dotted">
					{
						this.props.building?.messages.map((e, index) => <li key={"v-" + index}>{e}</li>)
					}
				</ul>
			</div>
		);
	}

}

interface ImportProgressProps {
	progress: number;
}

@observer
class ImportProgress extends React.Component<ImportProgressProps> {

	render() {
		return (
			<div className="slds-m-around_medium">
				<ProgressBar id="progress-bar" value={this.props.progress} />
			</div>
		);
	}

}
