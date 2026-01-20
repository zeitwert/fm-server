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
	disabled: boolean;
	contacts?: AccountContact[];
}

export function AccountMainForm({ disabled, contacts = [] }: AccountMainFormProps) {
	const { t } = useTranslation("account");
	const { sessionInfo } = useSessionStore();
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
			<Card size="small" title={t("basicInfo")} style={{ marginBottom: 16 }}>
				<AfInput name="name" label={t("name")} required readOnly={disabled} />
				<AfTextArea name="description" label={t("description")} rows={4} readOnly={disabled} />
			</Card>

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
