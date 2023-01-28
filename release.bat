
@echo off

mvn -B clean release:prepare -Dresume=false

for /f %%a in ('git describe --tags --abbrev^=0 main') do (
	set LATEST_TAG=%%a
)

echo Pushing %LATEST_TAG% to heroku
git push heroku %LATEST_TAG%:main

git add -u
git commit -m "released to heroku"

del pom.xml.releaseBackup
