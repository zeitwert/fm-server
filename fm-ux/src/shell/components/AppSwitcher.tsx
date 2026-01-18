import { Flex, Segmented, theme } from "antd";
import { useMemo } from "react";
import { useTranslation } from "react-i18next";
import { ApplicationMap } from "../../app/config/AppConfig";
import { useSessionStore } from "../../session/model/sessionStore";

const { useToken } = theme;

interface AppSwitcherProps {
	collapsed?: boolean;
}

export function AppSwitcher({ collapsed = false }: AppSwitcherProps) {
	const { t } = useTranslation("app");
	const { token } = useToken();
	const { sessionInfo, switchApplication } = useSessionStore();

	// Get available applications
	const availableApplications = useMemo(
		() => sessionInfo?.availableApplications ?? [],
		[sessionInfo?.availableApplications]
	);

	// Get current application for collapsed display
	const currentApp = sessionInfo?.applicationId ? ApplicationMap[sessionInfo.applicationId] : null;

	// Build segmented options from available applications
	const appSegmentOptions = useMemo(() => {
		return availableApplications
			.map((appId) => {
				const app = ApplicationMap[appId];
				if (!app) return null;
				return {
					value: appId,
					label: t(app.shortName),
				};
			})
			.filter((option): option is { value: string; label: string } => option !== null);
	}, [availableApplications, t]);

	// Only render if multiple apps available
	const hasMultipleApps = availableApplications.length > 1;
	if (!hasMultipleApps) {
		return null;
	}

	return (
		<Flex
			justify="center"
			style={{
				padding: collapsed ? "12px 0" : "12px 8px",
				borderBottom: `1px solid ${token.colorBorderSecondary}`,
			}}
			data-testid="app-switcher"
		>
			{collapsed ? (
				// Show two-letter app key when collapsed
				<div
					style={{
						width: 40,
						height: 32,
						display: "flex",
						alignItems: "center",
						justifyContent: "center",
						fontWeight: 600,
						fontSize: 14,
						color: token.colorPrimary,
						background: token.colorPrimaryBg,
						borderRadius: token.borderRadius,
					}}
					data-testid="app-switcher-badge"
				>
					{currentApp?.appKey}
				</div>
			) : (
				<Segmented
					value={sessionInfo?.applicationId}
					options={appSegmentOptions}
					onChange={(value) => switchApplication(value as string)}
					block
					style={{ width: "100%" }}
					data-testid="app-switcher-segmented"
				/>
			)}
		</Flex>
	);
}
