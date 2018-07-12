# Twitter4J

[ ![Download](https://api.bintray.com/packages/panecraft/maven/Twitter4J/images/download.svg) ](https://bintray.com/panecraft/maven/Twitter4J/_latestVersion)


Twitter4J is a Twitter API binding library for the Java language licensed under Apache License 2.0.

This repository is provided for convenience of existing users of Twitter4J for the stopgap hiatus of the official Twitter4J.

# Get Started

```groovy
allprojects {
  repositories {
    ...
    maven { url "http://dl.bintray.com/panecraft/Twitter4J" }
  }
}
```

```groovy
implementation "org.twitter4j:twitter4j-core:$twitter4jVersion"
```

# CHANGELOG

ChangeLog since official Twitter4J 4.0.6

## 4.1.0 Beta2
  - ```#243``` [Add un-retweet api by aniketdivekar](https://github.com/yusuke/twitter4j/pull/243)
  - ```#268``` [Fix javadoc of EntitySupport by akshanshjain95](https://github.com/yusuke/twitter4j/pull/268)
  - ```#266``` [Make StreamListener public (for Kotlin) by IEnoobong](https://github.com/yusuke/twitter4j/pull/266)

## 4.1.0 Beta1
  - ```#260``` [Support direct messages events api (create message with attach media) by takke](https://github.com/yusuke/twitter4j/pull/260)
  - ```#280``` [Support quoted_status_permalink by takke](https://github.com/yusuke/twitter4j/pull/280)
  - ```#232``` [Add property for &quot;tweet_volume&quot; to Trend, YouTrack Issue #TFJ-886 by Bunkerbewohner ](https://github.com/yusuke/twitter4j/pull/232)
  - ```#248``` [Support attachment_url by takke](https://github.com/yusuke/twitter4j/pull/248)
  - ```#265``` [Add URLs for profile_banner by moko256](https://github.com/yusuke/twitter4j/pull/265)
  - ```#271``` [Add 400x400 size of profile image by takke](https://github.com/yusuke/twitter4j/pull/271)
  - ```#275``` [Care empty string of profile image url by takke](https://github.com/yusuke/twitter4j/pull/275)
  - ```#274``` [Add getter of OkHttpClient from instance of AlternativeHttpClientImpl by moko256](https://github.com/yusuke/twitter4j/pull/274)
  - Gradle build support [takke/twitter4j at switch_to_gradle](https://github.com/takke/twitter4j/tree/switch_to_gradle "takke/twitter4j at switch_to_gradle")
