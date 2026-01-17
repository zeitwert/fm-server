import { createFileRoute } from '@tanstack/react-router';
import { HomeArea } from '../areas/home/ui/HomeArea';

export const Route = createFileRoute('/home')({
	component: HomeArea,
});
