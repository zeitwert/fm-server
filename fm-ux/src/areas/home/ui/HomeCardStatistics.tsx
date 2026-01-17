import { Col, Row, Typography } from 'antd';
import { Cell, Pie, PieChart, ResponsiveContainer } from 'recharts';
import { DashboardCard } from './components/DashboardCard';

const CHART_DATA = [
	{ name: 'gut', value: 21, color: '#52c41a' },
	{ name: 'mittel', value: 53, color: '#b7eb8f' },
	{ name: 'schlecht', value: 17, color: '#fa8c16' },
	{ name: 'sehr schlecht', value: 9, color: '#f5222d' },
];

export function HomeCardStatistics() {
	return (
		<DashboardCard title="Auswertung des Tages">
			<div style={{ height: '100%', padding: 12 }}>
				<Row align="middle" style={{ marginBottom: 12 }}>
					<Col span={12}>
						<Typography.Text strong>Gebaeudezustand</Typography.Text>
					</Col>
					<Col span={12} style={{ textAlign: 'right' }}>
						<Typography.Text type="secondary">Z/N Portfolio</Typography.Text>
						<div>
							<Typography.Text strong style={{ fontSize: 18 }}>
								0.79
							</Typography.Text>
						</div>
					</Col>
				</Row>
				<div style={{ width: '100%', height: 220 }}>
					<ResponsiveContainer>
						<PieChart>
							<Pie
								data={CHART_DATA}
								dataKey="value"
								nameKey="name"
								innerRadius="60%"
								outerRadius="90%"
								stroke="none"
							>
								{CHART_DATA.map((entry) => (
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
