package presentation.screens.authScreen

import androidx.compose.ui.text.input.TextFieldValue
import data.repository.ProfileRepository
import domain.model.Profile
import kotlinx.coroutines.launch
import presentation.base.BaseViewModel
import presentation.base.postEffect
import presentation.base.postSharedEvent
import presentation.base.postSideEffect
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource
import presentation.model.shared.OnReceivedTokenSharedEvent
import presentation.navigation.NavigateBackEffect
import presentation.navigation.ShowSnackErrorEffect


class AuthViewModel(
    private val profileRepository: ProfileRepository,
) : BaseViewModel<AuthViewModel.State>() {

    init {
        fetchProfile()
    }

    data class State(
        val profile: Profile? = null,
        val email: TextFieldValue = TextFieldValue(),
        val password: TextFieldValue = TextFieldValue(),
        val readyState: Resource<Unit> = IdleResource,
    )

    fun onBackClick() = viewModelScope.launch {
        postEffect(NavigateBackEffect())
    }

    fun fetchProfile() {
        val profile = profileRepository.fetchProfileLocal()
        profile?.let {
            reduce { copy(
                profile = it
            ) }
        }
    }

    override fun onInitState(): State = State()

    fun onEmailChanged(value: TextFieldValue) {
        reduce { copy(email = value) }
    }

    fun onPasswordChanged(value: TextFieldValue) {
        reduce { copy(password = value) }
    }

    fun onSubmitClick() {
        viewModelScope.launch {
            try {
                reduce { copy(readyState = LoadingResource) }
                val profile = profileRepository.createSession(
                    email = state.value.email.text,
                    password = state.value.password.text,
                )
                reduce { copy(readyState = CompleteResource(Unit)) }
                postSharedEvent(OnReceivedTokenSharedEvent(profile.token, profile.email))
                onBackClick()
            } catch (e: Throwable) {
                reduce { copy(readyState = ExceptionResource(e)) }
                e.printStackTrace()
            }
        }
    }

    fun onSubmitError(exception: Throwable) {
        postSideEffect(ShowSnackErrorEffect(exception.message ?: "Unknown error"))
    }

}

