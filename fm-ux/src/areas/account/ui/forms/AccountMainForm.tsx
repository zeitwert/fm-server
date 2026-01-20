/**
 * AccountMainForm - Main tab form for account details.
 *
 * Displays:
 * - Basic info: name, description
 * - Type/Segment: accountType, clientSegment
 * - Relations: tenant, owner
 * - Calculation parameters: inflationRate, discountRate
 * - Contacts table (read-only)
 */

import { Card, Table, Typography } from "antd";
import type { ColumnType } from "antd/es/table";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfNumber,
	AfFieldRow,
} from "../../../../common/components/form";
import type { AccountContact } from "../../types";
import { useSessionStore } from "../../../../session/model/sessionStore";
import { KERNEL_TENANT } from "../../../../session/model/types";

interface AccountMainFormProps {
	/** Whether form is in read-only mode (not editing) */
	disabled: boolean;
	/** Contacts associated with the account (read-only display) */
	contacts?: AccountContact[];
}

export function AccountMainForm({ disabled, contacts = [] }: AccountMainFormProps) {
	const { t } = useTranslation("account");
	const { sessionInfo } = useSessionStore();

	// Only kernel tenants can change the tenant
	const isKernelTenant = sessionInfo?.tenant?.tenantType?.id === KERNEL_TENANT;

	const contactColumns: ColumnType<AccountContact>[] = [
		{
			title: t("contactName"),
			dataIndex: "name",
			key: "name",
		},
		{
			title: t("contactEmail"),
			dataIndex: "email",
			key: "email",
		},
		{
			title: t("contactPhone"),
			dataIndex: "phone",
			key: "phone",
		},
	];

	return (
		<div>
			{/* Basic Info Section */}
			<Card size="small" title={t("basicInfo")} style={{ marginBottom: 16 }}>
				<AfInput name="name" label={t("name")} required readOnly={disabled} />
				<AfTextArea name="description" label={t("description")} rows={4} readOnly={disabled} />
			</Card>

			{/* Type & Classification Section */}
			<Card size="small" title={t("classification")} style={{ marginBottom: 16 }}>
				<AfFieldRow>
					<AfSelect
						name="accountType"
						label={t("accountType")}
						source="account/codeAccountType"
						required
						readOnly={disabled}
						size={6}
					/>
					<AfSelect
						name="clientSegment"
						label={t("clientSegment")}
						source="account/codeClientSegment"
						readOnly={disabled}
						size={6}
					/>
				</AfFieldRow>
			</Card>

			{/* Organization Section */}
			<Card size="small" title={t("organization")} style={{ marginBottom: 16 }}>
				<AfFieldRow>
					{isKernelTenant ? (
						<AfSelect
							name="tenant"
							label={t("tenant")}
							source="oe/objTenant"
							required
							readOnly={disabled}
							size={6}
						/>
					) : (
						<AfSelect
							name="tenant"
							label={t("tenant")}
							source="oe/objTenant"
							required
							readOnly
							size={6}
						/>
					)}
					<AfSelect
						name="owner"
						label={t("owner")}
						source="oe/objUser"
						required
						readOnly={disabled}
						size={6}
					/>
				</AfFieldRow>
			</Card>

			{/* Calculation Parameters Section */}
			<Card size="small" title={t("calculationParams")} style={{ marginBottom: 16 }}>
				<AfFieldRow>
					<AfNumber
						name="inflationRate"
						label={t("inflationRate")}
						suffix="%"
						precision={2}
						min={0}
						max={100}
						readOnly={disabled}
						size={6}
					/>
					<AfNumber
						name="discountRate"
						label={t("discountRate")}
						suffix="%"
						precision={2}
						min={0}
						max={100}
						readOnly={disabled}
						size={6}
					/>
				</AfFieldRow>
			</Card>

			{/* Contacts Section (always read-only) */}
			{contacts.length > 0 && (
				<Card size="small" title={t("contacts")}>
					<Table<AccountContact>
						columns={contactColumns}
						dataSource={contacts}
						rowKey="id"
						size="small"
						pagination={false}
					/>
				</Card>
			)}

			{contacts.length === 0 && !disabled && (
				<Card size="small" title={t("contacts")}>
					<Typography.Text type="secondary">{t("noContacts")}</Typography.Text>
				</Card>
			)}
		</div>
	);
}
