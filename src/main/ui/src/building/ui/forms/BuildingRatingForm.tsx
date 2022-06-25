
import { Button, Card, Checkbox } from "@salesforce/design-system-react";
import { DateField, EnumeratedField, FieldGroup, FieldRow, Input, Select, TextField } from "@zeitwert/ui-forms";
import { BuildingModel, BuildingStore } from "@zeitwert/ui-model";
import { Col, Grid } from "@zeitwert/ui-slds/common/Grid";
import { makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import { converters, Field, Form } from "mstform";
import React from "react";
import ElementListRatingForm from "./ElementListRatingForm";
import ElementRatingForm, { ElementRatingFormModel } from "./ElementRatingForm";

const BuildingRatingFormModel = new Form(
	BuildingModel,
	{
		id: new Field(converters.string),
		name: new TextField({ required: true }),
		//
		currency: new EnumeratedField({ source: "{{enumBaseUrl}}/account/codeCurrency" }),
		//
		partCatalog: new EnumeratedField({ source: "{{enumBaseUrl}}/building/codeBuildingPartCatalog" }),
		maintenanceStrategy: new EnumeratedField({ source: "{{enumBaseUrl}}/building/codeBuildingMaintenanceStrategy" }),
		//
		ratingStatus: new EnumeratedField({ source: "{{enumBaseUrl}}/building/codeBuildingRatingStatus" }),
		ratingDate: new DateField(),
		ratingUser: new EnumeratedField({ source: "{{enumBaseUrl}}/oe/objUser" }),
		//
		elements: ElementRatingFormModel
	}
);

export interface BuildingRatingFormProps {
	store: BuildingStore;
}

@observer
export default class BuildingRatingForm extends React.Component<BuildingRatingFormProps> {

	formState: typeof BuildingRatingFormModel.FormStateType;

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
					}
					return false;
				},
				isDisabled: (accessor) => {
					if (["partCatalog"].indexOf(accessor.fieldref) >= 0) {
						return !!building.partCatalog;
					} else if (["elements[].condition", "elements[].conditionYear"].indexOf(accessor.fieldref) >= 0) {
						return !(accessor.parent as any)?.fieldAccessors.get("valuePart").value;
					}
					return false;
				},
			}
		);
	}

	render() {
		const building = this.props.store.item!;
		const elementForms = this.formState.repeatingForm("elements");
		const isInTrx = this.props.store.isInTrx;
		return (
			<div onClick={() => this.currentElementId = undefined} className="fm-rating-container">
				<div className="slds-grid slds-wrap slds-m-top_small" key={this.currentElementId}>
					<div className="slds-col slds-size_1-of-1">
						<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium" style={{ zIndex: 200 }}>
							<div className="slds-card__body slds-card__body_inner">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<Select label="Gebäudekategorie" accessor={this.formState.field("partCatalog")} size={2} onChange={this.onSetPartCatalog} />
											<div className="slds-size_1-of-12">
												<FieldGroup label="&nbsp;">
													{
														building.partCatalog &&
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
											<Select label="Unterhaltsplanung" accessor={this.formState.field("maintenanceStrategy")} size={2} />
											<Input label="Bewertungsdatum" accessor={this.formState.field("ratingDate")} size={1} />
											<Select label="Bewertung durch" accessor={this.formState.field("ratingUser")} size={2} />
											<div className="slds-size_1-of-12" />
											<Select label="Bewertungsstatus" accessor={this.formState.field("ratingStatus")} size={2} />
										</FieldRow>
									</FieldGroup>
								</div>
							</div>
						</Card>
					</div>
				</div>
				<hr style={{ marginTop: "1rem", marginBottom: 0 }} />
				{
					building.elements.map((element, index) => {
						if (element.id === this.currentElementId) {
							const elementForm = elementForms.index(index);
							return (
								<div
									className={isInTrx ? "fa-isInTrx" : ""}
									style={{ position: "absolute", right: 0, top: isInTrx ? "4px" : "1px", bottom: 0, width: "calc(25% + 0.75rem)", backgroundColor: "white", zIndex: 100 }}
									onClick={(e: React.MouseEvent<HTMLDivElement>) => e.stopPropagation()}
									key={"elementRating-" + index}
								>
									<ElementRatingForm
										isInTrx={this.props.store.isInTrx}
										element={element}
										elementForm={elementForm}
									/>
								</div>
							);
						} else {
							return <div key={"elementRating-" + index} />;
						}
					})
				}
				<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium">
					<div className="slds-card__body slds-card__body_inner">
						<div className="slds-grid slds-m-top_small">
							<div className="slds-col slds-size_1-of-1">
								<FieldGroup>
									<Grid isVertical={false}>
										<Col className="slds-size_6-of-12">&nbsp;</Col>
										<Col className="slds-size_3-of-12 slds-align_absolute-center">Instandsetzungszeitpunkt</Col>
										<Col className="slds-size_3-of-12">&nbsp;</Col>
									</Grid>
									<Grid isVertical={false}>
										<Col className="slds-size_6-of-12">&nbsp;</Col>
										<Col className="slds-size_1-of-12 slds-align_absolute-center">0 - 1 J.</Col>
										<Col className="slds-size_1-of-12 slds-align_absolute-center">2 - 5 J.</Col>
										<Col className="slds-size_1-of-12 slds-align_absolute-center">&gt; 5J.</Col>
										<Col className="slds-size_3-of-12">&nbsp;</Col>
									</Grid>
									<Grid isVertical={false} className="slds-form-element__row slds-text-title_bold">
										<Col className="slds-size_2-of-12 slds-form-element">Bauteil</Col>
										<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
											<div className="slds-float_right">Anteile</div>
										</Col>
										<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
											<div className="slds-float_right">Jahr</div>
										</Col>
										<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
											<div className="slds-float_right">Z/N 100</div>
										</Col>
										{
											/*
												<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
													<div className="slds-float_right">IS Zpt</div>
												</Col>
											*/
										}
										<Col className="slds-size_1-of-12 slds-form-element slds-clearfix">
											<div className="slds-float_right">IS Kosten</div>
										</Col>
										<Col className="slds-size_1-of-12 slds-form-element slds-align_absolute-center">Kurzfristig</Col>
										<Col className="slds-size_1-of-12 slds-form-element slds-align_absolute-center">Mittelfristig</Col>
										<Col className="slds-size_1-of-12 slds-form-element slds-align_absolute-center">Langfristig</Col>
										<Col className="slds-size_3-of-12 slds-form-element">Beschreibung / Zustand / Massnahmen</Col>
									</Grid>
								</FieldGroup>
								<ElementListRatingForm
									building={building}
									elementForms={elementForms}
									showAllElements={this.showAllElements}
									currentElementId={this.currentElementId}
									onSelectElement={(id) => this.currentElementId = (this.currentElementId === id ? undefined : id)}
								/>
							</div>
						</div>
					</div>
				</Card>
			</div >
		);
	}

	private onEditPartCatalog = (e: React.ChangeEvent<HTMLSelectElement>): void => {
		this.props.store.edit();
		const building = this.props.store.item!;
		building.setPartCatalog(undefined);
	}

	private onSetPartCatalog = (e: React.ChangeEvent<HTMLSelectElement>): void => {
		const building = this.props.store.item!;
		const partCatalog = toJS(this.formState.field("partCatalog").references.getById(e.target.value));
		building.setPartCatalog(partCatalog);
	}

}
