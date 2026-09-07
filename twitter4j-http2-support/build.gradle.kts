// twitter4j-http2-support は OkHttp ベースの JVM 専用実装のため KMP 化はせず kotlin("jvm") のまま。
plugins {
    kotlin("jvm")
    `maven-publish`
}

kotlin {
    jvmToolchain(11)
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    // sourcesJar / javadocJar を java コンポーネントに追加（従来の artifacts を踏襲）
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc>().configureEach {
    setFailOnError(false)
    options.encoding = "UTF-8"
}

dependencies {
    // KMP モジュール twitter4j-core の JVM(android) バリアントを解決する
    implementation(project(":twitter4j-core"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.2.0")
    // this is just to install alpn-boot jar to local .m2 repository. to be used by maven-surefire-plugin and included in the bootclasspath
    testImplementation("org.mortbay.jetty.alpn:alpn-boot:8.1.7.v20160121")
}

// publishing: 従来同様 components.java(=components["java"]) ベース
publishing {
    publications {
        create<MavenPublication>("MyPublication") {
            from(components["java"])
            groupId = "org.twitter4j"
            artifactId = "twitter4j-http2-support"
            version = project.version.toString()
        }
    }
    // takke.github.io/maven (GitHub Pages の静的 Maven リポジトリ) への公開先
    // 既定は ../takke.github.io/maven (git clone git@github.com:takke/takke.github.io.git)
    // -PgithubIoMavenDir=/path/to/takke.github.io/maven で上書き可能
    // publish: ./gradlew publishAllPublicationsToGithubIoRepository -x test
    repositories {
        maven {
            name = "githubIo"
            val dir = project.findProperty("githubIoMavenDir")?.toString() ?: "${rootDir}/../takke.github.io/maven"
            url = file(dir).toURI()
        }
    }
}
