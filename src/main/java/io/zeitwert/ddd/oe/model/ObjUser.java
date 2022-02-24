
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;
import io.zeitwert.ddd.oe.model.enums.CodeUserRole;

import java.util.List;

public interface ObjUser extends Obj {

	String getEmail();

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

	String getPicture();

	List<CodeUserRole> getRoleList();

}
