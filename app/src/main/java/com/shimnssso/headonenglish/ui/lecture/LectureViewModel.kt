package com.shimnssso.headonenglish.ui.lecture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.repository.LectureRepository

class LectureViewModel(
    private val subjectId: Int,
    private val date: String,
    private val repository: LectureRepository = Graph.lectureRepository,
) : ViewModel() {
    val cards = repository.getCards(subjectId, date)
    val lecture = repository.getLecture(date)

    class Factory(
        private val subjectId: Int,
        private val date: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LectureViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LectureViewModel(subjectId, date) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}