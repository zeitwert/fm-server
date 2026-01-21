import { AutoComplete, Typography, Spin } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { useState } from "react";
import { AfField } from "./AfField";
import { useDebouncedValue } from "../../hooks/useDebouncedValue";
import { useAggregateSearch } from "../../hooks/useAggregateSearch";
import { useStyles } from "../../hooks/useStyles";
import type { AfFieldProps, Enumerated } from "../../types";

interface AfComboboxProps extends AfFieldProps {
	/** Entity type for search, e.g., "contact", "user" */
	entityType: string;
	/** API module (defaults to entityType) */
	module?: string;
	/** Explicit options (no search, use for pre-loaded lists) */
	options?: Enumerated[];
	/** Minimum characters to trigger search (default: 2) */
	minSearchLength?: number;
	/** External change handler */
	onChange?: (value: Enumerated | null) => void;
}

/**
 * Autocomplete input with search functionality.
 *
 * Replaces `Combobox` + `AggregateField` from fm-ui. Searches entities
 * on the server as the user types.
 *
 * @example
 * // Search contacts
 * <AfCombobox
 *   name="contact"
 *   label="Kontakt"
 *   entityType="contact"
 * />
 *
 * // Search users with custom module
 * <AfCombobox
 *   name="owner"
 *   label="Verantwortlich"
 *   entityType="user"
 *   module="oe"
 * />
 *
 * // With explicit options (no search)
 * <AfCombobox
 *   name="account"
 *   label="Kunde"
 *   entityType="account"
 *   options={availableAccounts}
 * />
 */
export function AfCombobox({
	name,
	entityType,
	module,
	options: explicitOptions,
	minSearchLength = 2,
	onChange: externalOnChange,
	readOnly,
	disabled,
	...fieldProps
}: AfComboboxProps) {
	const { control } = useFormContext();
	const [searchText, setSearchText] = useState("");
	const debouncedSearch = useDebouncedValue(searchText, 200);
	const { styles } = useStyles();

	const { data: searchResults = [], isLoading } = useAggregateSearch(
		entityType,
		debouncedSearch,
		module,
		{ minLength: minSearchLength }
	);

	const options = explicitOptions ?? searchResults;

	return (
		<AfField name={name} {...fieldProps}>
			<Controller
				name={name}
				control={control}
				render={({
					field: { value, onChange },
				}: {
					field: {
						value: Enumerated | null | undefined;
						onChange: (value: Enumerated | null) => void;
					};
				}) => {
					const currentValue = value as Enumerated | null | undefined;

					if (readOnly) {
						return (
							<Typography.Text style={styles.readonlyField}>
								{currentValue?.name || "\u00A0"}
							</Typography.Text>
						);
					}

					return (
						<AutoComplete
							value={currentValue ? currentValue.name : searchText}
							onChange={(text) => {
								// When typing, clear the selected value and update search text
								if (currentValue) {
									onChange(null);
								}
								setSearchText(text);
							}}
							onSelect={(id) => {
								const selected = options.find((o) => o.id === id) ?? null;
								onChange(selected);
								externalOnChange?.(selected);
								setSearchText("");
							}}
							onClear={() => {
								onChange(null);
								externalOnChange?.(null);
								setSearchText("");
							}}
							disabled={disabled}
							allowClear
							placeholder="Suchen..."
							className="af-full-width"
							notFoundContent={
								isLoading ? (
									<Spin size="small" />
								) : searchText.length < minSearchLength ? (
									`Min. ${minSearchLength} Zeichen`
								) : (
									"Keine Ergebnisse"
								)
							}
							options={options.map((o) => ({ value: o.id, label: o.name }))}
						/>
					);
				}}
			/>
		</AfField>
	);
}
