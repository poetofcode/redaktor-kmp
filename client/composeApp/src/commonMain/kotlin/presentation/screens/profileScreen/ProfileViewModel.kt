package presentation.screens.profileScreen

import data.repository.ProfileRepository
import domain.model.Profile
import domain.usecase.EditorUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSharedEvent
import presentation.base.postSideEffect
import presentation.model.ActionUI
import presentation.model.shared.OnQuitProfileSharedEvent
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.model.shared.OnRefreshDBSharedEvent
import presentation.navigation.HideBottomSheetEffect
import presentation.navigation.NavigateEffect
import presentation.navigation.SharedEvent
import presentation.navigation.ShowSnackErrorEffect
import presentation.screens.authScreen.AuthScreen
import presentation.screens.regScreen.RegScreen

class ProfileViewModel(
    private val profileRepository: ProfileRepository,
    private val editorUseCase: EditorUseCase,
) : BaseViewModel<ProfileViewModel.State>() {

    data class State(
        val profile: Profile? = null,
        val lastDBContent: String = "",
        val modifiedDBContent: String = "",
    )

    init {
        fetchProfile()

        fetchNotifis()

        fetchDBContent()
    }

    private fun fetchDBContent() {
        editorUseCase.loadDBContent()
            .onEach {
                reduce {
                    copy(
                        lastDBContent = it,
                        modifiedDBContent = it,
                    )
                }
            }
            .catch {
                postSideEffect(ShowSnackErrorEffect(it.toString()))
            }
            .launchIn(viewModelScope)
    }

    fun fetchNotifis() {
        viewModelScope.launch {
            try {
                val notifis = profileRepository.fetchNotifications()
            } catch(t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    private fun fetchProfile() {
        val profile = profileRepository.fetchProfileLocal()
        reduce { copy(profile = profile) }
    }

    fun onSignInToAccountButtonClick() {
        postEffect(
            NavigateEffect(
                AuthScreen(),
            )
        )
    }

    override fun onInitState() : State = State()

    override fun obtainSharedEvent(event: SharedEvent) {
        when (event) {
            is OnReceivedTokenSharedEvent -> {
                fetchProfile()
            }

            OnQuitProfileSharedEvent -> {
                fetchProfile()
                postSideEffect(HideBottomSheetEffect)
            }

            OnRefreshDBSharedEvent -> {
                fetchDBContent()
            }
        }
    }

    fun onConfirmQuit() {
        viewModelScope.launch {
            try {
                profileRepository.deleteSession()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            joinAll()
            postSharedEvent(OnQuitProfileSharedEvent)
        }
    }

    fun onRegistrationButtonClick() {
        postEffect(
            NavigateEffect(
                RegScreen(),
            )
        )
    }

    fun onImportDbApply() {
        editorUseCase.saveDBContent(state.value.modifiedDBContent)
            .onEach { postSharedEvent(OnRefreshDBSharedEvent) }
            .catch { postSideEffect(ShowSnackErrorEffect(it.toString())) }
            .launchIn(viewModelScope)
    }

    fun handleActionClick(action: ActionUI) {
        when (action) {
            ActionUI.Copy -> {

            }

            else -> Unit
        }
    }

    fun onDBContentChanged(dbContentNew: String) {
        reduce { copy(
            modifiedDBContent = dbContentNew
        ) }
    }

}