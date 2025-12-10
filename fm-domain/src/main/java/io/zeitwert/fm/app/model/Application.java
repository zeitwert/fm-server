
package io.zeitwert.fm.app.model;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class Application {

	private String id;

	private String name;

	private String icon;

	private String description;

}
