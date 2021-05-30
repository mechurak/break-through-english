package com.shimnssso.headonenglish.ui.lecture

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val _originCards: LiveData<List<DomainCard>> = Transformations.map(repository.getCards(subjectId, date)) {
        it.asDomainCard()
    }

    private val _cards: MutableLiveData<List<DomainCard>> = MutableLiveData(mutableListOf())

    val cards: LiveData<List<DomainCard>>
        get() = _cards

    init {
        _originCards.observeForever {
            _cards.value = it!!.toMutableList()
        }
    }

    val lecture = repository.getLecture(date)

    fun update(card: DomainCard) {
        Timber.e("update $card")
        var targetIndex = -1
        val newCards = _cards.value!!.toMutableList()
        _cards.value!!.forEachIndexed { index, originCard ->
            if (card.order == originCard.order) {
                Timber.e("found!!")
                targetIndex = index
                Timber.e("found!! targetIndex $targetIndex")
                return@forEachIndexed
            }
        }
        newCards[targetIndex] = card.copy(showDescription = !card.showDescription)
        _cards.value = newCards
    }

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