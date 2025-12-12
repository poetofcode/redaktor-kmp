package presentation.factories

import data.repository.JokeRepository
import data.repository.ProfileRepository
import data.repository.RepositoryFactory
import data.repository.UseCaseFactory
import domain.usecase.EditorUseCase
import presentation.base.ViewModelFactory
import presentation.screens.authScreen.AuthViewModel
import presentation.screens.catalog.CatalogViewModel
import presentation.screens.homeTabScreen.HomeTabViewModel
import presentation.screens.notificationsScreen.NotificationsViewModel
import presentation.screens.profileScreen.ProfileViewModel
import presentation.screens.profileTabScreen.ProfileTabViewModel
import presentation.screens.regScreen.RegViewModel


class HomeTabViewModelFactory : ViewModelFactory<HomeTabViewModel> {
    override fun createViewModel(): HomeTabViewModel {
        return HomeTabViewModel()
    }

    override val vmTypeName: String
        get() = HomeTabViewModel::class.java.typeName

}

class CatalogViewModelFactory(val editorUseCase: EditorUseCase) : ViewModelFactory<CatalogViewModel> {
    override fun createViewModel(): CatalogViewModel {
        return CatalogViewModel(editorUseCase)
    }

    override val vmTypeName: String
        get() = CatalogViewModel::class.java.typeName

}

/*
class StartViewModelFactory(
    val jokeRepository: JokeRepository,
    val profileRepository: ProfileRepository,
) : ViewModelFactory<StartViewModel> {
    override fun createViewModel(): StartViewModel {
        return StartViewModel(
            jokeRepository = jokeRepository,
            profileRepository = profileRepository,
        )
    }

    override val vmTypeName: String
        get() = StartViewModel::class.java.typeName

}
*/

class ProfileTabViewModelFactory
    : ViewModelFactory<ProfileTabViewModel> {
    override fun createViewModel(): ProfileTabViewModel {
        return ProfileTabViewModel()
    }

    override val vmTypeName: String
        get() = ProfileTabViewModel::class.java.typeName

}

class ProfileViewModelFactory(val profileRepository: ProfileRepository) :
    ViewModelFactory<ProfileViewModel> {
    override fun createViewModel(): ProfileViewModel {
        return ProfileViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = ProfileViewModel::class.java.typeName

}

class AuthViewModelFactory(private val profileRepository: ProfileRepository) :
    ViewModelFactory<AuthViewModel> {
    override fun createViewModel(): AuthViewModel {
        return AuthViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = AuthViewModel::class.java.typeName

}

class RegViewModelFactory(private val profileRepository: ProfileRepository) :
    ViewModelFactory<RegViewModel> {
    override fun createViewModel(): RegViewModel {
        return RegViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = RegViewModel::class.java.typeName

}

class NotificationsViewModelFactory(private val profileRepository: ProfileRepository) :
    ViewModelFactory<NotificationsViewModel> {
    override fun createViewModel(): NotificationsViewModel {
        return NotificationsViewModel(profileRepository)
    }

    override val vmTypeName: String
        get() = NotificationsViewModel::class.java.typeName

}


fun viewModelFactories(
    repositoryFactory: RepositoryFactory,
    useCaseFactory: UseCaseFactory,
): List<ViewModelFactory<*>> {
    val profileRepository = repositoryFactory.createProfileRepository()
    return listOf<ViewModelFactory<*>>(
        HomeTabViewModelFactory(),
      /*
StartViewModelFactory(
            repositoryFactory.createJokeRepository(),
            profileRepository
        ),
*/
        ProfileTabViewModelFactory(),
        ProfileViewModelFactory(profileRepository),
        AuthViewModelFactory(profileRepository),
        RegViewModelFactory(profileRepository),
        NotificationsViewModelFactory(profileRepository),
        CatalogViewModelFactory(useCaseFactory.createEditorUseCase())
    )
}