package com.individual_project3.kodegame.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.individual_project3.kodegame.data.progress.ChildWithProgress
import com.individual_project3.kodegame.data.progress.ProgressDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParentDashboardViewModel(
    private val dao: ProgressDao
) : ViewModel() {

    data class UiState(
        val loading: Boolean = true,
        val children: List<ChildWithProgress> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()

    fun loadChildren(parentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState(loading = true)

            val results = dao.loadChildrenProgress(parentId)

            _uiState.value = UiState(
                loading = false,
                children = results
            )
        }
    }
}
