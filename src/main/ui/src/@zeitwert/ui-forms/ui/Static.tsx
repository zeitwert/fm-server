
import classNames from "classnames";
import { observer } from "mobx-react";
import { FC } from "react";
import { getFieldId } from "./Field";

export interface StaticProps {
	label?: string;
	value?: string;
	size?: number;
	error?: string;
	helpText?: string;
	align?: "left" | "center" | "right";
	readOnlyLook?: "static" | "plain"; // static: standard look with underline, plain: no underline
	isMultiline?: boolean;
}

export const Static: FC<StaticProps> = observer((props) => {
	const fieldId = getFieldId(props);
	const { label, value, size, error, align, readOnlyLook, isMultiline } = props;
	return (
		<div className={"slds-size_" + (size ? size + "-of-12" : "1-of-1")}>
			<div className={
				classNames(
					"slds-form-element slds-form-element_readonly",
					{
						"fa-form-element_readonly_plain": readOnlyLook === "plain",
						"slds-has-error": error,
						["slds-form-element-" + align]: align
					}
				)
			} >
				{label && <span className="slds-form-element__label">{label}</span>}
				<div className="slds-form-element__control">
					{
						isMultiline &&
						<div className="slds-form-element__static">
							<p style={{ whiteSpace: "pre-line" }} dangerouslySetInnerHTML={{ __html: value ? value : "<>&nbsp;</>" }} />
						</div>
					}
					{
						!isMultiline &&
						<div className={"slds-form-element__static slds-truncate" + (align ? " slds-form-element-" + align : "")}>
							{value ? value : <>&nbsp;</>}
						</div>
					}
					{
						error &&
						<div className="slds-form-element__help" id={fieldId + "-error"}>{error}</div>
					}
				</div>
			</div>
		</div >
	);

});
