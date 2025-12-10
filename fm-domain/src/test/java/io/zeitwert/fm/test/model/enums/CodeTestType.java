package io.zeitwert.fm.test.model.enums;

import io.dddrive.core.enums.model.Enumeration;
import io.dddrive.core.enums.model.base.EnumeratedBase;

/**
 * CodeTestType enum using the NEW dddrive framework.
 * Used for testing enum properties without disturbing other enums like CodeCountry.
 */
public final class CodeTestType extends EnumeratedBase {

    public CodeTestType(Enumeration<CodeTestType> enumeration, String id, String name) {
        super(enumeration, id, name);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        CodeTestType that = (CodeTestType) other;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}

