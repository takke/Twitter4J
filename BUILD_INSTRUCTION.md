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

- Release to bintray
```bash
$ ./gradlew clean bintrayUpload -x test
```

- Publish [package](https://bintray.com/takke/maven/twitter4j-takke-mod) on bintray

