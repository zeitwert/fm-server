
import classNames from "classnames";
import { IAnyModelType, Instance } from "mobx-state-tree";
import { Form, FormDefinition, FormState, FormStateOptions, GroupDefinition } from "mstform";
import React, { PropsWithChildren } from "react";

export const FormStateContext: React.Context<FormState<any, any, any>> = React.createContext(undefined as unknown as FormState<any, any, any>);

interface FormProps<M extends IAnyModelType, D extends FormDefinition<M>, G extends GroupDefinition<D>> {
	formModel: Form<M, D, G>;
	options?: FormStateOptions<any>;
	item: Instance<M>;
	className?: string;
}

export class SldsForm<M extends IAnyModelType, D extends FormDefinition<M>, G extends GroupDefinition<D>> extends React.Component<PropsWithChildren<FormProps<M, D, G>>> {

	formState: FormState<D, G, M>;

	constructor(props: PropsWithChildren<FormProps<M, D, G>>) {
		super(props);
		this.formState = props.formModel.state(
			this.props.item,
			Object.assign(
				{},
				{
					converterOptions: {
						decimalSeparator: ".",
						thousandSeparator: "'",
						renderThousands: true,
					}
				},
				this.props.options
			)
		);
	}

	render() {
		const { className, children } = this.props;
		const classes = classNames("slds-form", className);
		return (
			<FormStateContext.Provider value={this.formState}>
				<div className={classes} role="list">
					{children}
				</div>
			</FormStateContext.Provider>
		);
	}

}

/*
interface FormRowProps {
	className?: string;
}

export function FormRow({ className, children }: PropsWithChildren<FormRowProps>) {
	const classes = classNames("slds-form__row", className);
	return (
		<div className={classes} role="list">
			{children}
		</div>
	);
}

interface FormItemProps {
	className?: string;
}

export function FormItem({ children, className }: PropsWithChildren<FormItemProps>) {
	const classes = classNames("slds-form__item", className);
	return (
		<div className={classes} role="listitem">
			{children}
		</div>
	);
}

interface FormElementProps {
	legend?: string;
	className?: string;
	isCompound?: boolean;
	isStacked?: boolean;
	isReadOnly?: boolean;
}

export function FormElement({
	legend,
	className,
	isCompound,
	isStacked,
	isReadOnly,
	children
}: PropsWithChildren<FormElementProps>) {
	const classes = classNames(
		"slds-form-element",
		{
			"slds-form-element_compound": isCompound,
			"slds-form-element_stacked": isStacked,
			"slds-is-editing": !isReadOnly
			// "slds-form-element_readonly": isReadOnly,
		},
		className
	);
	return (
		<fieldset className={classes}>
			{legend && <legend className="slds-form-element__legend slds-form-element__label">{legend}</legend>}
			<div className="slds-form-element__control">{children}</div>
		</fieldset>
	);
}

interface FormElementRowProps {
	className?: string;
	isReadOnly?: boolean;
}

export function FormElementRow({ isReadOnly, className, children }: PropsWithChildren<FormElementRowProps>) {
	const classes = classNames(
		"slds-form-element__row",
		isReadOnly ? "slds-form-element__row-readonly slds-gutters_small" : "slds-form-element__row-editable",
		className
	);
	return <div className={classes}>{children}</div>;
}

interface FormElementColProps {
	className?: string;
	size?: number;
	isReadOnly?: boolean;
	isBorderless?: boolean;
}

export function FormElementCol({
	className,
	size,
	children,
	isReadOnly = true,
	isBorderless = false
}: PropsWithChildren<FormElementColProps>) {
	const classes = classNames(
		isBorderless ? "" : "slds-border_bottom",
		"slds-p-bottom_xx-small fa-forms-col__readonly",
		className
	);
	return (
		<Col className={classNames(`slds-size_${size}-of-12`)}>
			{isReadOnly && <div className={classes}>{children}</div>}
			{!isReadOnly && children}
		</Col>
	);
}
*/
