
import { AggregateStore, DateFormat, ItemPartNote, ItemPartNotePayload, session } from "@zeitwert/ui-model";
import { ItemWithNotes } from "@zeitwert/ui-model/fm/item/model/ItemWithNotesModel";
import { computed, makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import { getSnapshot } from "mobx-state-tree";
import React, { FC } from "react";
import ReactMarkdown from "react-markdown";

interface NotesTabProps {
	store: AggregateStore;
}

@observer
export default class NotesTab extends React.Component<NotesTabProps> {

	@observable editNoteId: string | undefined;

	constructor(props: NotesTabProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		const item = this.props.store.item! as unknown as ItemWithNotes;
		return (
			<div className="slds-is-relative fa-height-100">
				{this.renderNoteList(item)}
			</div>
		);
	}

	private renderNoteList(item: ItemWithNotes) {
		return (
			<div className="slds-m-around_medium">
				<div className="slds-feed">
					<ul className="slds-feed__list">
						{
							!item.notes.length &&
							<li className="slds-feed__item" key="note-0">
								<div>Keine Notizen</div>
								<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
							</li>
						}
						{
							item.notes.map((note, index) => (
								<li className="slds-feed__item" key={"note-" + note.id}>
									{
										this.editNoteId === note.id &&
										<NoteEditor
											note={getSnapshot(note)}
											onCancel={this.cancelNoteEditor}
											onOk={(note) => { this.modifyNote(this.editNoteId!, note); }}
										/>
									}
									{
										(!this.editNoteId || (this.editNoteId !== note.id)) &&
										<Note
											note={note}
											onEdit={(note) => { this.editNoteId = note.id }}
											onChangeVisibility={(note) => { this.toggleVisibility(note) }}
											onDelete={(note) => this.removeNote(note.id)}
										/>
									}
									<hr style={{ marginBlockStart: "0px", marginBlockEnd: 0 }} />
								</li>
							))
						}
						<li className="slds-feed__item" key="note-add">
							<NoteEditor
								isNew={true}
								onCancel={this.cancelNoteEditor}
								onOk={(note) => this.addNote(note)}
							/>
						</li>
					</ul>
				</div>
			</div>
		);
	}

	private cancelNoteEditor(): void {
		this.editNoteId = undefined;
	}

	private addNote(note: ItemPartNotePayload): void {
		console.log("addNote", note);
		if (!this.props.store.inTrx) {
			this.props.store.startTrx();
		}
		const item = this.props.store.item! as unknown as ItemWithNotes;
		item!.addNote(note);
		this.editNoteId = undefined;
	}

	private modifyNote(id: string, note: ItemPartNotePayload): void {
		console.log("modifyNote", note);
		if (!this.props.store.inTrx) {
			this.props.store.startTrx();
		}
		const item = this.props.store.item! as unknown as ItemWithNotes;
		item!.modifyNote(id, note);
		this.editNoteId = undefined;
	}

	private toggleVisibility(note: ItemPartNote): void {
		if (!this.props.store.inTrx) {
			this.props.store.startTrx();
		}
		note.setPrivate(!note.isPrivate);
	}

	private removeNote(id: string): void {
		if (!this.props.store.inTrx) {
			this.props.store.startTrx();
		}
		const item = this.props.store.item! as unknown as ItemWithNotes;
		item!.removeNote(id);
	}

}


interface NoteProps {
	note: ItemPartNote;
	onEdit: (note: ItemPartNote) => void;
	onChangeVisibility: (note: ItemPartNote) => void;
	onDelete: (note: ItemPartNote) => void;
}

const Note: FC<NoteProps> = (props) => {
	const note = props.note;
	const userName = note.createdByUser?.caption;
	const time = DateFormat.relativeTime(new Date(), (note.modifiedAt || note.createdAt)!);
	return (
		<article className="slds-post">
			<header className="slds-post__header slds-media">
				<div className="slds-media__figure">
					<a href="/#" className="slds-avatar slds-avatar_circle slds-avatar_medium">
						<img alt={userName} src={note.createdByUser?.picture || "/assets/images/avatar1.jpg"} title={userName} />
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
								icon={note.isPrivate ? "lock" : "unlock"}
								label={note.isPrivate ? "Private Notiz" : "Öffentliche Notiz"}
								onClick={() => props.onChangeVisibility(note)}
							/>
							<NoteHeaderAction
								icon={"delete"}
								label={"Löschen"}
								onClick={() => props.onDelete(note)}
							/>
							<NoteHeaderAction
								icon={"edit"}
								label={"Bearbeiten"}
								onClick={() => props.onEdit(note)}
							/>
						</div>
					</div>
					<p className="slds-text-body_small">
						<a href="/#" title="..." className="slds-text-link_reset">{time}</a>
					</p>
				</div>
			</header>
			<div className="slds-post__content slds-text-longform">
				<div><strong>{note.subject || "(kein Titel)"}</strong></div>
				<ReactMarkdown className="fa-note-content">
					{note.content || "(kein Inhalt)"}
				</ReactMarkdown>
			</div>
			{
				/*
			<footer className="slds-post__footer">
				<ul className="slds-post__footer-actions-list slds-list_horizontal">
					<li className="slds-col slds-item slds-m-right_medium">
						<NoteFooterAction note={note} icon="edit" label="Bearbeiten" onClick={(note) => props.onEdit(note)} />
					</li>
					<li className="slds-col slds-item slds-m-right_medium">
						<NoteFooterAction note={note} icon="delete" label="Löschen" onClick={(note) => props.onDelete(note)} />
					</li>
				</ul>
			</footer>
				*/
			}
		</article>
	);

};

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

// interface NoteFooterActionProps {
// 	note: ItemPartNote;
// 	icon: string;
// 	label: string;
// 	onClick?: (note: ItemPartNote) => void;
// }

// const NoteFooterAction: FC<NoteFooterActionProps> = (props) => {
// 	const { note, icon, label } = props;
// 	return (<>
// 		<button className="slds-button_reset slds-post__footer-action" title={label} onClick={() => props.onClick?.(note)}>
// 			<svg className="slds-icon slds-icon-text-default slds-icon_x-small slds-align-middle">
// 				<use xlinkHref={"/assets/icons/utility-sprite/svg/symbols.svg#" + icon}></use>
// 			</svg>{label}
// 		</button>
// 	</>
// 	);
// };

interface NoteEditorProps {
	isNew?: boolean;
	note?: ItemPartNotePayload;
	onCancel: () => void;
	onOk: (note: ItemPartNotePayload) => void;
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
							<img alt={session.sessionInfo?.user.caption} src={session.sessionInfo?.user.picture} title={session.sessionInfo?.user.caption} />
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
									<textarea id="textarea-02" className="slds-publisher__input slds-input_bare slds-text-longform" placeholder="Notiz…" onChange={(e) => this.content = e.currentTarget.value || ""} value={this.content} />
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

	private asNote(): ItemPartNotePayload {
		return {
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
