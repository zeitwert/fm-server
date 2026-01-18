import { ArrowLeftOutlined } from "@ant-design/icons";
import { Alert, Avatar, Button, Card, Col, Row, Spin, Typography } from "antd";
import { useTranslation } from "react-i18next";
import { getRestUrl } from "../../common/api/client";
import { useSessionStore } from "../model/sessionStore";
import { Enumerated, TypedEnumerated } from "../model/types";

// Helper functions to build logo URLs
const getTenantLogoUrl = (tenantId: string): string => {
	return getRestUrl("oe", `tenants/${tenantId}/logo`);
};

const getAccountLogoUrl = (accountId: string): string => {
	return getRestUrl("account", `accounts/${accountId}/logo`);
};

const { Title, Text } = Typography;

export function SelectionWizard() {
	const { t } = useTranslation("login");
	const { t: tCommon } = useTranslation("common");
	const {
		userInfo,
		selectedTenant,
		tenantInfo,
		selectedAccount,
		error,
		selectTenant,
		selectAccount,
		completeLogin,
		clearError,
		logout,
		goBackToTenantSelection,
	} = useSessionStore();

	// Determine current step
	const hasMultipleTenants = (userInfo?.tenants?.length ?? 0) > 1;
	const hasMultipleAccounts = (tenantInfo?.accounts?.length ?? 0) > 1;

	const showTenantSelection = !selectedTenant && hasMultipleTenants;
	const showAccountSelection = selectedTenant && hasMultipleAccounts && !selectedAccount;
	const isLoading = selectedTenant && !tenantInfo && !showAccountSelection;

	// Only allow going back to tenant selection if there are multiple tenants to choose from
	const canGoBackToTenantSelection = hasMultipleTenants;

	const handleTenantSelect = async (tenant: TypedEnumerated) => {
		await selectTenant(tenant);
	};

	const handleAccountSelect = async (account: Enumerated) => {
		selectAccount(account);
		await completeLogin();
	};

	const handleBack = () => {
		// Go back to tenant selection (not logout)
		goBackToTenantSelection();
	};

	// Determine title and hint based on current step
	const getTitle = () => {
		if (showTenantSelection) return t("selectTenant");
		if (showAccountSelection) return t("selectAccount");
		return tCommon("loading");
	};

	const getHint = () => {
		if (showTenantSelection) return t("selectTenantHint");
		if (showAccountSelection) return t("selectAccountHint");
		return "";
	};

	return (
		<div
			style={{
				minHeight: "100vh",
				display: "flex",
				justifyContent: "center",
				alignItems: "center",
				background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
				padding: "20px",
			}}
		>
			<Card
				style={{
					width: "100%",
					maxWidth: 600,
					boxShadow: "0 8px 32px rgba(0, 0, 0, 0.1)",
				}}
			>
				{/* Header */}
				<div style={{ textAlign: "center", marginBottom: 32 }}>
					<Title level={3} style={{ marginBottom: 8 }}>
						{getTitle()}
					</Title>
					<Text type="secondary">{getHint()}</Text>
				</div>

				{/* Error display */}
				{error && (
					<Alert
						message={error}
						type="error"
						showIcon
						closable
						onClose={clearError}
						style={{ marginBottom: 24 }}
					/>
				)}

				{/* Loading state */}
				{isLoading && (
					<div style={{ textAlign: "center", padding: "40px 0" }}>
						<Spin size="large" />
					</div>
				)}

				{/* Tenant Selection */}
				{showTenantSelection && (
					<Row gutter={[16, 16]}>
						{userInfo?.tenants.map((tenant) => (
							<Col xs={24} sm={12} key={tenant.id}>
								<Card
									hoverable
									onClick={() => handleTenantSelect(tenant)}
									style={{
										textAlign: "center",
										cursor: "pointer",
										border: "2px solid transparent",
										transition: "all 0.3s",
									}}
									bodyStyle={{ padding: "24px 16px" }}
								>
									<Avatar
										src={getTenantLogoUrl(tenant.id)}
										size={64}
										style={{ marginBottom: 12 }}
									/>
									<div>
										<Text strong style={{ fontSize: 16 }}>
											{tenant.name}
										</Text>
									</div>
								</Card>
							</Col>
						))}
					</Row>
				)}

				{/* Account Selection */}
				{showAccountSelection && (
					<>
						<Row gutter={[16, 16]}>
							{tenantInfo?.accounts.map((account) => (
								<Col xs={24} sm={12} key={account.id}>
									<Card
										hoverable
										onClick={() => handleAccountSelect(account)}
										style={{
											textAlign: "center",
											cursor: "pointer",
											border: "2px solid transparent",
											transition: "all 0.3s",
										}}
										bodyStyle={{ padding: "24px 16px" }}
									>
										<Avatar
											src={getAccountLogoUrl(account.id)}
											size={64}
											style={{ marginBottom: 12 }}
										/>
										<div>
											<Text strong style={{ fontSize: 16 }}>
												{account.name}
											</Text>
										</div>
									</Card>
								</Col>
							))}
						</Row>

						{/* Back button - only show if there are multiple tenants to go back to */}
						{canGoBackToTenantSelection && (
							<div style={{ marginTop: 24, textAlign: "center" }}>
								<Button
									icon={<ArrowLeftOutlined />}
									onClick={handleBack}
									aria-label="login:backToTenant"
								>
									{t("backToTenant")}
								</Button>
							</div>
						)}
					</>
				)}

				{/* Logout option - always available */}
				<div style={{ marginTop: 24, textAlign: "center" }}>
					<Button type="link" onClick={logout} aria-label="login:signInDifferent">
						{t("signInDifferent")}
					</Button>
				</div>
			</Card>
		</div>
	);
}
