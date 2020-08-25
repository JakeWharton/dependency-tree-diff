# Dependency Tree Diff

An intelligent diff tool for the output of Gradle's `dependencies` task which always shows
the path to the root dependency.

```diff
 +--- com.squareup.sqldelight:android-driver:1.4.0
 |    +--- com.squareup.sqldelight:runtime-jvm:1.4.0
-|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72
-|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.3.72 (*)
+|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0
+|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.4.0 (*)
-|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.3.72
+|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.3.72 -> 1.4.0
-|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 (*)
+|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0 (*)
 \--- com.squareup.sqldelight:rxjava2-extensions:1.4.0
-     \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 (*)
+     \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0 (*)
```

Unlike a normal `diff`, unchanged entries are displayed only when they are a parent dependency
to one that changed.

Compare the above diff to `diff -U 0`:

```diff
@@ -31,3 +31,3 @@
-|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72
-|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.3.72 (*)
-|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.3.72
+|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0
+|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.4.0 (*)
+|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.3.72 -> 1.4.0
@@ -35 +35 @@
-|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 (*)
+|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0 (*)
@@ -40 +40 @@
-|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 (*)
+|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0 (*)
@@ -51 +51 @@
```

Note how there is no context as to what is affected.

In this particular sample we can reach the root dependencies with `diff -U 2`:

```diff
@@ -29,14 +29,14 @@
 +--- com.squareup.sqldelight:android-driver:1.4.0
 |    +--- com.squareup.sqldelight:runtime-jvm:1.4.0
-|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72
-|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.3.72 (*)
-|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.3.72
+|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0
+|    |    |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.4.0 (*)
+|    |    \--- org.jetbrains.kotlin:kotlin-stdlib-common:1.3.72 -> 1.4.0
 |    +--- androidx.sqlite:sqlite:2.1.0 (*)
-|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 (*)
+|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0 (*)
 |    \--- androidx.sqlite:sqlite-framework:2.1.0 (*)
 +--- com.squareup.sqldelight:rxjava2-extensions:1.4.0
 |    +--- com.squareup.sqldelight:runtime:1.4.0
 |    |    \--- com.squareup.sqldelight:runtime-jvm:1.4.0 (*)
-|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 (*)
+|    +--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.72 -> 1.4.0 (*)
 |    \--- io.reactivex.rxjava2:rxjava:2.2.5 -> 2.2.19 (*)
 \--- com.squareup.inject:inflation-inject:0.5.1
@@ -49,4 +49,4 @@
```

Now, however, additional unchanged dependencies are displayed such as `androidx.sqlite` and the
SQLDelight runtime dependency by the RxJava 2 extensions artifact.

For deep dependency chains, `-U 2` will not be enough to see all of the roots. Increasing the value
will only show more unchanged dependencies. `dependency-tree-diff` will always display the
minimal subset needed to provide context.


## Usage

The tool parses the output of Gradle's `dependencies` task. Specify `--configuration <name>` when
running the task so that only a single tree will be shown.

```
$ ./gradlew :app:dependencies --configuration releaseRuntimeClasspath > old.txt
$ # Update a dependency...
$ ./gradlew :app:dependencies --configuration releaseRuntimeClasspath > new.txt
$ ./dependency-tree-diff.jar old.txt new.txt
```

See `src/test/fixtures/` for example outputs and their expected diffs.

## Install

**Mac OS**

```
$ brew install JakeWharton/repo/dependency-tree-diff
```

**Other**

Download standalone JAR from
[latest release](https://github.com/JakeWharton/dependency-tree-diff/releases/latest).
On MacOS and Linux you can `chmod +x` and execute the `.jar` directly.
On Windows use `java -jar`.


## License

    Copyright 2020 Jake Wharton

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
