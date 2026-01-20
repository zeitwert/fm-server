import { Switch } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { AfField } from "./AfField";
import type { AfFieldProps } from "../../types";

type AfCheckboxProps = AfFieldProps;

/**
 * Boolean toggle field using Ant Design Switch.
 *
 * Replaces `Checkbox` from fm-ui. Uses switch style to match the legacy UI toggle variant.
 *
 * @example
 * <AfCheckbox name="isActive" label="Aktiv" />
 */
export function AfCheckbox({ name, readOnly, disabled, ...fieldProps }: AfCheckboxProps) {
	const { control } = useFormContext();

	return (
		<AfField name={name} {...fieldProps}>
			<Controller
				name={name}
				control={control}
				render={({
					field: { value, onChange },
				}: {
					field: { value: boolean | undefined; onChange: (value: boolean) => void };
				}) => <Switch checked={!!value} onChange={onChange} disabled={disabled || readOnly} />}
			/>
		</AfField>
	);
}
