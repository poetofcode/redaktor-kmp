package presentation.base

import kotlinx.coroutines.CoroutineScope
import presentation.viewModelCoroutineScopeProvider

class ViewModelStore(
    val coroutineScope: CoroutineScope,
    val vmFactories: List<ViewModelFactory<*>>
) {

    init {
        viewModelCoroutineScopeProvider.scope = coroutineScope
    }
    
    val viewModels: MutableMap<String, ViewModel<*>> = mutableMapOf()

    inline fun <reified T: ViewModel<*>> getViewModel(_key: String = String()) : T {
        val key = _key.takeIf { it.isNotBlank() } ?: T::class.java.typeName

        if (viewModels.containsKey(key)) {
            return viewModels[key] as? T ?: throw Exception("ViewModelFactory unknown exception")
        }

        var createdVm: T? = null
        vmFactories.firstOrNull { factory ->
            factory.vmTypeName == T::class.java.typeName
        }?.let { found ->
            createdVm = found.createViewModel() as T
            viewModels[key] = createdVm as T
        }
        return createdVm ?: throw Exception("ViewModelFactory not found")
    }
    
    fun removeViewModel(viewModel: ViewModel<*>) {
        viewModels.values.removeIf { it == viewModel }
        viewModel.onCleared()
    }

    fun clearAll() {
        viewModels.forEach { it.value.onCleared() }
    }

}