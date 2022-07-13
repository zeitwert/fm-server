
import { EnumeratedField, FieldGroup, FieldRow, NumberField, OptionField, RadioButtonGroup, TextArea, TextField } from "@zeitwert/ui-forms";
import { BuildingElement, StrainOptions, StrengthOptions } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { converters, Field, IRepeatingFormIndexedAccessor, RepeatingForm } from "mstform";
import React from "react";

export const ElementRatingFormModel = new RepeatingForm({
	id: new Field(converters.string),
	buildingPart: new EnumeratedField({ source: "{{enumBaseUrl}}/building/codeBuildingPart" }),
	valuePart: new NumberField(),
	condition: new NumberField(),
	strain: new OptionField(converters.maybe(converters.integer), { options: StrainOptions }),
	strength: new OptionField(converters.maybe(converters.integer), { options: StrengthOptions }),
	description: new TextField(),
	conditionDescription: new TextField(),
	measureDescription: new TextField(),
	// materialDescriptions: new EnumeratedListField(),
	// conditionDescriptions: new EnumeratedListField(),
	// measureDescriptions: new EnumeratedListField(),
});

export interface ElementRatingFormProps {
	elementForm: IRepeatingFormIndexedAccessor<any, any, any>;
	element: BuildingElement;
	onClose: () => void;
}

@observer
export default class ElementRatingForm extends React.Component<ElementRatingFormProps> {

	componentDidMount() {
		document.addEventListener("keydown", this.escFunction, false);
	}

	componentWillUnmount() {
		document.removeEventListener("keydown", this.escFunction, false);
	}

	render() {
		const { element, elementForm } = this.props;
		return (
			<div style={{ position: "absolute", right: 0, top: 0, bottom: 0, width: "100%", backgroundColor: "white" }}>

				<div>
					<button className="slds-button slds-button_icon slds-button_icon slds-button_icon-small slds-float_right slds-popover__close" title="Close dialog" onClick={() => { this.props.onClose() }}>
						<svg className="slds-button__icon" aria-hidden="true">
							<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#close"></use>
						</svg>
						<span className="slds-assistive-text">Close</span>
					</button>
				</div>
				<div className="slds-popover__header">
					<header className="slds-media slds-media_center slds-m-bottom_small">
						<span className="slds-icon_container slds-icon-standard-account slds-media__figure">
							<svg className="slds-icon slds-icon_small" aria-hidden="true">
								<use xlinkHref="/assets/icons/standard-sprite/svg/symbols.svg#store"></use>
							</svg>
						</span>
						<div className="slds-media__body">
							<h2 className="slds-text-heading_medium slds-hyphenate" id="panel-heading-id">
								{element.buildingPart?.name}
							</h2>
						</div>
					</header>
				</div>

				<div style={{ position: "absolute", top: "61px", left: "0", bottom: "0", right: "0", overflowY: "auto" }}>
					<div className="slds-grid slds-wrap slds-m-top_none" style={{ padding: "1rem" }}>
						<div className="slds-col slds-size_1-of-1">

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
													<div id="zn" className={element.condition ? "zn-" + (5 * Math.round(element.condition! / 5)) : ""}>
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

							<hr></hr>
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

							<div className="slds-form" role="list">
								<FieldGroup>
									<div className="slds-m-top_small">
										<FieldRow>
											<TextArea
												label="Beschreibung / Bemerkungen"
												accessor={elementForm.field("description")}
												rows={4}
											/>
										</FieldRow>
									</div>
									<div className="slds-m-top_small">
										<FieldRow>
											<TextArea
												label="Zustandsbeschreibung"
												accessor={elementForm.field("conditionDescription")}
												rows={4}
											/>
										</FieldRow>
									</div>
									<div className="slds-m-top_small">
										<FieldRow>
											<TextArea
												label="Massnahmen"
												accessor={elementForm.field("measureDescription")}
												rows={4}
											/>
										</FieldRow>
									</div>
								</FieldGroup>
							</div>

						</div>
					</div>

				</div>
			</div>
		);
	}

	escFunction = (event: KeyboardEvent) => {
		if (event.key === "Escape") {
			this.props.onClose();
		}
	}

}
