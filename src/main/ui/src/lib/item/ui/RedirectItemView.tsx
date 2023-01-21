
import { RouteComponentProps, withRouter } from "app/frame/withRouter";
import { makeObservable, observable } from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { Navigate } from "react-router-dom";

interface RedirectItemViewProps extends RouteComponentProps {
	itemType: string;
}

@observer
class RedirectItemView extends React.Component<RedirectItemViewProps> {

	@observable itemType?: string;

	constructor(props: RedirectItemViewProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const itemId = this.props.params.itemId!;
		const repository = {} as any;//await ITEM_API.loadAggregate(itemId);
		this.itemType = repository?.item?.[itemId]?.meta?.itemType?.id?.substr(4);
	}

	render() {
		if (!this.itemType) {
			return <>This should redirect to corresponding item (NYI)</>;
		}
		return <Navigate to={`/${this.itemType}/${this.props.params.itemId}`} />;
	}

}

export default withRouter(RedirectItemView);
