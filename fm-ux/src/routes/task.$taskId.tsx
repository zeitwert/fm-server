import { createFileRoute } from "@tanstack/react-router";
import { TaskPage } from "../areas/task/ui/TaskPage";

export const Route = createFileRoute("/task/$taskId")({
	component: TaskPageRoute,
});

function TaskPageRoute() {
	const { taskId } = Route.useParams();
	return <TaskPage taskId={taskId} />;
}
