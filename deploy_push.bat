
@echo off

rem go back to release version commit
git reset HEAD~1 --quiet
git stash --quiet

rem push release version to heroku
git push heroku main

rem roll forward to snapshot version commit
git stash pop --quiet
git add .
git commit -m "[deploy_push] prepare for next development iteration" --quiet

rem don't push, makes it easier to roll back if something goes wrong
rem git push
