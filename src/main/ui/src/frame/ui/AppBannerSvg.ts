
export const AppBanner =
	`<?xml version="1.0" encoding="UTF-8"?>
<svg width="300px" height="50px" viewBox="0 0 300 50" xmlns="http://www.w3.org/2000/svg" version="1.1">
	<style>
		.account {
			font: bold 20px arial;
			fill: #333;
		}
		.tenant {
			font: 12px arial;
			fill: #444;
		}
	</style>
	<image x="5" y="5" height="40" width="40" href="{logo}"/>
	<text x="50" y="25" class="account">{title}</text>
	<text x="51" y="42" class="tenant">{subTitle}</text>
</svg>
`;

export default AppBanner;
