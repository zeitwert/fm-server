
import classNames from "classnames";
import { IAnyModelType, Instance } from "mobx-state-tree";
import { Form, FormDefinition, FormState, FormStateOptions, GroupDefinition } from "mstform";
import React from "react";

export const FormStateContext: React.Context<FormState<any, any, any>> = React.createContext(undefined as unknown as FormState<any, any, any>);

export interface SldsFormProps<M extends IAnyModelType> {
	formModel: Form<M, FormDefinition<M>, GroupDefinition<FormDefinition<M>>>;
	formStateOptions?: FormStateOptions<M>;
	item: Instance<M>;
	className?: string;
}

export interface SldsEmbeddedFormProps<M extends IAnyModelType> {
	formState: FormState<FormDefinition<M>, GroupDefinition<FormDefinition<M>>, M>;
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
		console.log("Form.render", !!children && typeof children === "function", this.formState?.node?.name, typeof children);
		return (
			<FormStateContext.Provider value={this.formState}>
				<div className={classes} role="list">
					{
						!!children && typeof children === "function"
							? children({ formState: this.formState, item: this.props.item })
							: children
					}
				</div>
			</FormStateContext.Provider>
		);
	}

}
