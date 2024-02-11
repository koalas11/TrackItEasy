package com.kotlinenjoyers.trackiteasy.repository.firebase

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope

interface IFireStoreRepository {
    fun loadToCloud(context: Context, auth: FirebaseAuth, scope: CoroutineScope, setState: (String) -> Unit)

    fun fetchParcels(context: Context, auth: FirebaseAuth, scope: CoroutineScope, setState: (String) -> Unit)

    fun clearCloud(auth: FirebaseAuth, scope: CoroutineScope, setState: (String) -> Unit)
}