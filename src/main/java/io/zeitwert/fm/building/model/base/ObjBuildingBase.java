
package io.zeitwert.fm.building.model.base;

import static io.zeitwert.ddd.util.Check.requireThis;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zeitwert.ddd.db.model.AggregateState;
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
import io.zeitwert.fm.account.model.enums.CodeCurrency;
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

	//@formatter:off
	protected final SimpleProperty<Integer> extnAccountId = this.addSimpleProperty("extnAccount", Integer.class);

	protected final SimpleProperty<String> name = this.addSimpleProperty("name", String.class);
	protected final SimpleProperty<String> description= this.addSimpleProperty("description", String.class);
	protected final SimpleProperty<String> buildingNr= this.addSimpleProperty("buildingNr", String.class);
	protected final SimpleProperty<String> insuranceNr= this.addSimpleProperty("insuranceNr", String.class);
	protected final SimpleProperty<String> plotNr= this.addSimpleProperty("plotNr", String.class);
	protected final SimpleProperty<String> nationalBuildingId= this.addSimpleProperty("nationalBuilding", String.class);
	protected final EnumProperty<CodeHistoricPreservation> historicPreservation= this.addEnumProperty("historicPreservation", CodeHistoricPreservation.class);

	protected final SimpleProperty<String> street= this.addSimpleProperty("street", String.class);
	protected final SimpleProperty<String> zip= this.addSimpleProperty("zip", String.class);
	protected final SimpleProperty<String> city= this.addSimpleProperty("city", String.class);
	protected final EnumProperty<CodeCountry> country= this.addEnumProperty("country", CodeCountry.class);

	protected final SimpleProperty<String> geoAddress= this.addSimpleProperty("geoAddress", String.class);
	protected final SimpleProperty<String> geoCoordinates= this.addSimpleProperty("geoCoordinates", String.class);
	protected final SimpleProperty<Integer> geoZoom= this.addSimpleProperty("geoZoom", Integer.class);

	protected final ReferenceProperty<ObjDocument> coverFoto= this.addReferenceProperty("coverFoto", ObjDocument.class);

	protected final EnumProperty<CodeCurrency> currency= this.addEnumProperty("currency", CodeCurrency.class);

	protected final SimpleProperty<BigDecimal> volume= this.addSimpleProperty("volume", BigDecimal.class);
	protected final SimpleProperty<BigDecimal> areaGross= this.addSimpleProperty("areaGross", BigDecimal.class);
	protected final SimpleProperty<BigDecimal> areaNet= this.addSimpleProperty("areaNet", BigDecimal.class);
	protected final SimpleProperty<Integer> nrOfFloorsAboveGround= this.addSimpleProperty("nrOfFloorsAboveGround", Integer.class);
	protected final SimpleProperty<Integer> nrOfFloorsBelowGround= this.addSimpleProperty("nrOfFloorsBelowGround", Integer.class);

	protected final EnumProperty<CodeBuildingType> buildingType= this.addEnumProperty("buildingType", CodeBuildingType.class);
	protected final EnumProperty<CodeBuildingSubType> buildingSubType= this.addEnumProperty("buildingSubType", CodeBuildingSubType.class);
	protected final SimpleProperty<Integer> buildingYear= this.addSimpleProperty("buildingYear", Integer.class);

	protected final SimpleProperty<BigDecimal> insuredValue= this.addSimpleProperty("insuredValue", BigDecimal.class);
	protected final SimpleProperty<Integer> insuredValueYear= this.addSimpleProperty("insuredValueYear", Integer.class);
	protected final SimpleProperty<BigDecimal> notInsuredValue= this.addSimpleProperty("notInsuredValue", BigDecimal.class);
	protected final SimpleProperty<Integer> notInsuredValueYear= this.addSimpleProperty("notInsuredValueYear", Integer.class);
	protected final SimpleProperty<BigDecimal> thirdPartyValue= this.addSimpleProperty("thirdPartyValue", BigDecimal.class);
	protected final SimpleProperty<Integer> thirdPartyValueYear= this.addSimpleProperty("thirdPartyValueYear", Integer.class);

	protected final PartListProperty<ObjBuildingPartRating> ratingList= this.addPartListProperty("ratingList", ObjBuildingPartRating.class);

	protected final ReferenceSetProperty<ObjContact> contactSet= this.addReferenceSetProperty("contactSet", ObjContact.class);
	//@formatter:on

	protected ObjBuildingBase(ObjBuildingRepository repository, AggregateState state) {
		super(repository, state);
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
		this.ratingList.loadParts(ratingRepo.getParts(this, ObjBuildingRepository.ratingListType()));
		ObjPartItemRepository itemRepo = this.getRepository().getItemRepository();
		this.contactSet.loadReferences(itemRepo.getParts(this, ObjBuildingRepository.contactSetType()));
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
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		if (property.equals(this.ratingList)) {
			return this.getRepository().getRatingRepository().create(this, partListType);
		} else if (property.equals(this.contactSet)) {
			return this.getRepository().getItemRepository().create(this, partListType);
		}
		return super.addPart(property, partListType);
	}

	@Override
	public void setAccountId(Integer id) {
		super.account.setId(id);
		this.extnAccountId.setValue(id);
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
