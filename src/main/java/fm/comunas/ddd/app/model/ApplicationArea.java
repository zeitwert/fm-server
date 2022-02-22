
package fm.comunas.ddd.app.model;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class ApplicationArea {

	private String id;

	private String name;

	private String icon;

	private String path;

	private String component;

	private AppMenu menu;

	private AppMenuAction menuAction;

}
