package presentation.screens.profileScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.LocalMainAppState
import presentation.Tabs
import presentation.model.ActionUI
import presentation.navigation.BaseScreen
import presentation.navigation.HideBottomSheetEffect
import presentation.navigation.HideOverlayEffect
import presentation.navigation.ShowModalBottomSheetEffect
import presentation.navigation.ShowOverlayEffect
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

    val focusRequester = FocusRequester()

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
                    title = "Импорт базы данных"
                ) {
                    ImportDB()
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
        val versionName = "0.1" // BuildConfig.VERSION_NAME
        val versionCode = 1 // BuildConfig.VERSION_CODE
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

//            // Github link
//            Row(
//                modifier = Modifier.padding(top = 10.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                ClickableItem(
//                    modifier = Modifier,
//                    text = "Страница на Github"
//                ) {
//                    viewModel.postSharedEvent(OnOpenExternalBrowserSharedEvent(githubLink))
//                }
//            }
        }
    }

    @Composable
    fun ImportDB() {
        Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
            Button(onClick = {
                postSideEffect(ShowOverlayEffect { ImportDBMenu() })
            }) {
                Text(
                    text = "Меню экспорта",
                    modifier = Modifier,
                )
            }
        }
    }

    @Composable
    private fun ImportDBMenu() {
        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val coroutineScope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).bringIntoViewRequester(bringIntoViewRequester)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        // .verticalScroll(textScrollState)
                        .focusRequester(focusRequester)
                        .onFocusEvent {
                            if (it.isFocused) {
                                coroutineScope.launch {
                                    delay(200)
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        },
                    value = state.modifiedDBContent,
                    maxLines = Int.MAX_VALUE,
                    onValueChange = {
                        viewModel.onDBContentChanged(it)
                        coroutineScope.launch {
                            bringIntoViewRequester.bringIntoView()
                        }
                    })
                Spacer(Modifier.size(floatingToolbarHeight))        // Extra space
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }

            FloatingToolbar(modifier = Modifier)
        }
    }

    @Composable
    fun FloatingToolbar(modifier: Modifier) {
        val actions: List<ActionUI> = listOf(
            ActionUI.Copy
        )
        Row(
            modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ActionButton(imageVector = Icons.Filled.Cancel) {
                postSideEffect(HideOverlayEffect)
            }
            Spacer(modifier = Modifier.weight(1f))
            // Row of dynamic actions
            actions.forEach {
                ActionItem(action = it)
            }
            Spacer(modifier = Modifier.size(10.dp))
            ActionButton(imageVector = Icons.Filled.Done) {
                viewModel.onImportDbApply()
            }
        }
    }

    @Composable
    private fun ActionItem(action: ActionUI) {
        ActionButton(
            imageVector = when (action) {
                ActionUI.Delete -> Icons.Filled.Delete
                ActionUI.Edit -> Icons.Filled.Edit
                ActionUI.BindLink -> Icons.Filled.Link
                ActionUI.Copy -> Icons.Filled.CopyAll
            }
        ) {
            viewModel.handleActionClick(action)
        }
    }

    @Composable
    private fun ActionButton(
        modifier: Modifier = Modifier,
        imageVector: ImageVector,
        onClick: () -> Unit
    ) {
        Box(
            modifier = modifier
                .clickable { onClick() }
                .border(width = 1.dp, color = Color.LightGray)
                .padding(5.dp)) {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
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

    companion object {
        private val floatingToolbarHeight = 50.dp
    }

}
