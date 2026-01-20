import { Card } from "antd";
import type { CSSProperties, ReactNode } from "react";

interface DashboardCardProps {
	title: ReactNode;
	children?: ReactNode;
	bodyStyle?: CSSProperties;
	cardStyle?: CSSProperties;
	headStyle?: CSSProperties;
}

export function DashboardCard({
	title,
	children,
	bodyStyle,
	cardStyle,
	headStyle,
}: DashboardCardProps) {
	return (
		<Card
			title={title}
			style={{
				height: "100%",
				display: "flex",
				flexDirection: "column",
				border: "1px solid #d9d9d9",
				...cardStyle,
			}}
			styles={{
				header: {
					padding: "6px 16px",
					minHeight: 0,
					...headStyle,
				},
				body: {
					flex: 1,
					overflow: "auto",
					background: "#ffffff",
					...bodyStyle,
				},
			}}
		>
			{children ?? <div style={{ height: "100%" }} />}
		</Card>
	);
}
