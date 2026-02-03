import { Select, Typography, Spin } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { useEffect, useRef } from "react";
import { AfField } from "./AfField";
import { useDependentCodeTable } from "../../hooks/useDependentCodeTable";
import { useStyles } from "../../hooks/useStyles";
import type { AfFieldProps, Enumerated } from "../../types";

interface AfDependentSelectProps extends AfFieldProps {
	/** Function to build source path from parent ID, e.g., (id) => `building/codeBuildingSubType/${id}` */
	sourceBuilder: (parentId: string) => string;
	/** Field name to watch for parent value, e.g., "buildingType" */
	parentField: string;
	/** External change handler */
	onChange?: (value: Enumerated | null) => void;
	/** Whether to show clear button (default: true unless required) */
	allowClear?: boolean;
}

/**
 * Select dropdown with dependent code table loading.
 *
 * Options are loaded based on a parent field's value. When the parent changes,
 * the options are reloaded and the current selection is cleared if no longer valid.
 *
 * @example
 * <AfSelect
 *   name="buildingType"
 *   label="Bauwerksart SIA I"
 *   source="building/codeBuildingType"
 * />
 * <AfDependentSelect
 *   name="buildingSubType"
 *   label="Bauwerksart SIA II"
 *   sourceBuilder={(id) => `building/codeBuildingSubType/${id}`}
 *   parentField="buildingType"
 * />
 */
export function AfDependentSelect({
	name,
	sourceBuilder,
	parentField,
	onChange: externalOnChange,
	allowClear = true,
	readOnly,
	disabled,
	required,
	...fieldProps
}: AfDependentSelectProps) {
	const { control, watch, setValue } = useFormContext();
	const parentValue = watch(parentField) as Enumerated | null | undefined;
	const parentId = parentValue?.id;
	const prevParentIdRef = useRef<string | undefined>(parentId);
	const { styles } = useStyles();

	const source = parentId ? sourceBuilder(parentId) : "";
	const { data: options = [], isLoading } = useDependentCodeTable(source, parentId);

	// Clear selection when parent changes
	useEffect(() => {
		if (prevParentIdRef.current !== parentId && prevParentIdRef.current !== undefined) {
			setValue(name, null);
		}
		prevParentIdRef.current = parentId;
	}, [parentId, name, setValue]);

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
							disabled={disabled || !parentId}
							allowClear={allowClear && !required}
							placeholder={!parentId ? "Zuerst übergeordnetes Feld auswählen" : "Auswählen..."}
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
