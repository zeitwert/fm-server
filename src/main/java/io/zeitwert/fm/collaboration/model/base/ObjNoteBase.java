
package io.zeitwert.fm.collaboration.model.base;

import io.dddrive.obj.model.base.ObjExtnBase;
import io.dddrive.property.model.EnumProperty;
import io.dddrive.property.model.SimpleProperty;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

public abstract class ObjNoteBase extends ObjExtnBase implements ObjNote {

	//@formatter:off
	protected final SimpleProperty<Integer> relatedToId = this.addSimpleProperty("relatedToId", Integer.class);
	protected final EnumProperty<CodeNoteType> noteType = this.addEnumProperty("noteType", CodeNoteType.class);
	protected final SimpleProperty<String> subject = this.addSimpleProperty("subject", String.class);
	protected final SimpleProperty<String> content = this.addSimpleProperty("content", String.class);
	protected final SimpleProperty<Boolean> isPrivate = this.addSimpleProperty("isPrivate", Boolean.class);
	//@formatter:on

	protected ObjNoteBase(ObjNoteRepository repository, Object state) {
		super(repository, state);
	}

	@Override
	public ObjNoteRepository getRepository() {
		return (ObjNoteRepository) super.getRepository();
	}

	@Override
	public void doCalcSearch() {
		this.addSearchText(this.getSubject());
		this.addSearchText(this.getContent());
	}

	@Override
	protected void doCalcAll() {
		super.doCalcAll();
		this.calcCaption();
	}

	private void calcCaption() {
		this.setCaption("Notiz");
	}

}
