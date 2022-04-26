
import { Combobox, Icon } from "@salesforce/design-system-react";
import { API, Config, EntityTypes } from "@zeitwert/ui-model";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import { debounce } from "lodash";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface Option {
	id: string;
	label: string;
	icon?: JSX.Element;
	subTitle?: string;
	disabled?: boolean;
	tooltipContent?: string;
}

@inject("logger", "session")
@observer
class SearchBar extends React.Component<RouteComponentProps> {

	@observable value = "";
	readonly options = observable<Option>([]);
	@observable searchNr = 0;
	debouncedSearch: (searchText: string) => void;

	get ctx() {
		return this.props as any;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
		this.debouncedSearch = debounce(this.search, 200);
	}

	render() {
		return (
			<Combobox
				id="header-search-custom-id"
				labels={{ placeholder: "Search zeitwert ..." }}
				assistiveText={{ label: "Search zeitwert" }}
				value={this.value}
				key={"search-" + this.searchNr}
				events={{
					onFocus: () => {
						this.value = "";
						this.options.clear();
					},
					onChange: (e: React.FormEvent<HTMLInputElement>, data: { value: string }) => {
						this.value = data.value;
						this.debouncedSearch(data.value);
					},
					onSubmit: (e: React.FormEvent<HTMLInputElement>, data: { value: string }) => {
						this.value = data.value;
						this.debouncedSearch(data.value);
					},
					onSelect: (e: React.MouseEvent<HTMLElement>, data: { selection: Option[] }) => {
						this.value = "";
						this.searchNr++;
						this.options.clear();
						this.props.navigate?.(data.selection[0]?.id);
					}
				}}
				options={this.options}
			/>
		);
	}

	private search = (searchText: string) => {
		const s = searchText?.replace(/\s/g, "");
		if (s.length < 2 || (parseInt(s) && s.length < 4)) {
			this.options.clear();
		} else {
			API.get(Config.getApiUrl("search", "?searchText=" + s)).then((response) => {
				this.options.clear();
				response.data.forEach((item: any) => {
					const type = item.itemType.id.substring(4);
					const entityType = EntityTypes[type];
					this.options.push({
						id: "/" + type + "/" + item.id,
						label: item.caption,
						icon: <Icon category={entityType.iconCategory} name={entityType.iconName} />,
						subTitle: entityType.labelSingular
					});
				});
			});
		}
	};

}

export default withRouter(SearchBar);
