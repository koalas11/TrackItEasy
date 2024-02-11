package com.kotlinenjoyers.trackiteasy.repository.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kotlinenjoyers.trackiteasy.TrackItEasyApplication
import com.kotlinenjoyers.trackiteasy.data.handler.Constants
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcel.Parcel
import com.kotlinenjoyers.trackiteasy.data.storage.room.parcelhistory.ParcelHistory
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Timestamp

object FireStoreRepository: IFireStoreRepository {
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("FireStoreRepository", coroutineContext.toString())
        Log.e("FireStoreRepository", throwable.toString())
        Log.e("FireStoreRepository", throwable.stackTraceToString())
    }

    private val dispatchersDefault = Dispatchers.Default


    override fun loadToCloud(context: Context, auth: FirebaseAuth, scope: CoroutineScope, setState: (String) -> Unit) {
        try {
            scope.launch(dispatchersDefault + exceptionHandler) {
                if (auth.currentUser == null)
                    return@launch

                val container = (context.applicationContext as TrackItEasyApplication).container
                val parcels = container.parcelsRepository.getAllParcels().filter {
                    it.id != Constants.AMAZON_ID
                }
                val parcelsJson = JSONArray()
                parcels.forEach {
                    val jsonObj = JSONObject()
                    jsonObj.put("id", it.id)
                    jsonObj.put("trackingId", it.trackingId)
                    jsonObj.put("name", it.name)
                    jsonObj.put("extraInfo", it.extraInfo)
                    jsonObj.put("trackingEnd", it.trackingEnd)
                    jsonObj.put("json", it.json)
                    jsonObj.put("lastUpdate", it.lastUpdate)

                    parcelsJson.put(jsonObj)
                }

                val parcelsHistory = container.parcelsHistoryRepository.getAllParcels().filter {
                    it.id != Constants.AMAZON_ID
                }

                val parcelsHistoryJson = JSONArray()
                parcelsHistory.forEach {
                    val jsonObj = JSONObject()
                    jsonObj.put("id", it.id)
                    jsonObj.put("trackingId", it.trackingId)
                    jsonObj.put("name", it.name)
                    jsonObj.put("extraInfo", it.extraInfo)
                    jsonObj.put("trackingEnd", it.trackingEnd)
                    jsonObj.put("json", it.json)

                    parcelsHistoryJson.put(jsonObj)
                }

                val parcelsToUpload = hashMapOf(
                    "Parcels" to parcelsJson.toString(),
                    "ParcelsHistory" to parcelsHistoryJson.toString(),
                )

                val db = Firebase.firestore
                db.collection("parcels").document(auth.currentUser!!.uid).set(
                    parcelsToUpload
                )
                setState("Parcles Uploaded!")
            }
        } catch (e: Exception) {
            Log.e("FireStoreRepository", e.toString())
        }
    }

    override fun fetchParcels(context: Context, auth: FirebaseAuth, scope: CoroutineScope, setState: (String) -> Unit) {
        scope.launch(dispatchersDefault + exceptionHandler) {
            if (auth.currentUser == null)
                return@launch

            val container = (context.applicationContext as TrackItEasyApplication).container
            val parcelsRepository = container.parcelsRepository
            val parcelsHistoryRepository = container.parcelsHistoryRepository

            val db = Firebase.firestore
            db.collection("parcels").document(auth.currentUser!!.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        scope.launch(dispatchersDefault  + exceptionHandler) {
                            val parcels = document.getString("Parcels")
                            if (parcels != null) {
                                val parcelsJsonArray = JSONArray(parcels)
                                for (i in 0..<parcelsJsonArray.length()) {
                                    val parcelJson = parcelsJsonArray.getJSONObject(i)
                                    parcelsRepository.insert(
                                        Parcel(
                                            id = parcelJson.getString("id"),
                                            trackingId = parcelJson.getString("trackingId"),
                                            name = parcelJson.getString("name"),
                                            extraInfo = if (parcelJson.has("extraInfo")) parcelJson.getString(
                                                "extraInfo"
                                            ) else null,
                                            trackingEnd = if (parcelJson.has("trackingEnd")) Timestamp.valueOf(
                                                parcelJson.getString("trackingEnd")
                                            ) else null,
                                            json = JSONObject(parcelJson.getString("json")),
                                            lastUpdate = Timestamp.valueOf(parcelJson.getString("lastUpdate")),
                                        )
                                    )
                                }
                                val parcelsHistoryJsonArray =
                                    JSONArray(document.getString("ParcelsHistory"))
                                for (i in 0..<parcelsHistoryJsonArray.length()) {
                                    val parcelJson = parcelsHistoryJsonArray.getJSONObject(i)
                                    parcelsHistoryRepository.insert(
                                        ParcelHistory(
                                            id = parcelJson.getString("id"),
                                            trackingId = parcelJson.getString("trackingId"),
                                            name = parcelJson.getString("name"),
                                            extraInfo = if (parcelJson.has("extraInfo")) parcelJson.getString(
                                                "extraInfo"
                                            ) else null,
                                            trackingEnd = Timestamp.valueOf(parcelJson.getString("trackingEnd")),
                                            json = JSONObject(parcelJson.getString("json")),
                                        )
                                    )
                                }
                            }
                        }.invokeOnCompletion {
                            setState("Parcles Fetched")
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("FireStoreRepository", it.toString())
                    setState("Error!")
                }
        }
    }

    override fun clearCloud(auth: FirebaseAuth, scope: CoroutineScope, setState: (String) -> Unit) {
        scope.launch(dispatchersDefault + exceptionHandler) {
            if (auth.currentUser == null)
                return@launch

            val db = Firebase.firestore
            db.collection("parcels").document(auth.currentUser!!.uid).delete()
                .addOnSuccessListener {
                    setState("Cloud Cleared!")
                }
                .addOnFailureListener {
                    setState("Error!")
                }
        }
    }
}