package com.kotlinenjoyers.trackiteasy.data.handler

import android.os.Looper
import android.util.Log
import com.kotlinenjoyers.trackiteasy.AppContainer
import com.kotlinenjoyers.trackiteasy.repository.parsers.interfaces.ParserRepository
import com.kotlinenjoyers.trackiteasy.repository.retrievers.RetrieverRepository
import com.kotlinenjoyers.trackiteasy.ui.navigation.FindingStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

class ParcelHandler private constructor(container: AppContainer) {
    private val isMainThread = Looper.getMainLooper().thread === Thread.currentThread()
    private var parserRepositories : List<ParserRepository>
    private var findingParser = false
    private var retrieving = false
    private var retrievingTime : Instant = Instant.now().minusSeconds(600)
    private var retrieverRepositories : List<RetrieverRepository>
    private val mutexRetrieving = Mutex()
    private val mutexFindingParser = Mutex()

    init {
        runBlocking {
            val removedList = mutableListOf<String>()
            val parsersTemp = mutableListOf<ParserRepository>()
            val retrieversTemp = mutableListOf<RetrieverRepository>()
            val idsTemp = mutableMapOf<String, String>()

            Constants.parsersExtractorsList.forEach { parcelInfo ->
                idsTemp[parcelInfo.id] = parcelInfo.version
                parsersTemp.add(parcelInfo.parserRepository)
                retrieversTemp.add(parcelInfo.retrieverRepository)
            }

            val ids = idsTemp.toMap()
            parserRepositories = parsersTemp.toList()
            retrieverRepositories = retrieversTemp.toList()

            var index = 0
            container.parsersRetrieversDataStore.data.first { parsersData ->
                parsersData.parsersRetrieversMap.forEach { (currentId, currentVersion) ->
                    val newVersion = idsTemp.remove(currentId)
                    if (newVersion != null) {
                        if (newVersion != currentVersion)
                            retrieverRepositories[index].fixVersion(currentVersion)
                    } else
                        removedList.add(currentId)
                    index++
                }
                true
            }

            removedList.forEach {
                container.parcelsRepository.deleteById(it)
            }

            if (idsTemp.isNotEmpty() || removedList.isNotEmpty()) {
                container.parsersRetrieversDataStore.updateData {
                    it.toBuilder()
                        .clear()
                        .build()
                }
                container.parsersRetrieversDataStore.updateData {
                    it.toBuilder()
                        .putAllParsersRetrievers(ids)
                        .build()
                }
            }
        }
    }

    suspend fun findParser(container: AppContainer, name : String,  trackingId : String) : FindingStatus {
        if (isMainThread)
            throw Exception("ERROR FINDING PARSER ON MAIN THREAD")
        if (findingParser)
            return FindingStatus.AlreadySearching
        var resultParsers = FindingStatus.AlreadySearching
        mutexFindingParser.withLock {
            if (findingParser)
                return@withLock

            findingParser = true
            Log.d("PARCEL HANDLER FINDER", "STARTED FINDING")
            val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
                Log.e("Find Parser", coroutineContext.toString())
                Log.e("Find Parser", throwable.toString())
                Log.e("Find Parser", throwable.stackTraceToString())
            }

            val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + exceptionHandler)

            scope.launch {
                for (parser in parserRepositories)
                    launch {
                        if (parser.fetchHandler(container, trackingId, name))
                            resultParsers = FindingStatus.Found
                    }
            }.join()

            scope.cancel()
            if (resultParsers == FindingStatus.AlreadySearching)
                resultParsers = FindingStatus.NotFound

            Log.d("PARCEL HANDLER FINDER", "FINISHED FINDING")
            findingParser = false
        }
        return resultParsers
    }


    suspend fun startRetrieving(appContainer: AppContainer) {
        if (isMainThread)
            throw Exception("ERROR RETRIEVING ON MAIN THREAD")
        if (retrieving || (Instant.now().isBefore(retrievingTime.plusSeconds(30))))
            return
        mutexRetrieving.withLock {
            if (retrieving)
                return@withLock

            retrieving = true
            val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
                Log.e("Start Retrieving", coroutineContext.toString())
                Log.e("Start Retrieving", throwable.toString())
                Log.e("Start Retrieving", throwable.stackTraceToString())

            }

            Log.d("PARCEL HANDLER RETRIEVER", "STARTED RETRIEVING")
            val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + exceptionHandler)
            scope.launch {
                for (retriever in retrieverRepositories)
                    launch {
                        retriever.fetchParcels(appContainer)
                    }
            }.join()

            scope.cancel()

            retrievingTime = Instant.now()
            Log.d("PARCEL HANDLER RETRIEVER", "FINISHED RETRIEVING")
            retrieving = false
        }
    }

    companion object {
        @Volatile
        private var Instance : ParcelHandler? = null

        fun getInstance(container: AppContainer): ParcelHandler =
            Instance ?: synchronized(this) {
                Instance
                    ?: ParcelHandler(container).also { Instance = it }
            }

        fun compare(version1: String, version2: String) : Int {
            if (getInt(version1) > getInt(version2))
                return 1
            return -1
        }

        private fun getInt(version: String) : Int {
            val list = version.split('.')
            assert(list.size == 2)
            return (list[0].toInt() shl 24) or (list[1].toInt() shl 16)
        }
    }
}