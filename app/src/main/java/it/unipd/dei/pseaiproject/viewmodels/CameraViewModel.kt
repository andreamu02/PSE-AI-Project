package it.unipd.dei.pseaiproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.unipd.dei.pseaiproject.detection.ObjectDetectorHelper

class CameraViewModel: ViewModel() {
    private val _objectDetectorHelper = MutableLiveData<ObjectDetectorHelper?>()
    val objectDetectorHelper: LiveData<ObjectDetectorHelper?> get() = _objectDetectorHelper

    fun setObjectDetectorHelper(helper: ObjectDetectorHelper) {
        _objectDetectorHelper.value = helper
    }
}