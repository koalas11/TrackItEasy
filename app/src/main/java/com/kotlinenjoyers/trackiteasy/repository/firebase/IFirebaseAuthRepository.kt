package com.kotlinenjoyers.trackiteasy.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import com.kotlinenjoyers.trackiteasy.ui.menu.login.AccountState
import kotlinx.coroutines.CoroutineScope

interface IFirebaseAuthRepository {
    fun signIn(auth: FirebaseAuth, email: String, password: String, scope: CoroutineScope, changeState: (AccountState) -> Unit, setError: (String) -> Unit)

    fun signOut(auth: FirebaseAuth, scope: CoroutineScope, changeState: (AccountState) -> Unit)

    fun register(auth: FirebaseAuth, email: String, password: String, scope: CoroutineScope, changeState: (AccountState) -> Unit, setError: (String) -> Unit)

    fun forgotPassword(auth: FirebaseAuth, email: String, scope: CoroutineScope, setState: (String) -> Unit)


    fun setNewPassword(auth: FirebaseAuth, email: String, scope: CoroutineScope, setState: (String) -> Unit)
}