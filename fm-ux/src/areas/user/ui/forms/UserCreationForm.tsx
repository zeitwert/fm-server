import { Button, Space } from "antd";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { AfForm, AfInput, AfTextArea, AfSelect } from "../../../../common/components/form";
import { useCreateUser } from "../../queries";
import { userCreationSchema, type UserCreationFormInput } from "../../schemas";
import type { CreateFormProps } from "../../../../common/components/items";
import { useSessionStore } from "../../../../session/model/sessionStore";
import { KERNEL_TENANT } from "../../../../session/model/types";

export function UserCreationForm({ onSuccess, onCancel }: CreateFormProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const createMutation = useCreateUser();

	const isKernelTenant = sessionInfo?.tenant?.tenantType?.id === KERNEL_TENANT;

	const defaultOwner = sessionInfo?.user
		? { id: sessionInfo.user.id, name: sessionInfo.user.name }
		: null;

	const defaultTenant = sessionInfo?.tenant
		? { id: sessionInfo.tenant.id, name: sessionInfo.tenant.name }
		: null;

	const defaultRole = { id: "user", name: "User" };

	const form = useForm<UserCreationFormInput>({
		resolver: zodResolver(userCreationSchema),
		defaultValues: {
			tenant: defaultTenant,
			owner: defaultOwner,
			email: "",
			name: "",
			password: "",
			role: defaultRole,
			description: "",
		},
	});

	const handleSubmit = async (data: UserCreationFormInput) => {
		try {
			const createdUser = await createMutation.mutateAsync({
				tenant: data.tenant!,
				owner: data.owner!,
				email: data.email,
				name: data.name,
				password: data.password,
				role: data.role!,
				description: data.description,
			});
			onSuccess();
			navigate({ to: "/user/$userId", params: { userId: createdUser.id } });
		} catch {
			// Error handling is done in useCreateUser's onError callback
		}
	};

	return (
		<AfForm form={form} onSubmit={handleSubmit}>
			<AfSelect
				name="tenant"
				label={t("user:label.tenant")}
				source="oe/objTenant"
				required
				readOnly={!isKernelTenant}
			/>

			<AfSelect name="owner" label={t("user:label.owner")} source="oe/objUser" required />

			<AfInput name="email" label={t("user:label.email")} required />

			<AfSelect name="role" label={t("user:label.role")} source="oe/codeUserRole" required />

			<AfInput name="name" label={t("user:label.name")} required />

			<AfInput name="password" label={t("user:label.password")} required />

			<AfTextArea name="description" label={t("user:label.description")} rows={3} />

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{t("common:action.cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="user:create"
					>
						{t("common:action.create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
