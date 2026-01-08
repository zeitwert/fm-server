---
alwaysApply: true
---

# Run Tests within the dddrive module

After any change, you run the tests to ensure the changes are working as expected.
The tests can be run via `mvn` on the windows command shell.

## run tests as specific as possible to minimize resource consumption

Run one specific test (need to specify the module):

`mvn surefire:test -Dtest=HouseholdMemTest`

Run all tests:

`mvn surefire:test`

Run all tests in all modules WITH recompilation (VERY SLOW):

`mvn clean test`

# Run Build

Before terminating your task, check that the builds are still correctly done.

`mvn clean test-compile -DskipTests -nsu`
