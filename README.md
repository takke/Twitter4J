Twitter4A
=========

[ ![Download](https://api.bintray.com/packages/panecraft/maven/Twitter4A/images/download.svg) ](https://bintray.com/panecraft/maven/Twitter4A/_latestVersion)

<!--*This repository is provided for convenience of existing users of Twitter4J for the stopgap hiatus of the official Twitter4J.*-->


Twitter4A is a Twitter API binding library for the Java language licensed under Apache License 2.0 forked from Twitter4J.

Twitter4A includes software from JSON.org to parse JSON response from the Twitter API. You can see the license term at http://www.JSON.org/license.html



```
LICENSE.txt - the terms of license of this software
build.gradle - gradle script
README.md - this file
twitter4a-core - core component : support REST and Search API
twitter4a-examples - examples
twitter4a-media-support - media API support
twitter4a-async - Async API support : depending on twitter4j-core
twitter4a-stream - Streaming API support : depending on twitter4j-core and twitter4j-async
twitter4a-http2-support - HTTP/2 support : adds HTTP/2 support, boosts Twitter4J performance, reduce packets, save the earth
twitter4a-appengine - support appengine
```


Get Started
===========

```groovy
allprojects {
  repositories {
    ...
    maven { url "http://dl.bintray.com/panecraft/Twitter4A" }
  }
}
```

```groovy
implementation "org.twitter4a:twitter4a-core:$twitter4aVersion"
```


Migrate from Twitter4J
======================

- Replace ```import twitter4j``` with ```import twitter4a``` for all files
- Replace another reference like ```twitter4j.Status``` to ```twitter4a.Status```
- Take care to grep with ```twitter4j``` to find using like below:
```java
-   System.setProperty("twitter4j.http.retryCount", "1");
+   System.setProperty("twitter4a.http.retryCount", "1");
```
- Replace ```twitter4j``` with ```twitter4a``` in proguard settings (```proguard-rules.pro```)


CHANGELOG
=========

ChangeLog since official Twitter4J 4.0.6

## 4.1.0 Beta5 (2018-07-xx)
  - Rename to Twitter4A
  - Rename package from twitter4j.* to twitter4a.*
  - Drop maven support (Remove pom.xml)
  

## 4.1.0 Beta4 (2018-07-19)
  - Support video uploading feature!
    * Based on ```#255``` [by dk8996](https://github.com/yusuke/twitter4j/pull/255) and ```#258``` [by rjean-gilles](https://github.com/yusuke/twitter4j/pull/258)
    * Builder pattern configuration for chunked uploading
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
    * Add GIF uploading feature
    ```java
    UploadedMedia media = twitter.uploadMediaChunked(
            new ChunkedUploadConfiguration.Builder()
                    .gif()
                    .from(new File(movieFileName))
                    .build());
    ...
    ```
    * Add callback for monitoring (see [example](https://github.com/takke/twitter4j/blob/master/twitter4j-examples/src/main/java/twitter4j/examples/tweets/UploadMovie.java#L57))

## 4.1.0 Beta3 (2018-07-13)
  - ```#262``` [Add auto_populate_reply_metadata by abhishek1203](https://github.com/yusuke/twitter4j/pull/262)
  - Add exclude_reply_user_ids for auto_populate_reply_metadata (see [example](https://github.com/takke/twitter4j/blob/master/twitter4j-examples/src/main/java/twitter4j/examples/tweets/ReplyStatus.java#L51-L53))
  - ```#279``` [Add method "setUploadBaseURL()" in ConfigurationBuilder by moko256](https://github.com/yusuke/twitter4j/pull/279)

## 4.1.0 Beta2 (2018-07-12)
  - ```#243``` [Add un-retweet api by aniketdivekar](https://github.com/yusuke/twitter4j/pull/243)
  - ```#268``` [Fix javadoc of EntitySupport by akshanshjain95](https://github.com/yusuke/twitter4j/pull/268)
  - ```#266``` [Make StreamListener public (for Kotlin) by IEnoobong](https://github.com/yusuke/twitter4j/pull/266)

## 4.1.0 Beta1 (2018-07-10)
  - Gradle build support
  - ```#260``` [Support new direct message API (events api, create message with attach media, etc...) by takke](https://github.com/yusuke/twitter4j/pull/260)
  - ```#280``` [Add quoted_status_permalink by takke](https://github.com/yusuke/twitter4j/pull/280)
  - ```#232``` [Add property for &quot;tweet_volume&quot; to Trend by Bunkerbewohner ](https://github.com/yusuke/twitter4j/pull/232)
  - ```#248``` [Add attachment_url by takke](https://github.com/yusuke/twitter4j/pull/248)
  - ```#265``` [Add URLs for profile_banner by moko256](https://github.com/yusuke/twitter4j/pull/265)
  - ```#271``` [Add 400x400 size of profile image by takke](https://github.com/yusuke/twitter4j/pull/271)
  - ```#275``` [Care empty string of profile image url by takke](https://github.com/yusuke/twitter4j/pull/275)
  - ```#274``` [Add getter of OkHttpClient from instance of AlternativeHttpClientImpl by moko256](https://github.com/yusuke/twitter4j/pull/274)


Contributors
============

```
Aaron Rankin <aaron at sproutsocial.com> @aaronrankin
Adam Samet <asamet at twitter.com> @damnitsamet
Adrian Petrescu <apetresc at gmail.com> @apetresc
Akash Nawani <akash.nawani at gmail.com> @AkashNawani
Alan Gutierrez <alan at blogometer.com>
Alessandro Bahgat <ale.bahgat at gmail.com> @abahgat
Alex Kira <alex.kira at gmail.com> @alexkira
Amine Bezzarga <abezzarg at gmail.com> @Go_Mino
Anton Novopashin <antonevane at gmail.com> @anton_evane
Andy Boothe <andy.boothe at gmail.com> @sigpwned
Blake Barnes <blake.barnes at gmail.com>
Borja Fernández @BorjaL
Bruno Torres Goyanna <bgoyanna at gmail.com> @bgoyanna
Chris Nix
Chris Romary
Ciaran Jessup <ciaranj at gmail.com> @ciaran_j
Cole Wen <wennnnke at gmail.com> @Pigwen
Dan Checkoway <dcheckoway at gmail.com>
Danaja Maldeniya <danajamkdt at gmail.com> @DanajaT
Daniel Huckaby <handlerexploit at gmail.com> @HandlerExploit
Denis Bardadym <bardadymchik at gmail.com> @bardadymchik
Dimitry Kudryavtsev <dk8996 at gmail.com> @dk8996
Dong Wang <dong at twitter.com> @dongwang218
Eli Israel <eli at meshfire.com> @eliasisrael
Eric Jensen <ej at twitter.com> @ej
Fiaz Hossain <fiaz at twitter.com> @fiazhossain
Gabriel Zanetti @pupi1985
George Ludwig <georgeludwig at gmail.com> @georgeludwig
Gonçalo Silva <goncalossilva at gmail.com> @goncalossilva
Hemant Kumar <hemant.singh13 at gmail.com> @hemantsingh1309
Hendy Irawan <hendy@hendyirawan.com>
Hiroaki Takeuchi <takke30 at gmail.com> @takke
Hiroki Takemoto <pompopo at gmail.com> @pompopo
Hiroshi Sugimori <mamamadata at gmail.com> @ham1975
Hitoshi Kunimatsu <hkhumanoid at gmail.com>
Igor Brigadir <igor.brigadir at insight-centre.org> @igorbrigadir
JJ Kelley <jjthemachine at gmail.com> @jjthemachine
Jacob Elder <jacob.elder@gmail.com> @jelder
Jacob S. Hoffman-Andrews <jsha at twitter.com> @j4cob
Jason Webb <bigjasonwebb at gmail.com> @BigJasonWebb
Jeff Buchbinder @jbuchbinder
Jenny Loomis <jenny at rockmelt.com>
Joe Sondow @JoeSondow
John Corwin <jcorwin at twitter.com> @johnxorz
John Sirois <jsirois at twitter.com> @johnsirois
Julien Letrouit <julien.letrouit at gmail.com> @jletroui
Jumpei Matsuda <j.matsuda.bb26th at gmail.com> @fat_daruuuuma
Keiichi Hirano <hirano.kei1 at gmail.com> @haushinka2dx
Kenji Yoshida <6b656e6a69 at gmail.com> @xuwei_k
Komiya Atsushi <komiya.atsushi at gmail.com> @komiya_atsushi
Koutaro Mori <koutaro.mo at gmail.com> @moko256
Linker Lin <linker.lin at live.com>
Ludovico Fischer @ludovicofischer
marr-masaaki <marr fiveflavors at gmail.com> @marr
matsumo <matsumo at ce.ns0.it> @sardinej
Mathias Kahl <mathias.kahl at gmail.com> @Bunkerbewohner1
Matteo Nicoletti <matteo at kaosmos.it> @kaosmos
Matteo Villa @mttvll
Max Penet <m at qbits.cc> @mpenet
Mike Champion <mike at graysky.org> @graysky
Mocel <docel77 at gmail.com> @Mocel
MortyChoi <soapsign at gmail.com> @Soapsign
Myyk Seok <myyk.seok at gmail.com> @myyk
Naoya Hatayama <applepedlar at gmail.com> @ApplePedlar
Ngoc Dao <ngocdaothanh@gmail.com> @ngocdaothanh
Nobutoshi Ogata <n-ogata at cnt.biglobe.co.jp> @nobu666
Norbert Bartels <n.bartels at phpmonkeys.de>
Nicolas Bouillon <nicolas at bouil.org>
Nicholas Dellamaggiore <nick.dellamaggiore at gmail.com> @nickdella
Nils Haldenwang @nilshaldenwang
Niv Singer <niv at innerlogics.com> @nivs
Pierre Lanvin <pierre.lanvin@gmail.com> @planvin
Pieter Meiresone @_MPieter
Perry Sakkaris <psakkaris at gmail.com>
Rajesh Koilpillai <rajesh.koilpillai at gmail.com> @rajeshkp
Roberto Estrada <robestradac at gmail.com>
Robson Cassol <robsoncassol at gmail.com> @robsoncassol
Roy Reshef <royreshef at gmail.com> @tsipo
Rui Silva
Sam Pullara <sam at sampullara.com> @sampullara
Sdk0815 <developer at terumode.net> @Sdk0815
Shane Gibson <shane.a.gibson at gmail.com> @shaneagibson
Shintaro Watanabe <shintaro.watanabe1226@gmail.com> @shin_taro_1226
Simone Aiello <aiello.simone103 at gmail.com> @saiello103
Steve Lhomme <slhomme at matroska.org> @robux4
Steven Liu <steven at twitter.com> @steven
Q.P.Liu <qpliu at yahoo.com>
Rémy Rakic <remy.rakic at gmail.com> @lqd
Talal Ahmed <talal at venexel.ca>
Takao Nakaguchi <takao.nakaguchi at gmail.com> @takawitter
Takatoshi Murakami
Takuro Yamane <alalwww at awairo.net> @alalwww
Tomasz Nurkiewicz @tnurkiewicz
Tomoaki Iwasaki <multicolorworld.shinku at gmail.com> @MulticolorWorld
Tomoaki Takezoe <sumito3478 at gmail.com> @sumito3478
Tomohisa Igarashi <tm.igarashi at gmail.com>
Tyler MacLeod
Will Glozer <will at glozer.net> @ar3te
William Morgan <william at twitter.com> @wm
William O'Hanley <william at wohanley.com> @wohanley
William Vanderhoef <william.vanderhoef at gmail.com> @thePoofy
withgod <noname at withgod.jp> @withgod
Yuichiro Kawano <tresener.yu1ro at gmail.com> @tresener_yu1ro
Yusuke Yamamoto <yusuke at mac.com> @yusuke
Yuto Uehara <mumei.himazin at gmail.com> @mumei_himazin
```