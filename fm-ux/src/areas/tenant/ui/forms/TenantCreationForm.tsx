import { Button, Space } from "antd";
import { useForm } from "react-hook-form";
import { zodResolver } from "@/common/utils/zodResolver";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { AfForm, AfInput, AfTextArea, AfSelect } from "@/common/components/form";
import { useCreateTenant } from "../../queries";
import { tenantCreationSchema, type TenantCreationFormInput } from "../../schemas";
import type { CreateFormProps } from "@/common/components/items";

export function TenantCreationForm({ onSuccess, onCancel }: CreateFormProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const createMutation = useCreateTenant();

	const form = useForm<TenantCreationFormInput>({
		resolver: zodResolver(tenantCreationSchema),
		defaultValues: {
			name: "",
			description: "",
			tenantType: null,
		},
	});

	const handleSubmit = async (data: TenantCreationFormInput) => {
		try {
			const createdTenant = await createMutation.mutateAsync({
				name: data.name,
				description: data.description,
				tenantType: data.tenantType!,
			});
			onSuccess();
			navigate({ to: "/tenant/$tenantId", params: { tenantId: createdTenant.id } });
		} catch {
			// Error handling is done in useCreateTenant's onError callback
		}
	};

	return (
		<AfForm form={form} onSubmit={handleSubmit}>
			<AfSelect
				name="tenantType"
				label={t("tenant:label.tenantType")}
				source="oe/codeTenantType"
				required
			/>

			<AfInput name="name" label={t("tenant:label.name")} required />

			<AfTextArea name="description" label={t("tenant:label.description")} rows={3} />

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{t("common:action.cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="tenant:create"
					>
						{t("common:action.create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
