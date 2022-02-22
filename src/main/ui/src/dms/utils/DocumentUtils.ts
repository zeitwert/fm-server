
const IconMap = {
	unknown: "doctype:unknown",
	url: "doctype:link",
	pdf: "doctype:pdf",
	doc: "doctype:word",
	docx: "doctype:word",
	xls: "doctype:excel",
	xlsx: "doctype:excel",
	ppt: "doctype:ppt",
	pptx: "doctype:ppt",
	form: "standard:display_text",
	questionnaire: "standard:question_feed",
	calculator: "standard:number_input"
};

const DEFAULT_ICON_TYPE = "unknown";

export const DocumentUtils = {
	fullIconName(contentTypeId: string | undefined) {
		return IconMap[contentTypeId || DEFAULT_ICON_TYPE] || IconMap[DEFAULT_ICON_TYPE];
	}
};
