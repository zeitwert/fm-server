
package fm.comunas.ddd.enums.model;

import java.util.List;

public interface Enumeration<E extends Enumerated> {

	String getModule();

	String getId();

	List<E> getItems();

	E getItem(String id);

	String getResourcePath();

}
