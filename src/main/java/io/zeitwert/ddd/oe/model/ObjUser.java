
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;

public interface ObjUser extends Obj {

	String getEmail();

	void setEmail(String email);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

}
