
import { Button, Card, Checkbox } from "@salesforce/design-system-react";
import { DateField, EnumeratedField, FieldGroup, FieldRow, Input, Select } from "@zeitwert/ui-forms";
import { BuildingModel, BuildingStore, requireThis, session } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds";
import { makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import { Form } from "mstform";
import React from "react";
import ElementListRatingForm from "./ElementListRatingForm";
import { ElementRatingFormModel } from "./ElementRatingForm";

const BuildingRatingFormModel = new Form(
	BuildingModel,
	{
		partCatalog: new EnumeratedField({ required: true, source: "building/codeBuildingPartCatalog" }),
		maintenanceStrategy: new EnumeratedField({ required: true, source: "building/codeBuildingMaintenanceStrategy" }),
		//
		ratingStatus: new EnumeratedField({ source: "building/codeBuildingRatingStatus" }),
		ratingDate: new DateField({ required: true }),
		ratingUser: new EnumeratedField({ source: "oe/objUser" }),
		//
		elements: ElementRatingFormModel
	}
);

export interface BuildingRatingFormProps {
	store: BuildingStore;
	onOpenElementRating: (element: any, elementForm: any) => void;
	onCloseElementRating: () => void;
}

@observer
export default class BuildingRatingForm extends React.Component<BuildingRatingFormProps> {

	formState: typeof BuildingRatingFormModel.FormStateType;

	@observable
	isLoading: boolean = false;

	@observable
	showAllElements: boolean = false;

	@observable
	currentElementId: string | undefined = undefined;

	constructor(props: BuildingRatingFormProps) {
		super(props);
		makeObservable(this);
		const building = props.store.item!;
		this.formState = BuildingRatingFormModel.state(
			building,
			{
				converterOptions: {
					decimalSeparator: ".",
					thousandSeparator: "'",
					renderThousands: true,
				},
				isReadOnly: (accessor) => {
					if (!props.store.isInTrx) {
						return true;
					} else {
						const building = props.store.building;
						if (!building?.ratingStatus || building.ratingStatus.id !== "open") {
							return true;
						}
					}
					return false;
				},
				isDisabled: (accessor) => {
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
			}
		);
	}

	render() {
		const building = this.props.store.item!;
		return (
			<div onClick={() => this.onCloseElementRating()} className="fm-rating-container">
				<div className="slds-grid slds-wrap slds-m-top_small" key={this.currentElementId}>
					<div className="slds-col slds-size_1-of-1">
						<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium" style={{ zIndex: 200 }}>
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Select label="Gebäudekategorie" fieldName="partCatalog" size={2} onChange={this.onSetPartCatalog} />
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
													<Checkbox labels={{ label: "Alle Bauteile" }} checked={this.showAllElements} onChange={() => this.showAllElements = !this.showAllElements}></Checkbox>
												</FieldGroup>
											</div>
											<Select label="Unterhaltsplanung" fieldName="maintenanceStrategy" size={2} />
											<Input label="Bewertungsdatum" fieldName="ratingDate" size={1} />
											<Select label="Bewertung durch" fieldName="ratingUser" size={2} />
											<div className="slds-size_1-of-12" />
											<Select label="Bewertungsstatus" fieldName="ratingStatus" size={2} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
				</div>
				{
					!!building.elements.length &&
					<>
						<hr style={{ marginTop: "1rem", marginBottom: 0 }} />
						<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium">
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-grid slds-m-top_small">
									<div className="slds-col slds-size_1-of-1">
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
											elementForms={this.formState.repeatingForm("elements")}
											showAllElements={this.showAllElements}
											currentElementId={this.currentElementId}
											onSelectElement={(id) => (this.currentElementId === id ? this.onCloseElementRating() : this.onOpenElementRating(id))}
										/>
									</div>
								</div>
							</div>
						</Card>
					</>
				}
			</div>
		);
	}

	private onEditPartCatalog = (e: React.ChangeEvent<HTMLSelectElement>) => {
		this.props.store.edit();
		const building = this.props.store.item!;
		building.setPartCatalog(undefined);
	}

	private onSetPartCatalog = async (e: React.ChangeEvent<HTMLSelectElement>) => {
		const building = this.props.store.item!;
		const partCatalog = toJS(this.formState.field("partCatalog").references.getById(e.target.value));
		await building.setPartCatalog(partCatalog);
	}

	private onOpenElementRating = (elementId: string) => {
		requireThis(!!elementId, "element id not null");
		this.currentElementId = elementId;
		let element: any;
		let elementForm: any;
		const building = this.props.store.item!;
		building.elements.forEach((el, index) => {
			if (el.id === this.currentElementId) {
				element = el;
				elementForm = this.formState.repeatingForm("elements").index(index);
			}
		});
		this.props.onOpenElementRating(element, elementForm);
	}

	private onCloseElementRating = () => {
		this.props.onCloseElementRating();
		this.currentElementId = undefined;
	}

}
