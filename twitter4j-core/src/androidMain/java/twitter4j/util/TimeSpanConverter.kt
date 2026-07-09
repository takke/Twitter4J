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

package twitter4j.util

import java.io.Serializable
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author Joern Huxhorn - jhuxhorn at googlemail.com
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.0
 */
class TimeSpanConverter @JvmOverloads constructor(locale: Locale = Locale.getDefault()) : Serializable {
    private val formats = arrayOfNulls<MessageFormat>(6)
    private val dateMonth: SimpleDateFormat
    private val dateMonthYear: SimpleDateFormat

    /**
     * Constructs an instance with the specified locale
     *
     * @param locale locale
     */
    init {
        val language = locale.language
        if ("it" == language) {
            formats[NOW] = MessageFormat("Ora")
            formats[N_SECONDS_AGO] = MessageFormat("{0} secondi fa")
            formats[A_MINUTE_AGO] = MessageFormat("1 minuto fa")
            formats[N_MINUTES_AGO] = MessageFormat("{0} minuti fa")
            formats[AN_HOUR_AGO] = MessageFormat("1 ora fa")
            formats[N_HOURS_AGO] = MessageFormat("{0} ore fa")
            dateMonth = SimpleDateFormat("d MMM", locale)
            dateMonthYear = SimpleDateFormat("d MMM yy", locale)
        } else if ("kr" == language) {
            formats[NOW] = MessageFormat("지금")
            formats[N_SECONDS_AGO] = MessageFormat("{0}초 전")
            formats[A_MINUTE_AGO] = MessageFormat("1분 전")
            formats[N_MINUTES_AGO] = MessageFormat("{0}분 전")
            formats[AN_HOUR_AGO] = MessageFormat("1시간 전")
            formats[N_HOURS_AGO] = MessageFormat("{0} ore fa")
            dateMonth = SimpleDateFormat("M월 d일", locale)
            dateMonthYear = SimpleDateFormat("yy년 M월 d일", locale)
        } else if ("es" == language) {
            formats[NOW] = MessageFormat("Ahora")
            formats[N_SECONDS_AGO] = MessageFormat("hace {0} segundos")
            formats[A_MINUTE_AGO] = MessageFormat("hace 1 minuto")
            formats[N_MINUTES_AGO] = MessageFormat("hace {0} minutos")
            formats[AN_HOUR_AGO] = MessageFormat("hace 1 hora")
            formats[N_HOURS_AGO] = MessageFormat("hace {0} horas")
            dateMonth = SimpleDateFormat("d MMM", locale)
            dateMonthYear = SimpleDateFormat("d MMM yy", locale)
        } else if ("fr" == language) {
            formats[NOW] = MessageFormat("Maintenant")
            formats[N_SECONDS_AGO] = MessageFormat("Il y a {0} secondes")
            formats[A_MINUTE_AGO] = MessageFormat("Il y a 1 minute")
            formats[N_MINUTES_AGO] = MessageFormat("Il y a {0} minutes")
            formats[AN_HOUR_AGO] = MessageFormat("Il y a 1 heure")
            formats[N_HOURS_AGO] = MessageFormat("Il y a {0} heures")
            dateMonth = SimpleDateFormat("d MMM", locale)
            dateMonthYear = SimpleDateFormat("d MMM yy", locale)
        } else if ("de" == language) {
            formats[NOW] = MessageFormat("Jetzt")
            formats[N_SECONDS_AGO] = MessageFormat("vor {0} Sekunden")
            formats[A_MINUTE_AGO] = MessageFormat("vor 1 Minute")
            formats[N_MINUTES_AGO] = MessageFormat("vor {0} Minuten")
            formats[AN_HOUR_AGO] = MessageFormat("vor 1 Stunde")
            formats[N_HOURS_AGO] = MessageFormat("vor {0} Stunden")
            dateMonth = SimpleDateFormat("d MMM", locale)
            dateMonthYear = SimpleDateFormat("d MMM yy", locale)
        } else if ("ja" == language) {
            formats[NOW] = MessageFormat("今")
            formats[N_SECONDS_AGO] = MessageFormat("{0}秒前")
            formats[A_MINUTE_AGO] = MessageFormat("1分前")
            formats[N_MINUTES_AGO] = MessageFormat("{0}分前")
            formats[AN_HOUR_AGO] = MessageFormat("1時間前")
            formats[N_HOURS_AGO] = MessageFormat("{0}時間前")
            dateMonth = SimpleDateFormat("M月d日", locale)
            dateMonthYear = SimpleDateFormat("yy年M月d日", locale)
        } else {
            formats[NOW] = MessageFormat("now")
            formats[N_SECONDS_AGO] = MessageFormat("{0} seconds ago")
            formats[A_MINUTE_AGO] = MessageFormat("1 minute ago")
            formats[N_MINUTES_AGO] = MessageFormat("{0} minutes ago")
            formats[AN_HOUR_AGO] = MessageFormat("1 hour ago")
            formats[N_HOURS_AGO] = MessageFormat("{0} hours ago")
            dateMonth = SimpleDateFormat("d MMM", Locale.ENGLISH)
            dateMonthYear = SimpleDateFormat("d MMM yy", Locale.ENGLISH)
        }
    }

    fun toTimeSpanString(date: Date): String {
        return toTimeSpanString(date.time)
    }

    fun toTimeSpanString(milliseconds: Long): String {
        val deltaInSeconds = ((System.currentTimeMillis() - milliseconds) / 1000).toInt()
        if (deltaInSeconds >= ONE_DAY_IN_SECONDS) {
            return if (deltaInSeconds >= ONE_MONTH_IN_SECONDS) {
                dateMonthYear.format(Date(milliseconds))
            } else {
                dateMonth.format(Date(milliseconds))
            }
        }
        return toTimeSpanString(deltaInSeconds)
    }

    private fun toTimeSpanString(deltaInSeconds: Int): String {
        if (deltaInSeconds <= 1) {
            return formats[NOW]!!.format(null as Any?)
        } else if (deltaInSeconds < 60) {
            return formats[N_SECONDS_AGO]!!.format(arrayOf<Any>(deltaInSeconds))
        }

        if (deltaInSeconds < 45 * 60) {
            val minutes = deltaInSeconds / 60
            if (minutes == 1) {
                return formats[A_MINUTE_AGO]!!.format(null as Any?)
            }
            return formats[N_MINUTES_AGO]!!.format(arrayOf<Any>(minutes))
        }

        if (deltaInSeconds < 105 * 60) { // between 0:45 and 1:45 => 1
            return formats[AN_HOUR_AGO]!!.format(null as Any?)
        }
        val hours = (deltaInSeconds + 15 * 60) / ONE_HOUR_IN_SECONDS
        return formats[N_HOURS_AGO]!!.format(arrayOf<Any>(hours))
    }

    companion object {
        private const val ONE_HOUR_IN_SECONDS = 60 * 60
        private const val ONE_DAY_IN_SECONDS = 24 * ONE_HOUR_IN_SECONDS
        private const val ONE_MONTH_IN_SECONDS = 30 * ONE_DAY_IN_SECONDS
        private const val serialVersionUID = 8665013607650804076L

        private const val NOW = 0
        private const val N_SECONDS_AGO = 1
        private const val A_MINUTE_AGO = 2
        private const val N_MINUTES_AGO = 3
        private const val AN_HOUR_AGO = 4
        private const val N_HOURS_AGO = 5
    }
}
