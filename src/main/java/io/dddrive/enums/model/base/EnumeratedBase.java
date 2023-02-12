
package io.dddrive.enums.model.base;

import io.dddrive.enums.model.Enumerated;
import io.dddrive.enums.model.Enumeration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class EnumeratedBase implements Enumerated {

	@EqualsAndHashCode.Include
	private Enumeration<? extends Enumerated> enumeration;

	@EqualsAndHashCode.Include
	private String id;

	private String name;

	// @Override
	// public boolean equals(Object other) {
	// if (other == null) {
	// return false;
	// } else if (this == other) {
	// return true;
	// } else if (this.getClass() != other.getClass()) {
	// return false;
	// }
	// EnumeratedBase otherEnum = (EnumeratedBase) other;
	// if (this.enumeration != otherEnum.enumeration) {
	// return false;
	// }
	// return Objects.equals(this.id, otherEnum.id);
	// }

	// @Override
	// public int hashCode() {
	// return Objects.hash(this.enumeration, this.id);
	// }

	@Override
	public String toString() {
		return this.getClass() + "(" + this.getId() + ", " + this.getName() + ")";
	}

}
