package fm.comunas.fm.building.model;

import java.math.BigDecimal;
import java.util.List;

import fm.comunas.ddd.common.model.enums.CodeCountry;
import fm.comunas.ddd.common.model.enums.CodeCurrency;
import fm.comunas.fm.account.model.ObjAccount;
import fm.comunas.fm.building.model.enums.CodeBuildingMaintenanceStrategy;
import fm.comunas.fm.building.model.enums.CodeBuildingPart;
import fm.comunas.fm.building.model.enums.CodeBuildingPartCatalog;
import fm.comunas.fm.building.model.enums.CodeBuildingSubType;
import fm.comunas.fm.building.model.enums.CodeBuildingType;
import fm.comunas.fm.building.model.enums.CodeHistoricPreservation;
import fm.comunas.fm.obj.model.FMObj;

/**
 * todo
 * - geschosse oberirdisch, unterirdisch
 */
public interface ObjBuilding extends FMObj {

	@Override
	ObjBuildingRepository getRepository();

	Integer getAccountId();

	void setAccountId(Integer id);

	ObjAccount getAccount();

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getBuildingNr();

	void setBuildingNr(String buildingNr);

	String getBuildingInsuranceNr();

	void setBuildingInsuranceNr(String buildingInsuranceNr);

	String getPlotNr();

	void setPlotNr(String plotNr);

	String getNationalBuildingId();

	void setNationalBuildingId(String nationalBuildingId);

	CodeHistoricPreservation getHistoricPreservation();

	void setHistoricPreservation(CodeHistoricPreservation historicPreservation);

	String getStreet();

	void setStreet(String street);

	String getZip();

	void setZip(String zip);

	String getCity();

	void setCity(String city);

	CodeCountry getCountry();

	void setCountry(CodeCountry country);

	CodeCurrency getCurrency();

	void setCurrency(CodeCurrency currency);

	BigDecimal getVolume();

	void setVolume(BigDecimal volume);

	BigDecimal getAreaGross();

	void setAreaGross(BigDecimal area);

	BigDecimal getAreaNet();

	void setAreaNet(BigDecimal area);

	Integer getNrOfFloorsAboveGround();

	void setNrOfFloorsAboveGround(Integer nrOfFloors);

	Integer getNrOfFloorsBelowGround();

	void setNrOfFloorsBelowGround(Integer nrOfFloors);

	CodeBuildingType getBuildingType();

	void setBuildingType(CodeBuildingType buildingType);

	CodeBuildingSubType getBuildingSubType();

	void setBuildingSubType(CodeBuildingSubType buildingSubType);

	Integer getBuildingYear();

	void setBuildingYear(Integer buildingYear);

	double getBuildingValue(int year);

	BigDecimal getInsuredValue();

	void setInsuredValue(BigDecimal value);

	Integer getInsuredValueYear();

	void setInsuredValueYear(Integer year);

	BigDecimal getNotInsuredValue();

	void setNotInsuredValue(BigDecimal value);

	Integer getNotInsuredValueYear();

	void setNotInsuredValueYear(Integer year);

	BigDecimal getThirdPartyValue();

	void setThirdPartyValue(BigDecimal value);

	Integer getThirdPartyValueYear();

	void setThirdPartyValueYear(Integer year);

	CodeBuildingPartCatalog getBuildingPartCatalog();

	void setBuildingPartCatalog(CodeBuildingPartCatalog buildingPartCatalog);

	CodeBuildingMaintenanceStrategy getBuildingMaintenanceStrategy();

	void setBuildingMaintenanceStrategy(CodeBuildingMaintenanceStrategy strategy);

	Integer getElementContributions();

	Integer getElementCount();

	ObjBuildingPartElement getElement(Integer seqNr);

	List<ObjBuildingPartElement> getElementList();

	ObjBuildingPartElement getElementById(Integer elementId);

	ObjBuildingPartElement getElement(CodeBuildingPart buildingPart);

	void clearElementList();

	ObjBuildingPartElement addElement(CodeBuildingPart buildingPart);

	void removeElement(Integer elementId);

}
