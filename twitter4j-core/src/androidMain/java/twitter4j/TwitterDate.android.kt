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

/**
 * JVM(Android) では `java.util.Date` への typealias とする。
 *
 * これにより `twitter4j.Date` を返す/受け取る公開API（例: `Status.getCreatedAt()`）は、
 * バイトコード上は従来通り `java.util.Date` を用いた状態と完全に等価となり、既存の
 * 公開APIシグネチャや JVM 側の挙動は一切変わらない。
 *
 * ※ `java.util.Date` は `time`(getTime)・`equals`・`hashCode`・`toString`・
 *   `compareTo`（`Comparable<Date>`）を備えるため、[TwitterDate.kt] の expect 宣言を満たす。
 */
actual typealias Date = java.util.Date
