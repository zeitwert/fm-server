
import { FieldGroup, FieldRow, Input, SldsSubForm, Static } from "@zeitwert/ui-forms";
import { Building, BuildingElement, BuildingModelType } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { FormDefinition } from "mstform";
import { RepeatingFormAccess } from "mstform/dist/src/accessor";
import React from "react";
import { ElementAccessor } from "./ElementRatingForm";

export type ElementsAccessor = RepeatingFormAccess<FormDefinition<BuildingModelType>, "elements", BuildingModelType>;

export interface ElementListRatingFormProps {
	building: Building;
	elementForms: ElementsAccessor;
	showAllElements: boolean;
	currentElementId: string | undefined;
	onSelectElement: (id: string) => void;
}

@observer
export default class ElementListRatingForm extends React.Component<ElementListRatingFormProps> {

	render() {
		const { building, elementForms, showAllElements, currentElementId, onSelectElement } = this.props;
		const rowCount = showAllElements ? building.elements.length : building.elements.filter(e => e.weight).length;
		let rows: JSX.Element[] = [];
		let row = 0, index = 0;
		for (const element of building.elements) {
			const elementForm = elementForms.index(index);
			if (showAllElements || !!element.weight) {
				rows.push(
					<ElementRowRatingForm
						key={"part-" + element.id}
						row={row}
						rowCount={rowCount}
						element={element}
						elementAccessor={elementForm}
						currentElementId={currentElementId}
						onSelectElement={onSelectElement}
					/>
				);
				row++;
			} else {
				rows.push(<div key={"part-" + index}></div>);
			}
			index++;
		}
		const isWeight100 = building.weightSum === 100;
		return (
			<div className="slds-form" role="list">
				<div className="fm-rating-body">
					{rows}
				</div>
				<div key={"part-total"}>
					<FieldGroup>
						<FieldRow>
							<Static value="" size={2} readOnlyLook="plain" />
							<Static value={building.weightSum.toString()} size={1} align="right" readOnlyLook="plain" error={!isWeight100 ? "muss 100% sein" : ""} />
							<Static value="" size={9} readOnlyLook="plain" />
						</FieldRow>
					</FieldGroup>
				</div>
			</div>
		);
	}

}

export interface ElementRowRatingFormProps {
	row: number;
	rowCount: number;
	element: BuildingElement;
	elementAccessor: ElementAccessor;
	currentElementId: string | undefined;
	onSelectElement: (id: string) => void;
}

@observer
class ElementRowRatingForm extends React.Component<ElementRowRatingFormProps> {

	render() {
		const { row, element, elementAccessor, currentElementId } = this.props;
		let description = element.description ? element.description : "";
		description += element.conditionDescription ? "\n<b>Zustand</b>: " + element.conditionDescription : "";
		description += element.measureDescription ? "\n<b>Massnahmen</b>: " + element.measureDescription : "";
		description = description.trim();
		return (
			<div
				onClick={(e) => this.onClickRow(element.id, e)}
				className={currentElementId === element.id ? "selected" : ""}
			>
				<SldsSubForm formAccessor={elementAccessor} item={element}>
					<FieldGroup>
						<FieldRow>
							<Static value={element.buildingPart?.name} size={2} readOnlyLook="plain" />
							<Input id={"cell-" + row + "-0"} fieldName="weight" size={1} align="right" readOnlyLook="plain" onClick={(e) => this.onClickInput(element.id, e)} onFocus={(e) => this.onFocusInput(element.id, e)} onKeyDown={(e) => this.onKeyDown(row, 0, e.key)} />
							<Input id={"cell-" + row + "-2"} fieldName="condition" size={1} align="right" readOnlyLook="plain" onClick={(e) => this.onClickInput(element.id, e)} onFocus={(e) => this.onFocusInput(element.id, e)} onKeyDown={(e) => this.onKeyDown(row, 2, e.key)} />
							<Static value={element.shortTermRestoration ? element.shortTermRestoration + " (" + element.restorationAge + ")" : ""} size={1} align="center" readOnlyLook="plain" />
							<Static value={element.midTermRestoration ? element.midTermRestoration + " (" + element.restorationAge + ")" : ""} size={1} align="center" readOnlyLook="plain" />
							<Static value={element.longTermRestoration ? element.longTermRestoration + " (" + element.restorationAge + ")" : ""} size={1} align="center" readOnlyLook="plain" />
							<Static value={element.restorationCosts ? element.restorationCosts + " kCHF" : ""} size={1} align="right" readOnlyLook="plain" />
							<Static value={description} size={4} readOnlyLook="plain" isMultiline />
						</FieldRow>
						<hr style={{ margin: "0.25rem 0" }}></hr>
					</FieldGroup>
				</SldsSubForm>
			</div>
		);
	}

	private onClickRow = (id: string, e: React.SyntheticEvent<any>): void => {
		e.stopPropagation();
		this.props.onSelectElement(id);
	}

	private onClickInput = (id: string, e: React.SyntheticEvent<any>): void => {
		e.stopPropagation();
		// don't toggle details window when clicking in field on same row
		if (!this.props.currentElementId || (id !== this.props.currentElementId)) {
			this.props.onSelectElement(id);
		}
	}

	private onFocusInput = (id: string, e: React.SyntheticEvent<any>): void => {
		e.stopPropagation();
		// don't toggle details window when focusing on field on same row
		if (!this.props.currentElementId || (id !== this.props.currentElementId)) {
			this.props.onSelectElement(id);
		}
	}

	private onKeyDown = (row: number, col: number, key: string): void => {
		const rowDelta = key === "ArrowDown" ? 1 : key === "ArrowUp" ? -1 : 0;
		if (rowDelta) {
			const targetRow = (this.props.rowCount + row + rowDelta) % this.props.rowCount;
			const target = document.getElementById("cell-" + targetRow + "-" + col);
			target?.focus(); // focus field and possibly trigger row change
			setTimeout(() => { (target as HTMLInputElement)?.select(); }, 10); // select text in field
		}
	}

}
