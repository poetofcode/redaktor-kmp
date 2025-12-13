package presentation.model.shared

import presentation.model.PageUI
import presentation.navigation.SharedEvent

data class OnPagePickedEvent(val page: PageUI) : SharedEvent