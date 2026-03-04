/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.auth

/**
 * Configuration for Google Sign-In SDK (ID Token flow).
 *
 * ── Setup ──────────────────────────────────────────────────────────────────────
 * 1. In Google Cloud Console create a **Web application** OAuth2 client.
 * 2. Add the Android app's SHA-1 fingerprint to an **Android** OAuth2 client in
 *    the same project (needed for the SDK to work on-device).
 * 3. Paste the **Web application** client ID into [WEB_CLIENT_ID] below.
 *    Only the Web client ID makes the token's `aud` claim match the backend.
 * 4. Backend must have `oauth2.google.client-id` set to the same [WEB_CLIENT_ID].
 * No redirect URI is needed anywhere in this flow.
 */
object OAuth2AppConfig {
    /**
     * Google **Web application** client ID.
     * Passed to [GoogleSignInOptions.requestIdToken] so the resulting ID token's
     * `aud` claim matches what the backend expects.
     */
    const val WEB_CLIENT_ID =
        "57725428107-l1vl8ffoug9s465evh9ntk0eqvtjopqt.apps.googleusercontent.com"
}
