
export const SvgHeader = `
<svg viewBox="0 0 400 80" xmlns="http://www.w3.org/2000/svg">
	<style>
		.tenant {
			font: bold 40px sans-serif;
			fill: #333;
		}
	</style>
	<image x="5" y="5" height="70" width="70" href="{logo}"/>
	<text x="80" y="60" class="tenant">{tenant}</text>
</svg>
`;
