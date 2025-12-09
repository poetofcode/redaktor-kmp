package presentation.model.shared

import presentation.navigation.SharedEvent

data class ShowDesktopNotificationSharedEvent(
    val title: String,
    val message: String,
) : SharedEvent