
import classNames from "classnames";
import { makeObservable, observable, transaction } from "mobx";
import { observer } from "mobx-react";
import { IAnyModelType, Instance, isAlive } from "mobx-state-tree";
import { Form, FormDefinition, FormState, FormStateOptions, GroupDefinition, IFormAccessor } from "mstform";
import React, { ReactNode } from "react";

export type GenericFormAccessor = IFormAccessor<FormDefinition<IAnyModelType>, GroupDefinition<FormDefinition<IAnyModelType>>, IAnyModelType>;
export const FormContext: React.Context<GenericFormAccessor> = React.createContext(undefined as unknown as GenericFormAccessor);

export interface AccessorContext<M extends IAnyModelType> {
	formAccessor: IFormAccessor<FormDefinition<M>, GroupDefinition<FormDefinition<M>>, M>;
	item: Instance<M>;
}

export interface SldsFormProps<M extends IAnyModelType> {
	formModel: Form<M, FormDefinition<M>, GroupDefinition<FormDefinition<M>>>;
	formStateOptions?: FormStateOptions<M>;
	item: Instance<M>;
	children: ReactNode | ((config: AccessorContext<M>) => ReactNode) | undefined
	className?: string;
}

@observer
export class SldsForm<M extends IAnyModelType> extends React.Component<SldsFormProps<M>> {

	@observable
	formState: FormState<FormDefinition<M>, GroupDefinition<FormDefinition<M>>, M> | undefined;

	constructor(props: SldsFormProps<M>) {
		super(props);
		makeObservable(this);
		this.attachState(props);
	}

	componentDidUpdate(prevProps: Readonly<SldsFormProps<M>>): void {
		if (prevProps.item !== this.props.item) {
			transaction(() => {
				this.detachState();
				this.attachState(this.props);
			});
		}
	}

	componentWillUnmount() {
		this.detachState();
	}

	render() {
		if (!isAlive(this.formState?.node)) {
			return null;
		}
		const { className, children } = this.props;
		const classes = classNames("slds-form", className);
		return (
			<FormContext.Provider value={this.formState as unknown as GenericFormAccessor}>
				<div className={classes} role="list">
					{
						!!children && typeof children === "function"
							? children({ formAccessor: this.formState!, item: this.props.item })
							: children
					}
				</div>
			</FormContext.Provider>
		);
	}

	private attachState(props: SldsFormProps<M>) {
		this.formState = props.formModel.state(
			props.item,
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
		// Start autoLoadReactions for fields with dependentQuery
		// mstform sets a default dummy function (i.e. () => {}) for all fields with references
		// so all fields with source will have an autoLoadReaction, even if not necessary (it does not seem to hurt though)
		this.formState.fieldAccessors.forEach(fieldAccessor => {
			if ((fieldAccessor.references as any)?.dependentQuery) {
				fieldAccessor.references.autoLoadReaction();
			}
		});
	}

	private detachState() {
		// Clear autoLoadReaction for fields with dependentQuery
		this.formState?.fieldAccessors.forEach(fieldAccessor => {
			if ((fieldAccessor.references as any)?.dependentQuery) {
				fieldAccessor.references.clearAutoLoadReaction();
			}
		});
		this.formState = undefined;
	}

}

export interface SldsSubFormProps<M extends IAnyModelType> {
	formAccessor: IFormAccessor<FormDefinition<M>, GroupDefinition<FormDefinition<M>>, M>;
	item: Instance<M>;
	children: ReactNode | ((config: AccessorContext<M>) => ReactNode) | undefined
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
