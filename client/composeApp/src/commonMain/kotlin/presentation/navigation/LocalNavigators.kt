package presentation.navigation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigators = staticCompositionLocalOf<List<NavigatorInfo>> { emptyList() }