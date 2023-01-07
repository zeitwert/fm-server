import React from "react";
import { Route, Routes } from "react-router-dom";
import HomePage from "./HomePage";

export default class HomeArea extends React.Component {
	render() {
		return (
			<Routes>
				<Route path="" element={<HomePage />} />
			</Routes>
		);
	}
}
