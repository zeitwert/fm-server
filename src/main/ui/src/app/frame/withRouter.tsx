
import { Location } from "history";
import { NavigateFunction, Params, useLocation, useNavigate, useParams } from "react-router-dom";

export interface RouteComponentProps {
	location: Location;
	navigate: NavigateFunction;
	params: Readonly<Params<string>>;
}

export const withRouter = (Component: any) => {
	const Wrapper = (props: any) => {
		const location = useLocation();
		const navigate = useNavigate();
		const params = useParams();
		return <Component location={location} navigate={navigate} params={params} {...props} />;
	};
	return Wrapper;
};
