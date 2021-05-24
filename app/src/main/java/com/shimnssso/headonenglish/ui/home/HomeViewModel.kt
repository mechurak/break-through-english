package com.shimnssso.headonenglish.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.repository.LectureRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val repository: LectureRepository = Graph.lectureRepository,
): ViewModel() {
    val lectures = repository.lectures
    val subject = repository.currentSubject
    val subjects = repository.subjects
    val global = repository.currentGlobal


    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading


    fun changeSubject(subjectId: Int) {
        viewModelScope.launch {
            repository.changeSubject(subjectId)
        }
    }

    fun refresh(shouldCheckInterval: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refresh(shouldCheckInterval)
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        Timber.e("onCleared()!!")
    }
}