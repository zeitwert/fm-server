import { Spin } from 'antd';
import { DashboardCard } from './components/DashboardCard';
import { BuildingMap } from './components/BuildingMap';
import { useHomeMapBuildings } from '../model';

export function HomeCardMap() {
	const { data, isLoading } = useHomeMapBuildings();
	const buildings = data?.buildings ?? [];

	return (
		<DashboardCard
			title="Übersicht Bestand"
			bodyStyle={{ padding: 0, background: 'transparent', borderRadius: 0 }}
		>
			<div style={{ height: '100%', position: 'relative' }}>
				{buildings.length > 0 && <BuildingMap buildings={buildings} />}
				{isLoading && (
					<div
						style={{
							position: 'absolute',
							inset: 0,
							display: 'flex',
							alignItems: 'center',
							justifyContent: 'center',
						}}
					>
						<Spin />
					</div>
				)}
			</div>
		</DashboardCard>
	);
}
