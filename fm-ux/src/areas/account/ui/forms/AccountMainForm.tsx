import { useMemo } from "react";
import { Col, Row, Table, Typography } from "antd";
import type { ColumnType } from "antd/es/table";
import { useFormContext } from "react-hook-form";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfNumber,
	AfFieldRow,
	AfFieldGroup,
} from "../../../../common/components/form";
import type { Enumerated } from "../../../../common/types";
import type { AccountContact } from "../../types";
import type { AccountFormInput } from "../../schemas";
import { useSessionStore } from "../../../../session/model/sessionStore";
import { KERNEL_TENANT } from "../../../../session/model/types";

interface AccountMainFormProps {
	disabled: boolean;
}

export function AccountMainForm({ disabled }: AccountMainFormProps) {
	const { t } = useTranslation("account");
	const { watch } = useFormContext<AccountFormInput>();
	const { sessionInfo } = useSessionStore();
	const isKernelTenant = sessionInfo?.tenant?.tenantType?.id === KERNEL_TENANT;

	// Read contacts from form context (display-only)
	const contacts = watch("contacts");

	// Transform contacts to select options for mainContact field
	const contactOptions: Enumerated[] = useMemo(
		() => (contacts ?? []).map((c) => ({ id: c.id, name: c.caption })),
		[contacts]
	);

	const contactColumns: ColumnType<AccountContact>[] = [
		{
			title: t("contactName"),
			dataIndex: "caption",
			key: "caption",
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
			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("basicInfo")}>
						<AfFieldRow>
							<AfInput name="name" label={t("name")} required readOnly={disabled} size={24} />
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="accountType"
								label={t("accountType")}
								source="account/codeAccountType"
								required
								readOnly={disabled}
								size={12}
							/>
							<AfSelect
								name="clientSegment"
								label={t("clientSegment")}
								source="account/codeClientSegment"
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend="&nbsp;">
						<AfTextArea
							name="description"
							label={t("description")}
							rows={4}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("organization")}>
						<AfFieldRow>
							{isKernelTenant ? (
								<AfSelect
									name="tenant"
									label={t("tenant")}
									source="oe/objTenant"
									required
									readOnly={disabled}
									size={12}
								/>
							) : (
								<AfSelect
									name="tenant"
									label={t("tenant")}
									source="oe/objTenant"
									required
									readOnly
									size={12}
								/>
							)}
							<AfSelect
								name="owner"
								label={t("owner")}
								source="oe/objUser"
								required
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="mainContact"
								label={t("mainContact")}
								options={contactOptions}
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend={t("calculationParams")}>
						<AfFieldRow>
							<AfNumber
								name="inflationRate"
								label={t("inflationRate")}
								suffix="%"
								precision={2}
								min={0}
								max={100}
								readOnly={disabled}
								size={12}
							/>
							<AfNumber
								name="discountRate"
								label={t("discountRate")}
								suffix="%"
								precision={2}
								min={0}
								max={100}
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row>
				<Col span={24}>
					<AfFieldGroup legend={t("contacts")}>
						{(!contacts || contacts.length === 0) && (
							<Typography.Text type="secondary">{t("noContacts")}</Typography.Text>
						)}
						{contacts && contacts.length > 0 && (
							<Table<AccountContact>
								columns={contactColumns}
								dataSource={contacts}
								rowKey="id"
								size="small"
								pagination={false}
							/>
						)}
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
