
package io.zeitwert.fm.app.model;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class AppMenuAction implements AppMenuItem {

	private String id;

	private String name;

	private Navigation navigation;

	private String icon;

}
