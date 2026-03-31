package edu.nd.pmcburne.hello.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacemarkDao {

    @Upsert
    suspend fun upsertLocations(items: List<LocationEntity>)

    @Upsert
    suspend fun upsertTags(items: List<TagEntity>)

    @Upsert
    suspend fun upsertLocationTags(items: List<LocationTagEntity>)

    @Query("SELECT tag FROM tags ORDER BY tag ASC")
    fun observeTags(): Flow<List<String>>

    @Query(
        """
        SELECT l.id, l.name, l.description, l.lat AS latitude, l.lon AS longitude
        FROM locations l
        INNER JOIN location_tags lt ON lt.location_id = l.id
        WHERE lt.tag = :tag
        ORDER BY l.name ASC
        """
    )
    fun observeLocationsByTag(tag: String): Flow<List<LocationMarker>>

    @Transaction
    suspend fun upsertAll(
        locations: List<LocationEntity>,
        tags: List<TagEntity>,
        crossRefs: List<LocationTagEntity>
    ) {
        upsertLocations(locations)
        upsertTags(tags)
        upsertLocationTags(crossRefs)
    }
}