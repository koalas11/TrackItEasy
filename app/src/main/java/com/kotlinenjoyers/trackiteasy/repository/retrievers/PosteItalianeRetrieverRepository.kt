package com.kotlinenjoyers.trackiteasy.repository.retrievers

import android.util.Log
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.handler.Constants.POSTE_ITALIANE_ID
import com.kotlinenjoyers.trackiteasy.repository.parsers.PosteItalianeParserRepository

object PosteItalianeRetrieverRepository : RetrieverRepository {
    override suspend fun fetchParcels(container: AppContainer) {
        Log.d("Poste Italiane RETRIEVER", "STARTED RETRIEVING")
        val parcelsList = container.parcelsRepository.getAllTrackingIdNameById(POSTE_ITALIANE_ID)

        if (parcelsList.isNotEmpty()) {
            val trackingIdsList = mutableListOf<String>()
            val namesList = mutableListOf<String>()
            parcelsList.forEach {
                trackingIdsList.add(it.trackingId)
                namesList.add(it.name)
            }

            PosteItalianeParserRepository.handleParcels(container, trackingIdsList, namesList)
        }
        Log.d("Poste Italiane RETRIEVER", "FINISHED RETRIEVING")
    }

    override fun fixVersion(oldVersion: String) {
        TODO("Not yet implemented")
    }
}