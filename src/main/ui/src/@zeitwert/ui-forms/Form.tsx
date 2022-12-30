
import classNames from "classnames";
import { IAnyModelType, Instance } from "mobx-state-tree";
import { Form, FormDefinition, FormState, FormStateOptions, GroupDefinition, IFormAccessor } from "mstform";
import React from "react";

export const FormContext: React.Context<IFormAccessor<any, any, any>> = React.createContext(undefined as unknown as IFormAccessor<any, any, any>);

export interface SldsFormProps<M extends IAnyModelType> {
	formModel: Form<M, FormDefinition<M>, GroupDefinition<FormDefinition<M>>>;
	formStateOptions?: FormStateOptions<M>;
	item: Instance<M>;
	className?: string;
}

export interface SldsSubFormProps<M extends IAnyModelType> {
	formAccessor: IFormAccessor<FormDefinition<M>, GroupDefinition<FormDefinition<M>>, M>;
	item: Instance<M>;
}

export class SldsForm<M extends IAnyModelType> extends React.Component<SldsFormProps<M>> {

	formState: FormState<FormDefinition<M>, GroupDefinition<FormDefinition<M>>, M>;

	constructor(props: SldsFormProps<M>) {
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
				this.props.formStateOptions
			)
		);
	}

	render() {
		const { className, children } = this.props;
		const classes = classNames("slds-form", className);
		return (
			<FormContext.Provider value={this.formState as unknown as IFormAccessor<any, any, any>}>
				<div className={classes} role="list">
					{
						!!children && typeof children === "function"
							? children({ formAccessor: this.formState, item: this.props.item })
							: children
					}
				</div>
			</FormContext.Provider>
		);
	}

}

export class SldsSubForm<M extends IAnyModelType> extends React.Component<SldsSubFormProps<M>> {

	render() {
		const { formAccessor, item, children } = this.props;
		return (
			<FormContext.Provider value={formAccessor}>
				<div className="slds-form" role="list">
					{
						!!children && typeof children === "function"
							? children({ formAccessor: formAccessor, item: item })
							: children
					}
				</div>
			</FormContext.Provider>
		);
	}

}
