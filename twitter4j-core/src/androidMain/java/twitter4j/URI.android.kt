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

import java.net.URISyntaxException

/**
 * JVM(Android) では `java.net.URI` への typealias とする。
 *
 * これにより [UserList.URI] は従来どおり `java.net.URI` を返し、`getURI()` の JVM シグネチャや
 * 既存の公開APIは一切変わらない。
 */
actual typealias URI = java.net.URI

/**
 * 旧 `UserListJSONImpl.URI` ゲッターの挙動を完全に踏襲する。
 * `URISyntaxException` 時は空URI（`java.net.URI("")`）を返す。
 * ※ [str] が null の場合は `java.net.URI((String)null)` が `NullPointerException` を送出する（旧実装と同一）。
 */
actual fun createURI(str: String?): URI = try {
    java.net.URI(str)
} catch (ex: URISyntaxException) {
    java.net.URI("")
}
