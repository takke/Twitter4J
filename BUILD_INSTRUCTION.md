Build Instruction using Gradle
==============================

Guide for Twitter4A Developers / Contributors using Gradle

- makes ```jar```, ```javadoc.jar``` and ```sources.jar```

```bash
$ ./gradlew clean build -x test
```

- mavenLocal : deploys to ```~/.m2/repository/org/twitter4a/```.
```bash
$ ./gradlew publishToMavenLocal
```

- upload to bintray
```bash
$ ./gradlew bintrayUpload
```


Release to Repository
=====================

Modify version name
-------------------

- update ```versionName```  in ```build.gradle```
- update ```VERSION``` in ```Version.java```

Build, upload to bintray
------------------------

```bash
$ ./gradlew clean build -x test
```
```bash
$ ./gradlew bintrayUpload
```

check on bintray
----------------
https://bintray.com/panecraft/maven/Twitter4A

Tagging
-------

```bash
$ git tag -a 4.1.0-beta1 678ee0a
$ git push origin 4.1.0-beta1
```


References
==========

- [まくまく Gradle ノート](http://maku77.github.io/gradle/)
- [Gradle Mavenプラグイン\(Hishidama's Gradle Maven plugin Memo\)](http://www.ne.jp/asahi/hishidama/home/tech/groovy/gradle/maven.html)

- https://github.com/bintray/bintray-examples/blob/master/gradle-example/build.gradle
- https://github.com/bintray/bintray-examples/tree/master/gradle-bintray-plugin-examples
- https://github.com/bintray/gradle-bintray-plugin


TODO
====

- Fix Contributors
- Java 1.8
- Remove media-support module
- Remove stream module
