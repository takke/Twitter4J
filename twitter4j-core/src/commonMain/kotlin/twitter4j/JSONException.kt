/*
 * Copyright (C) 2010 The Android Open Source Project
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

// Note: this class was written without inspecting the non-free org.json sourcecode.

/**
 * Thrown to indicate a problem with the JSON API. Such problems include:
 *
 *  * Attempts to parse or construct malformed documents
 *  * Use of null as a name
 *  * Use of numeric types not available to JSON, such as NaNs or infinities.
 *  * Lookups using an out of range index or nonexistent name
 *  * Type mismatches on lookups
 *
 * Although this is a checked exception in the original, here it extends
 * [RuntimeException] and is rarely recoverable. Most callers should simply wrap
 * this exception in an unchecked exception and rethrow.
 */
// KMP対応: RuntimeException を継承（java.lang 非依存の Kotlin 共通型）
open class JSONException : RuntimeException {

    constructor(s: String?) : super(s)

    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, cause: Throwable?) : super(message, cause)
}
