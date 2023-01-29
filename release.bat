
@echo off

mvn -B -Darguments=-DskipTests clean release:prepare -Dresume=false

git reset HEAD~1
git stash
git push heroku main
git stash pop
git add -u
git commit -m "Deployed to heroku, prepare for next release"

del pom.xml.releaseBackup

git push
