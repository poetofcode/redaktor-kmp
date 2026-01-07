import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import data.repository.RepositoryFactoryImpl
import data.repository.UseCaseFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import data.utils.FileContentProvider
import data.utils.PersistentStorage
import data.utils.ProfileStorageImpl
import data.utils.getValue
import data.utils.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.App
import presentation.LocalMainAppState
import presentation.MainAppState
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.viewModelFactories
import presentation.model.shared.ShowDesktopNotificationSharedEvent
import presentation.navigation.SharedMemory


const val DEFAULT_WINDOW_WIDTH = 600
const val DEFAULT_WINDOW_HEIGHT = 400
const val DEFAULT_POSITION_X = 300
const val DEFAULT_POSITION_Y = 300

fun main() = application {
    // val repositoryFactory = MockRepositoryFactory()
    val profileStorage = ProfileStorageImpl(
        FileContentProvider(
            fileName = "sessioncache.json",
            relativePath = "appcache",
        )
    )
    val networkingFactory: NetworkingFactory = NetworkingFactoryImpl(
        profileStorage,
        Config.DeviceTypes.DESKTOP,
    )

    val editorContentProvider = FileContentProvider(
        fileName = "editor_db.json",
        relativePath = "appcache",
    )

    val repositoryFactory = RepositoryFactoryImpl(
        api = networkingFactory.createApi(),
        profileStorage = profileStorage,
        editorContentProvider = editorContentProvider,
    )

    val useCaseFactory = UseCaseFactoryImpl(repositoryFactory)

    val vmStoreImpl = ViewModelStore(
        coroutineScope = rememberCoroutineScope(),
        vmFactories = viewModelFactories(
            repositoryFactory = repositoryFactory,
            useCaseFactory = useCaseFactory,
        )
    )

    val storage = PersistentStorage(
        FileContentProvider(
            fileName = "config.json",
            relativePath = "appcache",
        )
    )

    var windowWidth: Int? by storage
    var windowHeight: Int? by storage
    var positionX: Int? by storage
    var positionY: Int? by storage
    var isMaximized: Boolean? by storage

    val windowState = rememberWindowState(
        size = DpSize(
            windowWidth?.dp ?: DEFAULT_WINDOW_WIDTH.dp,
            windowHeight?.dp ?: DEFAULT_WINDOW_HEIGHT.dp
        ),
        position = WindowPosition(
            positionX?.dp ?: DEFAULT_POSITION_X.dp,
            positionY?.dp ?: DEFAULT_POSITION_Y.dp
        ),
        placement = if (isMaximized == true) WindowPlacement.Maximized else WindowPlacement.Floating
    )

    LaunchedEffect(windowState) {
        snapshotFlow {
            windowState.size
        }.onEach {
            with(it) {
                if (windowState.placement == WindowPlacement.Floating) {
                    windowWidth = width.value.toInt()
                    windowHeight = height.value.toInt()
                }
                if (width.value < DEFAULT_WINDOW_WIDTH) {
                    windowState.size = DpSize(DEFAULT_WINDOW_WIDTH.dp, height)
                }
                if (height.value < DEFAULT_WINDOW_HEIGHT) {
                    windowState.size = DpSize(height, DEFAULT_WINDOW_HEIGHT.dp)
                }
            }
        }.launchIn(this)

        snapshotFlow {
            windowState.position
        }.onEach {
            with(it) {
                if (windowState.placement == WindowPlacement.Floating) {
                    positionX = x.value.toInt()
                    positionY = y.value.toInt()
                }
            }
        }.launchIn(this)

        snapshotFlow {
            windowState.placement
        }.onEach {
            isMaximized = it == WindowPlacement.Maximized
        }.launchIn(this)
    }

    val trayState = rememberTrayState()

    /*
    Tray(
        state = trayState,
        icon = TrayIcon,
        menu = {
            Item(
                "Exit",
                onClick = ::exitApplication
            )
        }
    )
     */

    Window(
        state = windowState,
        onCloseRequest = ::exitApplication,
        title = "redaktor",
        icon = painterResource("ic_logo.png")
    ) {
        CompositionLocalProvider(LocalMainAppState provides MainAppState()) {
            App(
                config = Config(
                    deviceType = Config.DeviceTypes.DESKTOP,
                    viewModelStore = vmStoreImpl,
                    repositoryFactory = repositoryFactory,
                    storage = storage,
                )
            )
        }
    }

    vmStoreImpl.coroutineScope.listenToSharedEvents(trayState)
}

fun CoroutineScope.listenToSharedEvents(trayState: TrayState) = launch {
    SharedMemory.eventFlow.collect { event ->
        when (event) {
            is ShowDesktopNotificationSharedEvent -> {
                trayState.sendNotification(
                    Notification(
                        title = event.title,
                        message = event.message,
                        type = Notification.Type.Info
                    )
                )
            }
        }
    }
}

