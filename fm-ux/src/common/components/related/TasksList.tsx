/**
 * TasksList component for displaying and managing entity tasks.
 *
 * Provides a task list with expandable sections for overdue and completed tasks.
 */

import { List, Avatar, Typography, Space, Empty, Skeleton, Button, Collapse } from "antd";
import { PlusOutlined, CheckOutlined } from "@ant-design/icons";
import { useTranslation } from "react-i18next";

const { Text } = Typography;

// ============================================================================
// Types
// ============================================================================

export interface Task {
	id: string;
	subject: string;
	dueAt?: string;
	caseStage?: {
		id: string;
		name: string;
	};
	meta?: {
		assignee?: {
			id: string;
			name: string;
		};
	};
}

export interface TasksListProps {
	/** Array of tasks to display */
	tasks: Task[];
	/** Whether the tasks are currently loading */
	isLoading?: boolean;
	/** Callback when a task is marked as complete */
	onCompleteTask?: (task: Task) => void;
	/** Callback when creating a new task */
	onCreateTask?: () => void;
	/** Function to generate avatar URL from user ID */
	getAvatarUrl?: (userId: string) => string;
}

// ============================================================================
// Helpers
// ============================================================================

/**
 * Check if a task is overdue.
 */
function isOverdue(task: Task): boolean {
	if (!task.dueAt) return false;
	return new Date(task.dueAt) < new Date();
}

/**
 * Check if a task is completed.
 */
function isCompleted(task: Task): boolean {
	return task.caseStage?.id === "task.done";
}

/**
 * Format a date for display.
 */
function formatDate(
	dateString: string | undefined,
	todayLabel: string,
	tomorrowLabel: string
): string {
	if (!dateString) return "";

	const date = new Date(dateString);
	const now = new Date();
	const tomorrow = new Date(now);
	tomorrow.setDate(tomorrow.getDate() + 1);

	// Check for today/tomorrow
	if (date.toDateString() === now.toDateString()) {
		return todayLabel;
	}
	if (date.toDateString() === tomorrow.toDateString()) {
		return tomorrowLabel;
	}

	return date.toLocaleDateString("de-CH", {
		day: "2-digit",
		month: "2-digit",
		year: "numeric",
	});
}

// ============================================================================
// Task Group Component
// ============================================================================

interface TaskGroupProps {
	tasks: Task[];
	onComplete?: (task: Task) => void;
	getAvatarUrl?: (userId: string) => string;
	todayLabel: string;
	tomorrowLabel: string;
	completeTaskLabel: string;
}

function TaskGroup({
	tasks,
	onComplete,
	getAvatarUrl,
	todayLabel,
	tomorrowLabel,
	completeTaskLabel,
}: TaskGroupProps) {
	return (
		<List
			size="small"
			dataSource={tasks}
			renderItem={(task) => (
				<List.Item
					actions={
						onComplete && !isCompleted(task)
							? [
									<Button
										key="complete"
										type="text"
										size="small"
										icon={<CheckOutlined />}
										onClick={() => onComplete(task)}
										title={completeTaskLabel}
										aria-label="common:completeTask"
									/>,
								]
							: undefined
					}
					style={{ padding: "8px 0" }}
				>
					<List.Item.Meta
						avatar={
							task.meta?.assignee?.id ? (
								<Avatar size="small" src={getAvatarUrl?.(task.meta.assignee.id)}>
									{task.meta.assignee.name?.[0]}
								</Avatar>
							) : (
								<Avatar size="small">?</Avatar>
							)
						}
						title={
							<Space size="small">
								{isCompleted(task) ? (
									<Text delete type="secondary">
										{task.subject}
									</Text>
								) : (
									<Text>{task.subject}</Text>
								)}
							</Space>
						}
						description={
							<Space size="small">
								{task.dueAt && (
									<Text
										type={isOverdue(task) && !isCompleted(task) ? "danger" : "secondary"}
										style={{ fontSize: 12 }}
									>
										{formatDate(task.dueAt, todayLabel, tomorrowLabel)}
									</Text>
								)}
								{task.meta?.assignee?.name && (
									<Text type="secondary" style={{ fontSize: 12 }}>
										{task.meta.assignee.name}
									</Text>
								)}
							</Space>
						}
					/>
				</List.Item>
			)}
		/>
	);
}

// ============================================================================
// Main Component
// ============================================================================

export function TasksList({
	tasks,
	isLoading = false,
	onCompleteTask,
	onCreateTask,
	getAvatarUrl,
}: TasksListProps) {
	const { t } = useTranslation();

	// Categorize tasks
	const activeTasks = tasks.filter((t) => !isCompleted(t) && !isOverdue(t));
	const overdueTasks = tasks.filter((t) => !isCompleted(t) && isOverdue(t));
	const completedTasks = tasks.filter((t) => isCompleted(t));

	if (isLoading) {
		return <Skeleton active paragraph={{ rows: 3 }} />;
	}

	return (
		<div>
			{/* Add task button */}
			{onCreateTask && (
				<Button
					type="dashed"
					block
					icon={<PlusOutlined />}
					onClick={onCreateTask}
					style={{ marginBottom: 12 }}
					aria-label="common:addTask"
				>
					{t("common:action.addTask")}
				</Button>
			)}

			{/* No tasks message */}
			{tasks.length === 0 && (
				<Empty description={t("common:message.noTasks")} image={Empty.PRESENTED_IMAGE_SIMPLE} />
			)}

			{/* Active tasks */}
			{activeTasks.length > 0 && (
				<TaskGroup
					tasks={activeTasks}
					onComplete={onCompleteTask}
					getAvatarUrl={getAvatarUrl}
					todayLabel={t("common:label.today")}
					tomorrowLabel={t("common:label.tomorrow")}
					completeTaskLabel={t("common:action.completeTask")}
				/>
			)}

			{/* Overdue tasks - expandable, open by default if has items */}
			{overdueTasks.length > 0 && (
				<Collapse
					ghost
					defaultActiveKey={["overdue"]}
					style={{ marginTop: activeTasks.length > 0 ? 8 : 0 }}
					items={[
						{
							key: "overdue",
							label: (
								<Text type="danger">
									{t("common:label.overdue")} ({overdueTasks.length})
								</Text>
							),
							children: (
								<TaskGroup
									tasks={overdueTasks}
									onComplete={onCompleteTask}
									getAvatarUrl={getAvatarUrl}
									todayLabel={t("common:label.today")}
									tomorrowLabel={t("common:label.tomorrow")}
									completeTaskLabel={t("common:action.completeTask")}
								/>
							),
						},
					]}
				/>
			)}

			{/* Completed tasks - expandable, closed by default */}
			{completedTasks.length > 0 && (
				<Collapse
					ghost
					style={{ marginTop: 8 }}
					items={[
						{
							key: "completed",
							label: (
								<Text type="secondary">
									{t("common:label.completed")} ({completedTasks.length})
								</Text>
							),
							children: (
								<TaskGroup
									tasks={completedTasks}
									getAvatarUrl={getAvatarUrl}
									todayLabel={t("common:label.today")}
									tomorrowLabel={t("common:label.tomorrow")}
									completeTaskLabel={t("common:action.completeTask")}
								/>
							),
						},
					]}
				/>
			)}
		</div>
	);
}
