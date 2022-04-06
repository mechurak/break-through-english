package com.shimnssso.headonenglish.ui.quiz.bold

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.repository.LectureRepository
import com.shimnssso.headonenglish.room.asDomainCard

class BoldQuizViewModel(
    private val subjectId: Int,
    private val date: String,
    private val repository: LectureRepository = Graph.lectureRepository,
) : ViewModel() {
    val cards: LiveData<List<DomainCard>> =
        Transformations.map(repository.getCardsForQuiz(subjectId, date)) {
            it.asDomainCard()
        }
    val lecture = repository.getLecture(subjectId, date)

    private val _curIdx: MutableLiveData<Int> = MutableLiveData<Int>(0)
    val curIdx: LiveData<Int>
        get() = _curIdx

    fun next() {
        cards.value?.let {
            var tempValue = _curIdx.value?.plus(1) ?: 0
            if (tempValue > it.lastIndex) {
                tempValue = 0
            }
            _curIdx.value = tempValue
        }
    }

    fun first() {
        cards.value?.let {
            _curIdx.value = 0
        }
    }

    class Factory(
        private val subjectId: Int,
        private val date: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BoldQuizViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BoldQuizViewModel(subjectId, date) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}