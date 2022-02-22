export enum ChannelType {
	CHAT = "chat",
	MEETING = "meeting",
	CALL = "call",
	VISIT = "visit",
	WHATSAPP = "whatsapp",
	EMAIL = "email"
}

export interface Channel {
	label: string;
	type: ChannelType;
	iconCategory: any;
	iconName: string;
	isReplyable: boolean;
}

export const channels = [
	{
		label: "Chat",
		type: ChannelType.CHAT,
		iconCategory: "standard",
		iconName: "feedback",
		isReplyable: true
	},
	{
		label: "Meeting",
		type: ChannelType.MEETING,
		iconCategory: "standard",
		iconName: "messaging_conversation",
		isReplyable: false
	},
	{
		label: "Call",
		type: ChannelType.CALL,
		iconCategory: "standard",
		iconName: "call",
		isReplyable: false
	},
	{
		label: "Visit",
		type: ChannelType.VISIT,
		iconCategory: "standard",
		iconName: "customers",
		isReplyable: false
	},
	{
		label: "Whatsapp",
		type: ChannelType.WHATSAPP,
		iconCategory: "finadvise",
		iconName: "whatsapp",
		isReplyable: false
	},
	{
		label: "Email",
		type: ChannelType.EMAIL,
		iconCategory: "standard",
		iconName: "email",
		isReplyable: true
	}
] as Channel[];
