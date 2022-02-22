
package fm.comunas.ddd.app.model;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class NavigationAction {

	private String actionType;

	private Map<String, String> params;

}
