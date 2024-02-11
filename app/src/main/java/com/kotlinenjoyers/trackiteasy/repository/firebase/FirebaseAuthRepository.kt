package com.kotlinenjoyers.trackiteasy.repository.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.kotlinenjoyers.trackiteasy.ui.menu.login.AccountState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FirebaseAuthRepository: IFirebaseAuthRepository {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("FirebaseAuthRepository", coroutineContext.toString())
        Log.e("FirebaseAuthRepository", throwable.toString())
        Log.e("FirebaseAuthRepository", throwable.stackTraceToString())
    }

    private val dispatchersDefault = Dispatchers.Default

    override fun signIn(
        auth: FirebaseAuth,
        email: String,
        password: String,
        scope: CoroutineScope,
        changeState: (AccountState) -> Unit,
        setError: (String) -> Unit,
    ) {
        scope.launch(dispatchersDefault + exceptionHandler) {
            auth.signInWithEmailAndPassword(email.trim(), password.trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        changeState(AccountState.Logged)
                        Log.d("Login", "signInWithEmail:success")
                    } else {
                        Log.w("Login", "signInWithEmail:failure", task.exception)
                        task.exception?.localizedMessage?.let { setError(it) }
                    }
                }
                .addOnFailureListener {
                    Log.e("FirebaseAuthRepository", it.toString())
                }
        }
    }

    override fun signOut(
        auth: FirebaseAuth,
        scope: CoroutineScope,
        changeState: (AccountState) -> Unit
    ) {
        scope.launch(dispatchersDefault + exceptionHandler) {
            auth.signOut()
            changeState(AccountState.Login)
        }
    }

    override fun register(
        auth: FirebaseAuth,
        email: String,
        password: String,
        scope: CoroutineScope,
        changeState: (AccountState) -> Unit,
        setError: (String) -> Unit,
    ) {
        scope.launch(dispatchersDefault + exceptionHandler) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        changeState(AccountState.Logged)
                        Log.d("Register", "createUserWithEmail:success")
                    } else {
                        Log.w("Register", "createUserWithEmail:failure", task.exception)
                        task.exception?.localizedMessage?.let { setError(it) }
                    }
                }
                .addOnFailureListener {
                    Log.e("FirebaseAuthRepository", it.toString())
                }
        }
    }

    override fun forgotPassword(
        auth: FirebaseAuth,
        email: String,
        scope: CoroutineScope,
        setState: (String) -> Unit
    ) {
        scope.launch(dispatchersDefault + exceptionHandler) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuthRepository", "Forgot Password Email Sent.")
                        setState("Email sent.")
                    }
                }
                .addOnFailureListener {
                    setState("Error!")
                }
        }
    }

    override fun setNewPassword(auth: FirebaseAuth, email: String, scope: CoroutineScope, setState: (String) -> Unit) {
        scope.launch(dispatchersDefault + exceptionHandler) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FirebaseAuthRepository", "Set New Password Email Sent.")
                        setState("Email sent.")
                    }
                }
                .addOnFailureListener {
                    setState("Error!")
                }
        }
    }
}