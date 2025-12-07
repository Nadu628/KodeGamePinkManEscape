package com.individual_project3.kodegame.ui.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.individual_project3.kodegame.data.db.AppDatabase
import com.individual_project3.kodegame.data.progress.ProgressDao
import com.individual_project3.kodegame.data.progress.ProgressRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChildProgressViewModel(
    private val progressDao: ProgressDao
) : ViewModel() {

    private val _progressRecords = MutableStateFlow<List<ProgressRecord>>(emptyList())
    val progressRecords: StateFlow<List<ProgressRecord>> = _progressRecords

    suspend fun loadProgress(childId: Int) {
        val records = progressDao.getProgressForChild(childId)
        _progressRecords.value = records
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(context)
            val dao = db.progressDao()
            @Suppress("UNCHECKED_CAST")
            return ChildProgressViewModel(dao) as T
        }
    }
}
