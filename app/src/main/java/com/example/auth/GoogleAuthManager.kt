package com.example.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

sealed class GoogleLinkResult {
    data class Success(
        val email: String,
        val displayName: String?,
        val googleId: String?,
        val photoUrl: String?
    ) : GoogleLinkResult()

    data class Error(val message: String) : GoogleLinkResult()
    data object Cancelled : GoogleLinkResult()
}

class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    suspend fun linkWithGoogle(
        activityContext: Context,
        serverClientId: String? = null
    ): GoogleLinkResult {
        try {
            // Build GoogleIdOption
            val clientId = serverClientId?.ifBlank { null }
                ?: "236407052059-dummyclient.apps.googleusercontent.com"

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activityContext,
                request = request
            )

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                return GoogleLinkResult.Success(
                    email = googleIdTokenCredential.id,
                    displayName = googleIdTokenCredential.displayName ?: googleIdTokenCredential.givenName,
                    googleId = googleIdTokenCredential.id,
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString()
                )
            } else {
                return GoogleLinkResult.Error("Unexpected credential type: ${credential.type}")
            }
        } catch (e: GetCredentialCancellationException) {
            return GoogleLinkResult.Cancelled
        } catch (e: NoCredentialException) {
            Log.w("GoogleAuthManager", "No credentials on device: ${e.message}")
            return GoogleLinkResult.Error("No Google Account found on this device. You can link your Google Account directly.")
        } catch (e: GetCredentialException) {
            Log.e("GoogleAuthManager", "Credential error: ${e.message}", e)
            return GoogleLinkResult.Error(e.message ?: "Google Sign-In failed")
        } catch (e: Exception) {
            Log.e("GoogleAuthManager", "General error during Google linking: ${e.message}", e)
            return GoogleLinkResult.Error(e.message ?: "An error occurred while linking Google Account")
        }
    }
}
