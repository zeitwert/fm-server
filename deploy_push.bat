
@echo off

rem go back to release version commit
git reset HEAD~1 --quiet
git stash --quiet

rem push release version to heroku
git push heroku main

rem roll forward to snapshot version commit
git stash pop --quiet
git add -u --quiet
git commit -m "Deployed to heroku, prepare for next release" --quiet

rem don't push, makes it easier to roll back if something goes wrong
rem git push
