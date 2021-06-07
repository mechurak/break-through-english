package com.shimnssso.headonenglish.ui.daylist

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.api.services.drive.model.File
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.shimnssso.headonenglish.Graph
import com.shimnssso.headonenglish.googlesheet.SheetHelper
import com.shimnssso.headonenglish.repository.LectureRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val repository: LectureRepository = Graph.lectureRepository,
) : ViewModel() {
    val lectures = repository.lectures
    val subject = repository.currentSubject
    val subjects = repository.subjects
    val global = repository.currentGlobal

    private var _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _showDialog = MutableLiveData<Boolean>(false)
    val showDialog: LiveData<Boolean>
        get() = _showDialog

    private var _sheetFiles = MutableLiveData<List<File>>(listOf())
    val sheetFiles: LiveData<List<File>>
        get() = _sheetFiles

    private var _isLogIn = MutableLiveData<Boolean>(false)
    val isLogIn: LiveData<Boolean>
        get() = _isLogIn

    fun setLogIn(isLogIn: Boolean) {
        _isLogIn.value = isLogIn
    }

    fun changeSubject(subjectId: Int) {
        viewModelScope.launch {
            repository.changeSubject(subjectId)
        }
    }

    fun removeSubject(subjectId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.removeSubject(subjectId)
            _isLoading.value = false
        }
    }

    fun refresh(shouldCheckInterval: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.refresh(shouldCheckInterval)
            _isLoading.value = false
        }
    }

    fun getSheetIdAndShowDialog(contentResolver: ContentResolver, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            val sheetFiles = SheetHelper.getFilesFromUri(contentResolver, uri)
            Timber.i("sheetFiles: $sheetFiles")
            _sheetFiles.value = sheetFiles
            _showDialog.value = true
            _isLoading.value = false
        }
    }

    fun dismissSheetFetchDialog() {
        viewModelScope.launch {
            _showDialog.value = false
        }
    }

    fun importSubject(name: String, sheetId: String) {
        viewModelScope.launch {
            _showDialog.value = false
            _isLoading.value = true
            val spreadsheet: Spreadsheet = SheetHelper.fetchSpreadsheet(sheetId)
            val subjectId = repository.createSubject(name, sheetId)

            repository.importSpreadsheet(spreadsheet, subjectId)

            _isLoading.value = false
        }
    }

    override fun onCleared() {
        Timber.e("onCleared()!!")
    }
}