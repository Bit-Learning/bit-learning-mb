/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

@Singleton
class PersistentCookieJar
@Inject
constructor(
    @ApplicationContext context: Context,
) : CookieJar {

    companion object {
        private const val PREF_NAME = "network_cookies"
        private const val KEYS_SET = "cookie_keys"
    }

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private val lock = Any()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        synchronized(lock) {
            val knownKeys = prefs.getStringSet(KEYS_SET, emptySet()).orEmpty().toMutableSet()
            val editor = prefs.edit()

            cookies.forEach { cookie ->
                val key = cookieKey(cookie)
                if (cookie.expiresAt < System.currentTimeMillis()) {
                    knownKeys.remove(key)
                    editor.remove(key)
                } else {
                    knownKeys.add(key)
                    editor.putString(key, encode(cookie))
                }
            }

            editor.putStringSet(KEYS_SET, knownKeys)
            editor.apply()
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> =
        synchronized(lock) {
            val knownKeys = prefs.getStringSet(KEYS_SET, emptySet()).orEmpty().toMutableSet()
            val validCookies = mutableListOf<Cookie>()
            var changed = false

            knownKeys.toList().forEach { key ->
                val encodedCookie = prefs.getString(key, null)
                val cookie = encodedCookie?.let(::decode)
                if (cookie == null || cookie.expiresAt < System.currentTimeMillis()) {
                    knownKeys.remove(key)
                    prefs.edit().remove(key).apply()
                    changed = true
                } else if (cookie.matches(url)) {
                    validCookies += cookie
                }
            }

            if (changed) {
                prefs.edit().putStringSet(KEYS_SET, knownKeys).apply()
            }

            validCookies
        }

    private fun cookieKey(cookie: Cookie): String = "${cookie.domain}|${cookie.path}|${cookie.name}"

    private fun encode(cookie: Cookie): String =
        listOf(
            cookie.name,
            cookie.value,
            cookie.expiresAt.toString(),
            cookie.domain,
            cookie.path,
            cookie.secure.toString(),
            cookie.httpOnly.toString(),
            cookie.hostOnly.toString(),
            cookie.persistent.toString(),
        ).joinToString("\n")

    private fun decode(value: String): Cookie? {
        val parts = value.split('\n')
        if (parts.size < 9) {
            return null
        }

        val builder = Cookie.Builder()
            .name(parts[0])
            .value(parts[1])
            .path(parts[4])

        val expiresAt = parts[2].toLongOrNull() ?: return null
        builder.expiresAt(expiresAt)

        if (parts[7].toBoolean()) {
            builder.hostOnlyDomain(parts[3])
        } else {
            builder.domain(parts[3])
        }

        if (parts[5].toBoolean()) {
            builder.secure()
        }
        if (parts[6].toBoolean()) {
            builder.httpOnly()
        }

        return runCatching { builder.build() }.getOrNull()
    }
}
