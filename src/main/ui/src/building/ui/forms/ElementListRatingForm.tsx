
import { FieldGroup, FieldRow, Input, Static } from "@zeitwert/ui-forms";
import { Building, BuildingElement } from "@zeitwert/ui-model";
import { observer } from "mobx-react";
import { IRepeatingFormIndexedAccessor } from "mstform";
import React from "react";

export interface ElementListRatingFormProps {
	building: Building;
	elementForms: any; // RepeatingFormAccess<any, any, any>; ärger mit Typescript
	showAllElements: boolean;
	currentElementId: string | undefined;
	onSelectElement: (id: string) => void;
}

@observer
export default class ElementListRatingForm extends React.Component<ElementListRatingFormProps> {

	render() {
		const { building, elementForms, showAllElements, currentElementId, onSelectElement } = this.props;
		const rowCount = showAllElements ? building.elements.length : building.elements.filter(e => e.weight).length;
		const weight100 = building.weightSum === 100;
		let rows: JSX.Element[] = [];
		let row = 0, index = 0;
		for (const element of building.elements) {
			const elementForm = elementForms.index(index);
			if (showAllElements || !!element.weight) {
				rows.push(
					<ElementRowRatingForm
						key={"part-" + index}
						index={index}
						row={row}
						rowCount={rowCount}
						element={element}
						elementForm={elementForm}
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
		return (
			<div className="slds-form" role="list">
				<div className="fm-rating-body">
					{rows}
				</div>
				<div key={"part-total"}>
					<FieldGroup>
						<FieldRow>
							<Static value="" size={2} readOnlyLook="plain" />
							<Static value={building.weightSum.toString()} size={1} align="right" readOnlyLook="plain" error={!weight100 ? "muss 100% sein" : ""} />
							<Static value="" size={9} readOnlyLook="plain" />
						</FieldRow>
					</FieldGroup>
				</div>
			</div>
		);
	}

}

export interface ElementRowRatingFormProps {
	index: number;
	row: number;
	rowCount: number;
	element: BuildingElement;
	elementForm: IRepeatingFormIndexedAccessor<any, any, any>;
	currentElementId: string | undefined;
	onSelectElement: (id: string) => void;
}

@observer
class ElementRowRatingForm extends React.Component<ElementRowRatingFormProps> {

	render() {
		const { index, row, element, /*elementForm,*/ currentElementId } = this.props;
		let description = element.description ? element.description : "";
		description += element.conditionDescription ? "\n<b>Zustand</b>: " + element.conditionDescription : "";
		description += element.measureDescription ? "\n<b>Massnahmen</b>: " + element.measureDescription : "";
		description = description.trim();
		return (
			<div
				key={"part-" + index}
				onClick={(e) => this.onSelectElement(element.id, e)}
				className={currentElementId === element.id ? "selected" : ""}
			>
				<FieldGroup>
					<FieldRow>
						<Static value={element.buildingPart?.name} size={2} readOnlyLook="plain" />
						<Input id={"cell-" + row + "-0"} fieldName="weight" /*accessor={elementForm.field("weight")}*/ size={1} align="right" readOnlyLook="plain" onFocus={() => this.onSelectElement(element.id)} onKeyDown={(e) => this.onKeyDown(row, 0, e.key)} />
						<Input id={"cell-" + row + "-2"} fieldName="condition" /*accessor={elementForm.field("condition")}*/ size={1} align="right" readOnlyLook="plain" onFocus={() => this.onSelectElement(element.id)} onKeyDown={(e) => this.onKeyDown(row, 2, e.key)} />
						<Static value={element.shortTermRestoration ? element.shortTermRestoration + " (" + element.restorationAge + ")" : ""} size={1} align="center" readOnlyLook="plain" />
						<Static value={element.midTermRestoration ? element.midTermRestoration + " (" + element.restorationAge + ")" : ""} size={1} align="center" readOnlyLook="plain" />
						<Static value={element.longTermRestoration ? element.longTermRestoration + " (" + element.restorationAge + ")" : ""} size={1} align="center" readOnlyLook="plain" />
						<Static value={element.restorationCosts ? element.restorationCosts + " kCHF" : ""} size={1} align="right" readOnlyLook="plain" />
						<Static value={description} size={4} readOnlyLook="plain" isMultiline />
					</FieldRow>
					<hr style={{ margin: "0.25rem 0" }}></hr>
				</FieldGroup>
			</div>
		);
	}

	private onSelectElement = (id: string, e?: React.MouseEvent<HTMLDivElement>): void => {
		e?.stopPropagation();
		this.props.onSelectElement(id);
	}

	private onKeyDown = (row: number, col: number, key: string): void => {
		const rowDelta = key === "ArrowDown" ? 1 : key === "ArrowUp" ? -1 : 0;
		if (rowDelta) {
			const targetRow = (this.props.rowCount + row + rowDelta) % this.props.rowCount;
			const target = document.getElementById("cell-" + targetRow + "-" + col);
			target?.focus();
			setTimeout(() => { (target as HTMLInputElement)?.select(); }, 10);
		}
	}

}
