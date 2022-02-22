package fm.comunas.fm.building.service.api;

import fm.comunas.fm.building.model.ObjBuilding;
import fm.comunas.fm.building.model.enums.CodeBuildingPart;
import fm.comunas.fm.portfolio.model.ObjPortfolio;

import java.util.List;

public interface ProjectionService {

	/**
	 * Get the accumulated cost projection for a given portfolio
	 * 
	 * @param portfolio the portfolio
	 * @return cost projection
	 */
	ProjectionResult getProjection(ObjPortfolio portfolio);

	/**
	 * Get the accumulated cost projection for a given building
	 * 
	 * @param building the building
	 * @return cost projection
	 */
	ProjectionResult getProjection(ObjBuilding building);

	/**
	 * Get the cost projection for a given element
	 * 
	 * @param buildingPart the building element
	 * @return cost projection
	 */
	//@formatter:off
	List<ProjectionPeriod> getProjection(
		CodeBuildingPart buildingPart,
		double elementValue,
		int conditionYear,
		double condition,
		int startYear,
		int duration
	);
	//@formatter:on

	/**
	 * Get the first renovation period for a given element and condition
	 * 
	 * @param buildingPart the building element
	 * @return first renovation period
	 */
	//@formatter:off
	ProjectionPeriod getNextRestoration(
		CodeBuildingPart buildingPart,
		double elementValue,
		int conditionYear,
		double condition
	);
	//@formatter:on

	/**
	 * Get the timeValue for a given element and the relative age (in years)
	 * 
	 * @param buildingPart the building element
	 * @param relativeAge  the (relative) age of the element in years
	 * @return time value ([0 .. 1])
	 */
	double getTimeValue(CodeBuildingPart buildingPart, double relativeAge);

	/**
	 * Get the relative age of a building element given its time value
	 * 
	 * @param buildingPart the building element
	 * @param timeValue    the time value ([0 .. 1])
	 * @return relative age in years
	 */
	double getRelativeAge(CodeBuildingPart buildingPart, double timeValue);

	/**
	 * Get the expected lifetime of a building element given its time value
	 * 
	 * @param buildingPart the building element
	 * @param timeValue    the time value ([0 .. 1])
	 * @return relative age in years
	 */
	Integer getLifetime(CodeBuildingPart buildingPart, double timeValue);

}
