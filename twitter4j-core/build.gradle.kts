plugins {
    kotlin("multiplatform")
    `maven-publish`
}

kotlin {
    jvmToolchain(11)

    // mastodon4j と同方式: AGP は使わず jvm ターゲットを "android" という名前で構成する
    jvm("android")

    sourceSets {
        // JVM(android)メインソースセット
        // 既存の compileOnly 依存（slf4j等4つ）を維持
        val androidMain by getting {
            dependencies {
                compileOnly("org.slf4j:slf4j-api:1.7.12")
                compileOnly("commons-logging:commons-logging-api:1.1")
                compileOnly("org.jetbrains:annotations:16.0.2")
                compileOnly("log4j:log4j:1.2.17")
            }
        }
        // JVM(android)ユニットテストソースセット
        val androidTest by getting {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
                implementation("com.googlecode:kryo:1.04")
                implementation("org.seleniumhq.selenium:selenium-htmlunit-driver:2.0.0")
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
