package com.harman.vsimakov2.CAPP.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ApplicationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Applications Fragment"
    }
    val text: LiveData<String> = _text
}