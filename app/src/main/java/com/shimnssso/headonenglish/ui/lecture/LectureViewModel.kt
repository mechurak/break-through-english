package com.shimnssso.headonenglish.ui.lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.repository.LectureRepository
import com.shimnssso.headonenglish.room.asDomainCard
import timber.log.Timber

class LectureViewModel(
    private val subjectId: Int,
    private val date: String,
    private val repository: LectureRepository = Graph.lectureRepository,
) : ViewModel() {
    val cards: LiveData<List<DomainCard>> = Transformations.map(repository.getCards(subjectId, date)) {
        it.asDomainCard()
    }
    val lecture = repository.getLecture(date)

    override fun onCleared() {
        Timber.e("onCleared()!!")
    }

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