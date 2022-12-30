
import classNames from "classnames";
import { observer } from "mobx-react";
import { FieldAccessor, FormState } from "mstform";
import { FC, useContext, useState } from "react";
import { FormStateContext } from "../Form";

export interface FieldGroupProps {
	legend?: string;
	label?: string;
	isAddress?: boolean;
	className?: string;
}

export const FieldGroup: FC<FieldGroupProps> = (props) => {
	const { legend, label, isAddress, className, children } = props;
	return (
		<fieldset className={classNames("slds-form-element slds-form-element_compound", isAddress && "slds-form-element_address", className)} >
			{legend && <legend className="slds-form-element__label slds-form-element__legend" style={{ whiteSpace: "nowrap" }}>{legend}</legend>}
			{label && <legend className="slds-form-element__label" style={{ whiteSpace: "nowrap" }}>{label}</legend>}
			<div className="slds-form-element__control">
				{children}
			</div>
		</fieldset>
	);
};

export interface FieldRowProps {
	className?: string;
}

export const FieldRow: FC<FieldRowProps> = (props) => {
	const { children } = props;
	return (
		<div className={classNames("slds-form-element__row", props.className)}>
			{children}
		</div>
	);
};

export interface FieldProps {
	fieldName?: string;
	readOnly?: boolean;
	required?: boolean;
	disabled?: boolean;
	error?: string;
	label?: string;
	size?: number;
	placeholder?: string;
	helpText?: string;
	isMultiline?: boolean;
	align?: "left" | "center" | "right";
	readOnlyLook?: "static" | "plain"; // static: standard look with underline, plain: no underline
}

let fieldId = 0;

export function getFieldId(props: FieldProps): string {
	if (props.fieldName) {
		return "field-" + props.fieldName;
	} else if (props["__field_id"]) {
		return props["__field_id"];
	}
	fieldId += 1;
	return "field-" + fieldId;
}

export interface ComponentProps {
	readOnly: boolean | undefined;
	inputProps: any;
}

export function getAccessor(props: any, formState: FormState<any, any, any>): FieldAccessor<any, any> | undefined {
	if (props.fieldName) {
		return formState.field(props.fieldName);
	}
	return undefined;
}

export function getComponentProps(accessor: FieldAccessor<any, any> | undefined, props: any): ComponentProps {
	let readOnly = props.readOnly === undefined ? accessor?.readOnly : props.readOnly;
	let inputProps = accessor?.inputProps || {};
	inputProps.disabled = props.disabled === undefined ? inputProps.disabled : props.disabled;
	inputProps.value = props.value === undefined ? inputProps.value : props.value;
	inputProps.checked = props.checked === undefined ? inputProps.checked : props.checked;
	inputProps.onChange = props.onChange === undefined ? inputProps.onChange : props.onChange;
	return { readOnly: readOnly, inputProps: inputProps };
}

export const Field: FC<FieldProps> = observer((props) => {

	const fieldId = getFieldId(props);
	const { label, size, helpText, align, readOnlyLook, isMultiline } = props;
	const accessor = getAccessor(props, useContext(FormStateContext));
	let required: boolean | undefined;
	let readOnly: boolean | undefined;
	let error: string | undefined;
	if (accessor) {
		required = props.required === undefined ? accessor.required : props.required;
		readOnly = props.readOnly === undefined ? accessor.readOnly : props.readOnly;
		error = props.error === undefined ? accessor.error : props.error;
	} else {
		required = props.required;
		readOnly = props.readOnly;
		error = props.error;
	}
	const [showHelpText, setShowHelpText] = useState(false);

	return (
		<div className={"slds-size_" + (size ? size + "-of-12" : "1-of-1")}>
			<div className={
				classNames(
					"slds-form-element",
					{
						"slds-form-element_readonly": readOnly,
						"fa-form-element_readonly_plain": readOnlyLook === "plain",
						"slds-has-error": error,
						["slds-form-element-" + align]: align
					}
				)
			} >
				{
					readOnly &&
					<>
						{label && <span className="slds-form-element__label" style={{ whiteSpace: "nowrap" }}>{label}{required && <abbr className="slds-required" title="required">*</abbr>}</span>}
						<div className="slds-form-element__control">
							<div className={classNames("slds-form-element__static", !isMultiline && "slds-truncate")}>
								{props.children}
							</div>
						</div>
					</>
				}
				{
					!readOnly &&
					<>
						{label && <label className="slds-form-element__label" htmlFor={fieldId} style={{ whiteSpace: "nowrap" }}>{label}{required && <abbr className="slds-required" title="required">*</abbr>}</label>}
						{
							helpText &&
							<div className="slds-form-element__icon slds-p-top_xxx-small">
								<button className="slds-button slds-button_icon" onClick={() => setShowHelpText(!showHelpText)}>
									<svg className="slds-button__icon" aria-hidden="true">
										<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#info"></use>
									</svg>
									<span className="slds-assistive-text">Help</span>
								</button>
								{
									showHelpText &&
									<div
										className="slds-popover slds-popover_tooltip slds-nubbin_top-left"
										role="tooltip"
										id={fieldId + "-help"}
										style={{ position: "absolute", top: "25px", left: "-15px", width: "300px", lineHeight: 1.5 }}
									>
										<div className="slds-popover__body" dangerouslySetInnerHTML={{ __html: helpText }} />
									</div>
								}
							</div>
						}
						<div className="slds-form-element__control">
							{props.children}
							{
								error &&
								<div className="slds-form-element__help" id={fieldId + "-error"}>{error}</div>
							}
						</div>
					</>
				}
			</div>
		</div >
	);

});
