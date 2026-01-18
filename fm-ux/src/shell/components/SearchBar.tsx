import { SearchOutlined } from "@ant-design/icons";
import { Input, theme } from "antd";
import { useTranslation } from "react-i18next";

const { useToken } = theme;

export function SearchBar() {
	const { t } = useTranslation("common");
	const { token } = useToken();

	return (
		<div style={{ width: 400 }}>
			<Input
				prefix={<SearchOutlined style={{ color: token.colorTextPlaceholder }} />}
				placeholder={t("searchPlaceholder")}
				size="middle"
				style={{
					borderRadius: 20,
					background: token.colorFillTertiary,
				}}
				variant="filled"
				aria-label="common:search"
			/>
		</div>
	);
}
