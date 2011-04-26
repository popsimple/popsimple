set -e
echo Creating branch...
git push origin HEAD:$1
git checkout -b $1 origin/$1
echo Done

