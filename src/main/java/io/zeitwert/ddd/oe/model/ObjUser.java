
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;
import io.zeitwert.fm.dms.model.ObjDocument;

public interface ObjUser extends Obj {

	String getEmail();

	void setEmail(String email);

	String getPassword();

	void setPassword(String password);

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	Integer getAvatarImageId();

	ObjDocument getAvatarImage();

	CodeUserRole getRole();

	boolean hasRole(CodeUserRole role);

	void setRole(CodeUserRole role);

}
