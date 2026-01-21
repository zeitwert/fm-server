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
	const { t } = useTranslation("contact");
	const { t: tCommon } = useTranslation("common");
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
			form.setError("lastName", { message: "Nachname ist erforderlich" });
			hasError = true;
		}
		if (!data.salutation) {
			form.setError("salutation", { message: "Anrede ist erforderlich" });
			hasError = true;
		}
		if (!data.tenant) {
			form.setError("tenant", { message: "Mandant ist erforderlich" });
			hasError = true;
		}
		if (!data.owner) {
			form.setError("owner", { message: "Verantwortlich ist erforderlich" });
			hasError = true;
		}

		if (hasError) {
			message.error("Bitte füllen Sie alle Pflichtfelder aus");
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
				label={t("salutation")}
				source="contact/codeSalutation"
				required
			/>

			<AfInput name="firstName" label={t("firstName")} />

			<AfInput name="lastName" label={t("lastName")} required />

			<AfSelect name="contactRole" label={t("contactRole")} source="contact/codeContactRole" />

			<AfInput name="email" label={t("email")} />

			<AfInput name="mobile" label={t("mobile")} />

			<AfInput name="phone" label={t("phone")} />

			{isKernelTenant && (
				<AfSelect name="tenant" label={t("tenant")} source="oe/objTenant" required />
			)}

			<AfSelect name="owner" label={t("owner")} source="oe/objUser" required />

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{tCommon("cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="contact:create"
					>
						{tCommon("create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
