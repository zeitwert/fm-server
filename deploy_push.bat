
@echo off

git reset HEAD~1 --quiet
git stash --quiet

git push heroku main

git stash pop --quiet
git add -u --quiet
git commit -m "Deployed to heroku, prepare for next release" --quiet

git push
