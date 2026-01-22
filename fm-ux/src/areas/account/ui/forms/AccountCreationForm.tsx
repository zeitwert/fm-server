import { Button, Space, message } from "antd";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { AfForm, AfInput, AfTextArea, AfSelect } from "../../../../common/components/form";
import { useCreateAccount } from "../../queries";
import type { AccountCreationFormInput } from "../../schemas";
import type { CreateFormProps } from "../../../../common/components/items";
import { useSessionStore } from "../../../../session/model/sessionStore";

export function AccountCreationForm({ onSuccess, onCancel }: CreateFormProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const createMutation = useCreateAccount();
	const defaultOwner = sessionInfo?.user
		? { id: sessionInfo.user.id, name: sessionInfo.user.name }
		: null;

	const form = useForm<AccountCreationFormInput>({
		defaultValues: {
			name: "",
			description: "",
			accountType: null,
			clientSegment: null,
			owner: defaultOwner,
		},
	});

	const handleSubmit = async (data: AccountCreationFormInput) => {
		let hasError = false;

		if (!data.name?.trim()) {
			form.setError("name", { message: t("account:message.validation.nameRequired") });
			hasError = true;
		}
		if (!data.accountType) {
			form.setError("accountType", {
				message: t("account:message.validation.accountTypeRequired"),
			});
			hasError = true;
		}
		if (!data.owner) {
			form.setError("owner", { message: t("account:message.validation.ownerRequired") });
			hasError = true;
		}

		if (hasError) {
			message.error(t("account:message.validation.fillRequiredFields"));
			return;
		}

		try {
			const createdAccount = await createMutation.mutateAsync({
				name: data.name,
				description: data.description,
				accountType: data.accountType!,
				clientSegment: data.clientSegment ?? undefined,
				owner: data.owner!,
			});
			onSuccess();
			navigate({ to: "/account/$accountId", params: { accountId: createdAccount.id } });
		} catch {
			// Error handling is done in useCreateAccount's onError callback
		}
	};

	return (
		<AfForm form={form} onSubmit={handleSubmit}>
			<AfInput name="name" label={t("account:label.name")} required />

			<AfSelect name="owner" label={t("account:label.owner")} source="oe/objUser" required />

			<AfSelect
				name="accountType"
				label={t("account:label.accountType")}
				source="account/codeAccountType"
				required
			/>

			<AfSelect
				name="clientSegment"
				label={t("account:label.clientSegment")}
				source="account/codeClientSegment"
			/>

			<AfTextArea name="description" label={t("account:label.description")} rows={3} />

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{t("common:action.cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="account:create"
					>
						{t("common:action.create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
