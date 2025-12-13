package presentation.screens.catalogScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import presentation.model.PageUI
import presentation.navigation.BaseScreen
import specific.BackHandler

class CatalogScreen(
    val isPicker: Boolean = false,
) : BaseScreen<CatalogViewModel>() {

    override val viewModel: CatalogViewModel
        get() = viewModelStore.getViewModel<CatalogViewModel>()

    val state get() = viewModel.state.value

    @Composable
    override fun Content() {
        BackHandler {
            offerIntent(CatalogIntent.OnCancelEditClick)
            true
        }
        Column {
            Toolbar()
            PageList()
        }
        LaunchedEffect(Unit) {
            offerIntent(CatalogIntent.PassParameter(isPicker = isPicker))
        }
    }

    @Composable
    private fun PageList() {
        val focusRequester = remember { FocusRequester() }
        LazyColumn {
            items(state.pages) { page ->
                Column(Modifier.then(
                    if (!state.isEditing) {
                        Modifier.clickable {
                            offerIntent(CatalogIntent.OnPageClick(pageId = page.id))
                        }
                    } else Modifier
                )
                ) {
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        if (state.isEditing && state.editablePage!!.id == page.id) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Transparent)
                                    .focusRequester(focusRequester),
                                value = state.editablePage?.title.orEmpty(),
                                onValueChange = {
                                    offerIntent(
                                        CatalogIntent.OnEditablePageChanged(
                                            newPage = state.editablePage?.copy(title = it)!!
                                        )
                                    )
                                })
                            LaunchedEffect(Unit) {
                                focusRequester.requestFocus()
                            }
                        } else {
                            if (!page.isNew) {
                                Text(
                                    text = page.title,
                                    modifier = Modifier
                                        .padding(),
                                )
                            } else {
                                CompositionLocalProvider(LocalContentColor provides Color.LightGray) {
                                    Text(
                                        text = "Введите название",
                                        modifier = Modifier.padding(),
                                        fontStyle = FontStyle.Italic,
                                    )
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            ActionList(page = page)
                        }
                    }
                    HorizontalDivider(Modifier.fillMaxSize())
                }
            }
        }
    }

    @Composable
    fun ActionList(modifier: Modifier = Modifier, page: PageUI) {
        Row(modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (isPicker) {
                ActionButton(imageVector = Icons.Filled.Link) {
                    offerIntent(CatalogIntent.OnBindLink(page))
                }
            }
            ActionButton(imageVector = Icons.Filled.Edit) {
                offerIntent(CatalogIntent.OnEditClick(page.id))
            }
            ActionButton(imageVector = Icons.Filled.Delete) {
                offerIntent(CatalogIntent.OnDeleteClick(page.id))
            }
        }
    }

    @Composable
    private fun ActionButton(
        modifier: Modifier = Modifier,
        imageVector: ImageVector,
        onClick: () -> Unit
    ) {
        Box(modifier = modifier
            .clickable { onClick() }
            .border(width = 1.dp, color = Color.LightGray)
            .padding(5.dp)) {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
        }
    }

    @Composable
    private fun Toolbar() {
        Row(
            Modifier
                .background(color = Color.Green)
                .height(54.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = if (!state.isPicker) "Redaktor" else "Привязка ссылки",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 20.dp),
                color = Color.Black
            )

            Spacer(modifier = Modifier.weight(1f))

            if (!state.isEditing) {
                Button(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    onClick = { offerIntent(CatalogIntent.OnAddPageClick) }
                ) {
                    Icon(
                        modifier = Modifier.padding(2.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                }
            } else {
                Row {
                    Button(
                        modifier = Modifier.padding(vertical = 8.dp),
                        onClick = { offerIntent(CatalogIntent.OnApplyEditClick) }
                    ) {
                        Icon(
                            modifier = Modifier.padding(2.dp),
                            imageVector = Icons.Filled.Done,
                            contentDescription = null
                        )
                    }
                    Button(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .padding(start = 8.dp, end = 12.dp),
                        onClick = { offerIntent(CatalogIntent.OnCancelEditClick) }
                    ) {
                        Icon(
                            modifier = Modifier.padding(2.dp),
                            imageVector = Icons.Filled.Cancel,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }

    private fun offerIntent(intent: CatalogIntent) {
        viewModel.handleIntent(intent)
    }

}