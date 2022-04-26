import { FormApi } from "@finadvise/forms";
import { Card, ExpandableSection, Icon, MediaObject, Modal } from "@salesforce/design-system-react";
import { FormWrapper } from "@zeitwert/ui-forms";
import { Contact, ContactStore, EntityType, EntityTypes, FORM_API, LifeEvent } from "@zeitwert/ui-model";
import { Timeline } from "@zeitwert/ui-slds/timeline/Timeline";
import { AppCtx } from "App";
import { ItemEditorButtons } from "item/ui/ItemEditorButtons";
import { action, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import LifeEventTimelineItem from "./components/LifeEventTimelineItem";

interface LifeEventProps {
	store: ContactStore;
	isPopupOpen: boolean;
	isCreateMode: boolean;
	cancelLifeEventEditor: () => Promise<void>;
	closeLifeEventEditor: (api: FormApi) => Promise<any>;
	onClickLifeEvent?: (lifeEvent: LifeEvent) => void;
	showEditButtons?: boolean;
	lifeEvent: LifeEvent;
}

@inject("appStore", "session")
@observer
export default class ContactTabLifeEvents extends React.Component<LifeEventProps> {
	@observable isFutureLifeEventsOpen = true;
	@observable isPastLifeEventsOpen = true;
	@observable.shallow formDefinition: any;
	@observable.shallow control?: any;
	@observable formApi!: FormApi;
	@observable isFormValid = true;
	@observable isFormProcessing = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

	async componentDidMount() {
		await this.loadFormDefinition();
	}

	render() {
		const contact = this.props.store.contact!;
		const { cancelLifeEventEditor, closeLifeEventEditor, showEditButtons, onClickLifeEvent } = this.props;
		const type = EntityTypes[EntityType.LIFE_EVENT];
		let filteredLifeEvents = contact.lifeEvents.filter((a) => !a.isNew);
		if (contact.contactRole?.id === "spouse") {
			const accountChildren: Contact[] = contact.account?.contacts.filter((c: Contact) => c.contactRole?.id === "child");
			if (accountChildren) {
				if (accountChildren?.length > 0) {
					accountChildren?.forEach((a) => {
						a.lifeEvents.forEach((le) => le.setLifeEventBirthDateName(a.firstName!));
						a.lifeEvents.forEach((le) => le.setLifeEventLegalMajorityAge(a.firstName!));
						a.lifeEvents.forEach((le) => le.setLifeEventFinancialIndependenceAge(a.firstName!));
						filteredLifeEvents = filteredLifeEvents.concat(
							a.lifeEvents
								.filter((le) => le.isDeterministic)
								.filter((le) => le.name?.startsWith(a.firstName!))
						);
					});
				}
			}
		}
		const futureLifeEvents = filteredLifeEvents.filter((a) => a.isFuture);
		const pastLiveEvents = filteredLifeEvents.filter((a) => a.isPast);
		const buttons = (
			<ItemEditorButtons
				showEditButtons={showEditButtons || false}
				doEdit={true}
				allowStore={this.props.isPopupOpen}
				onCancelEditor={action(() => cancelLifeEventEditor())}
				onCloseEditor={action(() => closeLifeEventEditor(this.formApi))}
			/>
		);
		const heading = (
			<MediaObject
				body={
					<>
						{this.props.lifeEvent
							? this.props.isCreateMode
								? "Add New"
								: "Edit '" + this.props.lifeEvent.name + "'"
							: this.props.isCreateMode
								? "Add New"
								: "Edit"}{" "}
						{type.labelSingular}
					</>
				}
				figure={<Icon category={type.iconCategory as any} name={type.iconName as any} size="small" />}
				verticalCenter
			/>
		);
		return (
			<div className="slds-m-around_medium">
				<ExpandableSection
					/* @ts-ignore */
					title={<span className="slds-text-title_bold">Future Life Events</span>}
					id="future-life-events"
				>
					<Card heading=" " className="fa-height-100" bodyClassName="slds-m-around_none">
						{futureLifeEvents.length <= 0 && (
							<p className="slds-m-horizontal_small slds-m-bottom_medium">
								There is no future life events for this contact.
							</p>
						)}
						<Timeline>
							{futureLifeEvents
								.sort((a, b) => (a.startDate! < b.startDate! ? 1 : -1))
								.map((lifeEvent, i) => (
									<LifeEventTimelineItem key={i} lifeEvent={lifeEvent} onClick={onClickLifeEvent} />
								))}
						</Timeline>
					</Card>
				</ExpandableSection>
				<ExpandableSection
					/* @ts-ignore */
					title={<span className="slds-text-title_bold">Past Life Events</span>}
					id="past-life-events"
				>
					<Card heading=" " className="fa-height-100" bodyClassName="slds-m-around_none">
						{pastLiveEvents.length <= 0 && (
							<p className="slds-m-horizontal_small">There is no past life events for this contact.</p>
						)}
						<>
							<Timeline>
								{pastLiveEvents
									.sort((a, b) => (a.startDate! < b.startDate! ? 1 : -1))
									.map((lifeEvent, i) => (
										<LifeEventTimelineItem
											key={i}
											lifeEvent={lifeEvent}
											onClick={onClickLifeEvent}
										/>
									))}
							</Timeline>
						</>
					</Card>
				</ExpandableSection>

				{this.props.isPopupOpen && (
					<Modal
						heading={heading}
						isOpen
						onRequestClose={cancelLifeEventEditor}
						dismissOnClickOutside={false}
						size="small"
						footer={buttons}
					>
						<div className="slds-m-around_medium">
							<FormWrapper
								formId="contact/editLifeEvent"
								payload={{
									[EntityType.LIFE_EVENT]: this.props.lifeEvent
								}}
								displayMode={this.props.isPopupOpen ? "enabled" : "readonly"}
								onReady={(api: FormApi) => (this.formApi = api)}
								onAfterChange={(path, value) => {
									this.onChange(path, value);
									return true;
								}}
								onValidChange={(isValid: boolean) => (this.isFormValid = isValid)}
							/>
						</div>
					</Modal>
				)}
			</div>
		);
	}

	async loadFormDefinition() {
		FORM_API.getDefinition("contact/editLifeEvent");
	}

	private onChange = async (path: string, value: any) => {
		if (path && path.startsWith("lifeEvent.")) {
			await this.props.lifeEvent.setField(path.substr("lifeEvent".length + 1), value);
		}
	};
}
