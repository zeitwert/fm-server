
@echo off

git push

mvn clean

call mvn -B release:prepare -DpushChanges=false -Darguments=-DskipTests -Dresume=true
