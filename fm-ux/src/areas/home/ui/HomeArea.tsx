import { Col, Row } from "antd";
import { HomeCardMap } from "./HomeCardMap";
import { HomeCardOpenActivities } from "./HomeCardOpenActivities";
import { HomeCardOverview } from "./HomeCardOverview";
import { HomeCardRecentActions } from "./HomeCardRecentActions";
import { HomeCardStatistics } from "./HomeCardStatistics";

const GRID_GUTTER = 16;

export function HomeArea() {
	return (
		<div
			style={{
				height: "100%",
				overflow: "hidden",
			}}
		>
			<Row gutter={GRID_GUTTER} wrap={false} style={{ height: "100%" }}>
				<Col span={12} style={{ height: "100%" }}>
					<HomeCardMap />
				</Col>
				<Col span={12} style={{ height: "100%" }}>
					<div
						style={{
							display: "flex",
							flexDirection: "column",
							height: "100%",
							gap: GRID_GUTTER,
						}}
					>
						<Row gutter={GRID_GUTTER} wrap={false} style={{ flex: 1, minHeight: 0 }}>
							<Col span={12} style={{ height: "100%" }}>
								<HomeCardOpenActivities />
							</Col>
							<Col span={12} style={{ height: "100%" }}>
								<HomeCardOverview />
							</Col>
						</Row>
						<Row gutter={GRID_GUTTER} wrap={false} style={{ flex: 1, minHeight: 0 }}>
							<Col span={12} style={{ height: "100%" }}>
								<HomeCardRecentActions />
							</Col>
							<Col span={12} style={{ height: "100%" }}>
								<HomeCardStatistics />
							</Col>
						</Row>
					</div>
				</Col>
			</Row>
		</div>
	);
}
