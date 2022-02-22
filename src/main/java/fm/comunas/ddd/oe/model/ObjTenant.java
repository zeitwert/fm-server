
package fm.comunas.ddd.oe.model;

import fm.comunas.ddd.obj.model.Obj;

public interface ObjTenant extends Obj {

	String getExtlKey();

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

}
