import { assertThis, requireThis, ensureThis } from "../Assertions";

describe("assertThis", () => {
	it("checks assertions", () => {
		assertThis(true, "must not crash");

		expect(() => {
			assertThis(false, "should not happen in the 1st place");
		}).toThrow(/^should not happen in the 1st place$/);

		expect(() => {
			assertThis(false, "should %s happen in the %sst place", "not", 1);
		}).toThrow(/^should not happen in the 1st place$/);
	});
});

describe("requireThis", () => {
	it("checks preconditions", () => {
		requireThis(true, "must not crash");

		expect(() => {
			requireThis(false, "should %s happen in the %sst place", "not", 1);
		}).toThrow(/^should not happen in the 1st place$/);
	});
});

describe("ensureThis", () => {
	it("checks postconditions", () => {
		ensureThis(true, "must not crash");

		expect(() => {
			ensureThis(false, "should %s happen in the %sst place", "not", 1);
		}).toThrow(/^should not happen in the 1st place$/);
	});
});
