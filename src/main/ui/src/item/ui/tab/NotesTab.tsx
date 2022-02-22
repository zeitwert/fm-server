import { AggregateStore, DateFormat, ItemPartNote, ItemPartNotePayload, session } from "@comunas/ui-model";
import { ItemWithNotes } from "@comunas/ui-model/fm/item/model/ItemWithNotesModel";
import { AppCtx } from "App";
import { computed, makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React, { FC } from "react";
import ReactMarkdown from "react-markdown";

interface NotesTabProps {
	store: AggregateStore;
}

@inject("session", "showAlert", "showToast")
@observer
export default class NotesTab extends React.Component<NotesTabProps> {

	@observable editNote: ItemPartNote | undefined = undefined;
	@observable isNew = false;

	get ctx() {
		return this.props as any as AppCtx;
	}

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
								<div>Keine Notizen bisher</div>
							</li>
						}
						{
							item.notes.map((note, index) => (
								<li className="slds-feed__item" key={"note-" + note.id}>
									<Note
										note={note}
										onEdit={(note) => { }}
										onChangeVisibility={(note) => { this.toggleVisibility(note) }}
										onDelete={(note) => this.removeNote(note.id)}
									/>
								</li>
							))
						}
						<li className="slds-feed__item" key="note-add">
							<NewNote onAdd={(note) => this.addNote(note)} />
						</li>
					</ul>
				</div>
			</div>
		);
	}

	private addNote(note: ItemPartNotePayload): void {
		if (!this.props.store.inTrx) {
			this.props.store.startTrx();
		}
		const item = this.props.store.item! as unknown as ItemWithNotes;
		item!.addNote(note);
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
					<div className="slds-grid slds-grid_align-spread slds-has-flexi-truncate">
						<p>
							<a href="/#" title={userName}>{userName}</a>
						</p>
						<NoteHeaderAction
							note={note}
							icon={note.isPrivate ? "lock" : "share"}
							label={note.isPrivate ? "Private Notiz" : "Öffentliche Notiz"}
							onClick={(note) => props.onChangeVisibility(note)}
						/>
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
			<footer className="slds-post__footer">
				<ul className="slds-post__footer-actions-list slds-list_horizontal">
					<li className="slds-col slds-item slds-m-right_medium">
						<NoteFooterAction note={note} icon="edit" label="Bearbeiten" onClick={(note) => props.onEdit(note)} />
					</li>
					<li className="slds-col slds-item slds-m-right_medium">
						<NoteFooterAction note={note} icon="delete" label="Löschen" onClick={(note) => props.onDelete(note)} />
					</li>
					<li className="slds-col slds-item slds-m-right_medium">
						<NoteFooterAction note={note} icon="like" label="Like" onClick={(note) => { }} />
					</li>
				</ul>
				<ul className="slds-post__footer-meta-list slds-list_horizontal slds-has-dividers_right slds-text-title">
					<li className="slds-item">20 likes</li>
				</ul>
			</footer>
		</article>
	);

};

interface NoteHeaderActionProps {
	note: ItemPartNote;
	icon: string;
	label: string;
	onClick?: (note: ItemPartNote) => void;
}

const NoteHeaderAction: FC<NoteHeaderActionProps> = (props) => {
	const { note, icon, label } = props;
	return (
		<button className="slds-button slds-button_icon slds-button_icon-x-small" title={label} onClick={() => props.onClick?.(note)}>
			<svg className="slds-icon slds-icon-text-default slds-icon_x-small slds-align-middle">
				<use xlinkHref={"/assets/icons/utility-sprite/svg/symbols.svg#" + icon}></use>
			</svg>
		</button>
	);

};

interface NoteFooterActionProps {
	note: ItemPartNote;
	icon: string;
	label: string;
	onClick?: (note: ItemPartNote) => void;
}

const NoteFooterAction: FC<NoteFooterActionProps> = (props) => {
	const { note, icon, label } = props;
	return (<>
		<button className="slds-button_reset slds-post__footer-action" title={label} onClick={() => props.onClick?.(note)}>
			<svg className="slds-icon slds-icon-text-default slds-icon_x-small slds-align-middle">
				<use xlinkHref={"/assets/icons/utility-sprite/svg/symbols.svg#" + icon}></use>
			</svg>{label}
		</button>
	</>
	);

};

interface NewNoteProps {
	onAdd: (note: ItemPartNotePayload) => void;
}

@observer
class NewNote extends React.Component<NewNoteProps> {

	@observable isActive: boolean = false;
	@observable subject: string = "";
	@observable content: string = "";
	@observable isPrivate: boolean = false;
	@computed get allowAdd() { return !!this.subject || !!this.content; }

	constructor(props: NewNoteProps) {
		super(props);
		makeObservable(this);
	}

	render() {
		return (
			<article className="slds-post">
				<div className="slds-media slds-comment slds-hint-parent">
					<div className="slds-media__figure">
						<a className="slds-avatar slds-avatar_circle slds-avatar_medium" href="/#">
							<img alt={session.sessionInfo?.user.caption} src={session.sessionInfo?.user.picture} title="User avatar" />
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
												<button className={"slds-button slds-button_icon slds-button_icon-container"} title={this.isPrivate ? "Private Notiz. Freigeben." : "Öffentliche Notiz. Auf Privat ändern."} onClick={() => this.isPrivate = !this.isPrivate}>
													<svg className="slds-button__icon" aria-hidden="true">
														<use xlinkHref={"/assets/icons/utility-sprite/svg/symbols.svg#" + (this.isPrivate ? "lock" : "share")}></use>
													</svg>
													<span className="slds-assistive-text">Privat</span>
												</button>
											</li>
											<li>
												<button className="slds-button slds-button_icon slds-button_icon-container" title="Benutzer">
													<svg className="slds-button__icon" aria-hidden="true">
														<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#adduser"></use>
													</svg>
													<span className="slds-assistive-text">Benutzer</span>
												</button>
											</li>
											<li>
												<button className="slds-button slds-button_icon slds-button_icon-container" title="Anhang">
													<svg className="slds-button__icon" aria-hidden="true">
														<use xlinkHref="/assets/icons/utility-sprite/svg/symbols.svg#attach"></use>
													</svg>
													<span className="slds-assistive-text">Anhang</span>
												</button>
											</li>
										</ul>
										<span>
											<button className="slds-button slds-button_neutral" onClick={() => this.init()}>
												Abbrechen
											</button>
											<button className="slds-button slds-button_brand" onClick={() => this.props.onAdd(this.asNote())} disabled={!this.allowAdd}>
												Notiz hinzufügen
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

	private asNote(): ItemPartNotePayload {
		const note = {
			subject: this.subject,
			content: this.content,
			isPrivate: this.isPrivate
		};
		this.init();
		return note;
	}

	private init(): void {
		this.isActive = false;
		this.subject = "";
		this.content = "";
		this.isPrivate = false;
	}

}
