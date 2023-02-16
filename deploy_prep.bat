
@echo off

git push

mvn release:clean

call mvn -B release:prepare -DpushChanges=false -Darguments=-DskipTests
