
import { Combobox, Icon } from "@salesforce/design-system-react";
import { API, Config, EntityTypes } from "@zeitwert/ui-model";
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
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

@inject("logger")
@observer
class SearchBar extends React.Component<RouteComponentProps> {

	@observable value = "";
	@observable options: Option[] = [];
	@observable searchNr = 0;

	get ctx() {
		return this.props as any;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
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
						this.options = [];
					},
					onChange: (e: React.FormEvent<HTMLInputElement>, data: { value: string }) => {
						this.value = data.value;
						this.search(data.value);
					},
					onSubmit: (e: React.FormEvent<HTMLInputElement>, data: { value: string }) => {
						this.value = data.value;
						this.search(data.value);
					},
					onSelect: (e: React.MouseEvent<HTMLElement>, data: { selection: Option[] }) => {
						this.value = "";
						this.searchNr++;
						this.options = [];
						this.props.navigate?.(data.selection[0]?.id);
					}
				}}
				options={this.options}
			/>
		);
	}

	private search = (searchText: string) => {
		const s = searchText?.replace(/\s/g, "");
		if (searchText?.replace(/\s/g, "").length < 2) {
			this.options = [];
		} else {
			this.options = [];
			API.get(Config.getRestUrl("search", "?searchText=" + s)).then((response) => {
				this.options = response.data.map((item: any) => {
					const type = item.itemType.id.substring(4);
					const entityType = EntityTypes[type];
					return {
						id: "/" + type + "/" + item.id,
						label: item.caption,
						icon: <Icon category={entityType.iconCategory} name={entityType.iconName} />,
						subTitle: entityType.labelSingular
					};
				});
			});
		}
	};

}

export default withRouter(SearchBar);
