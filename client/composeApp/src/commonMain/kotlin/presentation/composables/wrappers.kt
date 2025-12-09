package presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import presentation.model.CompleteResource
import presentation.model.ExceptionResource
import presentation.model.IdleResource
import presentation.model.LoadingResource
import presentation.model.Resource
import presentation.navigation.BaseScreen
import presentation.navigation.ShowSnackErrorEffect
import presentation.navigation.postSideEffect

@Composable
fun <T> BaseScreen<*>.WrapPostResource(
    modifier: Modifier = Modifier,
    resource: Resource<T>,
    onReload: () -> Unit,
    content: @Composable (result: CompleteResource<T>) -> Unit,
) {
    Box(modifier) {
        when (resource) {
            is CompleteResource -> content(resource)
            is ExceptionResource -> {
                Button(
                    modifier = Modifier.align(Alignment.Center),
                    onClick = { onReload() },
                ) {
                    Text(text = "Ещё раз")
                }
                LaunchedEffect(Unit) {
                    postSideEffect(
                        ShowSnackErrorEffect(
                            resource.exception.message ?: "Unknown error"
                        )
                    )
                }
            }

            IdleResource -> Unit
            LoadingResource -> {
                CircularProgressIndicator()
            }
        }
    }
}
