import { Button, Space } from "antd";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import { useNavigate } from "@tanstack/react-router";
import { useQuery } from "@tanstack/react-query";
import {
	AfForm,
	AfInput,
	AfSelect,
	AfNumber,
	AfFieldGroup,
} from "../../../../common/components/form";
import { useCreateBuilding } from "../../queries";
import { buildingCreationSchema } from "../../schemas";
import type { CreateFormProps } from "../../../../common/components/items";
import { useSessionStore } from "../../../../session/model/sessionStore";
import { accountListApi } from "../../../account/api";

type BuildingCreationFormValues = ReturnType<typeof buildingCreationSchema.parse>;

export function BuildingCreationForm({ onSuccess, onCancel }: CreateFormProps) {
	const { t } = useTranslation();
	const navigate = useNavigate();
	const { sessionInfo } = useSessionStore();
	const createMutation = useCreateBuilding();

	const defaultOwner = sessionInfo?.user
		? { id: sessionInfo.user.id, name: sessionInfo.user.name }
		: null;

	const defaultAccount = sessionInfo?.account
		? { id: sessionInfo.account.id, name: sessionInfo.account.name }
		: null;

	const defaultCountry = { id: "ch", name: "Switzerland" };
	const defaultCurrency = { id: "chf", name: "CHF" };

	const { data: accounts = [] } = useQuery({
		queryKey: ["account", "list", "building-creation"],
		queryFn: () => accountListApi.list(),
	});

	const form = useForm<BuildingCreationFormValues>({
		resolver: zodResolver(buildingCreationSchema),
		defaultValues: {
			name: "",
			buildingNr: "",
			owner: defaultOwner,
			account: defaultAccount,
			insuredValue: 0,
			insuredValueYear: new Date().getFullYear(),
			street: "",
			zip: "",
			city: "",
			country: defaultCountry,
		},
	});

	const selectedAccount = form.watch("account");

	const handleSubmit = async (data: BuildingCreationFormValues) => {
		try {
			const createdBuilding = await createMutation.mutateAsync({
				name: data.name,
				buildingNr: data.buildingNr,
				owner: data.owner!,
				account: data.account!,
				insuredValue: data.insuredValue,
				insuredValueYear: data.insuredValueYear,
				street: data.street,
				zip: data.zip,
				city: data.city,
				country: data.country ?? defaultCountry,
				currency: defaultCurrency,
			});
			onSuccess();
			navigate({ to: "/building/$buildingId", params: { buildingId: createdBuilding.id } });
		} catch {
			// Error handling is done in useCreateBuilding's onError callback
		}
	};

	const accountOptions = accounts.map((a) => ({
		id: a.id,
		name: a.name,
	}));

	return (
		<AfForm form={form} onSubmit={handleSubmit}>
			<AfFieldGroup legend={t("building:label.accountSection")}>
				<AfSelect
					name="account"
					label={t("building:label.account")}
					options={accountOptions}
					required
					readOnly={!!selectedAccount?.id}
				/>
			</AfFieldGroup>

			<AfFieldGroup legend={t("building:label.basicInfo")}>
				<AfInput name="buildingNr" label={t("building:label.buildingNr")} required size={6} />
				<AfInput name="name" label={t("building:label.name")} required size={18} />
				<AfSelect name="owner" label={t("building:label.owner")} source="oe/objUser" required />
			</AfFieldGroup>

			<AfFieldGroup legend={t("building:label.valuation")}>
				<AfNumber
					name="insuredValue"
					label={t("building:label.insuredValueKCHF")}
					required
					size={16}
				/>
				<AfNumber
					name="insuredValueYear"
					label={t("building:label.insuredValueYear")}
					required
					formatNumber={false}
					size={8}
				/>
			</AfFieldGroup>

			<AfFieldGroup legend={t("building:label.address")}>
				<AfInput name="street" label={t("building:label.street")} />
				<AfInput name="zip" label={t("building:label.zip")} size={6} />
				<AfInput name="city" label={t("building:label.city")} size={18} />
				<AfSelect
					name="country"
					label={t("building:label.country")}
					source="oe/codeCountry"
					readOnly
				/>
			</AfFieldGroup>

			<div style={{ marginTop: 24, textAlign: "right" }}>
				<Space>
					<Button onClick={onCancel}>{t("common:action.cancel")}</Button>
					<Button
						type="primary"
						htmlType="submit"
						loading={createMutation.isPending}
						aria-label="building:create"
					>
						{t("common:action.create")}
					</Button>
				</Space>
			</div>
		</AfForm>
	);
}
