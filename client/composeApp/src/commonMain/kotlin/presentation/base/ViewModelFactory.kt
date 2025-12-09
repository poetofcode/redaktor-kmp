package presentation.base

import presentation.base.ViewModel

interface ViewModelFactory<out T : ViewModel<*>> {
    fun createViewModel() : T

    val vmTypeName: String
}