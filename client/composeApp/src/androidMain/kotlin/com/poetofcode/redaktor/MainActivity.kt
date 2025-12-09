package com.poetofcode.redaktor

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStarted
import data.repository.RepositoryFactoryImpl
import data.service.NetworkingFactory
import data.service.NetworkingFactoryImpl
import data.utils.ProfileStorageImpl
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.App
import presentation.base.Config
import presentation.base.ViewModelStore
import presentation.factories.viewModelFactories
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.SetBackHandlerEffect
import presentation.navigation.SharedMemory
import specific.AndroidContentProvider


class MainActivity : ComponentActivity() {
    // val repositoryFactory = MockRepositoryFactory()
    val profileStorage = ProfileStorageImpl(
        AndroidContentProvider(
            fileName = "sessioncache.json",
            context = this,
        )
    )

    val networkingFactory: NetworkingFactory = NetworkingFactoryImpl(
        profileStorage,
        Config.DeviceTypes.ANDROID,
    )

    val repositoryFactory = RepositoryFactoryImpl(
        api = networkingFactory.createApi(),
        profileStorage = profileStorage
    )

    private var backHandleCallback: (() -> Boolean)? = null

    val vmStoreImpl = ViewModelStore(
        coroutineScope = lifecycleScope,
        vmFactories = viewModelFactories(
            repositoryFactory = repositoryFactory
        )
    )

    private var firebasePushToken: String? = null

    private val profileRepository by lazy {
        repositoryFactory.createProfileRepository()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Делает активити защищённым от скриншотов, отображения контента в миниатюре и т.д.
        // window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        retrieveFirebasePushToken { token ->
            val msg = "FCM Token: $token"
            Log.d("mylog", msg)
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

            firebasePushToken = token
            saveTokenOnServer()
        }

        setContent {
            App(
                Config(
                    deviceType = Config.DeviceTypes.ANDROID,
                    viewModelStore = vmStoreImpl,
                    repositoryFactory = repositoryFactory,
                )
            )
        }

        lifecycleScope.launch {
            SharedMemory.effectFlow
                .onEach { effect ->
                    if (effect is SetBackHandlerEffect) {
                        backHandleCallback = effect.cb
                    }
                }.launchIn(this)
        }

        listenToSharedEvents()
    }

    private fun listenToSharedEvents() {
        lifecycleScope.launch {
            withStarted {}

            SharedMemory.eventFlow.collect { event ->
                when (event) {
                    is OnReceivedTokenSharedEvent -> {
                        saveTokenOnServer()
                    }
                }
            }
        }
    }

    private fun saveTokenOnServer() {
        lifecycleScope.launch {
            try {
                profileRepository.saveFirebasePushToken(firebasePushToken ?: return@launch)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    override fun onBackPressed() {
        backHandleCallback?.let { cb ->
            if (cb()) {
                return
            } else {
                return@let
            }
        }
        super.onBackPressed()
    }

}

@Preview
@Composable
fun AppAndroidPreview() {
//
//    val vmStoreImpl = ViewModelStore(
//        coroutineScope = rememberCoroutineScope(),
//        vmFactories = viewModelFactories
//    )
//    App(Config(viewModelStore = vmStoreImpl))
}