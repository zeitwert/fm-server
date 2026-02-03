import { Modal, Select, Typography } from "antd";
import { useState } from "react";
import type { CaseStage } from "@/areas/task/types";

const { Text } = Typography;

export interface StageSelectorProps {
	open: boolean;
	title: string;
	abstractStage: CaseStage;
	stages: CaseStage[];
	onSelect: (stage: CaseStage) => void;
	onCancel: () => void;
}

export function StageSelector({
	open,
	title,
	abstractStage,
	stages,
	onSelect,
	onCancel,
}: StageSelectorProps) {
	const [selectedStageId, setSelectedStageId] = useState<string | undefined>();

	const concreteStages = stages.filter(
		(stage) => stage.abstractCaseStageId === abstractStage.id && !stage.isAbstract
	);

	const handleOk = () => {
		const stage = concreteStages.find((s) => s.id === selectedStageId);
		if (stage) {
			onSelect(stage);
			setSelectedStageId(undefined);
		}
	};

	const handleCancel = () => {
		setSelectedStageId(undefined);
		onCancel();
	};

	return (
		<Modal
			title={title}
			open={open}
			onOk={handleOk}
			onCancel={handleCancel}
			okButtonProps={{ disabled: !selectedStageId }}
		>
			<div style={{ marginBottom: 16 }}>
				<Text>Select the specific stage within &ldquo;{abstractStage.name}&rdquo;:</Text>
			</div>
			<Select
				style={{ width: "100%" }}
				placeholder="Select a stage"
				value={selectedStageId}
				onChange={setSelectedStageId}
				options={concreteStages.map((stage) => ({
					label: stage.name,
					value: stage.id,
				}))}
			/>
		</Modal>
	);
}
