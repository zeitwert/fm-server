package io.zeitwert.fm.building.model;

import java.math.BigDecimal;
import java.util.List;

import io.zeitwert.fm.account.model.enums.CodeCountry;
import io.zeitwert.fm.account.model.enums.CodeCurrency;
import io.zeitwert.fm.account.model.ObjAccount;
import io.zeitwert.fm.building.model.enums.CodeBuildingSubType;
import io.zeitwert.fm.building.model.enums.CodeBuildingType;
import io.zeitwert.fm.building.model.enums.CodeHistoricPreservation;
import io.zeitwert.fm.dms.model.ObjDocument;
import io.zeitwert.fm.obj.model.FMObj;

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

	String getGeoAddress();

	void setGeoAddress(String geoAddress);

	String getGeoCoordinates();

	void setGeoCoordinates(String geoCoordinates);

	Integer getGeoZoom();

	void setGeoZoom(Integer geoZoom);

	Integer getCoverFotoId();

	void setCoverFotoId(Integer id);

	ObjDocument getCoverFoto();

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

	ObjBuildingPartRating getCurrentRating();

	Integer getRatingCount();

	ObjBuildingPartRating getRating(Integer seqNr);

	List<ObjBuildingPartRating> getRatingList();

	ObjBuildingPartRating getRatingById(Integer ratingId);

	ObjBuildingPartRating addRating();

	void removeRating(Integer ratingId);

	double getBuildingValue(int year);

	double getBuildingValue(int year, double inflationRate);

}
