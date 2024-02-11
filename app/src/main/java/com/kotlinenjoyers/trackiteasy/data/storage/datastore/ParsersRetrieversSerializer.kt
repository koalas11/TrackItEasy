package com.kotlinenjoyers.trackiteasy.data.storage.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import com.kotlinenjoyers.trackiteasy.parceldatastore.ParsersRetriever
import java.io.InputStream
import java.io.OutputStream

object ParsersExtractorsSerializer : Serializer<ParsersRetriever> {

    override val defaultValue: ParsersRetriever = ParsersRetriever.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): ParsersRetriever {
        try {
            return ParsersRetriever.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: ParsersRetriever,
        output: OutputStream
    ) {
        t.writeTo(output)
    }
}

val Context.ParsersRetrieversDataStore: DataStore<ParsersRetriever> by dataStore(
    fileName = "ParsersRetrievers.pb",
    serializer = ParsersExtractorsSerializer
)