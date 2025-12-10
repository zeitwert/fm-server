package io.dddrive.core.property.model;

public interface PropertyChangeListener {

	void propertyChange(String op, String path, String value, String oldValue, boolean isInCalc);

}
