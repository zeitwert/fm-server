import { Spin } from "antd";
import { useTranslation } from "react-i18next";
import { DashboardCard } from "./components/DashboardCard";
import { BuildingMap } from "./components/BuildingMap";
import { useHomeMapBuildings } from "../model";
import { useSessionStore } from "@/session/model/sessionStore";

export function HomeCardMap() {
	const { t } = useTranslation();
	const accountId = useSessionStore((state) => state.sessionInfo?.account?.id);
	const { data, isLoading } = useHomeMapBuildings(accountId);
	const buildings = data?.buildings ?? [];

	return (
		<DashboardCard
			title={t("home:label.mapTitle")}
			bodyStyle={{ padding: 0, background: "transparent", borderRadius: 0 }}
		>
			<div style={{ height: "100%", position: "relative" }}>
				{buildings.length > 0 && <BuildingMap buildings={buildings} />}
				{isLoading && (
					<div
						style={{
							position: "absolute",
							inset: 0,
							display: "flex",
							alignItems: "center",
							justifyContent: "center",
						}}
					>
						<Spin />
					</div>
				)}
			</div>
		</DashboardCard>
	);
}
