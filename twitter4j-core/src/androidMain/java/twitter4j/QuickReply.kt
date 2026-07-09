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

class QuickReply
/**
 * @param label The text label displayed on the button face. Label text is returned as the user’s message response. String, max length of 36 characters including spaces. Values with URLs are not allowed and will return an error.
 * @param description Optional description text displayed under label text. All options must have this property defined if property is present in any option. Text is auto-wrapped and will display on a max of two lines and supports n for controling line breaks. Description text is not include in the user’s message response. String, max length of 72 characters including spaces.
 * @param metadata Metadata that will be sent back in the webhook request. String, max length of 1,000 characters including spaces.
 */
    (
    val label: String?,
    val description: String?,
    val metadata: String?
) : Serializable {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val that = o as QuickReply

        if (if (label != null) label != that.label else that.label != null) return false
        if (if (description != null) description != that.description else that.description != null) return false
        return if (metadata != null) metadata == that.metadata else that.metadata == null
    }

    override fun hashCode(): Int {
        var result = label?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (metadata?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return ("QuickReply{" +
                "label='" + label + '\'' +
                ", description='" + description + '\'' +
                ", metadata='" + metadata + '\'' +
                '}')
    }

    companion object {
        private const val serialVersionUID = 2928983476392757806L
    }
}
