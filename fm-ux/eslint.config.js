import js from "@eslint/js";
import tseslint from "@typescript-eslint/eslint-plugin";
import tsparser from "@typescript-eslint/parser";
import react from "eslint-plugin-react";
import reactHooks from "eslint-plugin-react-hooks";
import prettier from "eslint-plugin-prettier";
import prettierConfig from "eslint-config-prettier";
import globals from "globals";

export default [
	js.configs.recommended,
	prettierConfig,
	{
		files: ["**/*.{ts,tsx}"],
		languageOptions: {
			parser: tsparser,
			parserOptions: {
				ecmaVersion: "latest",
				sourceType: "module",
				ecmaFeatures: {
					jsx: true,
				},
			},
			globals: {
				...globals.browser,
				React: "readonly",
			},
		},
		plugins: {
			"@typescript-eslint": tseslint,
			react,
			"react-hooks": reactHooks,
			prettier,
		},
		rules: {
			...tseslint.configs.recommended.rules,
			...react.configs.recommended.rules,
			...reactHooks.configs.recommended.rules,
			"prettier/prettier": "error",
			"react/react-in-jsx-scope": "off",
			"react/prop-types": "off",
		},
		settings: {
			react: {
				version: "detect",
			},
		},
	},
	{
		ignores: ["dist/", "node_modules/"],
	},
];
