
package io.zeitwert.fm.building.model.base;

import static io.dddrive.util.Invariant.requireThis;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zeitwert.fm.oe.model.enums.CodeCountry;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.PartListProperty;
import io.dddrive.property.model.ReferenceProperty;
import io.dddrive.property.model.ReferenceSetProperty;
import io.dddrive.property.model.SimpleProperty;
import io.dddrive.validation.model.enums.CodeValidationLevelEnum;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.account.service.api.ObjAccountCache;
import io.zeitwert.fm.building.model.ObjBuilding;
import io.zeitwert.fm.building.model.ObjBuildingPartElementRating;
import io.zeitwert.fm.building.model.ObjBuildingPartRating;
import io.zeitwert.fm.building.model.ObjBuildingRepository;
import io.zeitwert.fm.building.model.enums.*;
import io.zeitwert.fm.collaboration.model.impl.AggregateWithNotesMixin;
import io.zeitwert.fm.contact.model.ObjContact;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.dms.model.ObjDocumentRepository;
import io.zeitwert.fm.dms.model.enums.CodeContentKindEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentCategoryEnum;
import io.zeitwert.fm.dms.model.enums.CodeDocumentKindEnum;
import io.zeitwert.fm.obj.model.base.FMObjBase;
import io.zeitwert.fm.oe.model.ObjTenantFM;
import io.zeitwert.fm.oe.model.ObjUserFM;
import io.zeitwert.fm.task.model.impl.AggregateWithTasksMixin;

public abstract class ObjBuildingBase extends FMObjBase
		implements ObjBuilding, AggregateWithNotesMixin, AggregateWithTasksMixin {

	protected static final Logger logger = LoggerFactory.getLogger(ObjBuildingBase.class);

	static final CodeBuildingRatingStatus RatingOpen = CodeBuildingRatingStatusEnum.getRatingStatus("open");
	static final CodeBuildingRatingStatus RatingDone = CodeBuildingRatingStatusEnum.getRatingStatus("done");
	static final CodeBuildingRatingStatus RatingDiscarded = CodeBuildingRatingStatusEnum.getRatingStatus("discard");
	static final CodeBuildingPriceIndex DefaultPriceIndex = CodeBuildingPriceIndexEnum.getBuildingPriceIndex("ch-ZRH");
	static final Integer DefaultGeoZoom = 17;

	//@formatter:off
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

	protected ObjBuildingBase(ObjBuildingRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjBuilding aggregate() {
		return this;
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
	public final ObjAccount getAccount() {
		return this.getAppContext().getBean(ObjAccountCache.class).get(this.getAccountId());
	}

	@Override
	public void doBeforeStore() {
		super.doBeforeStore();
		if (this.getCoverFotoId() == null) {
			this.addCoverFoto();
		}
	}

	@Override
	public double getInflationRate() {
		BigDecimal inflationRate = this.getAccount().getInflationRate();
		inflationRate = inflationRate != null ? inflationRate : ((ObjTenantFM) this.getTenant()).getInflationRate();
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
			rating.setRatingUser((ObjUserFM) this.getMeta().getRequestContext().getUser());
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
			this.addSearchText(this.getBuildingType() != null ? this.getBuildingType().getName() : null);
		}
		if (this.getBuildingSubType() != null) {
			this.addSearchText(this.getBuildingSubType() != null ? this.getBuildingSubType().getName() : null);
		}
		if (this.getCurrentRating() != null) {
			this.addSearchText(this.getCurrentRating() != null
					? this.getCurrentRating().getPartCatalog() != null
							? this.getCurrentRating().getPartCatalog().getName()
							: null
					: null);
		}
		this.addSearchText(this.getDescription());
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
