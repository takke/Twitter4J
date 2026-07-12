plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvmToolchain(11)

    // expect/actual クラス（Date/URI/HttpParameter 等の typealias 戦略）を多用しているため、
    // Beta 警告（KT-61573）を抑制する
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        // kotlin.time.Instant を公開APIで使用するため一括オプトイン
        optIn.add("kotlin.time.ExperimentalTime")
    }

    // mastodon4j と同方式: AGP は使わず jvm ターゲットを "android" という名前で構成する
    jvm("android") {
        // JUnit 5 (Jupiter) を使うため JUnit Platform を有効化する
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }

    // KMP移行 Phase 3: iOS ターゲットを追加（TwitPane 本体の慣例に合わせ iosX64 は含めない）
    // これにより commonMain / iosMain ソースセットが利用可能になる。
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        // 共通ソースセット
        // KMP移行 Phase 3-4 で日付処理を kotlinx-datetime ベースに移行するため依存を追加。
        // バージョンは TwitPane 本体と同一の 0.8.0（kotlin.time.Instant を使う世代）。
        // 過去にバージョン不一致で起動クラッシュ / iOS リンク衝突の実績があるため、必ず 0.8.0 とする。
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.8.0")
            }
        }
        // JVM(android)メインソースセット
        // KMP移行 Phase 3-2 でロギングを再設計し、Class.forName による動的検出（SLF4J / Log4J /
        // Commons Logging）を廃止したため、それらの compileOnly 依存を削除した。
        // jetbrains annotations も core 内で未使用のため削除済み。
        val androidMain by getting {
        }
        // JVM(android)ユニットテストソースセット
        // ネットワーク不要のユニットテスト用に JUnit Jupiter（API + 実行エンジン）を追加
        val androidTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
            }
        }
    }
}

// -------------------------------------------------------------------------
// publishing 設定
// KMP移行 Phase 6: 標準的な KMP publishing に変更。
//   背景: twitter4j-v2-support を KMP 化して commonMain / iOS から twitter4j-core を利用するには、
//   `twitter4j-core` の umbrella（kotlinMultiplatform root）publication が必須。
//   umbrella の Gradle module metadata が各ターゲット（android / iosArm64 / iosSimulatorArm64）の
//   variant を宣言し、metadata(common klib) と iOS klib を解決可能にするため。
//   旧設定（root を無効化し android publication を "twitter4j-core" にオーバーライド）では
//   `twitter4j-core` が JVM(android) variant のみとなり、KMP consumer（v2）の commonMain/iOS 解決が
//   "Couldn't resolve dependency 'org.twitter4j:twitter4j-core'" で失敗する。
//
//   標準 KMP publishing により以下が publish される:
//     - org.twitter4j:twitter4j-core                     … umbrella（module metadata。available-at で各 variant へ redirect）
//     - org.twitter4j:twitter4j-core-android             … JVM(android) の実クラス jar
//     - org.twitter4j:twitter4j-core-iosarm64            … iOS klib
//     - org.twitter4j:twitter4j-core-iossimulatorarm64   … iOS klib
//   Gradle module metadata を使う consumer（TwitPane / v2）は `org.twitter4j:twitter4j-core` を
//   参照するだけで android / iOS の適切な variant に解決される（従来の Android ビルドも動作）。
//
//   ⚠️ mastodon4j の教訓（components["kotlin"] の空jar問題）は KMP 自動生成 publication を使うことで回避済み。
//      実クラスは twitter4j-core-android jar に含まれる（空ではない）。
// -------------------------------------------------------------------------
publishing {
    repositories {
        maven {
            url = uri("file://${rootDir}/release_to_github_io/takke.github.io/maven/")
        }
    }
}
