package com.example.runningevents.data.repositories

import android.app.Application
import android.content.SharedPreferences
import com.example.runningevents.R
import com.example.runningevents.data.dto.RaceDto
import com.example.runningevents.data.mappers.toRace
import com.example.runningevents.data.mappers.toRaceDto
import com.example.runningevents.domain.models.Race
import com.example.runningevents.domain.repositories.RacesRepository
import com.example.runningevents.utils.Constants.ORDER_BY_DATE
import com.example.runningevents.utils.Constants.ORDER_BY_GEOHASH
import com.example.runningevents.utils.RacesSortedBy
import com.example.runningevents.utils.Result
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

class RacesRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val application: Application,
    private val firestore: FirebaseFirestore
) : RacesRepository {

    private val racesCollection = firestore.collection("races")
    private val racesLimit = 4L
    private val myRacesLimit = 10L
    private var lastVisible: DocumentSnapshot? = null

    override suspend fun getRacesByDate(
        isRefreshing: Boolean,
        racesDistanceFilter: List<String>
    ): Result<List<Race>> {
        return try {
            if (isRefreshing) {
                lastVisible = null
            }
            val timestampNow = Timestamp.now()
            val races: Query = if (lastVisible == null) {
                racesCollection
                    .whereArrayContainsAny("distanceFilter", racesDistanceFilter)
                    .orderBy(ORDER_BY_DATE, Query.Direction.ASCENDING)
                    .startAfter(timestampNow)
                    .limit(racesLimit)
            } else {
                racesCollection
                    .whereArrayContainsAny("distanceFilter", racesDistanceFilter)
                    .orderBy(ORDER_BY_DATE, Query.Direction.ASCENDING)
                    .startAfter(timestampNow)
                    .startAfter(lastVisible as DocumentSnapshot)
                    .limit(racesLimit)
            }

            val query = races.get().await()
            if (!query.isEmpty) {
                lastVisible = query.documents[query.documents.size - 1]
            } else if (query.isEmpty && lastVisible != null) {
                return Result.Success(emptyList())
            } else if (query.isEmpty) {
                throw Exception("There are no races.")
            }
            val racesResult = query.toObjects(RaceDto::class.java).map { it.toRace() }
            Result.Success(racesResult)
        } catch (e: Exception) {
            return Result.Failure(e.message.toString())
        }
    }

    override suspend fun getRacesByLocation(
        isRefreshing: Boolean,
        latitude: Double,
        longitude: Double,
        radius: Int,
        racesDistanceFilter: List<String>
    ): Result<List<Race>> {
        return try {
            if (isRefreshing) {
                lastVisible = null
            }
            val timestampNow = Timestamp.now()
            val centerPointLocation = GeoLocation(latitude, longitude)
            val radiusInMeters = (radius * 1000).toDouble()
            val bounds = GeoFireUtils.getGeoHashQueryBounds(centerPointLocation, radiusInMeters)
            val tasks = mutableListOf<Task<QuerySnapshot>>()
            for (bound in bounds) {
                val query: Query = if (lastVisible == null) {
                    racesCollection
                        .whereArrayContainsAny("distanceFilter", racesDistanceFilter)
                        .orderBy(ORDER_BY_GEOHASH)
                        .orderBy(ORDER_BY_DATE, Query.Direction.ASCENDING)
                        .startAt(bound.startHash, timestampNow)
                        .endAt(bound.endHash)
                        .limit(racesLimit)
                } else {
                    racesCollection
                        .whereArrayContainsAny("distanceFilter", racesDistanceFilter)
                        .orderBy(ORDER_BY_GEOHASH)
                        .orderBy(ORDER_BY_DATE, Query.Direction.ASCENDING)
                        .startAt(bound.startHash, timestampNow)
                        .endAt(bound.endHash)
                        .startAfter(lastVisible as DocumentSnapshot)
                        .limit(racesLimit)
                }
                tasks.add(query.get())
            }

            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val coroutine = coroutineScope.async {
                val races = mutableListOf<RaceDto>()
                Tasks.whenAllComplete(tasks)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            var snap: QuerySnapshot
                            for (task in tasks) {
                                snap = task.result

                                for (document in snap.documents) {
                                    val docLatitude = document.getDouble("latitude")
                                    val docLongitude = document.getDouble("longitude")
                                    if (docLatitude != null && docLongitude != null) {
                                        val documentLocation =
                                            GeoLocation(docLatitude, docLongitude)
                                        val documentDistanceInMeters =
                                            GeoFireUtils.getDistanceBetween(
                                                documentLocation,
                                                centerPointLocation
                                            )

                                        if (documentDistanceInMeters <= radiusInMeters) {
                                            val race = document.toObject(RaceDto::class.java)
                                            race?.let { it -> races.add(it) }
                                        }
                                    }
                                }
                                if (!snap.isEmpty) {
                                    lastVisible = snap.documents[snap.documents.size - 1]
                                }
                            }
                        }
                    }.await()
                return@async races
            }
            val result = coroutine.await()
            delay(500L)
            val racesData = result.map { it.toRace() }
            Result.Success(racesData)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override suspend fun getMyRaces(isRefreshing: Boolean, userId: String): Result<List<Race>> {
        return try {
            if (isRefreshing) {
                lastVisible = null
            }
            val races: Query = if (lastVisible == null) {
                racesCollection
                    .whereEqualTo("createdBy", userId)
                    .orderBy("date", Query.Direction.ASCENDING)
                    .limit(myRacesLimit)
            } else {
                racesCollection
                    .whereEqualTo("createdBy", userId)
                    .orderBy("date", Query.Direction.ASCENDING)
                    .startAfter(lastVisible as DocumentSnapshot)
                    .limit(myRacesLimit)
            }
            val query = races.get().await()
            if (!query.isEmpty) {
                lastVisible = query.documents[query.documents.size - 1]
            }
            val racesResult = query.toObjects(RaceDto::class.java).map { it.toRace() }
            Result.Success(racesResult)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override suspend fun addNewRace(race: Race): Result<Unit> {
        return try {
            val document = racesCollection.document()
            val id = document.id
            val raceWithId = race.copy(
                raceId = id
            )
            document.set(raceWithId.toRaceDto()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override suspend fun deleteRace(raceId: String): Result<Unit> {
        return try {
            racesCollection.document(raceId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e.message.toString())
        }
    }

    override fun getRacesSortedBy(): RacesSortedBy {
        return when (sharedPreferences.getString(
            application.getString(R.string.races_sort_preference_key),
            application.getString(R.string.races_sort_option_date_key)
        )) {
            application.getString(R.string.races_sort_option_date_key) -> {
                RacesSortedBy.DATE
            }
            application.getString(R.string.races_sort_option_nearby_key) -> {
                RacesSortedBy.LOCATION
            }
            else -> RacesSortedBy.DATE
        }
    }

    override fun getRacesRadiusDistance(): Int {
        return sharedPreferences.getInt(
            application.getString(R.string.radius_preference_key),
            10
        )
    }

    override fun getRaceFilterDistances(): List<String> {
        val distances = mutableListOf<String>()
        val distanceAll = sharedPreferences.getBoolean(
            application.getString(R.string.filter_distance_option_all_key),
            true
        )
        val distance5km = sharedPreferences.getBoolean(
            application.getString(R.string.filter_distance_option_5km_key),
            false
        )
        val distance10km = sharedPreferences.getBoolean(
            application.getString(R.string.filter_distance_option_10km_key),
            false
        )
        val distanceHalf = sharedPreferences.getBoolean(
            application.getString(R.string.filter_distance_option_half_key),
            false
        )
        val distanceMarathon = sharedPreferences.getBoolean(
            application.getString(R.string.filter_distance_option_marathon_key),
            false
        )

        if (distanceAll) {
            distances.addAll(listOf("1", "2", "3", "4"))
        }
        if (distance5km) {
            distances.add("1")
        }
        if (distance10km) {
            distances.add("2")
        }
        if (distanceHalf) {
            distances.add("3")
        }
        if (distanceMarathon) {
            distances.add("4")
        }
        return distances
    }
}