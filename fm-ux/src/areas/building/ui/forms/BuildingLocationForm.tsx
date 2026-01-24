import { useEffect, useMemo } from "react";
import { Col, Row, Card, Typography, Alert } from "antd";
import { EnvironmentOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";
import { useFormContext } from "react-hook-form";
import { AfInput, AfSelect, AfFieldRow, AfFieldGroup } from "../../../../common/components/form";
import { BuildingMap } from "../../../home/ui/components/BuildingMap";
import type { BuildingInfo } from "../../../home/model";
import { geocodeAddress } from "../../api";

const { Text } = Typography;

interface BuildingLocationFormProps {
	disabled: boolean;
}

export function BuildingLocationForm({ disabled }: BuildingLocationFormProps) {
	const { t } = useTranslation();
	const { watch, setValue } = useFormContext();

	const street = watch("street");
	const zip = watch("zip");
	const city = watch("city");
	const country = watch("country");
	const geoAddress = watch("geoAddress");
	const geoCoordinates = watch("geoCoordinates");
	const geoZoom = watch("geoZoom");

	// Auto-geocode when editing and address changes
	useEffect(() => {
		// Only geocode when editing (not disabled)
		if (disabled) return;

		// Check if ready for geocoding: either geoAddress is set, or zip+city+country are set
		const isReady = geoAddress || (zip && city && country);
		if (!isReady) return;

		const timer = setTimeout(async () => {
			const result = await geocodeAddress({
				street: street ?? undefined,
				zip: zip ?? undefined,
				city: city ?? undefined,
				country: country?.name ?? undefined,
				geoAddress: geoAddress ?? undefined,
			});
			if (result) {
				setValue("geoCoordinates", result.geoCoordinates, { shouldDirty: true });
				setValue("geoZoom", result.geoZoom, { shouldDirty: true });
			}
		}, 500);

		return () => clearTimeout(timer);
	}, [disabled, street, zip, city, country, geoAddress, setValue]);

	// Parse coordinates for map display
	const buildingInfo: BuildingInfo | undefined = useMemo(() => {
		if (!geoCoordinates?.startsWith("WGS:")) return undefined;

		const [latRaw, lngRaw] = geoCoordinates.substring(4).split(",");
		const lat = Number.parseFloat(latRaw ?? "");
		const lng = Number.parseFloat(lngRaw ?? "");

		if (Number.isNaN(lat) || Number.isNaN(lng)) return undefined;

		const addressParts = [street, [zip, city].filter(Boolean).join(" ")].filter(Boolean).join(", ");

		return {
			id: "current",
			name: addressParts || t("building:label.entity"),
			address: addressParts,
			lat,
			lng,
		};
	}, [geoCoordinates, street, zip, city, t]);

	return (
		<Row gutter={16}>
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
						<AfInput name="zip" label={t("building:label.zip")} readOnly={disabled} size={8} />
						<AfInput name="city" label={t("building:label.city")} readOnly={disabled} size={16} />
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

				<AfFieldGroup legend={t("building:label.alternativeGeoAddress")}>
					<AfFieldRow>
						<AfInput
							name="geoAddress"
							label={t("building:label.geoAddress")}
							readOnly={disabled}
							size={24}
						/>
					</AfFieldRow>
					<Alert
						message={t("building:message.geoAddressHelp")}
						type="info"
						showIcon
						style={{ marginTop: 8 }}
					/>
				</AfFieldGroup>
			</Col>

			<Col span={16}>
				<Card
					style={{ height: "100%", minHeight: 400 }}
					styles={{ body: { height: "100%", display: "flex", flexDirection: "column" } }}
				>
					{buildingInfo ? (
						<div style={{ flex: 1, minHeight: 380 }}>
							<BuildingMap buildings={[buildingInfo]} zoom={geoZoom ?? undefined} />
						</div>
					) : (
						<div
							style={{
								flex: 1,
								display: "flex",
								flexDirection: "column",
								alignItems: "center",
								justifyContent: "center",
								backgroundColor: "#fafafa",
								borderRadius: 4,
							}}
						>
							<EnvironmentOutlined style={{ fontSize: 48, color: "#d9d9d9", marginBottom: 16 }} />
							<Text type="secondary">{t("building:message.enterAddressForMap")}</Text>
						</div>
					)}
				</Card>
			</Col>
		</Row>
	);
}
