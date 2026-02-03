import { useState } from "react";
import { ItemPageHeader, type ItemPageHeaderProps } from "../items/ItemPageHeader";
import { WorkflowPath } from "./WorkflowPath";
import { StageSelector } from "./StageSelector";
import type { CaseStage } from "@/areas/task/types";

export interface DocPageHeaderProps extends ItemPageHeaderProps {
	currentStage?: CaseStage;
	stages?: CaseStage[];
	onTransition: (stage: CaseStage) => Promise<void>;
	isEditing?: boolean;
}

export function DocPageHeader({
	currentStage,
	stages,
	onTransition,
	isEditing = false,
	...itemHeaderProps
}: DocPageHeaderProps) {
	const [showStageSelector, setShowStageSelector] = useState(false);
	const [abstractStage, setAbstractStage] = useState<CaseStage | undefined>();

	const handleAbstractStageClick = (stage: CaseStage) => {
		setAbstractStage(stage);
		setShowStageSelector(true);
	};

	const handleStageSelect = async (stage: CaseStage) => {
		setShowStageSelector(false);
		setAbstractStage(undefined);
		await onTransition(stage);
	};

	const handleCancel = () => {
		setShowStageSelector(false);
		setAbstractStage(undefined);
	};

	return (
		<>
			<ItemPageHeader {...itemHeaderProps} />

			{currentStage && stages && (
				<div style={{ marginTop: 0 }}>
					<WorkflowPath
						currentStage={currentStage}
						stages={stages}
						onTransition={onTransition}
						onAbstractStageClick={handleAbstractStageClick}
						readOnly={isEditing}
					/>
				</div>
			)}

			{abstractStage && (
				<StageSelector
					open={showStageSelector}
					title="Select Stage"
					abstractStage={abstractStage}
					stages={stages || []}
					onSelect={handleStageSelect}
					onCancel={handleCancel}
				/>
			)}
		</>
	);
}
