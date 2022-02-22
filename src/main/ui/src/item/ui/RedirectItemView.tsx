import { ITEM_API } from "@comunas/ui-model";
import { AppCtx } from "App";
import { RouteComponentProps, withRouter } from "frame/app/withRouter";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";
import { Navigate } from "react-router-dom";

interface RedirectItemViewProps extends RouteComponentProps {
	itemType: string;
}

@inject("session", "showAlert")
@observer
class RedirectItemView extends React.Component<RedirectItemViewProps> {
	@observable itemType?: string;

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: RedirectItemViewProps) {
		super(props);
		makeObservable(this);
	}

	async componentDidMount() {
		const itemId = this.props.params.itemId!;
		const repository = await ITEM_API.loadAggregate(itemId);
		this.itemType = repository?.item?.[itemId]?.meta?.itemType?.id?.substr(4);
	}

	render() {
		if (!this.itemType) {
			return <></>;
		}
		return <Navigate to={`/${this.itemType}/${this.props.params.itemId}`} />;
	}
}

export default withRouter(RedirectItemView);
