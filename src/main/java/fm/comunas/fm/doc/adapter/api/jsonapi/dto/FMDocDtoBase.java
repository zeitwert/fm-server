package fm.comunas.fm.doc.adapter.api.jsonapi.dto;

import fm.comunas.fm.doc.model.FMDoc;
import fm.comunas.fm.item.adapter.api.jsonapi.dto.ItemPartNoteDto;
import fm.comunas.fm.doc.model.DocPartNote;
import fm.comunas.ddd.doc.adapter.api.jsonapi.dto.DocDtoBase;
import fm.comunas.ddd.session.model.SessionInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@ToString(callSuper = true, includeFieldNames = true)
public abstract class FMDocDtoBase<O extends FMDoc> extends DocDtoBase<O> {

	private List<ItemPartNoteDto> notes;

	public void toDoc(O doc) {
		super.toDoc(doc);
		List<Integer> oldNotes = new ArrayList<>(doc.getNoteList().stream().map(note -> note.getId()).toList());
		this.notes.forEach(noteDto -> {
			DocPartNote note = null;
			if (noteDto.getId() == null) {
				note = doc.addNote();
			} else {
				oldNotes.remove(noteDto.getId());
				note = doc.getNoteById(noteDto.getId());
			}
			noteDto.toPart(note);
		});
		oldNotes.forEach((noteId) -> doc.removeNote(noteId));
	}

	public static void fromDoc(FMDocDtoBaseBuilder<?, ?, ?> dtoBuilder, FMDoc doc, SessionInfo sessionInfo) {
		DocDtoBase.fromDoc(dtoBuilder, doc, sessionInfo);
		// @formatter:off
		dtoBuilder
			.notes(doc.getNoteList().stream().map(a -> ItemPartNoteDto.fromPart(a)).toList());
		// @formatter:on
	}

	public static void fromRecord(FMDocDtoBaseBuilder<?, ?, ?> dtoBuilder, Record doc, SessionInfo sessionInfo) {
		DocDtoBase.fromRecord(dtoBuilder, doc, sessionInfo);
	}

}
