package com.authentication.firebaseauth.data.googleAuthUiClient

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
// 2. The Main Client Class
class MyGoogleAuthUiClient(
    private val credentialManager: CredentialManager,
    private val webClientId: String
) {
    private val auth = Firebase.auth
    suspend fun signIn(activityContext: Context): SignInResult {
        return try {
            val request = buildSignInRequest()
            val result = credentialManager.getCredential(request=request, context=activityContext)
            val token = extractGoogleToken(result.credential)
            val user = authenticateWithFirebase(token)

            SignInResult(data = user, errorMessage = null)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(data = null, errorMessage = e.message)
        }
    }
    // Build the request
    private fun buildSignInRequest(): GetCredentialRequest {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(webClientId)
            .setAutoSelectEnabled(true)
            .build()

        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }
    // Job 2: Extract the token from the Google result
    private fun extractGoogleToken(credential: Credential): String {
        if (credential !is CustomCredential || credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            throw Exception("Received an invalid credential type.")
        }
        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        return googleIdTokenCredential.idToken
    }
    // Job 3: Send the token to Firebase and return our clean UserData
    private suspend fun authenticateWithFirebase(googleTokenId: String): UserData {
        val firebaseCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        val authResult = auth.signInWithCredential(firebaseCredential).await()
        val user = authResult.user ?: throw Exception("Firebase user is null.")

        return UserData(
            userId = user.uid,
            username = user.displayName,
            profilePictureUrl = user.photoUrl?.toString()
        )
    }

    // Helper function to check if someone is already logged in when the app opens
    fun getSignedInUser(): UserData? {
        val user = auth.currentUser ?: return null
        return UserData(
            userId = user.uid,
            username = user.displayName,
            profilePictureUrl = user.photoUrl?.toString()
        )
    }
    // Signs the user out of Firebase and clears the Google Credential session
    suspend fun signOut() {
        try {
            auth.signOut()
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }
}


