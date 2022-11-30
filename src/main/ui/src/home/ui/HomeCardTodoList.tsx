
import { Card, Icon } from "@salesforce/design-system-react";
import { API, Config, session } from "@zeitwert/ui-model";
import { AppCtx } from "frame/App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface Rating {
	buildingId: number;
	buildingName: string;
	buildingOwner: string;
	buildingAddress: string;
	ratingDate: string;
	ratingUser: string;
}

@inject("appStore", "session")
@observer
export default class HomeCardTodoList extends React.Component {

	@observable ratingList: Rating[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadRatingList();
	}

	render() {
		return (
			<Card
				icon={<Icon category="custom" name="custom24" size="small" />}
				heading={<b>{`Laufende Bewertungen (${this.ratingList.length})`}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-horizontal_small"
			>
				{
					!this.ratingList.length &&
					<p className="slds-m-horizontal_medium">Keine laufenden Bewertungen.</p>
				}
				{
					!!this.ratingList.length &&
					<div>
						{
							this.ratingList.map((rating: Rating, index: number) => (
								<article className="slds-tile slds-media" key={"todo-" + index}>
									<div className="slds-media__body">
										<h3 className="slds-tile__title slds-truncate">
											<a href={"/building/" + rating.buildingId}><b>{rating.buildingName}</b>, {rating.buildingAddress}</a>
										</h3>
										<div className="slds-tile__detail">
											<p className="slds-truncate">Begehung am {rating.ratingDate}{rating.ratingUser ? ` (${rating.ratingUser})` : ""}</p>
										</div>
									</div>
								</article>
							))
						}
					</div>
				}
			</Card>
		);
	}

	private async loadRatingList() {
		const rsp = await API.get(Config.getRestUrl("home", "activeRatings/" + session.sessionInfo?.account?.id))
		this.ratingList = rsp.data;
	}

}

