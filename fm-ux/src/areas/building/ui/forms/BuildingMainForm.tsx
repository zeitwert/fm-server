import { Col, Row } from "antd";
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

interface BuildingMainFormProps {
	disabled: boolean;
}

export function BuildingMainForm({ disabled }: BuildingMainFormProps) {
	const { t } = useTranslation();
	const { watch } = useFormContext();

	const buildingType = watch("buildingType");
	const notInsuredValue = watch("notInsuredValue");
	const thirdPartyValue = watch("thirdPartyValue");

	return (
		<div>
			<Row gutter={16}>
				<Col span={8}>
					<AfFieldGroup legend={t("building:label.basicInfo")}>
						<AfFieldRow>
							<AfInput
								name="buildingNr"
								label={t("building:label.buildingNr")}
								required
								readOnly={disabled}
								size={6}
							/>
							<AfInput
								name="name"
								label={t("building:label.name")}
								required
								readOnly={disabled}
								size={18}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="owner"
								label={t("building:label.owner")}
								source="oe/objUser"
								required
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>

				<Col span={8}>
					<AfFieldGroup legend={t("building:label.classification")}>
						<AfFieldRow>
							<AfSelect
								name="buildingType"
								label={t("building:label.buildingType")}
								source="building/codeBuildingType"
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="buildingSubType"
								label={t("building:label.buildingSubType")}
								source={
									buildingType?.id ? `building/codeBuildingSubType/${buildingType.id}` : undefined
								}
								readOnly={disabled || !buildingType}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="historicPreservation"
								label={t("building:label.historicPreservation")}
								source="building/codeHistoricPreservation"
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>

				<Col span={8}>
					<AfFieldGroup legend={t("building:label.address")}>
						<AfFieldRow>
							<AfInput
								name="street"
								label={t("building:label.street")}
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfInput name="zip" label={t("building:label.zip")} readOnly={disabled} size={6} />
							<AfInput name="city" label={t("building:label.city")} readOnly={disabled} size={18} />
						</AfFieldRow>
						<AfFieldRow>
							<AfSelect
								name="country"
								label={t("building:label.country")}
								source="oe/codeCountry"
								readOnly
								size={24}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row gutter={16}>
				<Col span={8}>
					<AfFieldGroup legend={t("building:label.identification")}>
						<AfFieldRow>
							<AfInput
								name="insuranceNr"
								label={t("building:label.insuranceNr")}
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfInput
								name="nationalBuildingId"
								label={t("building:label.nationalBuildingId")}
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfInput
								name="plotNr"
								label={t("building:label.plotNr")}
								readOnly={disabled}
								size={24}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>

				<Col span={8}>
					<AfFieldGroup legend={t("building:label.dimensions")}>
						<AfFieldRow>
							<AfNumber
								name="volume"
								label={t("building:label.volume")}
								readOnly={disabled}
								size={12}
							/>
							<AfNumber
								name="areaGross"
								label={t("building:label.areaGross")}
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfNumber
								name="nrOfFloorsAboveGround"
								label={t("building:label.nrOfFloorsAboveGround")}
								readOnly={disabled}
								size={12}
							/>
							<AfNumber
								name="nrOfFloorsBelowGround"
								label={t("building:label.nrOfFloorsBelowGround")}
								readOnly={disabled}
								size={12}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>

				<Col span={8}>
					<AfFieldGroup legend={t("building:label.valuation")}>
						<AfFieldRow>
							<AfSelect
								name="currency"
								label={t("building:label.currency")}
								source="account/codeCurrency"
								readOnly
								size={16}
							/>
							<AfNumber
								name="buildingYear"
								label={t("building:label.buildingYear")}
								readOnly={disabled}
								formatNumber={false}
								size={8}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfNumber
								name="insuredValue"
								label={t("building:label.insuredValue")}
								required
								readOnly={disabled}
								size={16}
							/>
							<AfNumber
								name="insuredValueYear"
								label={t("building:label.insuredValueYear")}
								required
								readOnly={disabled}
								formatNumber={false}
								size={8}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfNumber
								name="notInsuredValue"
								label={t("building:label.notInsuredValue")}
								readOnly={disabled}
								size={16}
							/>
							<AfNumber
								name="notInsuredValueYear"
								label={t("building:label.notInsuredValueYear")}
								readOnly={disabled || !notInsuredValue}
								formatNumber={false}
								size={8}
							/>
						</AfFieldRow>
						<AfFieldRow>
							<AfNumber
								name="thirdPartyValue"
								label={t("building:label.thirdPartyValue")}
								readOnly={disabled}
								size={16}
							/>
							<AfNumber
								name="thirdPartyValueYear"
								label={t("building:label.thirdPartyValueYear")}
								readOnly={disabled || !thirdPartyValue}
								formatNumber={false}
								size={8}
							/>
						</AfFieldRow>
					</AfFieldGroup>
				</Col>
			</Row>

			<Row gutter={16} style={{ marginTop: 16 }}>
				<Col span={16}>
					<AfFieldGroup legend={t("building:label.description")}>
						<AfTextArea name="description" label="" rows={6} readOnly={disabled} size={24} />
					</AfFieldGroup>
				</Col>
			</Row>
		</div>
	);
}
