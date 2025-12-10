package io.dddrive.core.enums.model;

public interface Enumerated {

	Enumeration<? extends Enumerated> getEnumeration();

	String getId();

	String getName();

}
