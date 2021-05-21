package com.shimnssso.headonenglish.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.repository.LectureRepository
import com.shimnssso.headonenglish.room.LectureDatabase
import kotlinx.coroutines.launch

class GlobalViewModel(
    private val repository: LectureRepository = Graph.lectureRepository,
    private val database: LectureDatabase = Graph.database,
): ViewModel() {
    val currentGlobal = database.globalDao.currentData()
    val subjects = database.subjectDao.getSubjects()

    fun changeSubject(subjectId: Int) {
        viewModelScope.launch {
            repository.changeSubject(subjectId)
        }
    }
}