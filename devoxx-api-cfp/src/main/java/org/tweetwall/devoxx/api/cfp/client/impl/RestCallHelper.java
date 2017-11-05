/*
 * The MIT License
 *
 * Copyright 2014-2017 TweetWallFX
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tweetwall.devoxx.api.cfp.client.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class RestCallHelper {

    private RestCallHelper() {
        // prevent instantiation
    }

    private static Client getClient() {
        return ClientBuilder.newClient();
    }

    private static String getHttpsUrl(final String url) {
        if (url.startsWith("http:")) {
            return url.replaceAll("^http:", "https:");
        } else {
            return url;
        }
    }

    public static Response getResponse(final String url, final Map<String, Object> queryParameters) {
        System.out.println("url: " + url);
        WebTarget webTarget = getClient().target(getHttpsUrl(url));

        if (null != queryParameters && !queryParameters.isEmpty()) {
            queryParameters.forEach((key, value) -> {
                if (value instanceof Object[]) {
                    webTarget.queryParam(key, Object[].class.cast(value));
                } else if (value instanceof Collection) {
                    webTarget.queryParam(key, ((Collection<?>) value).toArray());
                } else {
                    webTarget.queryParam(key, value);
                }
            });
        }

        return webTarget
                .request(MediaType.APPLICATION_JSON)
                .get();
    }

    public static Response getResponse(final String url) {
        return getResponse(url, Collections.emptyMap());
    }

    public static <T> T getData(final String url, final Class<T> typeClass, final Map<String, Object> queryParameters) {
        return getResponse(url, queryParameters).readEntity(typeClass);
    }

    public static <T> T getData(final String url, final Class<T> typeClass) {
        return getResponse(url).readEntity(typeClass);
    }

    public static <T> T getData(final String url, final GenericType<T> genericType, final Map<String, Object> queryParameters) {
        return getResponse(url, queryParameters).readEntity(genericType);
    }

    public static <T> T getData(final String url, final GenericType<T> genericType) {
        return getResponse(url).readEntity(genericType);
    }
}