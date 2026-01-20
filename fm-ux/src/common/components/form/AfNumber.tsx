import { InputNumber, Typography } from "antd";
import { Controller, useFormContext } from "react-hook-form";
import { AfField } from "./AfField";
import type { AfFieldProps } from "../../types";

interface AfNumberProps extends AfFieldProps {
	/** Minimum allowed value */
	min?: number;
	/** Maximum allowed value */
	max?: number;
	/** Number of decimal places (0 for integer, undefined for any) */
	precision?: number;
	/** Prefix text (e.g., "CHF") */
	prefix?: string;
	/** Suffix text (e.g., "m²", "kCHF") */
	suffix?: string;
	/** Step increment for arrows */
	step?: number;
	/** Text alignment */
	align?: "left" | "center" | "right";
}

/**
 * Numeric input with Swiss number formatting (apostrophe as thousand separator).
 *
 * Replaces `NumberField` and `IntField` from fm-ui.
 * Use `precision={0}` for integers.
 *
 * @example
 * <AfNumber name="insuredValue" label="Versicherungswert" suffix="kCHF" />
 * <AfNumber name="buildingYear" label="Baujahr" precision={0} min={1000} max={2100} />
 * <AfNumber name="volume" label="Volumen" suffix="m³" />
 */
export function AfNumber({
	name,
	min,
	max,
	precision,
	prefix,
	suffix,
	step = 1,
	align = "left",
	readOnly,
	disabled,
	...fieldProps
}: AfNumberProps) {
	const { control } = useFormContext();

	const formatValue = (value: number | null | undefined): string => {
		if (value === undefined || value === null) return "\u00A0";
		const formatted =
			precision !== undefined
				? value.toLocaleString("de-CH", {
						minimumFractionDigits: precision,
						maximumFractionDigits: precision,
					})
				: value.toLocaleString("de-CH");
		return `${prefix ? prefix + " " : ""}${formatted}${suffix ? " " + suffix : ""}`;
	};

	return (
		<AfField name={name} {...fieldProps}>
			<Controller
				name={name}
				control={control}
				render={({ field: { value, onChange, onBlur } }) =>
					readOnly ? (
						<Typography.Text style={{ textAlign: align, display: "block", fontWeight: "600" }}>
							{formatValue(value as number | null | undefined)}
						</Typography.Text>
					) : (
						<InputNumber
							value={value as number | null | undefined}
							onChange={(val) => onChange(val)}
							onBlur={onBlur}
							controls={false}
							min={min}
							max={max}
							precision={precision}
							step={step}
							prefix={prefix}
							suffix={suffix}
							disabled={disabled}
							className={`af-number-align-${align}`}
							style={{ width: "100%" }}
							decimalSeparator="."
							formatter={(val) => (val ? `${val}`.replace(/\B(?=(\d{3})+(?!\d))/g, "'") : "")}
							parser={(val) => (val ? Number(val.replace(/'/g, "")) : (null as unknown as number))}
						/>
					)
				}
			/>
		</AfField>
	);
}
