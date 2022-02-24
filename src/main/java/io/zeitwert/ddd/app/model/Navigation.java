
package io.zeitwert.ddd.app.model;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class Navigation {

	private NavigationTarget target;

	private NavigationAction action;

}
