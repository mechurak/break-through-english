package com.shimnssso.headonenglish.ui.quiz

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.model.DomainCard
import com.shimnssso.headonenglish.repository.LectureRepository
import com.shimnssso.headonenglish.room.asDomainCard

class QuizViewModel(
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
        val last = cards.value!!.size - 1
        _curIdx.value = _curIdx.value?.plus(1)
        if (_curIdx.value!! > last) {
            _curIdx.value = 0
        }
    }

    class Factory(
        private val subjectId: Int,
        private val date: String,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QuizViewModel(subjectId, date) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}