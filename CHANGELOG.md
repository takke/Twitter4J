Change Log
==========

v4.0.8-2025xxxx
---------------
- Add Card.description

v4.0.8-20250302
---------------
- Gradle 8.13

v4.0.8-20250101
---------------
- Gradle 8.12

v4.0.8-20240903
---------------
- Add TwitterObjectFactory.removeJSONObject

v4.0.8-20240823
---------------
- Add `User.following`, `User.followedBy`, `User.canDm`
- Kotlin 2.0.0

v4.0.8-20240311
---------------
- OkHttp 4.12.0

v4.0.8-20240309
---------------
- OkHttp 5.0.0-alpha.12

v4.0.8-20240226
---------------
- support core and http2-support only
- v1Resources
- Introduce Kotlin
- OkHttp 5.0.0-alpha.3
- Gradle 7.5 -> 8.6
- Java 1.8

v4.0.8-20221004
---------------
- Add `CHANGELOG.md`
- Add `Status.EditControl` and `Status.initialTweetId` to get edited tweets
- Add `includeExtEditControl` property to enable retrieving edit history
- Add @Nullable/@NotNull annotations to Status
- Remove twitter4j-async module
- Remove twitter4j-appengine module
- Gradle 6.2.2 -> 7.5.1

v4.0.8-20220607
---------------
- Add Query.setNextPage
- Migrate from bintray to github.io

v4.0.8-20200820
---------------
- Add `additional_media_info` for video (#344)
- Add `OnBuildOkHttpClientCallback` to support TLS 1.2 (#329)
- Remove user obsolete values (#325)
- Fix #299 re duplicated interface methods (#301)
- More flexible video uploading (#289)
- Add `exclude_reply_user_ids` for `auto_populate_reply_metadata` (#290)
- Support creating ext alt text (#291)
