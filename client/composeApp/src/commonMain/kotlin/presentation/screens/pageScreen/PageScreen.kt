package presentation.screens.pageScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import presentation.composables.DragDropList
import presentation.model.ActionUI
import presentation.model.ElementUI
import presentation.model.PageMode
import presentation.navigation.BaseScreen
import presentation.screens.pageScreen.misc.ElementType
import specific.BackHandler

class PageScreen : BaseScreen<PageViewModel>() {

    override val viewModel: PageViewModel
        get() = viewModelStore.getViewModel<PageViewModel>()

    val state get() = viewModel.state.value

    @Composable
    override fun Content() {
        BackHandler {
            when (state.mode) {
                is PageMode.Edit -> offerIntent(PageIntent.OnDiscardChangesElementClick)
                PageMode.Select -> offerIntent(PageIntent.OnFinishEditModeClick)
                else -> Unit
            }
            state.mode != PageMode.View
        }

        val focusRequester = remember { FocusRequester() }

        // TODO Запилить что-то вроде CollectEffects и в нём обозревать эффект на старт редактирования
        //      текстового элемента: OnTextElementStartEditingEffect
        //      по нему делать: focusRequester.requestFocus()
        //      https://stackoverflow.com/questions/64181930/request-focus-on-textfield-in-jetpack-compose

        //      TODO АКТУАЛЬНО ЛИ ДАННОЕ TO_DO ???

        /*
        val coroutineScope = rememberCoroutineScope()
        val modalSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmStateChange = { it != ModalBottomSheetValue.HalfExpanded },
            skipHalfExpanded = true,
        )

        ModalBottomSheetLayout(
            sheetState = modalSheetState,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetContent = {
                ModalBottomSheetContent(coroutineScope, modalSheetState)
            }
        ) {
            PageContent(focusRequester, onOptionButtonClick = {
                coroutineScope.launch { modalSheetState.show() }
            })
        }

         */
    }

    /*
    @Composable
    private fun ModalBottomSheetContent(
        coroutineScope: CoroutineScope,
        modalSheetState: ModalBottomSheetState
    ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.size(20.dp))

            elementTypes.forEach {
                ElementTypeItem(titleFromType(elementType = it)) {
                    coroutineScope.launch { modalSheetState.hide() }
                    offerIntent(PageIntent.OnSelectElementType(elementType = it))
                }
            }

            Button(
                modifier = Modifier.padding(bottom = 20.dp),
                onClick = {
                    coroutineScope.launch { modalSheetState.hide() }
                }
            ) {
                Text(text = "Отмена")
            }
        }
    }
     */
    
    private fun titleFromType(elementType: ElementType): String {
        return when (elementType) {
            ElementType.TEXT -> "Текст"
            ElementType.LINK -> "Ссылка"
        }
    }

    @Composable
    private fun ElementTypeItem(title: String, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 8.dp, start = 16.dp, end = 16.dp)
                .background(Color.LightGray.copy(alpha = 0.5f), shape = RoundedCornerShape(5.dp))
                .clickable { onClick() },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            Text(
                text = title,
                modifier = Modifier.padding()
            )
        }
    }

    @Composable
    private fun PageContent(focusRequester: FocusRequester, onOptionButtonClick: () -> Unit) {
        Column {
            Toolbar()
            Box(Modifier.fillMaxSize()) {
                val floatingToolbarHeight = 50.dp
                ElementList(
                    contentPaddingBottom = floatingToolbarHeight,
                    focusRequester = focusRequester
                )
                FloatingToolbar(
                    Modifier
                        .height(floatingToolbarHeight)
                        .align(Alignment.BottomCenter)
                )
                AddElementButton(
                    modifier = Modifier
                        .height(floatingToolbarHeight)
                        .align(Alignment.BottomCenter),
                    onOptionButtonClick = onOptionButtonClick
                )
            }
        }
    }

    @Composable
    private fun AddElementButton(
        modifier: Modifier = Modifier,
        onOptionButtonClick: () -> Unit,
    ) {
        when (state.mode) {
            is PageMode.Select -> {
                Row(modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.Cyan)
                            .clickable { offerIntent(PageIntent.OnAddNewElementClick) }
                            .padding(5.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.CenterVertically),
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                        Text(
                            text = titleFromType(state.elementType),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                        .fillMaxHeight()
                        .width(50.dp)
                        .background(Color.Green)
                        .clickable {
                            onOptionButtonClick()
                        }
                        .padding(5.dp)
                    ) {
                        Icon(
                            modifier = Modifier.align(Alignment.Center),
                            imageVector = Icons.Filled.ArrowDropUp,
                            contentDescription = null
                        )
                    }
                }
            }

            else -> Unit
        }
    }

    @Composable
    private fun ElementList(contentPaddingBottom: Dp, focusRequester: FocusRequester) {
        DragDropList(
            items = state.elements,
            itemView = {
                ElementItem(it, focusRequester)
            },
            contentPadding = PaddingValues(bottom = contentPaddingBottom),
            onMove = { oldPos, newPos ->
                offerIntent(
                    PageIntent.OnReorderListElement(
                        oldPosition = oldPos,
                        newPosition = newPos
                    )
                )
            },
            onStartDragging = { itemIndex ->
                offerIntent(PageIntent.OnStartDragging(itemIndex ?: return@DragDropList))
            },
            onStopDragging = {
                offerIntent(PageIntent.OnFinishDragging)
            },
            isDraggable = state.mode is PageMode.Select
        )
    }

    @Composable
    private fun ElementItem(element: ElementUI, focusRequester: FocusRequester) {
        val paddingVert = 20.dp
        val paddHor = 16.dp
        val editableElement: ElementUI? = (state.mode as? PageMode.Edit)?.element
        Column(
            modifier = Modifier
                .then(
                    if (state.isDragging) {
                        Modifier
                            .border(
                                width = 1.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .then(
                                if (element.id == state.elements[state.draggableIndex!!].id) {
                                    Modifier.background(Color.Yellow)
                                } else Modifier
                            )
                    } else Modifier
                )
        ) {
            when (element) {
                is ElementUI.Text -> {
                    if (editableElement is ElementUI.Text && editableElement.id == element.id) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                                .focusRequester(focusRequester),
                            value = editableElement.text,
                            onValueChange = {
                                offerIntent(
                                    PageIntent.OnEditableElementChanged(editableElement.copy(text = it))
                                )
                            })
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        Text(
                            text = element.text,
                            modifier = Modifier.padding(
                                horizontal = paddHor,
                                vertical = paddingVert
                            )
                        )
                    }
                }

                is ElementUI.Link -> {
                    val linkTitle =
                        element.relatedPage?.title?.takeIf { it.isNotBlank() } ?: "без названия"
                    if (editableElement is ElementUI.Link && editableElement.id == element.id) {
                        Column(Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .focusRequester(focusRequester),
                                value = editableElement.text,
                                onValueChange = {
                                    offerIntent(
                                        PageIntent.OnEditableElementChanged(
                                            editableElement.copy(text = it)
                                        )
                                    )
                                })

                            Row(Modifier.padding(10.dp)) {
                                val color =
                                    if (element.isBound) Color(0, 200, 0, 255) else Color.Gray
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(
                                    modifier = Modifier.size(18.dp),
                                    imageVector = if (element.isBound) Icons.Filled.Link else Icons.Filled.LinkOff,
                                    contentDescription = null,
                                    tint = color,
                                )
                                Text(
                                    text = if (element.isBound) {
                                        "Ссылка установлена ($linkTitle)"
                                    } else {
                                        "Ссылка не задана"
                                    },
                                    modifier = Modifier.padding(start = 10.dp),
                                    fontStyle = FontStyle.Italic,
                                    color = color,
                                )
                            }
                        }
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        Row(
                            Modifier
                                .clickable { offerIntent(PageIntent.OnElementClick(element)) }
                                .padding(
                                    horizontal = paddHor,
                                    vertical = paddingVert
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = element.text,
                                )
                                CompositionLocalProvider(LocalContentColor provides Color.LightGray) {
                                    Text(
                                        text = linkTitle,
                                        fontStyle = FontStyle.Italic,
                                    )
                                }
                            }
                            Icon(
                                modifier = Modifier.padding(start = 10.dp),
                                imageVector = if (element.isBound) Icons.Filled.ArrowForward else Icons.Filled.LinkOff,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            // Actions with divider
            when (state.mode) {
                PageMode.Select -> {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                        modifier = Modifier.padding(horizontal = paddHor)
                    )

                    Row(
                        modifier = Modifier.padding(horizontal = paddHor),
                    ) {
                        Spacer(Modifier.weight(1f))
                        listOf(ActionUI.Edit, ActionUI.Delete).forEach {
                            ActionItem(action = it, element = element)
                        }
                    }
                }

                else -> Unit
            }
        }
    }

    @Composable
    private fun ActionItem(action: ActionUI, element: ElementUI) {
        ActionButton(
            imageVector = when (action) {
                ActionUI.Delete -> Icons.Filled.Delete
                ActionUI.Edit -> Icons.Filled.Edit
                ActionUI.BindLink -> Icons.Filled.Link
            }
        ) {
            offerIntent(PageIntent.OnActionClick(element, action))
        }
    }

    @Composable
    private fun ActionButton(
        modifier: Modifier = Modifier,
        imageVector: ImageVector,
        onClick: () -> Unit
    ) {
        Box(
            modifier = modifier
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
            val buttonParams: Pair<String, () -> Unit> = when (state.mode) {
                PageMode.View -> {
                    Pair("Ред.") { offerIntent(PageIntent.OnStartEditModeClick) }
                }

                PageMode.Select -> {
                    Pair("Просмотр") { offerIntent(PageIntent.OnFinishEditModeClick) }
                }

                is PageMode.Edit -> {
                    Pair("Сохранить") { offerIntent(PageIntent.OnApplyElementChangesClick) }
                }
            }

            Button(
                modifier = Modifier.padding(horizontal = 12.dp),
                onClick = buttonParams.second
            ) {
                Text(text = buttonParams.first)
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Redaktor",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 20.dp),
                color = Color.Black
            )
        }
    }

    @Composable
    private fun FloatingToolbar(modifier: Modifier) {
        when (state.mode) {
            is PageMode.Edit -> {
                val element = (state.mode as PageMode.Edit).element
                val actions = (state.mode as PageMode.Edit).element.actions
                Row(
                    modifier
                        .fillMaxWidth()
                        .background(Color.Cyan)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ActionButton(imageVector = Icons.Filled.Cancel) {
                        offerIntent(PageIntent.OnDiscardChangesElementClick)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    // Row of dynamic actions
                    actions.forEach {
                        ActionItem(action = it, element = element)
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    ActionButton(imageVector = Icons.Filled.Done) {
                        offerIntent(PageIntent.OnApplyElementChangesClick)
                    }
                }
            }

            else -> Unit
        }
    }

    private fun offerIntent(intent: PageIntent) {
        viewModel.handleIntent(intent)
    }

    companion object {
        val elementTypes = listOf(ElementType.TEXT, ElementType.LINK)
    }

}