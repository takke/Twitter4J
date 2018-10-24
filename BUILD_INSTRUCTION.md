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

