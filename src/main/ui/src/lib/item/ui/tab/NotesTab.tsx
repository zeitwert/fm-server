
import { Avatar } from "@salesforce/design-system-react";
import { DateFormat, NotesStore, NotesStoreModel, session } from "@zeitwert/ui-model";
import { NOTE, Note, NotePayload } from "@zeitwert/ui-model/fm/collaboration/model/NoteModel";
import { computed, makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React, { FC } from "react";
import ReactMarkdown from "react-markdown";

export interface NotesTabProps {
	relatedToId: string;
}

@observer
export default class NotesTab extends React.Component<NotesTabProps> {

	@observable notesStore: NotesStore = NotesStoreModel.create({});
	@observable editNoteId: string | undefined;

	constructor(props: NotesTabProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		await this.notesStore.load(this.props.relatedToId);
	}

	async componentDidUpdate(prevProps: NotesTabProps) {
		if (this.props.relatedToId !== prevProps.relatedToId) {
			await this.notesStore.load(this.props.relatedToId);
		}
	}

	render() {
		const notes = this.notesStore.notes;
		return (
			<div className="slds-is-relative">
				<div className="slds-m-around_medium">
					<div className="slds-feed">
						<ul className="slds-feed__list">
							<li className="slds-feed__item" key="note-add">
								{
									!this.editNoteId &&
									<>
										<NoteEditor
											isNew={true}
											onCancel={this.cancelNoteEditor}
											onOk={(note) => this.addNote(note)}
										/>
										<hr style={{ marginBlockStart: "12px", marginBlockEnd: 0 }} />
									</>
								}
							</li>
							{
								!notes.length &&
								<li className="slds-feed__item" key="note-0">
									<div>Keine Notizen</div>
									<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
								</li>
							}
							{
								notes.map((note, index) => (
									<li className="slds-feed__item" key={"note-" + index}>
										{
											(!this.editNoteId || (this.editNoteId !== note.id)) &&
											<NoteView
												note={note}
												onEdit={(note) => { this.editNoteId = note.id }}
												onChangePrivacy={(note) => { this.changePrivacy(note) }}
												onRemove={(note) => this.removeNote(note.id)}
											/>
										}
										{
											this.editNoteId === note.id &&
											<NoteEditor
												note={toJS(note)}
												onCancel={this.cancelNoteEditor}
												onOk={(note) => { this.modifyNote(this.editNoteId!, note); }}
											/>
										}
										<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
									</li>
								))
							}
						</ul>
					</div>
				</div>
			</div>
		);
	}

	private cancelNoteEditor = (): void => {
		this.editNoteId = undefined;
	}

	private addNote = (note: NotePayload): void => {
		this.notesStore.addNote(this.props.relatedToId, note);
		this.editNoteId = undefined;
	}

	private modifyNote = (id: string, note: NotePayload): void => {
		this.notesStore.storeNote(id, note);
		this.editNoteId = undefined;
	}

	private changePrivacy = (note: Note): void => {
		this.modifyNote(note.id, Object.assign({}, note, { isPrivate: !note.isPrivate }));
	}

	private removeNote = (id: string): void => {
		this.notesStore.removeNote(id);
	}

}

interface NoteViewProps {
	note: Note;
	onEdit: (note: Note) => void;
	onChangePrivacy: (note: Note) => void;
	onRemove: (note: Note) => void;
}

@observer
class NoteView extends React.Component<NoteViewProps> {

	render() {

		const note = this.props.note;
		const isPrivate = note.isPrivate;
		const user = note.meta?.createdByUser!;
		const userName = user.name;
		const userAvatar = session.avatarUrl(user.id);
		const time = DateFormat.relativeTime(note.meta?.modifiedAt || note.meta?.createdAt!);
		const isOwner = session.sessionInfo?.user?.id! == note.meta?.createdByUser?.id!;

		return (
			<article className="slds-post">
				<header className="slds-post__header slds-media">
					<div className="slds-media__figure">
						<Avatar
							variant="user"
							size="medium"
							imgSrc={userAvatar}
							label={userName}
						/>
					</div>
					<div className="slds-media__body">
						<div className="slds-clearfix xslds-grid xslds-grid_align-spread xslds-has-flexi-truncate">
							<div className="slds-float_left">
								<p>
									<a href={`/user/${user.id}`} title={userName}>{userName}</a>
								</p>
							</div>
							<div className="slds-float_right">
								{
									isOwner &&
									<NoteHeaderAction
										icon={isPrivate ? "lock" : "unlock"}
										label={isPrivate ? "Private Notiz" : "Öffentliche Notiz"}
										onClick={() => this.props.onChangePrivacy(note)}
									/>
								}
								{
									isOwner &&
									<NoteHeaderAction
										icon={"delete"}
										label={"Löschen"}
										onClick={() => this.props.onRemove(note)}
									/>
								}
								<NoteHeaderAction
									icon={"edit"}
									label={"Bearbeiten"}
									onClick={() => this.props.onEdit(note)}
								/>
							</div>
						</div>
						<p className="slds-text-body_small">
							<a href="/#" title="..." className="slds-text-link_reset">{time}</a>
						</p>
					</div>
				</header>
				<div className="slds-post__content xslds-text-longform">
					{note.subject && <div><strong>{note.subject}</strong></div>}
					{
						note.content &&
						<ReactMarkdown className="fa-note-content">
							{note.content}
						</ReactMarkdown>
					}
				</div>
			</article>
		);
	}

}

interface NoteHeaderActionProps {
	icon: string;
	label: string;
	onClick?: () => void;
}

const NoteHeaderAction: FC<NoteHeaderActionProps> = (props) => {
	const { icon, label } = props;
	return (
		<span className="slds-m-left_small">
			<button className="slds-button slds-button_icon slds-button_icon-x-small" title={label} onClick={() => props.onClick?.()}>
				<svg className="slds-icon slds-icon-text-default slds-icon_x-small slds-align-middle">
					<use xlinkHref={"/assets/icons/utility-sprite/svg/symbols.svg#" + icon}></use>
				</svg>
			</button>
		</span>
	);

};

interface NoteEditorProps {
	isNew?: boolean;
	note?: NotePayload;
	onCancel: () => void;
	onOk: (note: NotePayload) => void;
}

@observer
class NoteEditor extends React.Component<NoteEditorProps> {

	@observable isActive: boolean = false;
	@observable subject: string = "";
	@observable content: string = "";
	@observable isPrivate: boolean = false;
	@computed get allowAdd() { return !!this.subject || !!this.content; }

	constructor(props: NoteEditorProps) {
		super(props);
		makeObservable(this);
		if (!props.isNew) {
			this.isActive = true;
			this.subject = props.note?.subject || "";
			this.content = props.note?.content || "";
			this.isPrivate = props.note?.isPrivate || false;
		}
	}

	render() {
		const sessionUser = session.sessionInfo?.user!;
		const sessionUserName = sessionUser.name;
		const sessionUserAvatar = session.avatarUrl(sessionUser.id);
		return (
			<article
				className="slds-post"
				onKeyDown={(e) => {
					if (e.key === "Escape") {
						this.onCancel();
						(document.activeElement as any)?.blur();
					}
				}}
			>
				<div className="slds-media slds-comment slds-hint-parent">
					<div className="slds-media__figure">
						<Avatar
							variant="user"
							size="medium"
							imgSrc={sessionUserAvatar}
							label={sessionUserName}
						/>
					</div>
					<div className="slds-media__body">
						<div className={"slds-publisher slds-publisher_comment" + (this.isActive ? " slds-is-active slds-has-focus" : "")}>
							<input
								className="slds-publisher__input slds-input_bare"
								placeholder="Titel…"
								value={this.subject}
								onFocus={(e) => this.isActive = true}
								onChange={(e) => this.subject = e.currentTarget.value || ""}
							/>
							{
								this.isActive &&
								<>
									<textarea
										className="slds-publisher__input slds-input_bare slds-text-longform"
										style={{ marginTop: "2px" }}
										placeholder="Notiz…"
										onChange={(e) => this.content = e.currentTarget.value || ""}
										onKeyDown={(e) => { if (e.key === "Escape") { this.onCancel(); } }}
										value={this.content}
										rows={6}
									/>
									<div className="slds-publisher__actions slds-grid slds-grid_align-spread">
										<ul className="slds-grid">
											<li>
												<button className={"slds-button slds-button_icon slds-button_icon-container"} title={this.isPrivate ? "Freigeben" : "Privat stellen"} onClick={() => this.isPrivate = !this.isPrivate}>
													<svg className="slds-button__icon" aria-hidden="true">
														<use xlinkHref={"/assets/icons/utility-sprite/svg/symbols.svg#" + (this.isPrivate ? "lock" : "unlock")}></use>
													</svg>
													<span className="slds-assistive-text">Privat</span>
												</button>
											</li>
										</ul>
										<span>
											<button className="slds-button slds-button_icon slds-button_icon-border-filled" onClick={this.onCancel}>
												<svg className="slds-button__icon" aria-hidden="true">
													<use xlinkHref={"/assets/icons/action-sprite/svg/symbols.svg#close"}></use>
												</svg>
											</button>
											<button className="slds-button slds-button_icon slds-button_icon-brand" onClick={this.onOk} disabled={!this.allowAdd}>
												<svg className="slds-button__icon" aria-hidden="true">
													<use xlinkHref={"/assets/icons/action-sprite/svg/symbols.svg#approval"}></use>
												</svg>
											</button>
										</span>
									</div>
								</>
							}
						</div>
					</div>
				</div>
			</article>
		);
	};

	private onCancel = (): void => {
		this.init();
		this.props.onCancel();
	}

	private onOk = (): void => {
		this.props.onOk(this.asNote());
		this.init();
	}

	private asNote(): any/*ItemPartNotePayload*/ {
		return {
			noteType: NOTE,
			subject: this.subject,
			content: this.content,
			isPrivate: this.isPrivate
		};
	}

	private init(): void {
		this.isActive = false;
		this.subject = "";
		this.content = "";
		this.isPrivate = false;
	}

}
