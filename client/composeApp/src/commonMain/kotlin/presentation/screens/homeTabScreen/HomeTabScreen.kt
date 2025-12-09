package presentation.screens.homeTabScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.m3.Markdown
import kotlinx.coroutines.flow.collectLatest
import presentation.Tabs
import presentation.navigation.*


class HomeTabScreen() : BaseScreen<HomeTabViewModel>() {

    override val screenId: String
        get() = Tabs.HOME.key

    override val viewModel: HomeTabViewModel
        get() = viewModelStore.getViewModel<HomeTabViewModel>()

    private val navState by lazy {
        NavStateImpl(viewModelStore).apply {
            // push(StartScreen())
        }
    }

    override val isMenuVisible: Boolean = true

    @Composable
    override fun Content() {
        LaunchedEffect(Unit) {
            SharedMemory.effectFlow.collectLatest { effect ->
                when (effect) {
                    is NavigateBackEffect -> {
                        navState.pop()
                    }

                    is NavigateEffect -> {
                        navState.push(effect.screen)
                    }

                    else -> Unit
                }
            }
        }

        // Navigator(modifier = Modifier.fillMaxSize(), state = navState)

        Markdown(
            """
            # Hello Markdown
            
            ## Заголовок 2
            
            > Там на неведомых дорожках следы неведомых зверей (с) Цитата
            
            This is a simple markdown example with:
        
            - Bullet points
            - **Bold text**
            - *Italic text*
        
            ```
            fun main() {
                // TODO create app
            }
            ```
        
            [Check out this link](https://github.com/mikepenz/multiplatform-markdown-renderer)
            """.trimIndent()
        )
    }

}
