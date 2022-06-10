package io.zeitwert.fm.obj.adapter.api.jsonapi.dto;

import io.zeitwert.fm.item.adapter.api.jsonapi.dto.ItemPartNoteDto;
import io.zeitwert.fm.obj.model.FMObj;
import io.zeitwert.fm.obj.model.ObjPartNote;
import io.zeitwert.ddd.obj.adapter.api.jsonapi.dto.ObjDtoBase;
import io.zeitwert.ddd.session.model.SessionInfo;
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
		List<Integer> oldNoteIds = new ArrayList<>(obj.getNoteList().stream().map(note -> note.getId()).toList());
		System.out.println("oldNoteIds: " + oldNoteIds);
		this.notes.forEach(noteDto -> {
			ObjPartNote note = null;
			if (noteDto.getId() == null) {
				note = obj.addNote();
				System.out.println("addNote: " + note.getId());
			} else {
				oldNoteIds.remove(noteDto.getId());
				note = obj.getNoteById(noteDto.getId());
				System.out.println("editNote: " + noteDto.getId() + ", oldIds: " + oldNoteIds);
			}
			noteDto.toPart(note);
		});
		oldNoteIds.forEach((noteId) -> obj.removeNote(noteId));
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
