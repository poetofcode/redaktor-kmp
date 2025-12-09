package presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import presentation.base.ViewModelStore


typealias AnyScreen = BaseScreen<*>

interface NavState {
    val screens: State<List<AnyScreen>>

    fun push(screen: AnyScreen)

    fun pop()

    fun moveToFront(screenId: String)

    fun isContainsScreenWithId(screenId: String) : Boolean
}

class NavStateImpl(val viewModelStore: ViewModelStore) : NavState {
    private val _screens = mutableStateOf<List<AnyScreen>>(emptyList())

    override val screens: State<List<AnyScreen>>
        get() = _screens

    override fun push(screen: AnyScreen) {
        screen.setVMStore(viewModelStore)
        _screens.value += screen
    }

    override fun pop() {
        if (_screens.value.size <= 1) {
            return
        }
        _screens.value.lastOrNull()?.let { topScreen ->
            viewModelStore.removeViewModel(topScreen.viewModel)
        }
        _screens.value = _screens.value.subList(0, _screens.value.size - 1)
    }

    override fun moveToFront(screenId: String) {
        var currScreens = _screens.value.toMutableList()
        currScreens.firstOrNull {
            it.screenId == screenId
        }?.let { found ->
            currScreens = currScreens.filterNot { it.screenId == screenId }.toMutableList()
            currScreens.add(found)
            _screens.value = currScreens
        }
    }

    override fun isContainsScreenWithId(screenId: String): Boolean {
        return screens.value.firstOrNull { it.screenId == screenId } != null
    }

}


@Composable
fun Navigator(
    modifier: Modifier = Modifier,
    state: NavState,
    tag: NavigatorTag = NavigatorTag.NONE,
) {
    val navigators = LocalNavigators.current + NavigatorInfo(state, tag)
    CompositionLocalProvider(LocalNavigators provides navigators) {
        Box(modifier) {
            val screens = state.screens.value
            screens.lastOrNull()?.PrepareContent()
        }
    }
}