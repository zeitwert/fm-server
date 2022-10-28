
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;

public interface ObjUser extends Obj {

	String getEmail();

	void setEmail(String email);

	String getPassword();

	void setPassword(String password);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getPicture();

	void setPicture(String picture);

	CodeUserRole getRole();

	boolean hasRole(CodeUserRole role);

	void setRole(CodeUserRole role);

}
