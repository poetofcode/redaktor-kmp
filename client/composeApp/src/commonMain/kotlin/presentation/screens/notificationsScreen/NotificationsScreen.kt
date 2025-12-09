package presentation.screens.notificationsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.utils.DateFormatter
import domain.model.Notification
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.navigation.BaseScreen
import specific.BackHandler
import specific.ScrollBar
import specific.ScrollBarOrientation
import specific.ScrollableComponentState

@OptIn(ExperimentalMaterial3Api::class)
class NotificationsScreen : BaseScreen<NotificationsViewModel>() {

    override val viewModel: NotificationsViewModel
        get() = viewModelStore.getViewModel<NotificationsViewModel>(screenId)

    override val isMenuVisible: Boolean = true

    private val listState = LazyListState()

    val state get() = viewModel.state.value

    @Composable
    override fun Content() = with(viewModel.state.value) {
        BackHandler {
            viewModel.onBackClick()
            true
        }

        MaterialTheme {
            Column {
                TopAppBar(
                    title = { Text(text = "Уведомления") },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.onBackClick()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                    actions = {
                        if (readyState !is LoadingResource) {
                            IconButton(onClick = {
                                viewModel.onReload()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                )
                            }
                        }
                    }
                )

                Box(Modifier.fillMaxSize().weight(1f)) {
                    MainContent(Modifier)
                }
            }
        }
    }

    @Composable
    fun MainContent(modifier: Modifier) {
        Box(modifier = modifier.fillMaxWidth()) {
            when (val readyState = state.readyState) {
                is CompleteResource -> Notifications(state.notifications)

                is ExceptionResource -> {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "Ошибка загрузки", color = Color.Red)
                        Spacer(Modifier.size(10.dp))
                        Text(text = "${readyState.exception}")
                    }
                }

                is IdleResource -> {
                    Box(Modifier.fillMaxSize()) {
                        Text(
                            text = "Список пуст",
                            color = Color.Gray,
                            modifier = Modifier.align(
                                Center
                            )
                        )
                    }
                }

                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

    @Composable
    private fun Notifications(notifications: List<Notification>) {
        LaunchedEffect(notifications) {
            if (notifications.size > 0) {
                viewModel.onShowNotifications()
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxHeight().padding().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(notifications) { notification ->
                    Notification(notification = notification)
                }
            }

            ScrollBar(
                modifier = Modifier.width(20.dp).fillMaxHeight(),
                orientation = ScrollBarOrientation.VERTICAL,
                state = ScrollableComponentState.LazyListComponentState(listState)
            )
        }
    }

    @Composable
    private fun Notification(notification: Notification) {
        // val context = LocalContext.current
        Column(
            modifier = Modifier
                .clickable {
                    // Do nothing
                }
                .padding(vertical = 5.dp)
                .fillMaxWidth()
                .background(
                    color = Color.LightGray, // MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(8.dp)
        ) {
            Spacer(modifier = Modifier.size(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = notification.title.orEmpty(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.size(8.dp))
                if (!notification.seen) {
                    Text(
                        modifier = Modifier.background(Color.Blue, shape = RoundedCornerShape(10))
                            .padding(horizontal = 5.dp),
                        text = "НОВОЕ",
                        fontSize = 10.sp,
                        color = Color.White,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = DateFormatter.getHumanDate(notification.createdAt),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = notification.text.orEmpty(), fontSize = 16.sp)
        }
    }

}

