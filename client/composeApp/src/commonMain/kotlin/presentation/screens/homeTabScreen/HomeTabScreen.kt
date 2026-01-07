package presentation.screens.homeTabScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.Tabs
import presentation.navigation.BaseScreen
import presentation.navigation.NavStateImpl
import presentation.navigation.Navigator
import presentation.screens.catalogScreen.CatalogScreen


class HomeTabScreen : BaseScreen<HomeTabViewModel>() {

    override val screenId: String
        get() = Tabs.HOME.key

    override val viewModel: HomeTabViewModel
        get() = viewModelStore.getViewModel<HomeTabViewModel>()

    private val navState by lazy {
        NavStateImpl(viewModelStore).apply {
            push(CatalogScreen())
        }
    }

    override val isMenuVisible: Boolean = true

    @Composable
    override fun Content() {
        Navigator(modifier = Modifier.fillMaxSize(), state = navState)
    }

}
