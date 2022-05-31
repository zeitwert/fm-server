
package io.zeitwert.fm.building.model.base;

import java.math.BigDecimal;
import java.util.Collection;

import org.jooq.UpdatableRecord;

import io.zeitwert.ddd.common.model.enums.CodeCountry;
import io.zeitwert.ddd.common.model.enums.CodeCountryEnum;
import io.zeitwert.ddd.common.model.enums.CodeCurrency;
import io.zeitwert.ddd.common.model.enums.CodeCurrencyEnum;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.property.model.enums.CodePartListType;
import io.zeitwert.ddd.session.model.SessionInfo;
import io.zeitwert.ddd.validation.model.enums.CodeValidationLevelEnum;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElement;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import io.zeitwert.fm.building.model.enums.CodeBuildingMaintenanceStrategyEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPart;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalog;
import io.zeitwert.fm.building.model.enums.CodeBuildingPartCatalogEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingPriceIndex;
import io.zeitwert.fm.building.model.enums.CodeBuildingPriceIndexEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeBuildingType;
import io.zeitwert.fm.building.model.enums.CodeBuildingTypeEnum;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservationEnum;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.obj.model.base.FMObjBase;

public abstract class ObjBuildingBase extends FMObjBase implements ObjBuilding {

	static final CodeBuildingPriceIndex DefaultPriceIndex = CodeBuildingPriceIndexEnum.getBuildingPriceIndex("ch-ZRH");
	static final Integer DefaultGeoZoom = 17;

	private final UpdatableRecord<?> dbRecord;

	protected final ReferenceProperty<ObjAccount> account;

	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final SimpleProperty<String> buildingNr;
	protected final SimpleProperty<String> buildingInsuranceNr;
	protected final SimpleProperty<String> plotNr;
	protected final SimpleProperty<String> nationalBuildingId;
	protected final EnumProperty<CodeHistoricPreservation> historicPreservation;

	protected final SimpleProperty<String> street;
	protected final SimpleProperty<String> zip;
	protected final SimpleProperty<String> city;
	protected final EnumProperty<CodeCountry> country;

	protected final SimpleProperty<String> geoAddress;
	protected final SimpleProperty<String> geoCoordinates;
	protected final SimpleProperty<Integer> geoZoom;

	protected final ReferenceProperty<ObjDocument> coverFoto;

	protected final EnumProperty<CodeCurrency> currency;

	protected final SimpleProperty<BigDecimal> volume;
	protected final SimpleProperty<BigDecimal> areaGross;
	protected final SimpleProperty<BigDecimal> areaNet;
	protected final SimpleProperty<Integer> nrOfFloorsAboveGround;
	protected final SimpleProperty<Integer> nrOfFloorsBelowGround;

	protected final EnumProperty<CodeBuildingType> buildingType;
	protected final EnumProperty<CodeBuildingSubType> buildingSubType;
	protected final SimpleProperty<Integer> buildingYear;

	protected final SimpleProperty<BigDecimal> insuredValue;
	protected final SimpleProperty<Integer> insuredValueYear;
	protected final SimpleProperty<BigDecimal> notInsuredValue;
	protected final SimpleProperty<Integer> notInsuredValueYear;
	protected final SimpleProperty<BigDecimal> thirdPartyValue;
	protected final SimpleProperty<Integer> thirdPartyValueYear;

	protected final EnumProperty<CodeBuildingPartCatalog> buildingPartCatalog;
	protected final EnumProperty<CodeBuildingMaintenanceStrategy> buildingMaintenanceStrategy;

	protected final PartListProperty<ObjBuildingPartElement> elementList;

	protected Integer elementContributions = null;

	protected ObjBuildingBase(SessionInfo sessionInfo, ObjBuildingRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> contactRecord) {

		super(sessionInfo, repository, objRecord);

		this.dbRecord = contactRecord;

		this.account = this.addReferenceProperty(dbRecord, ObjBuildingFields.ACCOUNT_ID, ObjAccount.class);

		this.name = this.addSimpleProperty(dbRecord, ObjBuildingFields.NAME);
		this.description = this.addSimpleProperty(dbRecord, ObjBuildingFields.DESCRIPTION);
		this.buildingNr = this.addSimpleProperty(dbRecord, ObjBuildingFields.BUILDING_NR);
		this.buildingInsuranceNr = this.addSimpleProperty(dbRecord, ObjBuildingFields.BUILDING_INSURANCE_NR);
		this.plotNr = this.addSimpleProperty(dbRecord, ObjBuildingFields.PLOT_NR);
		this.nationalBuildingId = this.addSimpleProperty(dbRecord, ObjBuildingFields.NATIONAL_BUILDING_ID);
		this.historicPreservation = this.addEnumProperty(dbRecord, ObjBuildingFields.HISTORIC_PRESERVERATION_ID,
				CodeHistoricPreservationEnum.class);

		this.street = this.addSimpleProperty(dbRecord, ObjBuildingFields.STREET);
		this.zip = this.addSimpleProperty(dbRecord, ObjBuildingFields.ZIP);
		this.city = this.addSimpleProperty(dbRecord, ObjBuildingFields.CITY);
		this.country = this.addEnumProperty(dbRecord, ObjBuildingFields.COUNTRY_ID, CodeCountryEnum.class);
		this.currency = this.addEnumProperty(dbRecord, ObjBuildingFields.CURRENCY_ID, CodeCurrencyEnum.class);

		this.geoAddress = this.addSimpleProperty(dbRecord, ObjBuildingFields.GEO_ADDRESS);
		this.geoCoordinates = this.addSimpleProperty(dbRecord, ObjBuildingFields.GEO_COORDINATES);
		this.geoZoom = this.addSimpleProperty(dbRecord, ObjBuildingFields.GEO_ZOOM);

		this.coverFoto = this.addReferenceProperty(dbRecord, ObjBuildingFields.COVER_FOTO_ID, ObjDocument.class);

		this.volume = this.addSimpleProperty(dbRecord, ObjBuildingFields.VOLUME);
		this.areaGross = this.addSimpleProperty(dbRecord, ObjBuildingFields.AREA_GROSS);
		this.areaNet = this.addSimpleProperty(dbRecord, ObjBuildingFields.AREA_NET);
		this.nrOfFloorsAboveGround = this.addSimpleProperty(dbRecord, ObjBuildingFields.NR_OF_FLOORS_ABOVE_GROUND);
		this.nrOfFloorsBelowGround = this.addSimpleProperty(dbRecord, ObjBuildingFields.NR_OF_FLOORS_BELOW_GROUND);

		this.buildingType = this.addEnumProperty(dbRecord, ObjBuildingFields.BUILDING_TYPE_ID, CodeBuildingTypeEnum.class);
		this.buildingSubType = this.addEnumProperty(dbRecord, ObjBuildingFields.BUILDING_SUB_TYPE_ID,
				CodeBuildingSubTypeEnum.class);
		this.buildingYear = this.addSimpleProperty(dbRecord, ObjBuildingFields.BUILDING_YEAR);

		this.insuredValue = this.addSimpleProperty(dbRecord, ObjBuildingFields.INSURED_VALUE);
		this.insuredValueYear = this.addSimpleProperty(dbRecord, ObjBuildingFields.INSURED_VALUE_YEAR);
		this.notInsuredValue = this.addSimpleProperty(dbRecord, ObjBuildingFields.NOT_INSURED_VALUE);
		this.notInsuredValueYear = this.addSimpleProperty(dbRecord, ObjBuildingFields.NOT_INSURED_VALUE_YEAR);
		this.thirdPartyValue = this.addSimpleProperty(dbRecord, ObjBuildingFields.THIRD_PARTY_VALUE);
		this.thirdPartyValueYear = this.addSimpleProperty(dbRecord, ObjBuildingFields.THIRD_PARTY_VALUE_YEAR);

		this.buildingPartCatalog = this.addEnumProperty(dbRecord, ObjBuildingFields.BUILDING_PART_CATALOG_ID,
				CodeBuildingPartCatalogEnum.class);
		this.buildingMaintenanceStrategy = this.addEnumProperty(dbRecord,
				ObjBuildingFields.BUILDING_MAINTENANCE_STRATEGY_ID, CodeBuildingMaintenanceStrategyEnum.class);

		this.elementList = this.addPartListProperty(this.getRepository().getElementListType());

	}

	@Override
	public ObjBuildingRepository getRepository() {
		return (ObjBuildingRepository) super.getRepository();
	}

	public abstract void loadElementList(Collection<ObjBuildingPartElement> nodeList);

	@Override
	public void doInit(Integer objId, Integer tenantId, Integer userId) {
		super.doInit(objId, tenantId, userId);
		this.dbRecord.setValue(ObjBuildingFields.OBJ_ID, objId);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.elementList) {
			return (P) this.getRepository().getElementRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	@Override
	public void beforeStore() {
		super.beforeStore();
		int seqNr = 0;
		for (ObjBuildingPartElement element : this.getElementList()) {
			element.setSeqNr(seqNr++);
		}
	}

	@Override
	public void doStore(Integer userId) {
		super.doStore(userId);
		this.dbRecord.store();
	}

	@Override
	public void setBuildingPartCatalog(CodeBuildingPartCatalog catalog) {
		if ((catalog == null) != (this.getBuildingPartCatalog() == null) || catalog != this.getBuildingPartCatalog()) {
			this.elementList.clearPartList();
		}
		this.buildingPartCatalog.setValue(catalog);
	}

	@Override
	public double getBuildingValue(int year) {
		if (this.getInsuredValueYear() != null && this.getInsuredValue() != null) {
			return DefaultPriceIndex.priceAt(this.getInsuredValueYear(), 1000.0 * this.getInsuredValue().doubleValue(), year);
		}
		return 0;
	}

	@Override
	public Integer getElementContributions() {
		return this.elementContributions;
	}

	@Override
	public ObjBuildingPartElement getElement(CodeBuildingPart buildingPart) {
		return this.elementList.getPartList().stream().filter(p -> p.getBuildingPart() == buildingPart).findFirst()
				.orElse(null);
	}

	@Override
	public ObjBuildingPartElement addElement(CodeBuildingPart buildingPart) {
		require(this.getElement(buildingPart) == null, "unique element for buildingPart");
		ObjBuildingPartElement e = this.elementList.addPart();
		e.setBuildingPart(buildingPart);
		return e;
	}

	@Override
	protected void doCalcAll() {
		this.doCalcVolatile();
	}

	@Override
	protected void doCalcVolatile() {
		this.calcCaption();
		this.calcElementContributions();
		this.validateElements();
	}

	private void calcCaption() {
		this.caption.setValue(this.getName() + " (" + this.getZip() + " " + this.getCity() + ")");
	}

	private void calcElementContributions() {
		this.elementContributions = 0;
		for (ObjBuildingPartElement element : this.getElementList()) {
			if (element.getValuePart() != null) {
				this.elementContributions += element.getValuePart();
			}
		}
	}

	private void validateElements() {
		if (this.getCoverFoto() == null || this.getCoverFoto().getContentType() == null) {
			this.addValidation(CodeValidationLevelEnum.ERROR, "Für den Druck muss ein Coverfoto hochgeladen werden");
		}
		if (this.getGeoCoordinates() == null) {
			this.addValidation(CodeValidationLevelEnum.ERROR, "Koordinaten der Immobilie fehlen");
		}
		if (this.getInsuredValue() == null || this.getInsuredValue().equals(BigDecimal.ZERO)) {
			this.addValidation(CodeValidationLevelEnum.ERROR, "Gebäudewert muss erfasst werden");
		}
		if (this.getInsuredValueYear() == null) {
			this.addValidation(CodeValidationLevelEnum.ERROR, "Jahr der Bestimmung des Gebäudewerts muss erfasst werden");
		}
		if (this.getElementContributions() != 100) {
			this.addValidation(CodeValidationLevelEnum.ERROR,
					"Summe der Bauteilwerte muss auf 100% summieren (ist " + this.getElementContributions() + "%)");
		}
		for (ObjBuildingPartElement element : this.getElementList()) {
			if (element.getValuePart() != null && element.getValuePart() != 0) {
				if (element.getCondition() == null || element.getCondition() == 0) {
					this.addValidation(CodeValidationLevelEnum.ERROR,
							"Zustand für Element [" + element.getBuildingPart().getName() + "] muss erfasst werden");
				} else if (element.getConditionYear() == null || element.getConditionYear() < 1800) {
					this.addValidation(CodeValidationLevelEnum.ERROR,
							"Jahr der Zustandsbewertung für Element [" + element.getBuildingPart().getName()
									+ "] muss erfasst werden");
				}
			}
		}
	}

}
