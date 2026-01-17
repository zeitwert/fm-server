export interface HomeOverview {
	accountId: number;
	accountName: string;
	buildingCount?: number | null;
	portfolioCount?: number | null;
	insuranceValue?: number | null;
	timeValue?: number | null;
	shortTermRenovationCosts?: number | null;
	midTermRenovationCosts?: number | null;
	ratingCount?: number | null;
}
