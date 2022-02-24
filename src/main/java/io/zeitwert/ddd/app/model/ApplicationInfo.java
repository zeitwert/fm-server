
package io.zeitwert.ddd.app.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class ApplicationInfo {

	private String id;

	private String name;

	private List<ApplicationArea> areas;

	private String defaultArea;

}
