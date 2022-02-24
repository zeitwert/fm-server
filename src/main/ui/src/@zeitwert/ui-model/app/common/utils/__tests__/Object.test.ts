import { valueByPath } from "../Object";

describe("valueByPath", () => {
	it("allows generic object access", () => {
		const obj = {
			a: 1,
			b: {
				c: 2
			},
			d: [{ e: 3 }],
			f: null
		};

		expect(valueByPath(obj, "a")).toBe(1);
		expect(valueByPath(obj, "b.c")).toBe(2);
		expect(valueByPath(obj, "d[0].e")).toBe(3);
		expect(valueByPath(obj, "f")).toBeNull();

		const arr = [
			{ a: 1 },
			{
				b: [
					{
						c: 2,
						d: (): number => {
							return 3;
						}
					}
				]
			}
		];

		expect(valueByPath(arr, "[0].a")).toBe(1);
		expect(valueByPath(arr, "[1].b[0].c")).toBe(2);
	});

	it("does not support function calls", () => {
		const arr = [
			{ a: 1 },
			{
				b: [
					{
						c: 2,
						d: (): number => {
							return 3;
						}
					}
				]
			}
		];

		expect(valueByPath(arr, "[1].b[0].d()")).toBeUndefined();
	});
});
