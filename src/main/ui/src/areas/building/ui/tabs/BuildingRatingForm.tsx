
import { Button, Card, Checkbox } from "@salesforce/design-system-react";
import { AccessorContext, FieldGroup, FieldRow, Input, Select, SldsForm } from "@zeitwert/ui-forms";
import { BuildingElement, BuildingModel, BuildingModelType, BuildingStore, Enumerated, requireThis, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { Form, FormDefinition, FormStateOptions, IFormAccessor } from "mstform";
import React from "react";
import BuildingFormDef from "../forms/BuildingFormDef";
import ElementListRatingForm from "./ElementListRatingForm";
import { ElementAccessor } from "./ElementRatingForm";

export interface BuildingRatingFormProps {
	store: BuildingStore;
	currentElementId: string | undefined;
	onOpenElementRating: (element: BuildingElement, elementAccessor: ElementAccessor) => void;
	onCloseElementRating: () => void;
}

const BuildingForm = new Form(
	BuildingModel,
	BuildingFormDef
);

export type FormAccessor = IFormAccessor<FormDefinition<BuildingModelType>, any, BuildingModelType>;

@observer
export default class BuildingRatingForm extends React.Component<BuildingRatingFormProps> {

	formStateOptions: FormStateOptions<BuildingModelType> = {
		isReadOnly: (accessor) => {
			if (!this.props.store.isInTrx) {
				return true;
			} else {
				const building = this.props.store.building;
				if (!building?.ratingStatus || building.ratingStatus.id !== "open") {
					return true;
				}
			}
			return false;
		},
		isDisabled: (accessor) => {
			const building = this.props.store.building!;
			if (["partCatalog"].indexOf(accessor.fieldref) >= 0) {
				return !!building.partCatalog;
			} else if (!building.ratingDate || building.ratingDate.getFullYear() < 1000) {
				return !!accessor.fieldref && (accessor.fieldref !== "ratingDate");
			} else if (["maintenanceStrategy", "ratingStatus"].indexOf(accessor.fieldref) >= 0) {
				return true;
			} else if (["elements[].condition", "elements[].conditionYear"].indexOf(accessor.fieldref) >= 0) {
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
		const building = this.props.store.item!;
		const currentElementId = this.props.currentElementId;
		const showAllElements = this.showAllElements;
		return (
			<div onClick={() => this.onCloseElementRating()} className="fm-rating-container">
				<SldsForm formModel={BuildingForm} formStateOptions={this.formStateOptions} item={this.props.store.building!}>
					{
						({ formAccessor }: AccessorContext<BuildingModelType>) =>
						(
							<>
								<Grid className="slds-wrap slds-m-top_small">
									<Col cols={1} totalCols={1}>
										<Card hasNoHeader bodyClassName="slds-card__body_inner" style={{ zIndex: 200 }}>
											<FieldGroup>
												<FieldRow>
													<Select label="Gebäudekategorie" fieldName="partCatalog" size={2} onChange={(e) => this.onSetPartCatalog(e, formAccessor)} />
													<div className="slds-size_1-of-12">
														<FieldGroup label="&nbsp;">
															{
																!this.props.store.inTrx && building.partCatalog && building?.ratingStatus?.id === "open" &&
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
													<Input label="Bewertungsdatum" fieldName="ratingDate" size={1} />
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
									!!building.elements.length &&
									<Card hasNoHeader heading="" bodyClassName="slds-card__body_inner">
										<Grid className="slds-wrap slds-m-top_small">
											<Col cols={1} totalCols={1}>
												<FieldGroup>
													<Grid isVertical={false}>
														<Col className="slds-size_4-of-12"><b>Bewertung {"#" + (building.ratingSeqNr! + 1)} {building.ratingDate ? " (per " + session.formatter.formatDate(building.ratingDate) + ")" : ""}</b></Col>
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
													elementForms={formAccessor.repeatingForm("elements")}
													showAllElements={showAllElements}
													currentElementId={currentElementId}
													onSelectElement={(id) => (this.props.currentElementId === id ? this.onCloseElementRating() : this.onOpenElementRating(id, formAccessor))}
												/>
											</Col>
										</Grid>
									</Card>
								}
							</>
						)
					}
				</SldsForm>
			</div>
		);
	}

	private onEditPartCatalog = (e: React.ChangeEvent<HTMLSelectElement>) => {
		this.props.store.edit();
		const building = this.props.store.item!;
		building.setPartCatalog(undefined);
	}

	private onSetPartCatalog = async (partCatalog: Enumerated | undefined, formAccessor: FormAccessor) => {
		await this.props.store.building!.setPartCatalog(partCatalog);
	}

	private onOpenElementRating = (elementId: string, formAccessor: FormAccessor) => {
		requireThis(!!elementId, "element id not null");
		const building = this.props.store.item!;
		building.elements.forEach((element, index) => {
			if (element.id === elementId) {
				this.props.onOpenElementRating(element, formAccessor.repeatingForm("elements").index(index));
			}
		});
	}

	private onCloseElementRating = () => {
		this.props.onCloseElementRating();
	}

}
