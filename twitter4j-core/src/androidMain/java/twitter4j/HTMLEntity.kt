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

internal object HTMLEntity {

    @JvmStatic
    fun escape(original: String): String {
        val buf = StringBuilder(original)
        escape(buf)
        return buf.toString()
    }

    @JvmStatic
    fun escape(original: StringBuilder) {
        var index = 0
        var escaped: String?
        while (index < original.length) {
            escaped = entityEscapeMap[original.substring(index, index + 1)]
            if (escaped != null) {
                original.replace(index, index + 1, escaped)
                index += escaped.length
            } else {
                index++
            }
        }
    }

    @JvmStatic
    fun unescape(original: String?): String? {
        var returnValue: String? = null
        if (original != null) {
            val buf = StringBuilder(original)
            unescape(buf)
            returnValue = buf.toString()
        }
        return returnValue
    }

    @JvmStatic
    fun unescape(original: StringBuilder) {
        var index = 0
        var semicolonIndex: Int
        var escaped: String
        var entity: String?
        while (index < original.length) {
            index = original.indexOf("&", index)
            if (-1 == index) {
                break
            }
            semicolonIndex = original.indexOf(";", index)
            if (-1 != semicolonIndex) {
                escaped = original.substring(index, semicolonIndex + 1)
                entity = escapeEntityMap[escaped]
                if (entity != null) {
                    original.replace(index, semicolonIndex + 1, entity)
                }
                index++
            } else {
                break
            }
        }
    }

    /**
     * @author Yusuke Yamamoto - yusuke at mac.com
     * @author Philip Hachey - philip dot hachey at gmail dot com
     */
    @JvmStatic
    fun unescapeAndSlideEntityIncdices(
        text: String,
        userMentionEntities: Array<UserMentionEntity>?,
        urlEntities: Array<URLEntity>?,
        hashtagEntities: Array<HashtagEntity>?,
        mediaEntities: Array<MediaEntity>?
    ): String {

        val entityIndexes = ArrayList<EntityIndex>()
        userMentionEntities?.forEach { entityIndexes.add(it as EntityIndex) }
        urlEntities?.forEach { entityIndexes.add(it as EntityIndex) }
        hashtagEntities?.forEach { entityIndexes.add(it as EntityIndex) }
        mediaEntities?.forEach { entityIndexes.add(it as EntityIndex) }

        entityIndexes.sort()
        var handlingStart = true
        var entityIndex = 0

        var delta = 0
        var semicolonIndex: Int
        var escaped: String
        var entity: String?
        val unescaped = StringBuilder(text.length)

        /*
         * Slide indices of twitter entities not only when replacing character
         * entity references but also adjust the twitter code point based
         * indexes with Java standard character indexes. See: HTMLEntityTest.
         * testUnescapeAndSlideEntityIncdicesWithSurrogateCodePoints
         */
        val textCodePointLength = text.codePointCount(0, text.length)
        var codePoint: Int
        var index = 0
        var twitterIndex = 0
        while (index < text.length) {
            codePoint = text.codePointAt(index)
            if (codePoint == '&'.code) {
                semicolonIndex = text.indexOf(";", index)
                if (-1 != semicolonIndex) {
                    escaped = text.substring(index, semicolonIndex + 1)
                    entity = escapeEntityMap[escaped]
                    if (entity != null) {
                        unescaped.append(entity)
                        index = semicolonIndex
                        twitterIndex = text.codePointCount(0, semicolonIndex)
                        delta = 1 - escaped.length
                    } else {
                        unescaped.appendCodePoint(codePoint)
                    }
                } else {
                    unescaped.appendCodePoint(codePoint)
                }
            } else {
                unescaped.appendCodePoint(codePoint)
            }
            if (entityIndex < entityIndexes.size) {
                if (handlingStart) {
                    if (entityIndexes[entityIndex].start == (delta + twitterIndex)) {
                        entityIndexes[entityIndex].start =
                            unescaped.length - Character.charCount(text.codePointAt(index))
                        handlingStart = false
                    }
                } else if (entityIndexes[entityIndex].end == (delta + twitterIndex)) {
                    entityIndexes[entityIndex].end =
                        unescaped.length - Character.charCount(text.codePointAt(index))
                    entityIndex++
                    handlingStart = true
                }
            }
            delta = 0
            index += Character.charCount(codePoint)
            twitterIndex++
        }
        if (entityIndex < entityIndexes.size) {
            if (entityIndexes[entityIndex].end == textCodePointLength) {
                entityIndexes[entityIndex].end = unescaped.length
            }
        }

        return unescaped.toString()
    }

    private val entityEscapeMap: MutableMap<String, String> = HashMap()
    private val escapeEntityMap: MutableMap<String, String> = HashMap()

    init {
        val entities = arrayOf(
            arrayOf("&nbsp;", "&#160;", "\u00A0"),
            arrayOf("&iexcl;", "&#161;", "\u00A1"),
            arrayOf("&cent;", "&#162;", "\u00A2"),
            arrayOf("&pound;", "&#163;", "\u00A3"),
            arrayOf("&curren;", "&#164;", "\u00A4"),
            arrayOf("&yen;", "&#165;", "\u00A5"),
            arrayOf("&brvbar;", "&#166;", "\u00A6"),
            arrayOf("&sect;", "&#167;", "\u00A7"),
            arrayOf("&uml;", "&#168;", "\u00A8"),
            arrayOf("&copy;", "&#169;", "\u00A9"),
            arrayOf("&ordf;", "&#170;", "\u00AA"),
            arrayOf("&laquo;", "&#171;", "\u00AB"),
            arrayOf("&not;", "&#172;", "\u00AC"),
            arrayOf("&shy;", "&#173;", "\u00AD"),
            arrayOf("&reg;", "&#174;", "\u00AE"),
            arrayOf("&macr;", "&#175;", "\u00AF"),
            arrayOf("&deg;", "&#176;", "\u00B0"),
            arrayOf("&plusmn;", "&#177;", "\u00B1"),
            arrayOf("&sup2;", "&#178;", "\u00B2"),
            arrayOf("&sup3;", "&#179;", "\u00B3"),
            arrayOf("&acute;", "&#180;", "\u00B4"),
            arrayOf("&micro;", "&#181;", "\u00B5"),
            arrayOf("&para;", "&#182;", "\u00B6"),
            arrayOf("&middot;", "&#183;", "\u00B7"),
            arrayOf("&cedil;", "&#184;", "\u00B8"),
            arrayOf("&sup1;", "&#185;", "\u00B9"),
            arrayOf("&ordm;", "&#186;", "\u00BA"),
            arrayOf("&raquo;", "&#187;", "\u00BB"),
            arrayOf("&frac14;", "&#188;", "\u00BC"),
            arrayOf("&frac12;", "&#189;", "\u00BD"),
            arrayOf("&frac34;", "&#190;", "\u00BE"),
            arrayOf("&iquest;", "&#191;", "\u00BF"),
            arrayOf("&Agrave;", "&#192;", "\u00C0"),
            arrayOf("&Aacute;", "&#193;", "\u00C1"),
            arrayOf("&Acirc;", "&#194;", "\u00C2"),
            arrayOf("&Atilde;", "&#195;", "\u00C3"),
            arrayOf("&Auml;", "&#196;", "\u00C4"),
            arrayOf("&Aring;", "&#197;", "\u00C5"),
            arrayOf("&AElig;", "&#198;", "\u00C6"),
            arrayOf("&Ccedil;", "&#199;", "\u00C7"),
            arrayOf("&Egrave;", "&#200;", "\u00C8"),
            arrayOf("&Eacute;", "&#201;", "\u00C9"),
            arrayOf("&Ecirc;", "&#202;", "\u00CA"),
            arrayOf("&Euml;", "&#203;", "\u00CB"),
            arrayOf("&Igrave;", "&#204;", "\u00CC"),
            arrayOf("&Iacute;", "&#205;", "\u00CD"),
            arrayOf("&Icirc;", "&#206;", "\u00CE"),
            arrayOf("&Iuml;", "&#207;", "\u00CF"),
            arrayOf("&ETH;", "&#208;", "\u00D0"),
            arrayOf("&Ntilde;", "&#209;", "\u00D1"),
            arrayOf("&Ograve;", "&#210;", "\u00D2"),
            arrayOf("&Oacute;", "&#211;", "\u00D3"),
            arrayOf("&Ocirc;", "&#212;", "\u00D4"),
            arrayOf("&Otilde;", "&#213;", "\u00D5"),
            arrayOf("&Ouml;", "&#214;", "\u00D6"),
            arrayOf("&times;", "&#215;", "\u00D7"),
            arrayOf("&Oslash;", "&#216;", "\u00D8"),
            arrayOf("&Ugrave;", "&#217;", "\u00D9"),
            arrayOf("&Uacute;", "&#218;", "\u00DA"),
            arrayOf("&Ucirc;", "&#219;", "\u00DB"),
            arrayOf("&Uuml;", "&#220;", "\u00DC"),
            arrayOf("&Yacute;", "&#221;", "\u00DD"),
            arrayOf("&THORN;", "&#222;", "\u00DE"),
            arrayOf("&szlig;", "&#223;", "\u00DF"),
            arrayOf("&agrave;", "&#224;", "\u00E0"),
            arrayOf("&aacute;", "&#225;", "\u00E1"),
            arrayOf("&acirc;", "&#226;", "\u00E2"),
            arrayOf("&atilde;", "&#227;", "\u00E3"),
            arrayOf("&auml;", "&#228;", "\u00E4"),
            arrayOf("&aring;", "&#229;", "\u00E5"),
            arrayOf("&aelig;", "&#230;", "\u00E6"),
            arrayOf("&ccedil;", "&#231;", "\u00E7"),
            arrayOf("&egrave;", "&#232;", "\u00E8"),
            arrayOf("&eacute;", "&#233;", "\u00E9"),
            arrayOf("&ecirc;", "&#234;", "\u00EA"),
            arrayOf("&euml;", "&#235;", "\u00EB"),
            arrayOf("&igrave;", "&#236;", "\u00EC"),
            arrayOf("&iacute;", "&#237;", "\u00ED"),
            arrayOf("&icirc;", "&#238;", "\u00EE"),
            arrayOf("&iuml;", "&#239;", "\u00EF"),
            arrayOf("&eth;", "&#240;", "\u00F0"),
            arrayOf("&ntilde;", "&#241;", "\u00F1"),
            arrayOf("&ograve;", "&#242;", "\u00F2"),
            arrayOf("&oacute;", "&#243;", "\u00F3"),
            arrayOf("&ocirc;", "&#244;", "\u00F4"),
            arrayOf("&otilde;", "&#245;", "\u00F5"),
            arrayOf("&ouml;", "&#246;", "\u00F6"),
            arrayOf("&divide;", "&#247;", "\u00F7"),
            arrayOf("&oslash;", "&#248;", "\u00F8"),
            arrayOf("&ugrave;", "&#249;", "\u00F9"),
            arrayOf("&uacute;", "&#250;", "\u00FA"),
            arrayOf("&ucirc;", "&#251;", "\u00FB"),
            arrayOf("&uuml;", "&#252;", "\u00FC"),
            arrayOf("&yacute;", "&#253;", "\u00FD"),
            arrayOf("&thorn;", "&#254;", "\u00FE"),
            arrayOf("&yuml;", "&#255;", "\u00FF"),
            arrayOf("&fnof;", "&#402;", "\u0192"),
            arrayOf("&Alpha;", "&#913;", "\u0391"),
            arrayOf("&Beta;", "&#914;", "\u0392"),
            arrayOf("&Gamma;", "&#915;", "\u0393"),
            arrayOf("&Delta;", "&#916;", "\u0394"),
            arrayOf("&Epsilon;", "&#917;", "\u0395"),
            arrayOf("&Zeta;", "&#918;", "\u0396"),
            arrayOf("&Eta;", "&#919;", "\u0397"),
            arrayOf("&Theta;", "&#920;", "\u0398"),
            arrayOf("&Iota;", "&#921;", "\u0399"),
            arrayOf("&Kappa;", "&#922;", "\u039A"),
            arrayOf("&Lambda;", "&#923;", "\u039B"),
            arrayOf("&Mu;", "&#924;", "\u039C"),
            arrayOf("&Nu;", "&#925;", "\u039D"),
            arrayOf("&Xi;", "&#926;", "\u039E"),
            arrayOf("&Omicron;", "&#927;", "\u039F"),
            arrayOf("&Pi;", "&#928;", "\u03A0"),
            arrayOf("&Rho;", "&#929;", "\u03A1"),
            arrayOf("&Sigma;", "&#931;", "\u03A3"),
            arrayOf("&Tau;", "&#932;", "\u03A4"),
            arrayOf("&Upsilon;", "&#933;", "\u03A5"),
            arrayOf("&Phi;", "&#934;", "\u03A6"),
            arrayOf("&Chi;", "&#935;", "\u03A7"),
            arrayOf("&Psi;", "&#936;", "\u03A8"),
            arrayOf("&Omega;", "&#937;", "\u03A9"),
            arrayOf("&alpha;", "&#945;", "\u03B1"),
            arrayOf("&beta;", "&#946;", "\u03B2"),
            arrayOf("&gamma;", "&#947;", "\u03B3"),
            arrayOf("&delta;", "&#948;", "\u03B4"),
            arrayOf("&epsilon;", "&#949;", "\u03B5"),
            arrayOf("&zeta;", "&#950;", "\u03B6"),
            arrayOf("&eta;", "&#951;", "\u03B7"),
            arrayOf("&theta;", "&#952;", "\u03B8"),
            arrayOf("&iota;", "&#953;", "\u03B9"),
            arrayOf("&kappa;", "&#954;", "\u03BA"),
            arrayOf("&lambda;", "&#955;", "\u03BB"),
            arrayOf("&mu;", "&#956;", "\u03BC"),
            arrayOf("&nu;", "&#957;", "\u03BD"),
            arrayOf("&xi;", "&#958;", "\u03BE"),
            arrayOf("&omicron;", "&#959;", "\u03BF"),
            arrayOf("&pi;", "&#960;", "\u03C0"),
            arrayOf("&rho;", "&#961;", "\u03C1"),
            arrayOf("&sigmaf;", "&#962;", "\u03C2"),
            arrayOf("&sigma;", "&#963;", "\u03C3"),
            arrayOf("&tau;", "&#964;", "\u03C4"),
            arrayOf("&upsilon;", "&#965;", "\u03C5"),
            arrayOf("&phi;", "&#966;", "\u03C6"),
            arrayOf("&chi;", "&#967;", "\u03C7"),
            arrayOf("&psi;", "&#968;", "\u03C8"),
            arrayOf("&omega;", "&#969;", "\u03C9"),
            arrayOf("&thetasym;", "&#977;", "\u03D1"),
            arrayOf("&upsih;", "&#978;", "\u03D2"),
            arrayOf("&piv;", "&#982;", "\u03D6"),
            arrayOf("&bull;", "&#8226;", "\u2022"),
            arrayOf("&hellip;", "&#8230;", "\u2026"),
            arrayOf("&prime;", "&#8242;", "\u2032"),
            arrayOf("&Prime;", "&#8243;", "\u2033"),
            arrayOf("&oline;", "&#8254;", "\u203E"),
            arrayOf("&frasl;", "&#8260;", "\u2044"),
            arrayOf("&weierp;", "&#8472;", "\u2118"),
            arrayOf("&image;", "&#8465;", "\u2111"),
            arrayOf("&real;", "&#8476;", "\u211C"),
            arrayOf("&trade;", "&#8482;", "\u2122"),
            arrayOf("&alefsym;", "&#8501;", "\u2135"),
            arrayOf("&larr;", "&#8592;", "\u2190"),
            arrayOf("&uarr;", "&#8593;", "\u2191"),
            arrayOf("&rarr;", "&#8594;", "\u2192"),
            arrayOf("&darr;", "&#8595;", "\u2193"),
            arrayOf("&harr;", "&#8596;", "\u2194"),
            arrayOf("&crarr;", "&#8629;", "\u21B5"),
            arrayOf("&lArr;", "&#8656;", "\u21D0"),
            arrayOf("&uArr;", "&#8657;", "\u21D1"),
            arrayOf("&rArr;", "&#8658;", "\u21D2"),
            arrayOf("&dArr;", "&#8659;", "\u21D3"),
            arrayOf("&hArr;", "&#8660;", "\u21D4"),
            arrayOf("&forall;", "&#8704;", "\u2200"),
            arrayOf("&part;", "&#8706;", "\u2202"),
            arrayOf("&exist;", "&#8707;", "\u2203"),
            arrayOf("&empty;", "&#8709;", "\u2205"),
            arrayOf("&nabla;", "&#8711;", "\u2207"),
            arrayOf("&isin;", "&#8712;", "\u2208"),
            arrayOf("&notin;", "&#8713;", "\u2209"),
            arrayOf("&ni;", "&#8715;", "\u220B"),
            arrayOf("&prod;", "&#8719;", "\u220F"),
            arrayOf("&sum;", "&#8721;", "\u2211"),
            arrayOf("&minus;", "&#8722;", "\u2212"),
            arrayOf("&lowast;", "&#8727;", "\u2217"),
            arrayOf("&radic;", "&#8730;", "\u221A"),
            arrayOf("&prop;", "&#8733;", "\u221D"),
            arrayOf("&infin;", "&#8734;", "\u221E"),
            arrayOf("&ang;", "&#8736;", "\u2220"),
            arrayOf("&and;", "&#8743;", "\u2227"),
            arrayOf("&or;", "&#8744;", "\u2228"),
            arrayOf("&cap;", "&#8745;", "\u2229"),
            arrayOf("&cup;", "&#8746;", "\u222A"),
            arrayOf("&int;", "&#8747;", "\u222B"),
            arrayOf("&there4;", "&#8756;", "\u2234"),
            arrayOf("&sim;", "&#8764;", "\u223C"),
            arrayOf("&cong;", "&#8773;", "\u2245"),
            arrayOf("&asymp;", "&#8776;", "\u2248"),
            arrayOf("&ne;", "&#8800;", "\u2260"),
            arrayOf("&equiv;", "&#8801;", "\u2261"),
            arrayOf("&le;", "&#8804;", "\u2264"),
            arrayOf("&ge;", "&#8805;", "\u2265"),
            arrayOf("&sub;", "&#8834;", "\u2282"),
            arrayOf("&sup;", "&#8835;", "\u2283"),
            arrayOf("&sube;", "&#8838;", "\u2286"),
            arrayOf("&supe;", "&#8839;", "\u2287"),
            arrayOf("&oplus;", "&#8853;", "\u2295"),
            arrayOf("&otimes;", "&#8855;", "\u2297"),
            arrayOf("&perp;", "&#8869;", "\u22A5"),
            arrayOf("&sdot;", "&#8901;", "\u22C5"),
            arrayOf("&lceil;", "&#8968;", "\u2308"),
            arrayOf("&rceil;", "&#8969;", "\u2309"),
            arrayOf("&lfloor;", "&#8970;", "\u230A"),
            arrayOf("&rfloor;", "&#8971;", "\u230B"),
            arrayOf("&lang;", "&#9001;", "\u2329"),
            arrayOf("&rang;", "&#9002;", "\u232A"),
            arrayOf("&loz;", "&#9674;", "\u25CA"),
            arrayOf("&spades;", "&#9824;", "\u2660"),
            arrayOf("&clubs;", "&#9827;", "\u2663"),
            arrayOf("&hearts;", "&#9829;", "\u2665"),
            arrayOf("&diams;", "&#9830;", "\u2666"),
            arrayOf("&quot;", "&#34;", "\""),
            arrayOf("&amp;", "&#38;", "\u0026"),
            arrayOf("&lt;", "&#60;", "\u003C"),
            arrayOf("&gt;", "&#62;", "\u003E"),
            arrayOf("&OElig;", "&#338;", "\u0152"),
            arrayOf("&oelig;", "&#339;", "\u0153"),
            arrayOf("&Scaron;", "&#352;", "\u0160"),
            arrayOf("&scaron;", "&#353;", "\u0161"),
            arrayOf("&Yuml;", "&#376;", "\u0178"),
            arrayOf("&circ;", "&#710;", "\u02C6"),
            arrayOf("&tilde;", "&#732;", "\u02DC"),
            arrayOf("&ensp;", "&#8194;", "\u2002"),
            arrayOf("&emsp;", "&#8195;", "\u2003"),
            arrayOf("&thinsp;", "&#8201;", "\u2009"),
            arrayOf("&zwnj;", "&#8204;", "\u200C"),
            arrayOf("&zwj;", "&#8205;", "\u200D"),
            arrayOf("&lrm;", "&#8206;", "\u200E"),
            arrayOf("&rlm;", "&#8207;", "\u200F"),
            arrayOf("&ndash;", "&#8211;", "\u2013"),
            arrayOf("&mdash;", "&#8212;", "\u2014"),
            arrayOf("&lsquo;", "&#8216;", "\u2018"),
            arrayOf("&rsquo;", "&#8217;", "\u2019"),
            arrayOf("&sbquo;", "&#8218;", "\u201A"),
            arrayOf("&ldquo;", "&#8220;", "\u201C"),
            arrayOf("&rdquo;", "&#8221;", "\u201D"),
            arrayOf("&bdquo;", "&#8222;", "\u201E"),
            arrayOf("&dagger;", "&#8224;", "\u2020"),
            arrayOf("&Dagger;", "&#8225;", "\u2021"),
            arrayOf("&permil;", "&#8240;", "\u2030"),
            arrayOf("&lsaquo;", "&#8249;", "\u2039"),
            arrayOf("&rsaquo;", "&#8250;", "\u203A"),
            arrayOf("&euro;", "&#8364;", "\u20AC"),
        )
        for (entity in entities) {
            entityEscapeMap[entity[2]] = entity[0]
            escapeEntityMap[entity[0]] = entity[2]
            escapeEntityMap[entity[1]] = entity[2]
        }
    }
}
