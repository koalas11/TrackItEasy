package com.kotlinenjoyers.trackiteasy.repository.retrievers

import com.kotlinenjoyers.trackiteasy.AppContainer

interface RetrieverRepository {

    suspend fun fetchParcels(container: AppContainer)

    fun fixVersion(oldVersion : String)
}