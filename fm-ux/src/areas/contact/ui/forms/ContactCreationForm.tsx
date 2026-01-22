import { Button, Space, message } from "antd";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { AfForm, AfInput, AfSelect } from "../../../../common/components/form";
import { useCreateContact } from "../../queries";
import type { ContactCreationFormInput } from "../../schemas";
import type { CreateFormProps } from "../../../../common/components/items";
import { useSessionStore } from "../../../../session/model/sessionStore";
import { KERNEL_TENANT } from "../../../../session/model/types";

interface ContactCreationFormProps extends CreateFormProps {
	account?: { id: string; name: string };
}

export function ContactCreationForm({ onSuccess, onCancel, account }: ContactCreationFormProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const createMutation = useCreateContact();
	const isKernelTenant = sessionInfo?.tenant?.tenantType?.id === KERNEL_TENANT;
	const defaultTenant =
		!isKernelTenant && sessionInfo?.tenant
			? { id: sessionInfo.tenant.id, name: sessionInfo.tenant.name }
			: null;
	const defaultOwner = sessionInfo?.user
		? { id: sessionInfo.user.id, name: sessionInfo.user.name }
		: null;

	const form = useForm<ContactCreationFormInput>({
		defaultValues: {
			firstName: "",
			lastName: "",
			email: "",
			phone: "",
			mobile: "",
			salutation: null,
			contactRole: null,
			account: account ?? null,
			tenant: defaultTenant,
			owner: defaultOwner,
		},
	});

	const handleSubmit = async (data: ContactCreationFormInput) => {
		let hasError = false;

		if (!data.lastName?.trim()) {
			form.setError("lastName", { message: t("contact:message.validation.lastNameRequired") });
			hasError = true;
		}
		if (!data.salutation) {
			form.setError("salutation", { message: t("contact:message.validation.salutationRequired") });
			hasError = true;
		}
		if (!data.tenant) {
			form.setError("tenant", { message: t("contact:message.validation.tenantRequired") });
			hasError = true;
		}
		if (!data.owner) {
			form.setError("owner", { message: t("contact:message.validation.ownerRequired") });
			hasError = true;
		}

		if (hasError) {
			message.error(t("contact:message.validation.fillRequiredFields"));
			return;
		}

		try {
			const createdContact = await createMutation.mutateAsync({
				firstName: data.firstName,
				lastName: data.lastName,
				email: data.email,
				phone: data.phone,
				mobile: data.mobile,
				salutation: data.salutation!,
				contactRole: data.contactRole ?? undefined,
				account: data.account ? { id: data.account.id, caption: data.account.name } : undefined,
				tenant: data.tenant!,
				owner: data.owner!,
			});
			onSuccess();
			navigate({ to: "/contact/$contactId", params: { contactId: createdContact.id } });
		} catch {
			// Error handling is done in useCreateContact's onError callback
		}
	};

	return (
		<AfForm form={form} onSubmit={handleSubmit}>
			<AfSelect
				name="salutation"
				label={t("contact:label.salutation")}
				source="contact/codeSalutation"
				required
			/>

			<AfInput name="firstName" label={t("contact:label.firstName")} />

			<AfInput name="lastName" label={t("contact:label.lastName")} required />

			<AfSelect
				name="contactRole"
				label={t("contact:label.contactRole")}
				source="contact/codeContactRole"
			/>

			<AfInput name="email" label={t("contact:label.email")} />

			<AfInput name="mobile" label={t("contact:label.mobile")} />

			<AfInput name="phone" label={t("contact:label.phone")} />

			{isKernelTenant && (
				<AfSelect name="tenant" label={t("contact:label.tenant")} source="oe/objTenant" required />
			)}

			<AfSelect name="owner" label={t("contact:label.owner")} source="oe/objUser" required />

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{t("common:action.cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="contact:create"
					>
						{t("common:action.create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
