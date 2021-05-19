package com.shimnssso.headonenglish.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.repository.LectureRepository
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: LectureRepository = Graph.lectureRepository,
): ViewModel() {
    val lectures = repository.lectures

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    fun refresh(shouldCheckInterval: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refresh(shouldCheckInterval)
            _isLoading.value = false
        }
    }
}