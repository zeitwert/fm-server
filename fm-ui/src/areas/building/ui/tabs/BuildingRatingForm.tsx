
import { Button, Card, Checkbox } from "@salesforce/design-system-react";
import { AccessorContext, FieldGroup, FieldRow, Select, SldsForm, SldsSubForm } from "@zeitwert/ui-forms";
import { DatePicker } from "@zeitwert/ui-forms/ui/DatePicker";
import { Building, BuildingElement, BuildingModelType, Enumerated, requireThis, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { FormDefinition, FormStateOptions, IFormAccessor } from "mstform";
import React from "react";
import BuildingForm from "../forms/BuildingForm";
import ElementListRatingForm from "./ElementListRatingForm";
import { ElementAccessor } from "./ElementRatingForm";

export interface BuildingRatingFormProps {
	building: Building;
	doEdit: boolean;
	currentElementId: string | undefined;
	onEditPartCatalog: () => void;
	onOpenElementRating: (element: BuildingElement, elementAccessor: ElementAccessor) => void;
	onCloseElementRating: () => void;
}

export type FormAccessor = IFormAccessor<FormDefinition<BuildingModelType>, any, BuildingModelType>;

@observer
export default class BuildingRatingForm extends React.Component<BuildingRatingFormProps> {

	formStateOptions: FormStateOptions<BuildingModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.doEdit) {
				return true;
			} else {
				const building = this.props.building;
				if (!building.currentRating?.ratingStatus || building.currentRating.ratingStatus.id !== "open") {
					return true;
				}
			}
			return false;
		},
		isDisabled: (accessor) => {
			const building = this.props.building;
			// Check both with and without prefix for subform field refs
			const fieldref = accessor.fieldref;
			if (fieldref === "currentRating.partCatalog" || fieldref === "partCatalog") {
				return !!building.currentRating?.partCatalog;
			} else if (!building.currentRating?.ratingDate || building.currentRating.ratingDate.getFullYear() < 1000) {
				return !!fieldref && (fieldref !== "currentRating.ratingDate" && fieldref !== "ratingDate");
			} else if (["currentRating.maintenanceStrategy", "maintenanceStrategy", "currentRating.ratingStatus", "ratingStatus"].indexOf(fieldref) >= 0) {
				return true;
			} else if (["currentRating.elements[].condition", "elements[].condition", "currentRating.elements[].conditionYear", "elements[].conditionYear"].indexOf(fieldref) >= 0) {
				return !(accessor.parent as any)?.fieldAccessors.get("weight").value;
			}
			return false;
		},
	};

	@observable
	showAllElements: boolean = false;

	constructor(props: BuildingRatingFormProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const building = this.props.building;
		const currentElementId = this.props.currentElementId;
		const showAllElements = this.showAllElements;
		return (
			<div onClick={() => this.onCloseElementRating()} className="fm-rating-container">
				<SldsForm
					formModel={BuildingForm}
					formStateOptions={this.formStateOptions}
					item={building}
				>
					{
						({ formAccessor }: AccessorContext<BuildingModelType>) => {
						const currentRating = building.currentRating;
						const ratingAccessor = currentRating ? (formAccessor.subForm("currentRating") as any) : null;
						return (
							<>
								{currentRating && ratingAccessor && (
									<SldsSubForm formAccessor={ratingAccessor} item={currentRating}>
										<Grid className="slds-wrap slds-m-top_small">
											<Col cols={1} totalCols={1}>
												<Card hasNoHeader bodyClassName="slds-card__body_inner" style={{ zIndex: 200 }}>
													<FieldGroup>
														<FieldRow>
															<Select
																label="Gebäudekategorie"
																fieldName="partCatalog"
																size={2}
																onChange={(e) => this.onSetPartCatalog(e, formAccessor)}
															/>
															<div className="slds-size_1-of-12">

																<FieldGroup label="&nbsp;">
																	{
																		!this.props.doEdit && currentRating.partCatalog && currentRating.ratingStatus?.id === "open" &&
																		<Button
																			variant="icon"
																			iconCategory="utility"
																			iconName="edit"
																			iconSize="medium"
																			className="slds-m-top_x-small"
																			onClick={this.onEditPartCatalog}
																		/>
																	}
																</FieldGroup>
															</div>
															<div className="slds-size_1-of-12">
																<FieldGroup label="Optionen">
																	<Checkbox labels={{ label: "Alle Bauteile" }} checked={showAllElements} onChange={() => this.showAllElements = !this.showAllElements}></Checkbox>
																</FieldGroup>
															</div>
															<Select label="Unterhaltsplanung" fieldName="maintenanceStrategy" size={2} />
															<DatePicker label="Bewertungsdatum" fieldName="ratingDate" size={1} yearRangeMax={1} />
															<Select label="Bewertung durch" fieldName="ratingUser" size={2} />
															<div className="slds-size_1-of-12" />
															<Select label="Bewertungsstatus" fieldName="ratingStatus" size={2} />
														</FieldRow>
													</FieldGroup>
												</Card>
											</Col>
										</Grid>
										<hr style={{ marginTop: "1rem", marginBottom: 0 }} />
										{
											!!currentRating.elements.length &&
											<Card hasNoHeader heading="" bodyClassName="slds-card__body_inner">
												<Grid className="slds-wrap slds-m-top_small">
													<Col cols={1} totalCols={1}>
														<FieldGroup>
															<Grid isVertical={false}>
																<Col className="slds-size_4-of-12"><b>Bewertung {"#" + ((currentRating.seqNr ?? 0) + 1)} {currentRating.ratingDate ? " (per " + session.formatter.formatDate(currentRating.ratingDate) + ")" : ""}</b></Col>
																<Col className="slds-size_3-of-12 slds-align_absolute-center">Instandsetzungszeitpunkt</Col>
																<Col className="slds-size_5-of-12">&nbsp;</Col>
															</Grid>
															<Grid isVertical={false}>
																<Col className="slds-size_4-of-12">&nbsp;</Col>
																<Col className="slds-size_1-of-12 slds-align_absolute-center">0 - 1 J.</Col>
																<Col className="slds-size_1-of-12 slds-align_absolute-center">2 - 5 J.</Col>
																<Col className="slds-size_1-of-12 slds-align_absolute-center">&gt; 5J.</Col>
																<Col className="slds-size_5-of-12">&nbsp;</Col>
															</Grid>
															<Grid isVertical={false} className="slds-form-element__row slds-text-title_bold">
																<Col className="slds-size_2-of-12 slds-form-element">Bauteil</Col>
																<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
																	<div className="slds-float_right">Anteil</div>
																</Col>
																<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
																	<div className="slds-float_right">Z/N 100</div>
																</Col>
																<Col className="slds-size_1-of-12 slds-form-element slds-align_absolute-center">Kurzfristig</Col>
																<Col className="slds-size_1-of-12 slds-form-element slds-align_absolute-center">Mittelfristig</Col>
																<Col className="slds-size_1-of-12 slds-form-element slds-align_absolute-center">Langfristig</Col>
																<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
																	<div className="slds-float_right">IS Kosten</div>
																</Col>
																<Col className="slds-size_4-of-12 slds-form-element">Beschreibung / Zustand / Massnahmen</Col>
															</Grid>
														</FieldGroup>
														<ElementListRatingForm
															building={building}
															elementForms={ratingAccessor.repeatingForm("elements")}
															showAllElements={showAllElements}
															currentElementId={currentElementId}
															onSelectElement={(id) => (this.props.currentElementId === id ? this.onCloseElementRating() : this.onOpenElementRating(id, formAccessor))}
														/>
													</Col>
												</Grid>
											</Card>
										}
									</SldsSubForm>
								)}
							</>
						);
					}
					}
				</SldsForm>
			</div>
		);
	}

	private onEditPartCatalog = () => {
		this.props.onEditPartCatalog();
		this.props.building.setPartCatalog(undefined);
	}

	private onSetPartCatalog = async (partCatalog: Enumerated | undefined, formAccessor: FormAccessor) => {
		await this.props.building.setPartCatalog(partCatalog);
	}

	private onOpenElementRating = (elementId: string, formAccessor: FormAccessor) => {
		requireThis(!!elementId, "element id not null");
		const building = this.props.building;
		building.currentRating?.elements.forEach((element, index) => {
			if (element.id === elementId) {
				this.props.onOpenElementRating(element, (formAccessor.subForm("currentRating") as any).repeatingForm("elements").index(index));
			}
		});
	}

	private onCloseElementRating = () => {
		this.props.onCloseElementRating();
	}

}
