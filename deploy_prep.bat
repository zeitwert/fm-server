
@echo off

git push

mvn release:clean

mvn release:prepare -D arguments="-D skipTests" -D pushChanges=false
