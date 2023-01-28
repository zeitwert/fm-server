
@echo off

mvn -B -Darguments=-DskipTests clean release:prepare -Dresume=false

git reset HEAD~1
git stash
git push heroku main
git stash pop
git add -u
git commit -m "Deployed to heroku, prepare for next release"

del pom.xml.releaseBackup

exit



for /f %%a in ('git describe --tags --abbrev^=0 main') do (
	set LATEST_TAG=%%a
)

echo Pushing %LATEST_TAG% to heroku
git push heroku %LATEST_TAG%:main

git add -u
git commit -m "released to heroku"

