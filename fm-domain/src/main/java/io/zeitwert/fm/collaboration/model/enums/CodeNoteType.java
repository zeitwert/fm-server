package io.zeitwert.fm.collaboration.model.enums;

import io.dddrive.core.enums.model.Enumeration;
import io.dddrive.core.enums.model.base.EnumeratedBase;

/**
 * CodeNoteType enum using the NEW dddrive framework.
 */
public final class CodeNoteType extends EnumeratedBase {

    public CodeNoteType(Enumeration<CodeNoteType> enumeration, String id, String name) {
        super(enumeration, id, name);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        CodeNoteType that = (CodeNoteType) other;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
