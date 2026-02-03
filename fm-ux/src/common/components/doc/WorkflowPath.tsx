import { Card, Button } from "antd";
import { CheckOutlined } from "@ant-design/icons";
import { useState } from "react";
import type { CaseStage } from "@/areas/task/types";

export interface WorkflowPathProps {
	currentStage?: CaseStage;
	stages?: CaseStage[];
	onTransition: (stage: CaseStage) => void;
	onAbstractStageClick: (stage: CaseStage) => void;
	readOnly?: boolean;
}

export function WorkflowPath({
	currentStage,
	stages = [],
	onTransition,
	onAbstractStageClick,
	readOnly = false,
}: WorkflowPathProps) {
	const [selectedStage, setSelectedStage] = useState<CaseStage | undefined>();

	const filteredStages = stages.filter((stage) => {
		if (stage.isAbstract) {
			return stage.id !== currentStage?.abstractCaseStageId;
		} else if (stage.abstractCaseStageId) {
			return stage.id === currentStage?.id;
		}
		return true;
	});

	const currentIndex = filteredStages.findIndex((stage) => stage.id === currentStage?.id);
	const isOtherStageSelected = !!selectedStage && selectedStage.id !== currentStage?.id;

	const handleStageClick = (stage: CaseStage) => {
		if (readOnly) return;
		if (stage.id === selectedStage?.id) {
			setSelectedStage(undefined);
		} else {
			setSelectedStage(stage);
		}
	};

	const handleModify = () => {
		if (!selectedStage) return;

		if (selectedStage.isAbstract) {
			onAbstractStageClick(selectedStage);
		} else {
			onTransition(selectedStage);
		}
		setSelectedStage(undefined);
	};

	const getStageType = (index: number, isLast: boolean) => {
		if (isLast) {
			return filteredStages[index].id === currentStage?.id ? "terminal" : "incomplete";
		}

		if (index < currentIndex) {
			return "complete";
		} else if (filteredStages[index].id === currentStage?.id) {
			return "current";
		} else {
			return "incomplete";
		}
	};

	if (!currentStage || filteredStages.length === 0) {
		return null;
	}

	return (
		<Card
			size="small"
			styles={{
				body: {
					padding: "8px 16px",
				},
			}}
		>
			<div style={{ display: "flex", alignItems: "center", gap: 16 }}>
				<div style={{ display: "flex", alignItems: "center", gap: 8, flex: 1 }}>
					{filteredStages.map((stage, index) => {
						const isLast = index === filteredStages.length - 1;
						const isActive = stage.id === (selectedStage?.id ?? currentStage?.id);
						const stageType = getStageType(index, isLast);

						return (
							<div key={stage.id} style={{ display: "flex", alignItems: "center", gap: 8 }}>
								<Button
									type={isActive ? "primary" : stageType === "complete" ? "default" : "dashed"}
									size="small"
									onClick={() => handleStageClick(stage)}
									disabled={readOnly}
									icon={stageType === "complete" ? <CheckOutlined /> : undefined}
									style={{
										opacity: stageType === "incomplete" ? 0.5 : 1,
									}}
								>
									{stage.name}
								</Button>
								{!isLast && <span style={{ color: "#d9d9d9" }}>→</span>}
							</div>
						);
					})}
				</div>

				{isOtherStageSelected && !readOnly && (
					<Button type="primary" size="small" onClick={handleModify}>
						Transition
					</Button>
				)}
			</div>
		</Card>
	);
}
