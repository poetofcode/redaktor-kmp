package presentation.screens.profileTabScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import presentation.Tabs
import presentation.navigation.BaseScreen
import presentation.navigation.NavStateImpl
import presentation.navigation.Navigator
import presentation.screens.profileScreen.ProfileScreen

class ProfileTabScreen : BaseScreen<ProfileTabViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: ProfileTabViewModel
        get() = viewModelStore.getViewModel<ProfileTabViewModel>()

    override val isMenuVisible: Boolean = true

    private val navState by lazy {
        NavStateImpl(viewModelStore).apply {
            push(ProfileScreen())
        }
    }

    @Composable
    override fun Content() {
        Navigator(modifier = Modifier.fillMaxSize(), state = navState)
    }

}