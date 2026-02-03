import { createFileRoute } from "@tanstack/react-router";
import { TaskArea } from "@/areas/task/ui/TaskArea";

export const Route = createFileRoute("/task/")({
	component: TaskArea,
});
