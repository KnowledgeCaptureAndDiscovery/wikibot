#!/bin/bash
git add visualizations/changes-pastnchanges.html
git add visualizations/changes-pastndays.html
git commit -m "latest histogram visual update"
git push origin master

git checkout gh-pages
git pull origin master
git push origin gh-pages
git checkout master
