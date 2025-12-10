package io.dddrive.core.enums.model;

import java.util.List;

public interface Enumeration<E extends Enumerated> {

	String getArea();

	String getModule();

	String getId();

	List<E> getItems();

	E getItem(String id);

	String getResourcePath();

}
