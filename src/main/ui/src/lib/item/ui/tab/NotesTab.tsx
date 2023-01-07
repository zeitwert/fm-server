
import { DateFormat, session } from "@zeitwert/ui-model";
import { NOTE, Note, NotePayload } from "@zeitwert/ui-model/ddd/collaboration/model/NoteModel";
import { StoreWithNotes } from "@zeitwert/ui-model/ddd/collaboration/model/StoreWithNotes";
import { computed, makeObservable, observable, toJS } from "mobx";
import { observer } from "mobx-react";
import React, { FC } from "react";
import ReactMarkdown from "react-markdown";

interface NotesTabProps {
	relatedToId: string;
	store: StoreWithNotes;
	notes: Note[];
}

@observer
export default class NotesTab extends React.Component<NotesTabProps> {

	@observable editNoteId: string | undefined;

	constructor(props: NotesTabProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const notes = this.props.notes;
		toJS(notes); // necessary to trigger re-render after update :-(
		return (
			<div className="slds-is-relative fa-height-100">
				<div className="slds-m-around_medium">
					<div className="slds-feed">
						<ul className="slds-feed__list">
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
							<li className="slds-feed__item" key="note-add">
								{
									!this.editNoteId &&
									<NoteEditor
										isNew={true}
										onCancel={this.cancelNoteEditor}
										onOk={(note) => this.addNote(note)}
									/>
								}
							</li>
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
		this.props.store.addNote(this.props.relatedToId, note);
		this.editNoteId = undefined;
	}

	private modifyNote = (id: string, note: NotePayload): void => {
		this.props.store.storeNote(id, note);
		this.editNoteId = undefined;
	}

	private changePrivacy = (note: Note): void => {
		this.modifyNote(note.id, Object.assign({}, note, { isPrivate: !note.isPrivate }));
	}

	private removeNote = (id: string): void => {
		this.props.store.removeNote(id);
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
		const userName = note.meta?.modifiedByUser?.caption || note.meta?.createdByUser?.caption;
		const userPicture = session.avatarUrl(note.meta?.modifiedByUser?.id);
		const time = DateFormat.relativeTime(new Date(), (note.meta?.modifiedAt || note.meta?.createdAt)!);

		return (
			<article className="slds-post">
				<header className="slds-post__header slds-media">
					<div className="slds-media__figure">
						<a href="/#" className="slds-avatar slds-avatar_circle slds-avatar_medium">
							<img alt={userName} src={userPicture} title={userName} />
						</a>
					</div>
					<div className="slds-media__body">
						<div className="slds-clearfix xslds-grid xslds-grid_align-spread xslds-has-flexi-truncate">
							<div className="slds-float_left">
								<p>
									<a href="/#" title={userName}>{userName}</a>
								</p>
							</div>
							<div className="slds-float_right">
								<NoteHeaderAction
									icon={isPrivate ? "lock" : "unlock"}
									label={isPrivate ? "Private Notiz" : "Öffentliche Notiz"}
									onClick={() => this.props.onChangePrivacy(note)}
								/>
								<NoteHeaderAction
									icon={"delete"}
									label={"Löschen"}
									onClick={() => this.props.onRemove(note)}
								/>
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
					<div><strong>{note.subject || "(kein Titel)"}</strong></div>
					<ReactMarkdown className="fa-note-content">
						{note.content || "(kein Inhalt)"}
					</ReactMarkdown>
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
	@computed get allowAdd() { return !!this.content; }

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
		return (
			<article className="slds-post">
				<div className="slds-media slds-comment slds-hint-parent">
					<div className="slds-media__figure">
						<a className="slds-avatar slds-avatar_circle slds-avatar_medium" href="/#">
							<img alt={session.sessionInfo?.user.caption} src={session.avatarUrl(session.sessionInfo?.user.id)} title={session.sessionInfo?.user.caption} />
						</a>
					</div>
					<div className="slds-media__body">
						<div className={"slds-publisher slds-publisher_comment" + (this.isActive ? " slds-is-active slds-has-focus" : "")}>
							<input
								id="in-01"
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
										id="textarea-02"
										className="slds-publisher__input slds-input_bare slds-text-longform"
										placeholder="Notiz…"
										onChange={(e) => this.content = e.currentTarget.value || ""}
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
		this.props.onCancel();
		this.init();
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
