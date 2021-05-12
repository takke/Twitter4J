Build Instruction using Gradle
==============================

Guide for Twitter4J Developers / Contributors using Gradle

- makes ```jar```, ```javadoc.jar``` and ```sources.jar```

```bash
$ ./gradlew clean build -x test
```

- mavenLocal : deploys to ```~/.m2/repository/org/twitter4j/```.
```bash
$ ./gradlew publishToMavenLocal
```


Update Version
==============

Modify version name
-------------------

- update ```versionName```  in ```build.gradle```
- update ```VERSION``` in ```Version.java```


Release
-------

- Modify version name
  - update ```versionName```  in build.gradle

- Commit it
```bash
$ git add build.gradle
$ git commit "bump to vX.Y.Z"
```

- git tag
```bash
$ git tag vX.Y.Z
$ git push --tags
```

- Release to github.io
```bash
$ mkdir release_to_github_io
$ cd release_to_github_io
$ git clone git@github.com:takke/takke.github.io.git
$ cd ../

// generate artifacts to `release_to_github_io/takke.github.io.git/maven/org/twitter4j/...`
$ ./gradlew clean publish -x test

$ cd release_to_github_io/takke.github.io.git/
$ git status
$ git add maven
$ git status
$ git commit -m "twitter4j vX.Y.Z"
$ git push
```
