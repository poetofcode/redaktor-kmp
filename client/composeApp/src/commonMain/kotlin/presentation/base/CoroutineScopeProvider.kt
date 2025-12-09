package presentation

import kotlinx.coroutines.CoroutineScope

interface CoroutineScopeProvider {
    val viewModelScope: CoroutineScope
}

object viewModelCoroutineScopeProvider : CoroutineScopeProvider {
    lateinit var scope: CoroutineScope
    
    override val viewModelScope: CoroutineScope
        get() = scope

}