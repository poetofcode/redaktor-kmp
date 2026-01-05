package presentation.base

import data.repository.RepositoryFactory
import data.utils.PersistentStorage

data class Config(

    val deviceType: DeviceTypes,

    val viewModelStore: ViewModelStore,

    val repositoryFactory: RepositoryFactory,

    val storage: PersistentStorage,

) {

    enum class DeviceTypes(
        val isMobile: Boolean,
        val title: String
    ) {
        ANDROID(true, "android"),
        DESKTOP(false, "desktop")
    }

}
