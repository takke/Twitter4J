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
 * @author Yusuke Yamamoto - yusuke at mac.com
 * @since Twitter4J 2.1.2
 */
interface HttpResponseCode {
    companion object {
        const val OK = 200 // OK: Success!
        const val MULTIPLE_CHOICES = 300 //
        const val FOUND = 302 //
        const val NOT_MODIFIED = 304 // Not Modified: There was no new data to return.
        const val BAD_REQUEST = 400 // Bad Request: The request was invalid. An accompanying error message will explain why. This is the status code will be returned during rate limiting.
        const val UNAUTHORIZED = 401 // Not Authorized: Authentication credentials were missing or incorrect.
        const val FORBIDDEN = 403 // Forbidden: The request is understood, but it has been refused.  An accompanying error message will explain why.
        const val NOT_FOUND = 404 // Not Found: The URI requested is invalid or the resource requested, such as a user, does not exists.
        const val NOT_ACCEPTABLE = 406 // Not Acceptable: Returned by the Search API when an invalid format is specified in the request.
        const val ENHANCE_YOUR_CLAIM = 420 // Enhance Your Calm: Returned by the Search and Trends API  when you are being rate limited. Not registered in RFC.
        const val UNPROCESSABLE_ENTITY = 422 //Returned when an image uploaded to POST account/update_profile_banner is unable to be processed.
        const val TOO_MANY_REQUESTS = 429 //Returned in API v1.1 when a request cannot be served due to the application's rate limit having been exhausted for the resource. See Rate Limiting in API v1.1.
        const val INTERNAL_SERVER_ERROR = 500 // Internal Server Error: Something is broken. Please post to the group so the Twitter team can investigate.
        const val BAD_GATEWAY = 502 // Bad Gateway: Twitter is down or being upgraded.
        const val SERVICE_UNAVAILABLE = 503 // Service Unavailable: The Twitter servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.
        const val GATEWAY_TIMEOUT = 504 // The Twitter servers are up, but the request couldn't be serviced due to some failure within our stack. Try again later.
    }
}
