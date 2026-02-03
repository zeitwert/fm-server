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
} from "@/common/components/form";
import type { Enumerated } from "@/common/types";
import type { AccountContact } from "../../types";
import type { AccountFormInput } from "../../schemas";

interface AccountMainFormProps {
	disabled: boolean;
}

export function AccountMainForm({ disabled }: AccountMainFormProps) {
	const { t } = useTranslation();
	const { watch } = useFormContext<AccountFormInput>();

	// Read contacts from form context (display-only)
	const contacts = watch("contacts");

	// Transform contacts to select options for mainContact field
	const contactOptions: Enumerated[] = useMemo(
		() => (contacts ?? []).map((c) => ({ id: c.id, name: c.caption })),
		[contacts]
	);

	const contactColumns: ColumnType<AccountContact>[] = [
		{
			title: t("account:label.contactName"),
			dataIndex: "caption",
			key: "caption",
		},
		{
			title: t("account:label.contactEmail"),
			dataIndex: "email",
			key: "email",
		},
		{
			title: t("account:label.contactPhone"),
			dataIndex: "phone",
			key: "phone",
		},
	];

	return (
		<div>
			<Row gutter={16}>
				<Col span={12}>
					<AfFieldGroup legend={t("account:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="name"
								label={t("account:label.name")}
								required
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="accountType"
								label={t("account:label.accountType")}
								source="account/codeAccountType"
								required
								readOnly={disabled}
								size={12}
							/>
							<AfSelect
								name="clientSegment"
								label={t("account:label.clientSegment")}
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
							label={t("account:label.description")}
							rows={4}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row gutter={16}>
				<Col span={12}>
					<AfFieldGroup legend={t("account:label.organization")}>
						<AfFieldRow>
							<AfSelect
								name="owner"
								label={t("account:label.owner")}
								source="oe/objUser"
								required
								readOnly={disabled}
								size={12}
							/>
							<AfSelect
								name="mainContact"
								label={t("account:label.mainContact")}
								options={contactOptions}
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
				<Col span={12}>
					<AfFieldGroup legend={t("account:label.calculationParams")}>
						<AfFieldRow>
							<AfNumber
								name="inflationRate"
								label={t("account:label.inflationRate")}
								suffix="%"
								precision={2}
								min={0}
								max={100}
								readOnly={disabled}
								size={12}
							/>
							<AfNumber
								name="discountRate"
								label={t("account:label.discountRate")}
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

			<Row gutter={16}>
				<Col span={24}>
					<AfFieldGroup legend={t("account:label.contacts")}>
						{(!contacts || contacts.length === 0) && (
							<Typography.Text type="secondary">{t("account:message.noContacts")}</Typography.Text>
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
