package it.unipd.dei.pseaiproject.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import it.unipd.dei.pseaiproject.detection.ObjectDetectorHelper

/**
 * ViewModel per la condivisione dell'ObjectDetectorHelper tra i componenti dell'applicazione.
 */
class CameraViewModel: ViewModel() {
    private val _objectDetectorHelper = MutableLiveData<ObjectDetectorHelper?>()
    val objectDetectorHelper: LiveData<ObjectDetectorHelper?> get() = _objectDetectorHelper

    /**
     * Imposta l'ObjectDetectorHelper nell'istanza di ViewModel.
     */
    fun setObjectDetectorHelper(helper: ObjectDetectorHelper) {
        _objectDetectorHelper.value = helper
    }
}
