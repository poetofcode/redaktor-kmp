package presentation.screens.profileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import presentation.LocalMainAppState
import presentation.Tabs
import presentation.base.postSharedEvent
import presentation.navigation.BaseScreen
import presentation.navigation.HideBottomSheetEffect
import presentation.navigation.ShowModalBottomSheetEffect
import presentation.navigation.postSideEffect
import presentation.theme.AppColors
import presentation.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
class ProfileScreen : BaseScreen<ProfileViewModel>() {
    override val screenId: String
        get() = Tabs.PROFILE.key

    override val viewModel: ProfileViewModel
        get() = viewModelStore.getViewModel<ProfileViewModel>()

    override val isMenuVisible: Boolean = true

    val state get() = viewModel.state.value

    @Composable
    override fun Content() {
        val isAuth = state.profile != null

        AppTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Настройки") },
                    navigationIcon = {},
                    actions = {
//                        if (readyState !is LoadingResource) {
//                            IconButton(onClick = {
//                                viewModel.fetchFeed()
//                            }) {
//                                Icon(
//                                    imageVector = Icons.Default.Refresh,
//                                    contentDescription = "Reload",
//                                )
//                            }
//                        }
                    }
                )

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    if (!isAuth) {
                        UnsignedProfile()
                    } else {
                        SignedProfile()
                    }
                }

            }
        }
    }

    @Composable
    fun UnsignedProfile() {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.wrapContentSize().padding(16.dp)) {
                /*
                Text(
                    text = "Войдите в аккаунт, чтобы получить доступ ко всем функциям приложения",
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Button(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 30.dp),
                    onClick = {
                        viewModel.onSignInToAccountButtonClick()
                    }
                ) {
                    Text(text = "Войти в аккаунт")
                }
                OutlinedButton(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp),
                    onClick = {
                        viewModel.onRegistrationButtonClick()
                    }
                ) {
                    Text(text = "Регистрация")
                }
                 */

                GroupedBox(
                    title = "Отображение"
                ) {
                    CommonPrefs()
                }

                GroupedBox(
                    modifier = Modifier.padding(top = 16.dp),
                    title = "О приложении"
                ) {
                    AboutApp()
                }
            }
        }
    }

    @Composable
    fun GroupedBox(
        modifier: Modifier = Modifier,
        title: String,
        content: @Composable () -> Unit
    ) {
        Column(
            modifier = modifier.clip(shape = RoundedCornerShape(10.dp))
                .background(color = AppColors.contentBackgroundColor)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = AppColors.iconMutedColor,
            )
            Spacer(modifier = Modifier.size(8.dp))
            Box(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp)) {
                content()
            }
        }
    }

    @Composable
    fun CommonPrefs(modifier: Modifier = Modifier) {
        val localMainAppState = LocalMainAppState.current
        val isDarkMode = localMainAppState.isDarkMode.value

        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = {
                        localMainAppState.isDarkMode.value = it
                    }
                )
                Text(
                    text = "Тёмная тема",
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }

    @Composable
    fun AboutApp(modifier: Modifier = Modifier) {
        /*
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        val githubLink = "https://github.com/poetofcode/redaktor"
        Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
            // App version
            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Версия $versionName ($versionCode)",
                    modifier = Modifier,
                )
            }

            // Github link
            Row(
                modifier = Modifier.padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ClickableItem(
                    modifier = Modifier,
                    text = "Страница на Github"
                ) {
                    viewModel.postSharedEvent(OnOpenExternalBrowserSharedEvent(githubLink))
                }
            }
        }
         */
    }

    @Composable
    fun SignedProfile() {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.wrapContentSize().align(Alignment.Center)) {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(
                        text = "Вы авторизованы",
                        color = Color.Gray,
                    )
                    Text(
                        text = state.profile?.email.orEmpty(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 6.dp),
                    )
                }

                ClickableItem(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    text = "Выйти из аккаунта"
                ) {
                    postSideEffect(ShowModalBottomSheetEffect {
                        ConfirmContent()
                    })
                }
            }
        }
    }

    @Composable
    fun ConfirmContent() = Surface {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Вы действительно хотите выйти из аккаунта?")
            Row(
                Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier,
                    onClick = {
                        postSideEffect(HideBottomSheetEffect)
                    }
                ) {
                    Text(text = "Отмена")
                }
                Button(
                    modifier = Modifier,
                    onClick = {
                        viewModel.onConfirmQuit()
                    }
                ) {
                    Text(text = "Да")
                }
            }
        }
    }

    @Composable
    fun ClickableItem(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
        Box(modifier.clickable {
            onClick()
        }) {
            Text(
                text = text,
                color = AppColors.linkColor,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }

}
