
package io.zeitwert.ddd.app.model;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class NavigationTarget {

	private String applicationId;

	private String applicationAreaId;

}
