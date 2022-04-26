import { Card, Dropdown } from "@salesforce/design-system-react";
import { Tile } from "@zeitwert/ui-slds/common/Tile";
import { observer } from "mobx-react";
import React from "react";
import { DragDropContext, Draggable, Droppable } from "react-beautiful-dnd";
import { Link } from "react-router-dom";

interface Field {
	value: any;
	displayName: string;
}

interface GroupBy {
	value: any;
	serverFieldName: string;
}

interface CardLayout {
	title: Field;
	body?: Field[];
}

export interface CardAction {
	name: string;
	action: (id: any) => Promise<any>;
}

interface HeaderDynamicProp {
	url: string;
	valueField: string;
	displayNameField: string;
}

interface HeaderProp {
	static?: Field[];
	dynamic?: HeaderDynamicProp;
}

interface KanbanBoardProps {
	items: any[];
	header: HeaderProp;
	cardLayout: CardLayout;
	docType: string;
	modifyUrl: string;
	groupBy: GroupBy;
	sortBy?: Field;
	readOnly?: boolean;
	isTimelineHeader?: boolean;
	cardActions?: CardAction[];
	onCardMoved: (itemId: any, destId: any) => void;
}

interface KanbanBoardState {
	isStateChange: boolean;
	boardData: any;
	header: Field[];
}

function laneItems(items: any[], lanes: Field[], groupBy: string, readOnly?: boolean, sortBy?: string) {
	let laneItems = {};

	// If readonly and sortby defined => sort lane
	if (readOnly && sortBy) {
		lanes.forEach((lane: any) => {
			laneItems = {
				...laneItems,
				[lane.value]: items
					.filter((item: any) => item[groupBy] === lane.value)
					.sort((item1, item2) => {
						if (item1[sortBy] > item2[sortBy]) {
							return 1;
						}
						if (item1[sortBy] < item2[sortBy]) {
							return -1;
						}
						return 0;
					})
			};
		});
	} else {
		lanes.forEach((lane: any) => {
			laneItems = {
				...laneItems,
				[lane.value]: items.filter((item: any) => item[groupBy] === lane.value)
			};
		});
	}

	return laneItems;
}

@observer
export class KanbanBoard extends React.Component<KanbanBoardProps, KanbanBoardState> {
	constructor(props: KanbanBoardProps) {
		super(props);
		this.state = {
			isStateChange: false,
			header: [],
			boardData: []
		};
	}

	async componentDidMount() {
		const { items, header, groupBy, readOnly, sortBy } = this.props;
		let stateHeader: Field[] = header.static || [];
		this.setState({
			isStateChange: false,
			header: stateHeader,
			boardData: items ? laneItems(items, stateHeader, groupBy.value, readOnly, sortBy?.displayName) : []
		});
	}

	render() {
		const { boardData, header } = this.state;
		const { cardActions, cardLayout, isTimelineHeader, readOnly } = this.props;
		return (
			<div className="slds-m-vertical_small">
				{isTimelineHeader && <KanbanBoardTimeLineHeader lanes={header} />}
				<KanbanBoardContent
					boardData={boardData}
					cardActions={cardActions}
					cardLayout={cardLayout}
					isTimelineHeader={isTimelineHeader}
					readOnly={readOnly}
					lanes={header}
					onDrop={this.onDrop}
				/>
			</div>
		);
	}

	onDrop = (result: any) => {
		const { source, destination } = result;
		const { boardData } = this.state;
		const item = boardData[source.droppableId][source.index];
		if (destination) {
			boardData[source.droppableId].splice(source.index, 1);
			boardData[destination.droppableId].splice(destination.index, 0, item);

			this.setState({
				boardData: boardData,
				isStateChange: true
			});

			if (source.droppableId !== destination.droppableId) {
				this.props.onCardMoved(item.id, destination.droppableId);
			}
		}
	};
}

interface KanbanBoardLineHeaderProps {
	lanes: Field[];
}

function KanbanBoardTimeLineHeader(props: KanbanBoardLineHeaderProps) {
	const { lanes } = props;
	return (
		<div className="slds-grid">
			<div className="slds-tabs_path" role="application">
				<ul className="slds-tabs_path__nav" role="tablist">
					{lanes.map((lane) => (
						<li
							className="slds-tabs_path__item slds-is-incomplete slds-is-active"
							role="presentation"
							key={lane.value}
						>
							<div className="slds-tabs_path__link" role="tab">
								<span className="slds-tabs_path__title slds-text-heading_medium">
									{lane.displayName}
								</span>
							</div>
						</li>
					))}
				</ul>
			</div>
		</div>
	);
}

interface KanbanBoardContentProps {
	boardData?: any;
	cardActions?: CardAction[];
	cardLayout: CardLayout;
	isTimelineHeader?: boolean;
	readOnly?: boolean;
	lanes: Field[];
	onDrop: (result: any) => void;
}

function KanbanBoardContent(props: KanbanBoardContentProps) {
	const { boardData, cardActions, cardLayout, isTimelineHeader, readOnly, lanes, onDrop } = props;
	const colWidth = lanes.length + "%";
	return (
		<div className="slds-grid">
			<DragDropContext onDragEnd={(result: any) => onDrop(result)}>
				{lanes.map((lane) => (
					<KanbanBoardLane
						cardActions={cardActions}
						cardLayout={cardLayout}
						colWidth={colWidth}
						isTimelineHeader={isTimelineHeader}
						readOnly={readOnly}
						key={lane.value}
						lane={lane}
						laneItems={boardData && boardData[lane.value]}
					/>
				))}
			</DragDropContext>
		</div>
	);
}

interface KanbanBoardLaneProps {
	cardActions?: CardAction[];
	cardLayout: CardLayout;
	isTimelineHeader?: boolean;
	readOnly?: boolean;
	lane: Field;
	laneItems?: any[];
	colWidth: string;
}

function KanbanBoardLane(props: KanbanBoardLaneProps) {
	const { cardActions, cardLayout, colWidth, isTimelineHeader, readOnly, laneItems, lane } = props;

	const divStyle = isTimelineHeader
		? {
			minHeight: "50px",
			padding: ".5rem 1rem",
			margin: "0rem .5rem",
			background: "#f3f2f2"
		}
		: {
			minHeight: "50px",
			padding: ".5rem 1rem",
			margin: "0rem .5rem",
			background: "#f3f2f2",
			border: "1px solid #dddbda",
			borderRadius: ".25rem",
			backgroundClip: "padding-box",
			boxShadow: "0 2px 2px 0 rgba(0, 0, 0, .1)"
		};

	return (
		<div className="slds-col slds-has-dividers_around-space slds-scrollable_y" style={{ width: colWidth }}>
			<div style={divStyle}>
				{!isTimelineHeader && (
					<div className="slds-card__header slds-grid slds-m-bottom_small">
						<header className="slds-media slds-media_center slds-has-flexi-truncate">
							<div className="slds-media__body">
								<h2 className="title">
									<span className="slds-text-heading_small" style={{ fontSize: "1.1rem" }}>
										{lane.displayName}
									</span>
								</h2>
							</div>
						</header>
					</div>
				)}
				<Droppable droppableId={lane.value}>
					{(provided) => (
						<div ref={provided.innerRef} {...provided.droppableProps} style={{ minHeight: "50px" }}>
							{laneItems &&
								laneItems.map((item: any, index: number) => {
									return (
										<KanbanBoardCard
											cardActions={cardActions}
											cardLayout={cardLayout}
											item={item}
											key={item.id}
											readOnly={readOnly}
											index={index}
										/>
									);
								})}
							{provided.placeholder}
						</div>
					)}
				</Droppable>
			</div>
		</div>
	);
}

interface KanbanBoardCardProps {
	item: any;
	cardActions?: CardAction[];
	cardLayout: CardLayout;
	readOnly?: boolean;
	index: number;
}

interface KanbanBoardCardState {
	hovered: boolean;
}

class KanbanBoardCard extends React.Component<KanbanBoardCardProps, KanbanBoardCardState> {
	constructor(props: KanbanBoardCardProps) {
		super(props);
		this.state = {
			hovered: false
		};
	}

	render() {
		const { cardActions, cardLayout, item, readOnly, index } = this.props;
		const { hovered } = this.state;
		return (
			<Draggable draggableId={item.id} index={index}>
				{(provided) => (
					<div
						ref={provided.innerRef}
						{...provided.draggableProps}
						{...provided.dragHandleProps}
						onMouseEnter={() => this.setState({ hovered: true })}
						onMouseLeave={() => this.setState({ hovered: false })}
					>
						<Card
							className="slds-m-vertical_small"
							heading={
								<div className="slds-media slds-media_center slds-has-flexi-truncate">
									<div className="slds-media__body">
										<h2
											className="slds-text-heading_small slds-truncate"
											title={item[cardLayout.title.displayName]}
										>
											{item.link ? (
												<Link to={item.link}>{item[cardLayout.title.displayName]}</Link>
											) : (
												item[cardLayout.title.displayName]
											)}
										</h2>
									</div>
								</div>
							}
							headerActions={
								!readOnly &&
								cardActions &&
								hovered && (
									<Dropdown
										align="right"
										width="xx-small"
										assistiveText={{ icon: "More Options" }}
										iconCategory="utility"
										iconName="down"
										iconVariant="border-filled"
										onSelect={(value: { id: string; label: string }) =>
											cardActions.find((ca) => ca.name === value.label)!.action(value.id)
										}
										options={cardActions.map((action: any) => ({
											id: item.id,
											label: action.name
										}))}
									/>
								)
							}
						>
							<div className="slds-card__body_inner slds-grid slds-wrap slds-grid_pull-padded">
								<div className="slds-p-horizontal_small slds-size_1-of-1 slds-medium-size_1-of-1 slds-p-bottom_small">
									<Tile title="">
										{cardLayout.body &&
											cardLayout.body.map((field, index) => {
												return item[field.displayName] ? (
													<React.Fragment key={index}>
														{item[field.displayName]}
														<br />
													</React.Fragment>
												) : null;
											})}
									</Tile>
								</div>
							</div>
						</Card>
					</div>
				)}
			</Draggable>
		);
	}
}
