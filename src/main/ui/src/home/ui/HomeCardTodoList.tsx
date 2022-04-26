import { Button, Card } from "@salesforce/design-system-react";
import { AppCtx } from "App";
import { makeObservable, observable } from "mobx";
import { inject, observer } from "mobx-react";
import React from "react";

interface Todo {
	id: string;
	description: string;
	dueDate: string;
	owner: string;
	priority: number;
}

@inject("appStore", "session")
@observer
export default class HomeCardTodoList extends React.Component {

	@observable todoList: Todo[] = [];

	get ctx() {
		return this.props as any as AppCtx;
	}

	constructor(props: any) {
		super(props);
		makeObservable(this);
	}

	componentDidMount() {
		this.loadTodoList();
	}

	render() {
		return (
			<Card
				heading={<b>{"To Do"}</b>}
				className="fa-height-100"
				bodyClassName="slds-m-around_none slds-p-horizontal_small slds-card__body_with_header_footer"
				footer={<Button>Show more</Button>}
			>
				{!this.todoList.length && <p className="slds-m-horizontal_medium">No todo items.</p>}
				{
					this.todoList.length &&
					<div>
						{
							this.todoList.map((todo: Todo, index: number) => (
								<article className="slds-tile slds-media" key={"todo-" + index}>
									<div className="slds-media__figure" key={"todo-d-" + index}>
										<div className="slds-checkbox">
											<input type="checkbox" name="options" id={"todo-xxx-" + todo.id} value={"todo-xxx-" + todo.id} />
											<label className="slds-checkbox__label" htmlFor={"todo-xxx-" + todo.id}>
												<span className="slds-checkbox_faux"></span>
											</label>
										</div>
									</div>
									<div className="slds-media__body">
										<h3 className="slds-tile__title slds-truncate">
											<a href="/#">{todo.description}</a>
										</h3>
										<div className="slds-tile__detail">
											<p className="slds-truncate">{todo.dueDate}, {todo.owner}</p>
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

	private loadTodoList() {
		this.todoList = [
			{
				id: "1",
				description: "Report für Gemeinderatssitzung",
				dueDate: "bis Dienstag",
				owner: "Margaret Muster",
				priority: 2
			},
			{
				id: "2",
				description: "Investitionsplanung finalisieren",
				dueDate: "bis 02.11.2020",
				owner: "Peter Ziegler",
				priority: 2
			},
			{
				id: "3",
				description: "Neue Begehung Musterstrasse 142",
				dueDate: "bis 04.11.2020",
				owner: "Martin Frey",
				priority: 1
			},
			{
				id: "4",
				description: "Unterlagen Objektstrategie Musterstrasse 19",
				dueDate: "bis 10.01.2021",
				owner: "Peter Mustermann",
				priority: 0
			},
		];
	}

}

