package io.dddrive.core.oe.model;

import io.dddrive.core.obj.model.Obj;

public interface ObjUser extends Obj {

	String getEmail();

	void setEmail(String email);

	String getPassword();

	void setPassword(String password);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

}
