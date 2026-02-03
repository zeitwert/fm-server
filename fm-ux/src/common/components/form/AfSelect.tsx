import { Select, Typography, Spin } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { AfField } from "./AfField";
import { useCodeTable } from "../../hooks/useCodeTable";
import { useStyles } from "../../hooks/useStyles";
import type { AfFieldProps, Enumerated } from "../../types";

interface AfSelectProps extends AfFieldProps {
	/** Code table path, e.g., "building/codeBuildingType" or "oe/codeCountry" */
	source?: string;
	/** Explicit options (alternative to source) */
	options?: Enumerated[];
	/** External change handler */
	onChange?: (value: Enumerated | null) => void;
	/** Whether to show clear button (default: true unless required) */
	allowClear?: boolean;
}

/**
 * Select dropdown with code table loading.
 *
 * Replaces `Select` + `EnumeratedField` from fm-ui.
 * Automatically loads options from the server when `source` is provided.
 *
 * @example
 * // With code table source
 * <AfSelect name="buildingType" label="Bauwerksart" source="building/codeBuildingType" />
 *
 * // With explicit options
 * <AfSelect name="status" label="Status" options={statusOptions} />
 *
 * // With external change handler
 * <AfSelect
 *   name="owner"
 *   label="Verantwortlich"
 *   source="oe/objUser"
 *   onChange={(value) => console.log('Selected:', value)}
 * />
 */
export function AfSelect({
	name,
	source,
	options: explicitOptions,
	onChange: externalOnChange,
	allowClear = true,
	readOnly,
	disabled,
	required,
	...fieldProps
}: AfSelectProps) {
	const { control } = useFormContext();
	const { data: codeTableOptions, isLoading } = useCodeTable(source ?? "", {
		enabled: !!source,
	});
	const { styles } = useStyles();

	const options = explicitOptions ?? codeTableOptions ?? [];

	return (
		<AfField name={name} required={required} {...fieldProps}>
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
						<Select
							value={currentValue?.id}
							onChange={(id) => {
								const selected = id ? (options.find((o) => o.id === id) ?? null) : null;
								onChange(selected);
								externalOnChange?.(selected);
							}}
							loading={isLoading}
							disabled={disabled}
							allowClear={allowClear && !required}
							placeholder={isLoading ? "Laden..." : "Auswählen..."}
							className={`af-full-width${required ? " af-mandatory" : ""}`}
							notFoundContent={isLoading ? <Spin size="small" /> : "Keine Optionen"}
							options={options.map((o) => ({ value: o.id, label: o.name }))}
							showSearch
							filterOption={(input, option) =>
								(option?.label?.toString() ?? "").toLowerCase().includes(input.toLowerCase())
							}
						/>
					);
				}}
			/>
		</AfField>
	);
}
