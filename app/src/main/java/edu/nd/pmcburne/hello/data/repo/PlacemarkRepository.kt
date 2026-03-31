package edu.nd.pmcburne.hello.data.repo

import edu.nd.pmcburne.hello.data.api.PlacemarkService
import edu.nd.pmcburne.hello.data.db.LocationEntity
import edu.nd.pmcburne.hello.data.db.LocationTagEntity
import edu.nd.pmcburne.hello.data.db.PlacemarkDao
import edu.nd.pmcburne.hello.data.db.TagEntity
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PlacemarkRepository(
    private val service: PlacemarkService,
    private val dao: PlacemarkDao
) {
    private val syncMutex = Mutex()
    private var syncedThisLaunch = false

    fun observeTags() = dao.observeTags()
    fun observeLocationsByTag(tag: String) = dao.observeLocationsByTag(tag)

    suspend fun syncFromApiOncePerLaunch() {
        syncMutex.withLock {
            if (syncedThisLaunch) return

            val api = service.getPlacemarks()

            val locations = api.map {
                LocationEntity(
                    id = it.id,
                    name = it.name,
                    description = it.description,
                    latitude = it.visualCenter.latitude,
                    longitude = it.visualCenter.longitude
                )
            }

            val tags = api.flatMap { it.tagList }
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toSet()
                .map { TagEntity(it) }

            val crossRefs = api.flatMap { p ->
                p.tagList.mapNotNull { t ->
                    val tag = t.trim()
                    if (tag.isEmpty()) null else LocationTagEntity(locationId = p.id, tag = tag)
                }
            }

            dao.upsertAll(locations, tags, crossRefs)
            syncedThisLaunch = true
        }
    }
}