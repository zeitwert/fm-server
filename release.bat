
@echo off

mvn -B clean release:prepare -Dresume=false

git stash

for /f %%a in ('git describe --tags --abbrev^=0 main') do (
	set LATEST_TAG=%%a
)

echo Checking out %LATEST_TAG%
git checkout %LATEST_TAG%

git push heroku main

git switch -

git stash pop

git add -u
git commit -m "released to heroku"

del pom.xml.releaseBackup