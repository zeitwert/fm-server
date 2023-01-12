
import { SLDSCombobox } from "@salesforce/design-system-react";
import { assertThis, Enumerated, isEnumerated, requireThis } from "@zeitwert/ui-model";
import { IReactionDisposer, IReactionPublic, makeObservable, observable, reaction, toJS } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { FormContext } from "../Form";
import { Field, FieldProps, getAccessor, getComponentProps, getFieldId } from "./Field";

export interface ComboboxProps extends FieldProps {
	isAutocomplete?: boolean;
	value?: Enumerated | undefined;
	values?: Enumerated[];
	onChange?: (e: Enumerated | undefined) => void;
}

interface Option {
	id: string;
	label: string;
	icon?: JSX.Element;
}

const AUTOCOMPLETE_MIN_LENGTH = 2;
const AUTOCOMPLETE_TIMEOUT = 200;

@observer
export class Combobox extends React.Component<ComboboxProps> {

	static contextType = FormContext;

	context!: React.ContextType<typeof FormContext>;

	searchDisposer: IReactionDisposer | undefined;

	@observable options: Option[] = [];
	@observable isLoadingOptions = false;
	@observable hasLoadedOptions = false;

	@observable searchText = "";

	constructor(props: ComboboxProps) {
		super(props);
		makeObservable(this);
		if (this.props.isAutocomplete) {
			this.searchDisposer = reaction(
				() => this.searchText,
				this.debounceEffect(() => { this.loadOptions(); }, AUTOCOMPLETE_TIMEOUT)
			);
		}
	}

	async componentDidMount() {
		const accessor = getAccessor(this.props, this.context);
		requireThis(!!accessor || (!!this.props.values && !!this.props.onChange), "either accessor or values and onChange must be provided");
		if (!this.props.isAutocomplete) { // same behavior as Select
			await this.loadOptions();
		}
	}

	componentWillUnmount() {
		this.searchDisposer?.();
	}

	render() {
		const accessor = getAccessor(this.props, this.context);
		assertThis(!!accessor || (!!this.props.values && !!this.props.onChange), "either accessor or values and onChange must be provided");
		assertThis(!this.props.values || !!this.props.isAutocomplete, "explicit values are only supported for autocomplete");
		const { readOnly, inputProps } = getComponentProps(accessor, this.props);
		const { value } = this.props;
		assertThis(isEnumerated(value), "value must be an Enumerated (" + value + ")");
		const currentItem = accessor ? accessor.raw : value;
		const fieldId = getFieldId(this.props);
		if (readOnly) {
			return (
				<Field {...this.props} key={fieldId}>
					{!!currentItem && <span>{currentItem.name}</span>}
					{!currentItem && <span>&nbsp;</span>}
				</Field>
			);
		}
		return (
			<Field {...this.props} key={fieldId}>
				<SLDSCombobox
					events={{
						onChange: (event: any, { value }: { value: string }) => { // text entry
							this.searchText = value;
						},
						onSelect: (event: any, data: any) => { // selection from list
							this.onSelect(data.selection);
						},
						onRequestRemoveSelectedOption: (event: any, data: any) => { // remove selection
							this.onSelect(data.selection);
						},
						onBlur: inputProps?.onBlur && inputProps.onBlur
					}}
					selection={currentItem ? [currentItem].map(this.toOption) : []}
					options={toJS(this.options)}
					variant="inline-listbox"
					menuItemVisibleLength={5}
					value={this.searchText}
					hasMenuSpinner={this.isLoadingOptions}
					//singleInputDisabled={!inputProps.isEnabled}
					required={inputProps.required}
				/>
			</Field>
		);
	}

	private debounceEffect<T>(effect: (arg: T, prev: T, r: IReactionPublic) => void, debounceMs: number) {
		let timer: NodeJS.Timeout
		return (arg: T, prev: T, r: IReactionPublic) => {
			clearTimeout(timer)
			timer = setTimeout(() => effect(arg, prev, r), debounceMs)
		}
	}

	private toOption = (value: Enumerated): Option => {
		requireThis(isEnumerated(value), "value must be an Enumerated (" + value + ")");
		return {
			id: value.id,
			label: value.name,
			icon: undefined
		};
	};

	private loadOptions = async () => {
		const { isAutocomplete, values } = this.props;
		if (!isAutocomplete && !!values) {
			this.options = values.map(this.toOption);
		} else if (!isAutocomplete || this.searchText.length >= AUTOCOMPLETE_MIN_LENGTH) {
			const accessor = getAccessor(this.props, this.context)!;
			try {
				this.options = [];
				this.isLoadingOptions = true;
				this.hasLoadedOptions = false;
				await accessor.references?.load({ searchText: this.searchText });
				this.options = accessor.references.values({ searchText: this.searchText })?.map(this.toOption) ?? [];
			} catch (e: any) {
				accessor.setError("Failed to load options: " + e.message);
			} finally {
				this.isLoadingOptions = false;
				this.hasLoadedOptions = true;
			}
		} else {
			this.options = [];
		}
	};

	private onSelect(values: Option[]) {
		const accessor = getAccessor(this.props, this.context);
		let newOption = values.length ? values[0] : undefined;
		let newItem: Enumerated | undefined;
		if (accessor) {
			newItem = newOption?.id ? accessor.references.getById(newOption.id) : undefined;
		} else {
			newItem = newOption?.id ? this.props.values?.find(item => item.id === newOption?.id) : undefined;
		}
		if (this.props.onChange) {
			this.props.onChange?.(newItem);
		} else {
			accessor!.setRaw(newItem);
		}
		this.searchText = "";
	}

}
