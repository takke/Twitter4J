/*
 * Copyright 2007 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package twitter4j

// JVM では従来どおり java.io.File / java.io.InputStream をそのまま用いる。
// typealias のため、バイトコード上のシグネチャは `java/io/File` / `java/io/InputStream` のまま変化しない。
actual typealias File = java.io.File

actual typealias InputStream = java.io.InputStream
