---
alwaysApply: true
---

# Context

This is an ERP application development framework written in Kotlin targeting a Spring Boot deployment.
It is part of a multi-module Maven project, but has no dependencies on other modules and can be compiled and tested standalone.

# Environment

We are on a Windows machine with the command shell.

# UI Module Notes

The `fm-ux` UI module has its own Cursor rules and uses `pnpm` for tooling.
When working on UI changes or tests, consult `fm-ux/.cursor/rules/ui-context.mdc` and `fm-ux/.cursor/rules/ui-tests.mdc`.

# Compilation and Linting

The linting inside the cursor IDE is not reliable, so you need to run the compilation to ensure the code is correct.

`mvn compile -DskipTests -nsu`

or even with full recompilation (VERY SLOW):

`mvn clean test-compile -DskipTests -nsu`
