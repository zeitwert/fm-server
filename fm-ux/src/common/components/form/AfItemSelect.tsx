import { useMemo } from "react";
import { Select, Space, Typography } from "antd";
import {
	getItemTypeMeta,
	getItemTypeSortOrder,
	defaultItemTypeMeta,
} from "../../../app/config/ItemTypeRegistry";
import type { Enumerated } from "../../types";

const { Text } = Typography;

export interface ItemSelectOption {
	id: string;
	name: string;
	itemType?: Enumerated;
}

interface AfItemSelectProps {
	/** Current selected value (item id or null) */
	value: string | null;
	/** Called when selection changes */
	onChange: (id: string) => void;
	/** Available options to select from */
	options: ItemSelectOption[];
	/** Placeholder text */
	placeholder?: string;
	/** Custom style */
	style?: React.CSSProperties;
	/** aria-label for testing */
	"aria-label"?: string;
}

/**
 * Select component for choosing entity items with type-aware rendering.
 *
 * Displays items with their icon (based on itemType), name, and type label.
 * Options are automatically sorted by itemType sortOrder (accounts first, then portfolios, etc.)
 *
 * @example
 * <AfItemSelect
 *   value={selectedId}
 *   onChange={handleSelect}
 *   options={availableObjects}
 *   placeholder="Select an object..."
 * />
 */
export function AfItemSelect({
	value,
	onChange,
	options,
	placeholder,
	style,
	"aria-label": ariaLabel,
}: AfItemSelectProps) {
	// Sort options by itemType sortOrder
	const sortedOptions = useMemo(
		() =>
			[...options].sort(
				(a, b) => getItemTypeSortOrder(a.itemType?.id) - getItemTypeSortOrder(b.itemType?.id)
			),
		[options]
	);

	// Build Select options with item data for optionRender
	const selectOptions = useMemo(
		() =>
			sortedOptions.map((obj) => ({
				value: obj.id,
				label: obj.name,
				itemType: obj.itemType,
			})),
		[sortedOptions]
	);

	return (
		<Select
			style={style ?? { width: "100%" }}
			placeholder={placeholder}
			value={value}
			onChange={onChange}
			options={selectOptions}
			showSearch
			filterOption={(input, option) =>
				(option?.label?.toString() ?? "").toLowerCase().includes(input.toLowerCase())
			}
			aria-label={ariaLabel}
			optionRender={(option) => {
				const meta = getItemTypeMeta(option.data.itemType?.id) ?? defaultItemTypeMeta;
				return (
					<Space>
						{meta.icon}
						<span>{option.label}</span>
						<Text type="secondary">{option.data.itemType?.name}</Text>
					</Space>
				);
			}}
		/>
	);
}
