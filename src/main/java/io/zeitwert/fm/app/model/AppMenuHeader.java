
package io.zeitwert.fm.app.model;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
class AppMenuHeader implements AppMenuItem {

	private String id;

	private String name;

}
