
package fm.comunas.ddd.app.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data()
@Builder
public class AppMenu {

	private List<AppMenuItem> items;

}
