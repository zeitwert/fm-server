
package io.dddrive.enums.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Enumerated {

	@JsonIgnore
	Enumeration<? extends Enumerated> getEnumeration();

	String getId();

	String getName();

}
