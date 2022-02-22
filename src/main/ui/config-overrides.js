
const path = require("path");

module.exports = function override(config, env) {

	// salesforce dependencies
	// this will compile salesforce lightning as src, not as package
	config.module.rules = [
		{
			test: /\.jsx?$/,
			include: [
				"node_modules/@salesforce/design-system-react",
			].map(
				someDir => path.resolve(
					process.cwd(),
					someDir
				)
			),
			loader: require.resolve("babel-loader"),
			options: {
				presets: [
					"react-app"
				],
			},
		},
	].concat(config.module.rules);

	// make tsc go into symbolic links
	return {
		...config,
		resolve: {
			...config.resolve,
			symlinks: false
		}
	};

}
