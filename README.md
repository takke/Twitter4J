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

## 4.1.0 Beta4 (2018-07-xx)
  - Support video uploading feature!
    * Based on ```#255``` [by dk8996](https://github.com/yusuke/twitter4j/pull/255) and ```#258``` [by rjean-gilles](https://github.com/yusuke/twitter4j/pull/258)
    * Builder pattern configuration for chunked uploading by takke
    ```java
    UploadedMedia media = twitter.uploadMediaChunked(
            new ChunkedUploadConfiguration.Builder()
                    .movie()
                    .from(new File(movieFileName))
                    .build());

    StatusUpdate update = new StatusUpdate(text);
    update.setMediaIds(media.getMediaId());
    twitter.updateStatus(update);
    ```
    * Add GIF uploading feature by takke
    ```java
    UploadedMedia media = twitter.uploadMediaChunked(
            new ChunkedUploadConfiguration.Builder()
                    .gif()
                    .from(new File(movieFileName))
                    .build());
    ...
    ```
    * Add callback for monitoring by takke (see [example](https://github.com/takke/twitter4j/blob/master/twitter4j-examples/src/main/java/twitter4j/examples/tweets/UploadMovie.java#L57))

## 4.1.0 Beta3 (2018-07-13)
  - ```#262``` [Add auto_populate_reply_metadata by abhishek1203](https://github.com/yusuke/twitter4j/pull/262)
  - Add exclude_reply_user_ids for auto_populate_reply_metadata by takke
  - ```#279``` [Add method "setUploadBaseURL()" in ConfigurationBuilder by moko256](https://github.com/yusuke/twitter4j/pull/279)

## 4.1.0 Beta2 (2018-07-12)
  - ```#243``` [Add un-retweet api by aniketdivekar](https://github.com/yusuke/twitter4j/pull/243)
  - ```#268``` [Fix javadoc of EntitySupport by akshanshjain95](https://github.com/yusuke/twitter4j/pull/268)
  - ```#266``` [Make StreamListener public (for Kotlin) by IEnoobong](https://github.com/yusuke/twitter4j/pull/266)

## 4.1.0 Beta1 (2018-07-10)
  - Gradle build support by takke
  - ```#260``` [Support new direct message API (events api, create message with attach media, etc...) by takke](https://github.com/yusuke/twitter4j/pull/260)
  - ```#280``` [Add quoted_status_permalink by takke](https://github.com/yusuke/twitter4j/pull/280)
  - ```#232``` [Add property for &quot;tweet_volume&quot; to Trend by Bunkerbewohner ](https://github.com/yusuke/twitter4j/pull/232)
  - ```#248``` [Add attachment_url by takke](https://github.com/yusuke/twitter4j/pull/248)
  - ```#265``` [Add URLs for profile_banner by moko256](https://github.com/yusuke/twitter4j/pull/265)
  - ```#271``` [Add 400x400 size of profile image by takke](https://github.com/yusuke/twitter4j/pull/271)
  - ```#275``` [Care empty string of profile image url by takke](https://github.com/yusuke/twitter4j/pull/275)
  - ```#274``` [Add getter of OkHttpClient from instance of AlternativeHttpClientImpl by moko256](https://github.com/yusuke/twitter4j/pull/274)
