import { Col, Row, Typography } from "antd";
import { useTranslation } from "react-i18next";
import { Cell, Pie, PieChart, ResponsiveContainer } from "recharts";
import { DashboardCard } from "./components/DashboardCard";

export function HomeCardStatistics() {
	const { t } = useTranslation("home");

	const chartData = [
		{ name: t("conditionGood"), value: 21, color: "#52c41a" },
		{ name: t("conditionMedium"), value: 53, color: "#b7eb8f" },
		{ name: t("conditionBad"), value: 17, color: "#fa8c16" },
		{ name: t("conditionVeryBad"), value: 9, color: "#f5222d" },
	];

	return (
		<DashboardCard title={t("statistics")}>
			<div style={{ height: "100%", padding: 12 }}>
				<Row align="middle" style={{ marginBottom: 12 }}>
					<Col span={12}>
						<Typography.Text strong>{t("buildingCondition")}</Typography.Text>
					</Col>
					<Col span={12} style={{ textAlign: "right" }}>
						<Typography.Text type="secondary">{t("znPortfolio")}</Typography.Text>
						<div>
							<Typography.Text strong style={{ fontSize: 18 }}>
								0.79
							</Typography.Text>
						</div>
					</Col>
				</Row>
				<div style={{ width: "100%", height: 220 }}>
					<ResponsiveContainer>
						<PieChart>
							<Pie
								data={chartData}
								dataKey="value"
								nameKey="name"
								innerRadius="60%"
								outerRadius="90%"
								stroke="none"
							>
								{chartData.map((entry) => (
									<Cell key={entry.name} fill={entry.color} />
								))}
							</Pie>
						</PieChart>
					</ResponsiveContainer>
				</div>
			</div>
		</DashboardCard>
	);
}
