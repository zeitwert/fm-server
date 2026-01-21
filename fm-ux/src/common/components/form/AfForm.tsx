import { Form } from "antd";
import { FormProvider, UseFormReturn, FieldValues } from "react-hook-form";
import type { FormProps } from "antd";
import type { ReactNode } from "react";

interface AfFormProps<T extends FieldValues = FieldValues> {
	/** React Hook Form instance */
	form: UseFormReturn<T>;
	/** Submit handler - if provided, wraps content in HTML form element */
	onSubmit?: (data: T) => void | Promise<void>;
	/** Ant Design Form layout (default: "vertical") */
	layout?: FormProps["layout"];
	/** Form content */
	children: ReactNode;
	/** Additional Ant Design Form props */
	formProps?: Omit<FormProps, "layout" | "form" | "onFinish">;
	/** HTML form element props (className, style, etc.) */
	htmlFormProps?: React.FormHTMLAttributes<HTMLFormElement>;
}

/**
 * Standardized form wrapper combining React Hook Form, Ant Design Form, and HTML form.
 *
 * Features:
 * - Wraps with FormProvider (React Hook Form context)
 * - Wraps with Ant Design Form with vertical layout by default
 * - Optionally includes HTML form element with submit handler
 *
 * @example
 * // Form without submit (ItemPage pattern)
 * <AfForm form={form}>
 *   <AccountMainForm disabled={!isEditing} />
 * </AfForm>
 *
 * @example
 * // Form with submit (creation forms)
 * <AfForm form={form} onSubmit={handleSubmit}>
 *   <AfInput name="name" label="Name" required />
 *   <Button htmlType="submit">Submit</Button>
 * </AfForm>
 */
export function AfForm<T extends FieldValues = FieldValues>({
	form,
	onSubmit,
	layout = "vertical",
	children,
	formProps,
	htmlFormProps,
}: AfFormProps<T>) {
	const handleSubmit = onSubmit
		? form.handleSubmit(async (data) => {
				await onSubmit(data);
			})
		: undefined;

	const content = onSubmit ? (
		<form onSubmit={handleSubmit} {...htmlFormProps}>
			{children}
		</form>
	) : (
		children
	);

	return (
		<FormProvider {...form}>
			<Form layout={layout} component={onSubmit ? false : undefined} {...formProps}>
				{content}
			</Form>
		</FormProvider>
	);
}
