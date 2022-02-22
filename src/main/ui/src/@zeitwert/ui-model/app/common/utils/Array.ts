import { transaction } from "mobx";

export function replace(original: any[], replaced: any[], compareFn: (a: any, b: any) => boolean) {
	transaction(() => {
		replaced.forEach((r) => {
			if (original.findIndex((o) => compareFn(o, r)) === -1) {
				original.push(r);
			}
		});
		original.forEach((o, i) => {
			if (replaced.findIndex((r) => compareFn(r, o)) === -1) {
				original.splice(i, 1);
			}
		});
	});
}
