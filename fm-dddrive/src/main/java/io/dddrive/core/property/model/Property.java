package io.dddrive.core.property.model;

public interface Property<T> {

	EntityWithProperties getEntity();

	String getRelativePath();

	String getPath();

	String getName();

	boolean isWritable();

}
