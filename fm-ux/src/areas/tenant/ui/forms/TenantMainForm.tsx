import { Col, Row } from "antd";
import { useTranslation } from "react-i18next";
import {
	AfInput,
	AfTextArea,
	AfSelect,
	AfNumber,
	AfFieldRow,
	AfFieldGroup,
} from "../../../../common/components/form";

interface TenantMainFormProps {
	disabled: boolean;
}

export function TenantMainForm({ disabled }: TenantMainFormProps) {
	const { t } = useTranslation();

	return (
		<div>
			<Row>
				<Col span={12}>
					<AfFieldGroup legend={t("tenant:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="name"
								label={t("tenant:label.name")}
								required
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="tenantType"
								label={t("tenant:label.tenantType")}
								source="oe/codeTenantType"
								required
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
							label={t("tenant:label.description")}
							rows={4}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row>
				<Col span={24}>
					<AfFieldGroup legend={t("tenant:label.calculationParams")}>
						<AfFieldRow>
							<AfNumber
								name="inflationRate"
								label={t("tenant:label.inflationRate")}
								suffix="%"
								precision={2}
								min={0}
								max={100}
								readOnly={disabled}
								size={6}
							/>
							<AfNumber
								name="discountRate"
								label={t("tenant:label.discountRate")}
								suffix="%"
								precision={2}
								min={0}
								max={100}
								readOnly={disabled}
								size={6}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
