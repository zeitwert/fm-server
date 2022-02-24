
package io.zeitwert.ddd.oe.model;

import io.zeitwert.ddd.obj.model.Obj;

public interface ObjTenant extends Obj {

	String getExtlKey();

	String getName();

	void setName(String name);

	String getDescription();

	void setDescription(String description);

}
