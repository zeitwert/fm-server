import { Navigate, createFileRoute } from '@tanstack/react-router';
import { getApplicationInfo } from '../app/config/AppConfig';
import { useSessionStore } from '../session/model/sessionStore';

export const Route = createFileRoute('/')({
	component: IndexComponent,
});

function IndexComponent() {
	const { sessionInfo } = useSessionStore();

	// Get default area for current application
	const appInfo = sessionInfo?.applicationId
		? getApplicationInfo(sessionInfo.applicationId)
		: null;

	const defaultArea = appInfo?.defaultArea ?? 'home';

	// Redirect to default area
	return <Navigate to={`/${defaultArea}` as '/'} replace />;
}
