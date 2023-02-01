
package io.zeitwert.fm.collaboration.model.base;

import io.zeitwert.ddd.db.model.AggregateState;
import io.zeitwert.ddd.obj.model.base.ObjBase;
import io.zeitwert.ddd.part.model.Part;
import io.zeitwert.ddd.part.model.enums.CodePartListType;
import io.zeitwert.ddd.property.model.EnumProperty;
import io.zeitwert.ddd.property.model.Property;
import io.zeitwert.ddd.property.model.SimpleProperty;
import io.zeitwert.fm.collaboration.model.ObjNote;
import io.zeitwert.fm.collaboration.model.ObjNoteRepository;
import io.zeitwert.fm.collaboration.model.enums.CodeNoteType;

public abstract class ObjNoteBase extends ObjBase implements ObjNote {

	//@formatter:off
	protected final SimpleProperty<Integer> relatedToId = this.addSimpleProperty("relatedToId", Integer.class);
	protected final EnumProperty<CodeNoteType> noteType = this.addEnumProperty("noteType", CodeNoteType.class);
	protected final SimpleProperty<String> subject = this.addSimpleProperty("subject", String.class);
	protected final SimpleProperty<String> content = this.addSimpleProperty("content", String.class);
	protected final SimpleProperty<Boolean> isPrivate = this.addSimpleProperty("isPrivate", Boolean.class);
	//@formatter:on

	protected ObjNoteBase(ObjNoteRepository repository, AggregateState state) {
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
	public Part<?> addPart(Property<?> property, CodePartListType partListType) {
		return super.addPart(property, partListType);
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
