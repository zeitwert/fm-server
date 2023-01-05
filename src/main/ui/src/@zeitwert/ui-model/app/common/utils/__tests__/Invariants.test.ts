import { assertThis, ensureThis, requireThis } from "../Invariants";

type A = {
	val: number;
}

type B = {
	a: A;
}

export const testTyping = (b1?: B, b2?: B, b3?: B) => {
	console.log(b1?.a.val);
	console.log(b2?.a.val);
	console.log(b3?.a.val);
	assertThis(!!b1);
	console.log(b1.a.val);
	requireThis(!!b2);
	console.log(b2.a.val);
	ensureThis(!!b3);
	console.log(b3.a.val);
}

describe("assertThis", () => {
	it("checks assertions", () => {
		assertThis(true, "must not crash");
		expect(() => {
			assertThis(false, "should not happen in the 1st place");
		}).toThrow(/^Assertion failed: should not happen in the 1st place$/);
		expect(() => {
			assertThis(false, "should %s happen in the %sst place", "not", 1);
		}).toThrow(/^Assertion failed: should not happen in the 1st place$/);
	});
});

describe("requireThis", () => {
	it("checks preconditions", () => {
		requireThis(true, "must not crash");
		expect(() => {
			requireThis(false, "should %s happen in the %sst place", "not", 1);
		}).toThrow(/^Precondition failed: should not happen in the 1st place$/);
	});
});

describe("ensureThis", () => {
	it("checks postconditions", () => {
		ensureThis(true, "must not crash");
		expect(() => {
			ensureThis(false, "should %s happen in the %sst place", "not", 1);
		}).toThrow(/^Postcondition failed: should not happen in the 1st place$/);
	});
});
