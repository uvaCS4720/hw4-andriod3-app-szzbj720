package edu.nd.pmcburne.hello.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val description: String,
    @ColumnInfo(name = "lat") val latitude: Double,
    @ColumnInfo(name = "lon") val longitude: Double
)

@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey val tag: String
)

@Entity(
    tableName = "location_tags",
    primaryKeys = ["location_id", "tag"]
)
data class LocationTagEntity(
    @ColumnInfo(name = "location_id") val locationId: Long,
    val tag: String
)

data class LocationMarker(
    val id: Long,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)