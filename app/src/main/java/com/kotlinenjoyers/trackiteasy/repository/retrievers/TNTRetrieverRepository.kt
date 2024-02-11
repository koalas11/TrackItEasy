package com.kotlinenjoyers.trackiteasy.repository.retrievers

import android.util.Log
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.data.handler.Constants
import com.kotlinenjoyers.trackiteasy.repository.parsers.TNTParserRepository

object TNTRetrieverRepository : RetrieverRepository {
    override suspend fun fetchParcels(container: AppContainer) {
        Log.d("TNT RETRIEVER", "STARTED RETRIEVING")
        val parcelsList = container.parcelsRepository.getAllTrackingIdNameById(Constants.TNT_ID)

        if (parcelsList.isNotEmpty()) {
            val trackingIdsList = mutableListOf<String>()
            val namesList = mutableListOf<String>()
            parcelsList.forEach {
                trackingIdsList.add(it.trackingId)
                namesList.add(it.name)
            }

            TNTParserRepository.handleParcels(container, trackingIdsList, namesList)
        }
        Log.d("TNT RETRIEVER", "FINISHED RETRIEVING")
    }

    override fun fixVersion(oldVersion: String) {
        TODO("Not yet implemented")
    }
}