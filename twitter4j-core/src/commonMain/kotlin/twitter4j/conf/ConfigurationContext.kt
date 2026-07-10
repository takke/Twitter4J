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

package twitter4j.conf

import kotlin.jvm.JvmStatic

/**
 * Static factory of Configuration.
 *
 * KMP移行 Phase 3-2 で PropertyConfiguration / PropertyConfigurationFactory を削除したため、
 * 従来の Class.forName による ConfigurationFactory 動的生成をやめ、
 * デフォルト設定を持つ [ConfigurationBase] のインスタンスを直接返すシンプルな実装へ変更した。
 * ルート設定は従来同様シングルトンとして共有する。
 *
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
object ConfigurationContext {
    // ルート設定（デフォルト値のみ）。従来の PropertyConfigurationFactory.ROOT_CONFIGURATION 相当。
    private val rootConfiguration: Configuration = ConfigurationBase()

    @JvmStatic
    fun getInstance(): Configuration {
        return rootConfiguration
    }

    @JvmStatic
    fun getInstance(configTreePath: String): Configuration {
        // twitter4j.properties によるツリー設定の読み込みは廃止したため、
        // configTreePath は無視してデフォルト設定を返す。
        return ConfigurationBase()
    }
}
