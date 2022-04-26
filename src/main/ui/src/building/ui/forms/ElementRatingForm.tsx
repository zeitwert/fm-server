
import { Card } from "@salesforce/design-system-react";
import { EnumeratedField, FieldGroup, FieldRow, IntField, NumberField, OptionField, RadioButtonGroup, Static, TextArea, TextField } from "@zeitwert/ui-forms";
import { BuildingElement, StrainOptions, StrengthOptions } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, IRepeatingFormIndexedAccessor, RepeatingForm } from "mstform";
import React from "react";
/*
const accounts = [
	{
		id: "1",
		label: "Ziegel",
		subTitle: "Dachhaut",
		type: "product",
	},
	{
		id: "2",
		label: "Faserzementplatten",
		subTitle: "Dachhaut",
		type: "product",
	},
	{
		id: "3",
		label: "Metalldeckung/Blech",
		subTitle: "Dachhaut",
		type: "product",
	},
	{
		id: "4",
		label: "Naturschiefer",
		subTitle: "Dachhaut",
		type: "product",
	},
	{
		id: "5",
		label: "Holzfaserplatten-Unterdach",
		subTitle: "Unterkonstruktion",
		type: "product",
	},
	{
		id: "6",
		label: "Schindel-Unterdach",
		subTitle: "Unterkonstruktion",
		type: "product",
	},
	{
		id: "7",
		label: "Wärmedämmung",
		subTitle: "Unterkonstruktion",
		type: "product",
	},
	{
		id: "8",
		label: "Metallblech Spenglerarbeiten",
		subTitle: "Spenglerarbeiten",
		type: "product"
	},
	{
		id: "9",
		label: "Kunststoff Spenglerarbeiten",
		subTitle: "Spenglerarbeiten",
		type: "product"
	},
	{
		id: "10",
		label: "Dachflächenfenster",
		subTitle: "Auf- und Einbauten",
		type: "product"
	},
	{
		id: "11",
		label: "Dachlukarne / -Gaube",
		subTitle: "Auf- und Einbauten",
		type: "product"
	},
	{
		id: "12",
		label: "Solaranlage",
		subTitle: "Auf- und Einbauten",
		type: "product"
	},
	{
		id: "13",
		label: "Photovoltaikanlage",
		subTitle: "Auf- und Einbauten",
		type: "product"
	},
];

const accountsWithIcon = accounts.map((elem) => ({
	...elem,
	...{
		icon: (
			<Icon
				assistiveText={{ label: "product" }}
				category="standard"
				name={elem.type}
			/>
		),
	},
}));
*/
export const ElementRatingFormModel = new RepeatingForm({
	id: new Field(converters.string),
	buildingPart: new EnumeratedField({ source: "{{enumBaseUrl}}/building/codeBuildingPart" }),
	valuePart: new NumberField(),
	condition: new NumberField(),
	conditionYear: new IntField(),
	strain: new OptionField(converters.maybe(converters.integer), { options: StrainOptions }),
	strength: new OptionField(converters.maybe(converters.integer), { options: StrengthOptions }),
	description: new TextField(),
	// materialDescriptions: new EnumeratedListField(),
	// conditionDescriptions: new EnumeratedListField(),
	// measureDescriptions: new EnumeratedListField(),
});

export interface ElementRatingFormProps {
	isInTrx: boolean;
	elementForm: IRepeatingFormIndexedAccessor<any, any, any>;
	element: BuildingElement;
}

@observer
export default class ElementRatingForm extends React.Component<ElementRatingFormProps> {

	// @observable
	// private inputValue: string | undefined = undefined;

	// @observable
	// private selection: any[] = [];

	// constructor(props: ElementRatingFormProps) {
	// 	super(props);
	// 	makeObservable(this);
	// }

	render() {
		const { /*isInTrx,*/ element, elementForm } = this.props;
		return (
			<div>
				<div>
					<div className="slds-grid slds-wrap slds-m-top_small">
						<div className="slds-col slds-size_1-of-1">
							<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium">
								<div className="slds-card__body slds-card__body_inner">
									<div className="slds-form" role="list">
										<FieldGroup>
											<FieldRow>
												<Static label="&nbsp;" value="&nbsp;" size={9} readOnlyLook="plain" />
											</FieldRow>
										</FieldGroup>
									</div>
								</div>
							</Card>
						</div>
					</div>
				</div>
				<hr style={{ marginTop: "1rem", marginBottom: 0 }} />
				<div style={{ position: "absolute", right: 0, top: "114px", bottom: 0, width: "98%", backgroundColor: "white", marginLeft: "20px", borderLeft: "1px solid #dddbda" }}>
					<div className="slds-grid slds-wrap slds-m-top_none">
						<div className="slds-col slds-size_1-of-1">
							<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium">
								<div className="slds-form" role="list">
									<FieldGroup legend="Baulicher Zustand">
										<FieldRow>
											<div style={{ position: "relative", fontWeight: "bolder", fontSize: "12px", color: "#909090", minWidth: "94%", minHeight: "160px", marginTop: "10px" }}>
												<div id="d">
													<div id="h">
														<div id="h1">Zustand</div>
														<div id="h4">Z/N 100</div>
														<div id="h5">Jahre</div>
													</div>
													<div id="d1">
														<div id="d11" className="slds-align_absolute-center">Schadhaft</div>
														<div id="d12" className="slds-align_absolute-center">Stark</div>
														<div id="d13" className="slds-align_absolute-center">Mittel</div>
														<div id="d14" className="slds-align_absolute-center">Leicht</div>
													</div>
													<div id="d2">
														<div id="d21">Intakt</div>
														<div id="d22"><div id="d22t"><span>Gebraucht</span></div></div>
														<div id="d23"><div id="d23t"><span>Neu</span></div></div>
													</div>
													<div id="d3">
														<div id="d31"></div>
														<div id="d32"></div>
														<div id="d33"></div>
														<div id="d34"></div>
														<div id="zn" className={element.condition ? "zn-" + (5 * Math.round(element.condition / 5)) : ""}>
														</div>
													</div>
													<div id="d4">
														<div id="t20" className="slds-align_absolute-center">20</div>
														<div id="t50" className="slds-align_absolute-center">50</div>
														<div id="t70" className="slds-align_absolute-center">70</div>
														<div id="t85" className="slds-align_absolute-center">85</div>
														<div id="t95" className="slds-align_absolute-center">95</div>
														<div id="t100" className="slds-align_absolute-center">100</div>
													</div>
													<div id="d5">
														<div id="t20" className="slds-align_absolute-center">{element.lifeTime20}</div>
														<div id="t50" className="slds-align_absolute-center">{element.lifeTime50}</div>
														<div id="t70" className="slds-align_absolute-center">{element.lifeTime70}</div>
														<div id="t85" className="slds-align_absolute-center">{element.lifeTime85}</div>
														<div id="t95" className="slds-align_absolute-center">{element.lifeTime95}</div>
														<div id="t100" className="slds-align_absolute-center">{element.lifeTime100}</div>
													</div>
												</div>
											</div>
										</FieldRow>
									</FieldGroup>
								</div>
							</Card>
						</div>
					</div>
					<div className="slds-grid slds-wrap slds-m-top_none">
						<div className="slds-col slds-size_1-of-1">
							<hr></hr>
							<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium">
								<div className="slds-form" role="list">
									<FieldGroup>
										<FieldRow>
											<RadioButtonGroup
												label="Widerstandsfähigkeit"
												accessor={elementForm.field("strength")}
												size={6}
											/>
											<RadioButtonGroup
												label="Belastung"
												accessor={elementForm.field("strain")}
												size={6}
											/>
										</FieldRow>
									</FieldGroup>
								</div>
							</Card>
							<Card hasNoHeader heading="" bodyClassName="slds-m-around_medium">
								<div className="slds-form" role="list">
									{
								/*
									<FieldGroup>
										<FieldRow>
											<div className="slds-size_1-of-1">
												<Combobox
													id="combobox-base-predefined-options"
													disabled={!isInTrx}
													style={{ zIndex: 200 }}
													events={{
														onChange: (event: any, c: any) => {
															const value = c.value;
															this.inputValue = value;
														},
														onRequestRemoveSelectedOption: (event: any, data: any) => {
															this.inputValue = "";
															this.selection = data.selection;
														},
														onSelect: (event: any, data: any) => {
															this.inputValue = "";
															this.selection = data.selection;
														},
													}}
													labels={{
														label: "Technische Beschreibung",
														placeholder: "",
													}}
													multiple
													options={
														comboboxFilterAndLimit({
															inputValue: this.inputValue,
															options: accountsWithIcon,
															selection: this.selection,
														})
													}
													predefinedOptionsOnly
													selection={this.selection}
													value={this.inputValue}
												/>
											</div>
										</FieldRow>
									</FieldGroup>
							*/}
									<FieldGroup>
										<FieldRow>
											<div className="slds-size_1-of-1">
												<TextArea
													label="Beschreibung / Bemerkungen"
													accessor={elementForm.field("description")}
												/>
											</div>
										</FieldRow>
									</FieldGroup>
								</div>
							</Card>
							<hr></hr>
						</div>
					</div>
				</div >
			</div >
		);
	}

}
