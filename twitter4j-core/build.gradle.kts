plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvmToolchain(11)

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
// ⚠️ mastodon4j の教訓:
//   - components["kotlin"] を使った独自 publication は中身が空のjar(648B)を生成する問題があった。
//     → KMP が自動生成する publication を使う。
//   - twitter4j-core は project 名が既に "twitter4j-core" のため、KMP自動生成の
//     kotlinMultiplatform(root) publication のデフォルト artifactId も "twitter4j-core" となり、
//     android publication の artifactId を "twitter4j-core" にオーバーライドすると衝突する。
//   - よって root(kotlinMultiplatform) publication の publish を無効化し、
//     実クラスを含む android publication のみを org.twitter4j:twitter4j-core として publish する。
//     （従来同様「JVM jar が org.twitter4j:twitter4j-core で取れる」状態を維持）
// -------------------------------------------------------------------------
publishing {
    publications {
        // android publication の artifactId を twitter4j-core にオーバーライド
        // （デフォルトは twitter4j-core-android）
        withType<MavenPublication> {
            if (name == "android") {
                artifactId = "twitter4j-core"
            }
        }
    }
    repositories {
        maven {
            url = uri("file://${rootDir}/release_to_github_io/takke.github.io/maven/")
        }
    }
}

// root(kotlinMultiplatform) publication は android publication と artifactId が衝突するため、
// この段階では publish タスクを無効化する（android publication のみ publish）。
tasks.matching {
    it.name.startsWith("publishKotlinMultiplatformPublication")
}.configureEach {
    enabled = false
}
