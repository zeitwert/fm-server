
export const SvgHeader =
	`<svg viewBox="0 0 400 80" xmlns="http://www.w3.org/2000/svg">
	<style>
		.account {
			font: bold 40px sans-serif;
			fill: #333;
		}
		.tenant {
			font: italic 20px sans-serif;
			fill: #444;
		}
	</style>
	<image x="5" y="5" height="70" width="70" href="{logo}"/>
	<text x="80" y="45" class="account">{account}</text>
	<text x="80" y="75" class="tenant">{tenant}</text>
</svg>
`;
