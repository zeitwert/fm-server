import { Button, Space } from "antd";
import { useForm } from "react-hook-form";
import { zodResolver } from "@/common/utils/zodResolver";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { AfForm, AfInput, AfTextArea, AfSelect } from "@/common/components/form";
import { useCreatePortfolio } from "../../queries";
import { portfolioCreationSchema, type PortfolioCreationFormInput } from "../../schemas";
import type { CreateFormProps } from "@/common/components/items";
import { useSessionStore } from "@/session/model/sessionStore";

export function PortfolioCreationForm({ onSuccess, onCancel }: CreateFormProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const createMutation = useCreatePortfolio();

	const defaultOwner = sessionInfo?.user
		? { id: sessionInfo.user.id, name: sessionInfo.user.name }
		: null;

	const form = useForm<PortfolioCreationFormInput>({
		resolver: zodResolver(portfolioCreationSchema),
		defaultValues: {
			name: "",
			portfolioNr: "",
			description: "",
			owner: defaultOwner,
		},
	});

	const handleSubmit = async (data: PortfolioCreationFormInput) => {
		try {
			const createdPortfolio = await createMutation.mutateAsync({
				name: data.name,
				portfolioNr: data.portfolioNr,
				description: data.description,
				owner: data.owner!,
				includes: [],
				excludes: [],
				buildings: [],
			});
			onSuccess();
			navigate({ to: "/portfolio/$portfolioId", params: { portfolioId: createdPortfolio.id } });
		} catch {
			// Error handling is done in useCreatePortfolio's onError callback
		}
	};

	return (
		<AfForm form={form} onSubmit={handleSubmit}>
			<AfInput name="name" label={t("portfolio:label.name")} required />

			<AfInput name="portfolioNr" label={t("portfolio:label.portfolioNr")} />

			<AfSelect name="owner" label={t("portfolio:label.owner")} source="oe/objUser" required />

			<AfTextArea name="description" label={t("portfolio:label.description")} rows={3} />

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{t("common:action.cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="portfolio:create"
					>
						{t("common:action.create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
