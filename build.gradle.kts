// Twitter4J 私家版 - KMP移行 Phase 1
// バージョン方針:
//   - KMP系列は 4.1 系とする（既存の 4.0.8-20250316 を mavenLocal で上書きしないため）
//   - 4.1.0-SNAPSHOT を採用
plugins {
    // KMPモジュール（twitter4j-core）で使用。ルートでは apply しない
    kotlin("multiplatform") version "2.2.21" apply false
    // JVM専用モジュール（twitter4j-http2-support）で使用
    kotlin("jvm") version "2.2.21" apply false
}

// 共通のグループ/バージョン定義
// ⚠️ mastodon4j の教訓: subprojects への java プラグイン一括適用は KMP モジュールと衝突するため行わない。
//    共通設定（プラグイン適用・publish設定）は各モジュール側の build.gradle.kts に寄せる。
allprojects {
    group = "org.twitter4j"
    version = "4.1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://repo.maven.apache.org/maven2") }
    }
}
