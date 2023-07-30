
@echo off

git push

mvn clean release:clean

rem don't push changes to remote repo, we'll do that manually later
rem otherwise new snapshot version will already be pushed to heroku
mvn release:prepare -D arguments="-D skipTests" -D pushChanges=false
