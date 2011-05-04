#!/bin/bash
set -e
git config --global push.default tracking
cat ./$(git rev-parse --show-cdup)/scripts/git/bash_profile >> ~/.bash_profile
chmod +x ./$(git rev-parse --show-cdup)/scripts/git/*.sh
cd ./$(git rev-parse --show-cdup) 
#echo cd `pwd` >> ~/.bash_profile

