import { Col, Empty, Row, Spin, Typography } from 'antd';
import { DashboardCard } from './components/DashboardCard';
import { useHomeOverview } from '../model';
import { useSessionStore } from '../../../session/model/sessionStore';
import { getRestUrl } from '../../../common/api/client';

const NUMBER_FORMAT = new Intl.NumberFormat('de-DE', { maximumFractionDigits: 0 });

function formatNumber(value?: number | null) {
	if (value === undefined || value === null) return null;
	return NUMBER_FORMAT.format(value);
}

function FactRow({
	value,
	singular,
	plural,
	url,
}: {
	value?: number | null;
	singular: string;
	plural?: string;
	url?: string;
}) {
	if (value === undefined || value === null) return null;
	const label = value === 1 ? singular : plural ?? singular;

	return (
		<Row style={{ width: '100%' }} align="middle">
			<Col span={8} style={{ textAlign: 'right' }}>
				<Typography.Text strong style={{ fontSize: 18 }}>
					{formatNumber(value)}
				</Typography.Text>
			</Col>
			<Col span={16} style={{ paddingLeft: 8 }}>
				<Typography.Text>
					{url ? <a href={url}>{label}</a> : label}
				</Typography.Text>
			</Col>
		</Row>
	);
}

export function HomeCardOverview() {
	const accountId = useSessionStore((state) => state.sessionInfo?.account?.id);
	const { data, isLoading } = useHomeOverview(accountId);

	const accountLogoUrl = data?.accountId
		? getRestUrl('account', `accounts/${data.accountId}/logo`)
		: null;

	return (
		<DashboardCard title={data?.accountName ?? 'Übersicht'}>
			<div style={{ height: '100%', position: 'relative', padding: 12 }}>
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
				{!isLoading && !data && <Empty description="Keine Übersicht verfügbar." />}
				{!isLoading && data && (
					<div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
						{accountLogoUrl && (
							<div style={{ display: 'flex', justifyContent: 'center' }}>
								<img
									key={data.accountId}
									src={accountLogoUrl}
									alt="Account logo"
									style={{ maxWidth: '100%', maxHeight: 80, objectFit: 'contain' }}
								/>
							</div>
						)}
						<div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
							<FactRow value={data.buildingCount} singular="Immobilie" plural="Immobilien" url="/building" />
							<FactRow value={data.portfolioCount} singular="Portfolio" plural="Portfolios" url="/portfolio" />
							<FactRow value={data.ratingCount} singular="Bewertung" plural="Bewertungen" />
							<FactRow value={data.insuranceValue} singular="kCHF Versicherungswert" />
							<FactRow value={data.timeValue} singular="kCHF Zeitwert" />
							<FactRow value={data.shortTermRenovationCosts} singular="kCHF IS kurzfristig" />
							<FactRow value={data.midTermRenovationCosts} singular="kCHF IS mittelfristig" />
						</div>
					</div>
				)}
			</div>
		</DashboardCard>
	);
}
