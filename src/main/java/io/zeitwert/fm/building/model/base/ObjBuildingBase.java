
package io.zeitwert.fm.building.model.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.jooq.UpdatableRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zeitwert.ddd.obj.model.ObjPartItemRepository;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.PartListProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.ReferenceProperty;
import io.zeitwert.ddd.property.model.ReferenceSetProperty;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.ddd.validation.model.enums.CodeValidationLevelEnum;
import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.account.model.enums.CodeCountryEnum;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.account.model.enums.CodeCurrencyEnum;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRatingRepository;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.*;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;

public abstract class ObjBuildingBase extends FMObjBase implements ObjBuilding {

	protected static final Logger logger = LoggerFactory.getLogger(ObjBuildingBase.class);

	static final CodeBuildingRatingStatus RatingOpen = CodeBuildingRatingStatusEnum.getRatingStatus("open");
	static final CodeBuildingRatingStatus RatingDone = CodeBuildingRatingStatusEnum.getRatingStatus("done");
	static final CodeBuildingRatingStatus RatingDiscarded = CodeBuildingRatingStatusEnum.getRatingStatus("discard");
	static final CodeBuildingPriceIndex DefaultPriceIndex = CodeBuildingPriceIndexEnum.getBuildingPriceIndex("ch-ZRH");
	static final Integer DefaultGeoZoom = 17;

	protected final SimpleProperty<String> name;
	protected final SimpleProperty<String> description;
	protected final SimpleProperty<String> buildingNr;
	protected final SimpleProperty<String> insuranceNr;
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

	protected final PartListProperty<ObjBuildingPartRating> ratingList;

	protected final ReferenceSetProperty<ObjContact> contactSet;

	protected ObjBuildingBase(ObjBuildingRepository repository, UpdatableRecord<?> objRecord,
			UpdatableRecord<?> buildingRecord) {

		super(repository, objRecord, buildingRecord);

		this.name = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.NAME);
		this.description = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.DESCRIPTION);
		this.buildingNr = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.BUILDING_NR);
		this.insuranceNr = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.INSURANCE_NR);
		this.plotNr = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.PLOT_NR);
		this.nationalBuildingId = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.NATIONAL_BUILDING_ID);
		this.historicPreservation = this.addEnumProperty(this.extnDbRecord(), ObjBuildingFields.HISTORIC_PRESERVERATION_ID,
				CodeHistoricPreservationEnum.class);

		this.street = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.STREET);
		this.zip = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.ZIP);
		this.city = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.CITY);
		this.country = this.addEnumProperty(this.extnDbRecord(), ObjBuildingFields.COUNTRY_ID, CodeCountryEnum.class);
		this.currency = this.addEnumProperty(this.extnDbRecord(), ObjBuildingFields.CURRENCY_ID, CodeCurrencyEnum.class);

		this.geoAddress = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.GEO_ADDRESS);
		this.geoCoordinates = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.GEO_COORDINATES);
		this.geoZoom = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.GEO_ZOOM);

		this.coverFoto = this.addReferenceProperty(this.extnDbRecord(), ObjBuildingFields.COVER_FOTO_ID, ObjDocument.class);

		this.volume = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.VOLUME);
		this.areaGross = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.AREA_GROSS);
		this.areaNet = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.AREA_NET);
		this.nrOfFloorsAboveGround = this.addSimpleProperty(this.extnDbRecord(),
				ObjBuildingFields.NR_OF_FLOORS_ABOVE_GROUND);
		this.nrOfFloorsBelowGround = this.addSimpleProperty(this.extnDbRecord(),
				ObjBuildingFields.NR_OF_FLOORS_BELOW_GROUND);

		this.buildingType = this.addEnumProperty(this.extnDbRecord(), ObjBuildingFields.BUILDING_TYPE_ID,
				CodeBuildingTypeEnum.class);
		this.buildingSubType = this.addEnumProperty(this.extnDbRecord(), ObjBuildingFields.BUILDING_SUB_TYPE_ID,
				CodeBuildingSubTypeEnum.class);
		this.buildingYear = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.BUILDING_YEAR);

		this.insuredValue = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.INSURED_VALUE);
		this.insuredValueYear = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.INSURED_VALUE_YEAR);
		this.notInsuredValue = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.NOT_INSURED_VALUE);
		this.notInsuredValueYear = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.NOT_INSURED_VALUE_YEAR);
		this.thirdPartyValue = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.THIRD_PARTY_VALUE);
		this.thirdPartyValueYear = this.addSimpleProperty(this.extnDbRecord(), ObjBuildingFields.THIRD_PARTY_VALUE_YEAR);

		this.ratingList = this.addPartListProperty(this.getRepository().getRatingListType());

		this.contactSet = this.addReferenceSetProperty(this.getRepository().getContactSetType(), ObjContact.class);
	}

	@Override
	public ObjBuildingRepository getRepository() {
		return (ObjBuildingRepository) super.getRepository();
	}

	@Override
	public void doAfterCreate() {
		super.doAfterCreate();
		this.addCoverFoto();
	}

	@Override
	public void doAssignParts() {
		super.doAssignParts();
		ObjBuildingPartRatingRepository ratingRepo = this.getRepository().getRatingRepository();
		this.ratingList.loadParts(ratingRepo.getParts(this, this.getRepository().getRatingListType()));
		ObjPartItemRepository itemRepo = this.getRepository().getItemRepository();
		this.contactSet.loadReferences(itemRepo.getParts(this, this.getRepository().getContactSetType()));
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getCoverFotoId() == null) {
			this.addCoverFoto();
		}
	}

	@Override
	public void doCalcSearch() {
		this.addSearchToken(this.getZip());
		this.addSearchToken(this.getBuildingNr());
		this.addSearchToken(this.getInsuranceNr());
		this.addSearchToken(this.getPlotNr());
		this.addSearchToken(this.getNationalBuildingId());
		this.addSearchText(this.getName());
		this.addSearchText(this.getStreet());
		this.addSearchText(this.getCity());
		if (this.getBuildingType() != null) {
			this.addSearchText(this.getBuildingType().getName());
		}
		if (this.getBuildingSubType() != null) {
			this.addSearchText(this.getBuildingSubType().getName());
		}
		if (this.getCurrentRating() != null) {
			this.addSearchText(this.getCurrentRating().getPartCatalog().getName());
		}
		this.addSearchText(this.getDescription());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <P extends Part<?>> P addPart(Property<P> property, CodePartListType partListType) {
		if (property == this.ratingList) {
			return (P) this.getRepository().getRatingRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	@Override
	public void setAccountId(Integer id) {
		super.account.setId(id);
		this.extnDbRecord().setValue(ObjBuildingFields.ACCOUNT_ID, id);
	}

	@Override
	public double getInflationRate() {
		BigDecimal inflationRate = this.getAccount().getInflationRate();
		inflationRate = inflationRate != null ? inflationRate : this.getTenant().getInflationRate();
		return inflationRate != null ? inflationRate.doubleValue() : 0;
	}

	@Override
	public double getBuildingValue(int year) {
		if (this.getInsuredValueYear() != null && this.getInsuredValue() != null) {
			return ObjBuildingBase.DefaultPriceIndex.priceAt(this.getInsuredValueYear(),
					1000.0 * this.getInsuredValue().doubleValue(), year,
					this.getInflationRate());
		}
		return 0;
	}

	@Override
	public ObjBuildingPartRating getCurrentRating() {
		for (int i = this.getRatingCount(); i > 0; i--) {
			ObjBuildingPartRating rating = this.getRating(i - 1);
			if (rating.getRatingStatus() == null || rating.getRatingStatus() != ObjBuildingBase.RatingDiscarded) {
				return rating;
			}
		}
		return null;
	}

	@Override
	public Integer getCondition(int year) {
		ObjBuildingPartRating rating = this.getCurrentRating();
		if (rating != null) {
			return rating.getCondition(year);
		}
		return null;
	}

	@Override
	public ObjBuildingPartRating addRating() {
		ObjBuildingPartRating oldRating = this.getCurrentRating();
		requireThis(oldRating == null || oldRating.getRatingStatus() == ObjBuildingBase.RatingDone, "rating done");
		ObjBuildingPartRating rating = this.ratingList.addPart();
		try {
			rating.getMeta().disableCalc();
			rating.setRatingStatus(ObjBuildingBase.RatingOpen);
			if (oldRating != null) {
				rating.setPartCatalog(oldRating.getPartCatalog());
				rating.setMaintenanceStrategy(oldRating.getMaintenanceStrategy());
			} else {
				rating.setMaintenanceStrategy(CodeBuildingMaintenanceStrategyEnum.getMaintenanceStrategy("N"));
			}
			rating.setRatingDate(this.getMeta().getRequestContext().getCurrentDate());
			rating.setRatingUser(this.getMeta().getRequestContext().getUser());
		} finally {
			rating.getMeta().enableCalc();
			rating.calcAll();
		}
		return rating;
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
		if (this.getCurrentRating() != null) {
			this.getCurrentRating().calcAll();
		}
		this.validateElements();
	}

	@Override
	protected void doCalcVolatile() {
		super.doCalcVolatile();
		this.calcCaption();
		if (this.getCurrentRating() != null) {
			this.getCurrentRating().calcVolatile();
		}
		this.validateElements();
	}

	private void calcCaption() {
		this.setCaption(this.getName());
	}

	private void validateElements() {
		if (this.getCoverFoto() == null || this.getCoverFoto().getContentType() == null) {
			this.addValidation(CodeValidationLevelEnum.WARNING, "Für den Druck muss ein Coverfoto hochgeladen werden");
		}
		if (this.getGeoCoordinates() == null || "".equals(this.getGeoCoordinates())) {
			this.addValidation(CodeValidationLevelEnum.WARNING, "Koordinaten der Immobilie fehlen");
		}
		if (this.getInsuredValue() == null || this.getInsuredValue().equals(BigDecimal.ZERO)) {
			this.addValidation(CodeValidationLevelEnum.ERROR, "Versicherungswert muss erfasst werden");
		}
		if (this.getInsuredValueYear() == null) {
			this.addValidation(CodeValidationLevelEnum.ERROR,
					"Jahr der Bestimmung des Versicherungswerts muss erfasst werden");
		}
		if (this.getCurrentRating() == null) {
			this.addValidation(CodeValidationLevelEnum.ERROR, "Es fehlt eine Zustandsbewertung");
		} else {
			if (this.getCurrentRating().getRatingDate() == null) {
				this.addValidation(CodeValidationLevelEnum.ERROR, "Datum der Zustandsbewertung muss erfasst werden");
			}
			if (this.getCurrentRating().getElementWeights() != 100) {
				this.addValidation(CodeValidationLevelEnum.ERROR,
						"Summe der Bauteilanteile muss 100% sein (ist " + this.getCurrentRating().getElementWeights() + "%)");
			}
			for (ObjBuildingPartElementRating element : this.getCurrentRating().getElementList()) {
				if (element.getWeight() != null && element.getWeight() != 0) {
					if (element.getCondition() == null || element.getCondition() == 0) {
						this.addValidation(CodeValidationLevelEnum.ERROR,
								"Zustand für Element [" + element.getBuildingPart().getName() + "] muss erfasst werden");
					} else if (element.getRatingYear() == null || element.getRatingYear() < 1800) {
						this.addValidation(CodeValidationLevelEnum.ERROR,
								"Jahr der Zustandsbewertung für Element [" + element.getBuildingPart().getName()
										+ "] muss erfasst werden");
					}
				}
			}
		}
	}

	private void addCoverFoto() {
		ObjDocumentRepository documentRepo = this.getRepository().getDocumentRepository();
		ObjDocument coverFoto = documentRepo.create(this.getTenantId());
		coverFoto.setName("CoverFoto");
		coverFoto.setContentKind(CodeContentKindEnum.getContentKind("foto"));
		coverFoto.setDocumentKind(CodeDocumentKindEnum.getDocumentKind("standalone"));
		coverFoto.setDocumentCategory(CodeDocumentCategoryEnum.getDocumentCategory("foto"));
		documentRepo.store(coverFoto);
		this.coverFoto.setId(coverFoto.getId());
	}

}
