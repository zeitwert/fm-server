import { describe, it, expect, beforeEach, vi } from "vitest";

// Unmock i18n and react-i18next for these tests so we can test the real implementation
vi.unmock("./index");
vi.unmock("react-i18next");

// Import the real i18n module after unmocking
import i18n, { changeLanguage } from "./index";

describe("i18n", () => {
	beforeEach(async () => {
		// Reset to default German language before each test
		await i18n.changeLanguage("de");
	});

	describe("basic translations", () => {
		it("resolves nested keys correctly", () => {
			expect(i18n.t("account:label.name")).toBe("Name");
		});

		it("resolves action keys correctly", () => {
			expect(i18n.t("common:action.save")).toBe("Speichern");
		});

		it("resolves message keys correctly", () => {
			expect(i18n.t("common:message.noNotes")).toBe("Keine Notizen");
		});

		it("returns key when translation is missing", () => {
			expect(i18n.t("nonexistent.key")).toBe("nonexistent.key");
		});
	});

	describe("ICU pluralization", () => {
		it("handles zero count", () => {
			expect(i18n.t("account:label.entityCount", { count: 0 })).toBe("Keine Kunden");
		});

		it("handles count of one", () => {
			expect(i18n.t("account:label.entityCount", { count: 1 })).toBe("1 Kunde");
		});

		it("handles count greater than one", () => {
			expect(i18n.t("account:label.entityCount", { count: 5 })).toBe("5 Kunden");
		});

		it("handles large counts", () => {
			expect(i18n.t("account:label.entityCount", { count: 100 })).toBe("100 Kunden");
		});
	});

	describe("language switching", () => {
		it("switches to English", async () => {
			await i18n.changeLanguage("en");
			expect(i18n.t("account:label.entity")).toBe("Account");
		});

		it("switches back to German", async () => {
			await i18n.changeLanguage("en");
			await i18n.changeLanguage("de");
			expect(i18n.t("account:label.entity")).toBe("Kunde");
		});

		it("updates translations after language change", async () => {
			expect(i18n.t("common:action.save")).toBe("Speichern");
			await i18n.changeLanguage("en");
			expect(i18n.t("common:action.save")).toBe("Save");
		});

		it("works with the changeLanguage helper function", async () => {
			changeLanguage("en");
			// Wait for the language change to complete
			await new Promise((resolve) => setTimeout(resolve, 10));
			expect(i18n.language).toBe("en");
		});

		it("does not change language when locale is undefined", () => {
			const originalLanguage = i18n.language;
			changeLanguage(undefined);
			expect(i18n.language).toBe(originalLanguage);
		});

		it("does not change language when already set to same locale", async () => {
			// First ensure we're on "de"
			await i18n.changeLanguage("de");
			// The changeLanguage helper checks if the language is already set
			// and only calls i18n.changeLanguage if different
			expect(i18n.language).toBe("de");
			changeLanguage("de");
			// Language should still be "de" (no change)
			expect(i18n.language).toBe("de");
		});
	});

	describe("interpolation", () => {
		it("interpolates entity in createEntity action", () => {
			expect(i18n.t("common:action.createEntity", { entity: "Account" })).toBe("Account erstellen");
		});

		it("interpolates message in error message", () => {
			expect(i18n.t("common:message.error", { message: "Test error" })).toBe("Fehler: Test error");
		});

		it("interpolates entity in loadError message", () => {
			expect(i18n.t("common:message.loadError", { entity: "Kunden" })).toBe(
				"Fehler beim Laden der Kunden"
			);
		});

		it("interpolates pagination range", () => {
			expect(i18n.t("common:message.paginationRange", { start: 1, end: 10, total: 100 })).toBe(
				"1-10 von 100"
			);
		});
	});

	describe("ICU pluralization in English", () => {
		beforeEach(async () => {
			await i18n.changeLanguage("en");
		});

		it("handles zero count in English", () => {
			expect(i18n.t("account:label.entityCount", { count: 0 })).toBe("No accounts");
		});

		it("handles count of one in English", () => {
			expect(i18n.t("account:label.entityCount", { count: 1 })).toBe("1 account");
		});

		it("handles count greater than one in English", () => {
			expect(i18n.t("account:label.entityCount", { count: 5 })).toBe("5 accounts");
		});
	});

	describe("interpolation in English", () => {
		beforeEach(async () => {
			await i18n.changeLanguage("en");
		});

		it("interpolates entity in createEntity action", () => {
			expect(i18n.t("common:action.createEntity", { entity: "Account" })).toBe("Create Account");
		});

		it("interpolates message in error message", () => {
			expect(i18n.t("common:message.error", { message: "Test error" })).toBe("Error: Test error");
		});
	});
});
