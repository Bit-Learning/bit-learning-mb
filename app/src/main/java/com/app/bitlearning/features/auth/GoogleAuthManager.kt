/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.features.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Handles all Google Sign-In SDK interactions in a single dedicated class.
 *
 * Inject this into [AuthViewModel] (or any other ViewModel that needs Google auth)
 * instead of managing the [GoogleSignInClient] directly inside the UI layer.
 */
@Singleton
class GoogleAuthManager
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso =
            GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(OAuth2AppConfig.WEB_CLIENT_ID)
                .requestEmail()
                .build()
        GoogleSignIn.getClient(context, gso)
    }

    /**
     * Returns the [Intent] that should be launched to start the Google
     * Sign-In account-picker.  Use this with an [ActivityResultLauncher].
     *
     * Signs out first so the account picker is always shown (avoids auto-
     * selecting a stale account silently).
     */
    suspend fun getSignInIntent(): Result<Intent> = withContext(Dispatchers.IO) {
        try {
            // Always show the account picker.
            googleSignInClient.signOut().await()
            Result.success(googleSignInClient.signInIntent)
        } catch (e: Exception) {
            Log.e(TAG, "getSignInIntent failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Convenience accessor for the sign-in intent when you don't need the
     * sign-out-first behaviour (e.g. when building the intent synchronously
     * outside a coroutine scope).
     */
    fun getSignInIntentSync(): Intent = googleSignInClient.signInIntent

    /**
     * Extracts the Google ID token from the [Intent] returned by the
     * account-picker activity result.
     *
     * @param data The [Intent] from [ActivityResult.data]; may be null if the
     *   user cancelled.
     * @return [Result.success] with the raw JWT string, or [Result.failure]
     *   with a descriptive exception.
     */
    suspend fun getIdTokenFromResult(data: Intent?): Result<String> = withContext(Dispatchers.IO) {
        if (data == null) {
            Log.e(TAG, "Sign-in intent result was null (user likely cancelled)")
            return@withContext Result.failure(Exception("Sign-in cancelled"))
        }
        Log.d(TAG, "Processing Google Sign-In result…")
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.await()
            Log.d(TAG, "Got account: ${account.email}")

            val idToken = account.idToken
            Log.d(
                TAG,
                "ID token is ${if (idToken == null) "NULL" else "present (length: ${idToken.length})"}",
            )

            if (idToken != null) {
                Result.success(idToken)
            } else {
                Result.failure(Exception("No ID token found in the sign-in result"))
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google Sign-In ApiException — code ${e.statusCode}: ${e.message}")
            Result.failure(Exception("Google Sign-In failed with code ${e.statusCode}: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In unexpected error: ${e.message}")
            Result.failure(e)
        }
    }

    /** Signs the current user out of Google on this device. */
    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            googleSignInClient.signOut().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "signOut failed: ${e.message}")
            Result.failure(e)
        }
    }

    private companion object {
        private const val TAG = "GoogleAuthManager"
    }
}
