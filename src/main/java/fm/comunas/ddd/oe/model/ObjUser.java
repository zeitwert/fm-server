
package fm.comunas.ddd.oe.model;

import fm.comunas.ddd.obj.model.Obj;
import fm.comunas.ddd.oe.model.enums.CodeUserRole;

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
