/**
 * This file will be overwritten on docker deployment with environment specifics
 */

window["comunas"] = {
	initEnv(setParam) {
		setParam("REACT_APP_VERSION", "Dev-Snapshot");
		setParam("REACT_APP_IMAGE", "Dev-Snapshot");
	}
};
