declare module "json-api-normalizer" {
	export interface NormalizeOpts {
		endpoint?: string;
		filterEndpoint?: boolean;
		camelizeKeys?: boolean;
		camelizeTypeValues?: boolean;
	}

	export default function normalize(json: any, opts: NormalizeOpts = {}): any;
}
