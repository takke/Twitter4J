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

- Commit it
```bash
$ git add build.gradle twitter4j-core/src/main/java/twitter4j/Version.java
$ git commit "bump to vX.Y.Z"
```

- git tag
```bash
$ git tag vX.Y.Z
$ git push --tags
```

- Release to github.io (takke.github.io/maven)

```bash
// clone takke.github.io next to this repository (only once)
$ cd ../
$ git clone git@github.com:takke/takke.github.io.git
$ cd Twitter4J

// generate artifacts to `../takke.github.io/maven/org/twitter4j/...`
// (output dir can be overridden by -PgithubIoMavenDir=/path/to/takke.github.io/maven)
$ ./gradlew clean publishAllPublicationsToGithubIoRepository -x test

$ cd ../takke.github.io/
$ git status
$ git add maven
$ git commit -m "twitter4j vX.Y.Z"
$ git push
```

- NOTE: twitter4j-v2 depends on twitter4j-core, so publish twitter4j-v2 together (see `../publish_all_libs_to_github_io.sh`).
- NOTE: Never re-publish the same version. Gradle caches released versions permanently.
