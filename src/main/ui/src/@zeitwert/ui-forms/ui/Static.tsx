
import classNames from "classnames";
import { observer } from "mobx-react";
import { FC } from "react";

export interface StaticProps {
	label?: string;
	value?: string;
	size?: number;
	error?: string;
	helpText?: string;
	align?: "left" | "center" | "right";
	readOnlyLook?: "static" | "plain"; // static: standard look with underline, plain: no underline
}

export const Static: FC<StaticProps> = observer((props) => {
	const { label, value, size, error, align, readOnlyLook } = props;
	return (
		<div className={"slds-size_" + (size ? size + "-of-12" : "1-of-1")}>
			<div className={classNames("slds-form-element slds-form-element_readonly", readOnlyLook === "plain" ? "fa-form-element_readonly_plain" : "", error ? "slds-has-error" : "", align ? "slds-form-element-" + align : "")} >
				{label && <span className="slds-form-element__label">{label}</span>}
				<div className="slds-form-element__control">
					<div className="slds-form-element__static slds-truncate">
						{value && value}
						{!value && <>&nbsp;</>}
					</div>
				</div>
			</div>
		</div >
	);

});
