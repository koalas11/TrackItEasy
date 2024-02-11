package com.kotlinenjoyers.trackiteasy.data.storage.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.kotlinenjoyers.trackiteasy.parceldatastore.Accounts
import com.kotlinenjoyers.trackiteasy.util.CryptoManager
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream


object AccountsSerializer : Serializer<Accounts> {

    override val defaultValue: Accounts = Accounts.getDefaultInstance()


    override suspend fun readFrom(input: InputStream): Accounts {
        try {
            return Accounts.parseFrom(
                CryptoManager.decrypt(DataInputStream(input.buffered()))
            )
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: Accounts,
        output: OutputStream
    ) {
        CryptoManager.encrypt(t.toByteArray(), DataOutputStream(output.buffered()))
    }
}

val Context.AccountsDataStore: DataStore<Accounts> by dataStore(
    fileName = "Accounts.pb",
    serializer = AccountsSerializer
)
