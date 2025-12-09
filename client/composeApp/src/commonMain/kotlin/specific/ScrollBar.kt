package specific

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ScrollBar(
    modifier: Modifier,
    orientation: ScrollBarOrientation,
    state: ScrollableComponentState,
)

enum class ScrollBarOrientation {
    VERTICAL,
    HORIZONTAL
}

sealed interface ScrollableComponentState {
    data class LazyListComponentState(val state: LazyListState) : ScrollableComponentState

    data class ColumnComponentState(val state: ScrollState) : ScrollableComponentState
}