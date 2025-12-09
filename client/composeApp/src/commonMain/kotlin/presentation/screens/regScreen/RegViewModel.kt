package presentation.screens.regScreen

import androidx.compose.ui.text.input.TextFieldValue
import data.repository.ProfileRepository
import data.utils.isValidEmail
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


class RegViewModel(
    private val profileRepository: ProfileRepository,
) : BaseViewModel<RegViewModel.State>() {

    init {
        fetchProfile()
    }

    data class State(
        val profile: Profile? = null,
        val email: TextFieldValue = TextFieldValue(),
        val password: TextFieldValue = TextFieldValue(),
        val passwordRepeat: TextFieldValue = TextFieldValue(),
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
        if (!isFieldsValid()) {
            return
        }

        viewModelScope.launch {
            try {
                reduce { copy(readyState = LoadingResource) }
                val profile = profileRepository.createAccount(
                    email = state.value.email.text,
                    password = state.value.password.text,
                )
                reduce { copy(readyState = CompleteResource(Unit)) }
            } catch (e: Throwable) {
                reduce { copy(readyState = ExceptionResource(e)) }
                e.printStackTrace()
            }
        }
    }

    private fun isFieldsValid() : Boolean {
        with(state.value) {
            if (email.text.isBlank() || password.text.isBlank() || passwordRepeat.text.isBlank()) {
                postSideEffect(ShowSnackErrorEffect(text = "Заполните все поля"))
                return false
            }
            if (!email.text.isValidEmail()) {
                postSideEffect(ShowSnackErrorEffect("Некорректный email"))
                return false
            }
            if (state.value.password != state.value.passwordRepeat) {
                postSideEffect(ShowSnackErrorEffect("Пароли не совпадают"))
                return false
            }
        }
        return true
    }

    fun onSubmitError(exception: Throwable) {
        postSideEffect(ShowSnackErrorEffect(exception.message ?: "Unknown error"))
    }

    fun onPasswordConfirmChanged(it: TextFieldValue) {
        reduce { copy(passwordRepeat = it) }
    }

}
