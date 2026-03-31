package edu.nd.pmcburne.hello

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.nd.pmcburne.hello.data.db.LocationMarker
import edu.nd.pmcburne.hello.data.repo.AppGraph
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiState(
    val selectedTag: String = "core",
    val tags: List<String> = emptyList(),
    val locations: List<LocationMarker> = emptyList(),
    val error: String? = null
)

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppGraph.createRepository(app.applicationContext)

    private val selectedTag = MutableStateFlow("core")
    private val error = MutableStateFlow<String?>(null)

    private val tagsFlow = repo.observeTags().distinctUntilChanged()

    private val effectiveTagFlow = combine(selectedTag, tagsFlow) { selected, tags ->
        if (selected in tags) selected else (tags.firstOrNull() ?: selected)
    }.distinctUntilChanged()

    private val locationsFlow = effectiveTagFlow.flatMapLatest { tag ->
        repo.observeLocationsByTag(tag)
    }

    val uiState: StateFlow<UiState> =
        combine(tagsFlow, effectiveTagFlow, locationsFlow, error) { tags, tag, locations, err ->
            UiState(
                selectedTag = tag,
                tags = tags,
                locations = locations,
                error = err
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState())

    init {
        viewModelScope.launch {
            try {
                repo.syncFromApiOncePerLaunch()
            } catch (t: Throwable) {
                error.value = t.message ?: "Failed to load placemarks"
            }
        }
    }

    fun setSelectedTag(tag: String) {
        selectedTag.value = tag
    }
}