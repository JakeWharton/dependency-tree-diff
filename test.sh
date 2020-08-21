#!/usr/bin/env bash

success=0
failures=0
for d in fixtures/*/; do
	echo -n "$(basename "$d")… "
	diff=$(diff -U 0 "$d""expected.txt" <(./dependency-tree-diff.main.kts "$d""old.txt" "$d""new.txt"))
	if [[ $? == 0 ]]; then
		echo "✅"
		success=$((success + 1))
	else
		echo "🚫"
		echo "$diff"
		echo
		failures=$((failures + 1))
	fi
done

echo
echo "$((success + failures)) tests, $success pass, $failures fail"
if [[ $failures != 0 ]]; then
	exit 1
fi
