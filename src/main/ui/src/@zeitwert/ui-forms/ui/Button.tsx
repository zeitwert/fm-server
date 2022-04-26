
import { SLDSButton } from "@salesforce/design-system-react";
import { FC } from "react";

export interface ButtonProps {
	name: string;
	label: string;
	type?: "reset" | "submit" | "button";
	variant?: "base" | "link" | "neutral" | "brand" | "outline-brand" | "destructive" | "success" | "text-destructive" | "icon";
	disabled?: boolean;
	onClick?: () => void;
}

const Button: FC<ButtonProps> = (props) => {
	const field: any = undefined;
	return (
		<SLDSButton {...field} {...props} />
	);
};

export default Button;
