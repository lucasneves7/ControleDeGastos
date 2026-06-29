package com.lucasneves.financecontrol.data.repository

import android.app.PendingIntent
import android.content.Context
import android.content.SharedPreferences
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefs: SharedPreferences
) {
    private val sheetsScope = "https://www.googleapis.com/auth/spreadsheets"
    private val driveScope = "https://www.googleapis.com/auth/drive.file"

    companion object {
        private const val KEY_EMAIL = "user_email"
        private const val KEY_NAME = "user_name"
        private const val KEY_PHOTO = "user_photo"
        const val WEB_CLIENT_ID = "310048045623-q1ltjjhln7dtqatakcu5ui8dbf9llgd5.apps.googleusercontent.com"
    }

    suspend fun signIn(activityContext: Context) {
        val credentialManager = CredentialManager.create(activityContext)
        val googleOption = GetSignInWithGoogleOption.Builder(WEB_CLIENT_ID).build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleOption)
            .build()

        val result = credentialManager.getCredential(activityContext, request)
        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)

        prefs.edit()
            .putString(KEY_EMAIL, credential.id)
            .putString(KEY_NAME, credential.displayName)
            .putString(KEY_PHOTO, credential.profilePictureUri?.toString())
            .apply()
    }

    fun isSignedIn(): Boolean = prefs.getString(KEY_EMAIL, null) != null

    fun getUserEmail(): String? = prefs.getString(KEY_EMAIL, null)
    fun getUserName(): String? = prefs.getString(KEY_NAME, null)
    fun getUserPhotoUrl(): String? = prefs.getString(KEY_PHOTO, null)

    suspend fun getAccessToken(): String? {
        if (prefs.getString(KEY_EMAIL, null) == null) return null
        return suspendCancellableCoroutine { cont ->
            val request = AuthorizationRequest.builder()
                .setRequestedScopes(listOf(Scope(sheetsScope), Scope(driveScope)))
                .build()
            Identity.getAuthorizationClient(context)
                .authorize(request)
                .addOnSuccessListener { result -> cont.resume(result.accessToken) }
                .addOnFailureListener { cont.resume(null) }
        }
    }

    suspend fun requestGoogleAuthorization(): PendingIntent? =
        suspendCancellableCoroutine { cont ->
            val request = AuthorizationRequest.builder()
                .setRequestedScopes(listOf(Scope(sheetsScope), Scope(driveScope)))
                .build()
            Identity.getAuthorizationClient(context)
                .authorize(request)
                .addOnSuccessListener { result ->
                    cont.resume(if (result.hasResolution()) result.pendingIntent else null)
                }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }

    suspend fun signOut(activityContext: Context) {
        val credentialManager = CredentialManager.create(activityContext)
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
        prefs.edit().remove(KEY_EMAIL).remove(KEY_NAME).remove(KEY_PHOTO).apply()
    }
}
