package fm.comunas.fm.obj.adapter.api.jsonapi.dto;

import fm.comunas.fm.item.adapter.api.jsonapi.dto.ItemPartNoteDto;
import fm.comunas.fm.obj.model.FMObj;
import fm.comunas.fm.obj.model.ObjPartNote;
import fm.comunas.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
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
public abstract class FMObjDtoBase<O extends FMObj> extends ObjDtoBase<O> {

	private List<ItemPartNoteDto> notes;

	public void toObj(O obj) {
		super.toObj(obj);
		List<Integer> oldNotes = new ArrayList<>(obj.getNoteList().stream().map(note -> note.getId()).toList());
		this.notes.forEach(noteDto -> {
			ObjPartNote note = null;
			if (noteDto.getId() == null) {
				note = obj.addNote();
			} else {
				oldNotes.remove(noteDto.getId());
				note = obj.getNoteById(noteDto.getId());
			}
			noteDto.toPart(note);
		});
		oldNotes.forEach((noteId) -> obj.removeNote(noteId));
	}

	public static void fromObj(FMObjDtoBaseBuilder<?, ?, ?> dtoBuilder, FMObj obj, SessionInfo sessionInfo) {
		ObjDtoBase.fromObj(dtoBuilder, obj, sessionInfo);
		// @formatter:off
		dtoBuilder
			.notes(obj.getNoteList().stream().map(a -> ItemPartNoteDto.fromPart(a)).toList());
		// @formatter:on
	}

	public static void fromRecord(FMObjDtoBaseBuilder<?, ?, ?> dtoBuilder, Record obj, SessionInfo sessionInfo) {
		ObjDtoBase.fromRecord(dtoBuilder, obj, sessionInfo);
	}

}
