import { Col, Row } from "antd";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfFieldRow,
	AfFieldGroup,
	AfDatePicker,
} from "../../../../common/components/form";
import { useSessionStore } from "../../../../session/model/sessionStore";
import { KERNEL_TENANT } from "../../../../session/model/types";

interface ContactMainFormProps {
	disabled: boolean;
}

export function ContactMainForm({ disabled }: ContactMainFormProps) {
	const { t } = useTranslation("contact");
	const { sessionInfo } = useSessionStore();
	const isKernelTenant = sessionInfo?.tenant?.tenantType?.id === KERNEL_TENANT;

	return (
		<div>
			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("basicInfo")}>
						<AfFieldRow>
							<AfSelect
								name="salutation"
								label={t("salutation")}
								source="contact/codeSalutation"
								required
								readOnly={disabled}
								size={6}
							/>
							<AfSelect
								name="title"
								label={t("title")}
								source="contact/codeTitle"
								readOnly={disabled}
								size={6}
							/>
							<AfInput name="firstName" label={t("firstName")} readOnly={disabled} size={12} />
						</AfFieldRow>
						<AfFieldRow>
							<AfInput
								name="lastName"
								label={t("lastName")}
								required
								readOnly={disabled}
								size={12}
							/>
							<AfSelect
								name="contactRole"
								label={t("contactRole")}
								source="contact/codeContactRole"
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfInput name="email" label={t("email")} readOnly={disabled} size={12} />
							<AfInput name="mobile" label={t("mobile")} readOnly={disabled} size={6} />
							<AfInput name="phone" label={t("phone")} readOnly={disabled} size={6} />
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
					<AfFieldGroup legend={t("personal")}>
						<AfFieldRow>
							<AfDatePicker name="birthDate" label={t("birthDate")} readOnly={disabled} size={8} />
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
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
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
