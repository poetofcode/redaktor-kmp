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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Pageview
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import presentation.composables.DragDropList
import presentation.model.ActionUI
import presentation.model.ElementUI
import presentation.model.PageMode
import presentation.navigation.BaseScreen
import presentation.navigation.HideBottomSheetEffect
import presentation.navigation.OnScrollToNewElementEffect
import presentation.navigation.ShowModalBottomSheetEffect
import presentation.navigation.SideEffect
import presentation.navigation.postSideEffect
import presentation.screens.pageScreen.misc.ElementType
import presentation.theme.AppColors
import presentation.theme.muted
import specific.BackHandler
import java.util.UUID


class PageScreen(
    val initialPageId: String
) : BaseScreen<PageViewModel>() {

    override val viewModel: PageViewModel
        get() = viewModelStore.getViewModel<PageViewModel>(screenId)

    val state get() = viewModel.state.value

    override val screenId by lazy {
        "PageScreen#${initialPageId}#${UUID.randomUUID()}"
    }

    private val lazyListState: LazyListState = LazyListState()
    private var isListNotDraggable: MutableState<Boolean> = mutableStateOf(false)
    private lateinit var scope: CoroutineScope

    override val isMenuVisible: Boolean = false
    val focusRequester = FocusRequester()

    @Composable
    override fun Content() {
        scope = rememberCoroutineScope()

        BackHandler {
            when (state.mode) {
                is PageMode.Edit -> offerIntent(PageIntent.OnDiscardChangesElementClick)
                PageMode.Select -> offerIntent(PageIntent.OnFinishEditModeClick)
                is PageMode.View -> viewModel.onBackPress()
            }
            true
        }

        LaunchedEffect(screenId) {
            viewModel.pageId = initialPageId
        }

        LaunchedEffect(state.mode) {
            val mode = state.mode
            reduceOverlayState {
                copy(
                    isVisible = when (mode) {
                        is PageMode.Edit -> true
                        else -> false
                    },
                    content = { EditElementOverlay() }
                )
            }
        }

        // TODO Запилить что-то вроде CollectEffects и в нём обозревать эффект на старт редактирования
        //      текстового элемента: OnTextElementStartEditingEffect
        //      по нему делать: focusRequester.requestFocus()
        //      https://stackoverflow.com/questions/64181930/request-focus-on-textfield-in-jetpack-compose

        //      TODO АКТУАЛЬНО ЛИ ДАННОЕ TO_DO ???

        PageContent(
            focusRequester,
            onOptionButtonClick = {
                postSideEffect(ShowModalBottomSheetEffect {
                    ModalBottomSheetContent()
                })
            }
        )

    }


    override fun handleSideEffect(sideEffect: SideEffect) {
        super.handleSideEffect(sideEffect)

        when (sideEffect) {
            is OnScrollToNewElementEffect -> {
                scope.launch {
                    if (sideEffect.elementPosition < 0) return@launch
                    lazyListState?.animateScrollToItem(sideEffect.elementPosition)
                }
            }
        }
    }

    @Composable
    private fun ModalBottomSheetContent() {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.size(20.dp))

            elementTypes.forEach {
                ElementTypeItem(titleFromType(elementType = it)) {
                    postSideEffect(HideBottomSheetEffect)
                    offerIntent(PageIntent.OnSelectElementType(elementType = it))
                }
            }

            Button(
                modifier = Modifier.padding(bottom = 20.dp),
                onClick = {
                    postSideEffect(HideBottomSheetEffect)
                }
            ) {
                Text(text = "Отмена")
            }
        }
    }

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
                .background(Color.LightGray.muted(), shape = RoundedCornerShape(5.dp))
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
                    focusRequester = focusRequester
                )

                /*
                FloatingToolbar(
                    Modifier
                        .height(floatingToolbarHeight)
                        .align(Alignment.BottomCenter)
                )
                */

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
                            .background(MaterialTheme.colorScheme.surfaceContainer)
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
                            .background(AppColors.sourceSolidColor)
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
    private fun ElementList(focusRequester: FocusRequester) {
        val bottomExtraSpace = 300.dp
        DragDropList(
            lazyListState = lazyListState,
            items = state.elements,
            itemView = {
                ElementItem(it, focusRequester)
            },
            contentPadding = PaddingValues(bottom = bottomExtraSpace),
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
            isDraggable = state.mode is PageMode.Select && !isListNotDraggable.value
        )
    }

    @Composable
    private fun ElementItem(element: ElementUI, focusRequester: FocusRequester) {
        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val coroutineScope = rememberCoroutineScope()
        val textScrollState = rememberScrollState()

        val paddingVert = 20.dp
        val paddHor = 16.dp
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
                                if (element.id == state.elements.getOrNull(state.draggableIndex!!)?.id) {
                                    Modifier.background(Color.Yellow)
                                } else Modifier
                            )
                    } else Modifier
                )
                .bringIntoViewRequester(bringIntoViewRequester)
        ) {
            when (element) {
                is ElementUI.Text -> {
                    val title = element.text.takeIf { it.isNotBlank() } ?: "Введите текст"
                    val color =
                        if (element.text.isNotBlank()) MaterialTheme.colorScheme.onSurface else Color.Gray
                    val fontStyle =
                        if (element.text.isNotBlank()) FontStyle.Normal else FontStyle.Italic
                    CompositionLocalProvider(LocalContentColor provides color) {
                        Markdown(
                            typography = markdownTypography(
                                text = MaterialTheme.typography.bodyLarge.copy(

                                )
                            ),
                            content = title,
                            modifier = Modifier.padding(
                                horizontal = paddHor,
                                vertical = paddingVert
                            )
                        )
                    }
                }

                is ElementUI.Link -> {
                    val linkTitle = element.relatedPage.run {
                        val relatedPage = this
                        if (relatedPage == null) {
                            "Ссылка не привязана"
                        } else if (relatedPage.title.isNotBlank()) {
                            relatedPage.title
                        } else "Страница без названия"
                    }
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
                            CompositionLocalProvider(LocalContentColor provides Color.LightGray) {
                                Text(
                                    text = linkTitle,
                                    fontStyle = FontStyle.Italic,
                                    color = AppColors.linkColor,
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
                ActionUI.Copy -> Icons.Filled.CopyAll
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
                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                .height(54.dp)
                .fillMaxWidth()
        ) {
            val buttonParams: Pair<ImageVector, () -> Unit> = when (state.mode) {
                PageMode.View -> {
                    Pair(Icons.Filled.Edit) {
                        offerIntent(PageIntent.OnStartEditModeClick)
                    }
                }

                PageMode.Select -> {
                    Pair(Icons.Filled.Visibility) { offerIntent(PageIntent.OnFinishEditModeClick) }
                }

                is PageMode.Edit -> {
                    Pair(Icons.Filled.Save) { offerIntent(PageIntent.OnApplyElementChangesClick) }
                }
            }

            IconButton(onClick = {
                when (state.mode) {
                    is PageMode.Edit -> offerIntent(PageIntent.OnDiscardChangesElementClick)
                    PageMode.Select -> offerIntent(PageIntent.OnFinishEditModeClick)
                    is PageMode.View -> viewModel.onBackPress()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }

            Text(
                text = state.pageTitle ?: "Redaktor",
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp),
                color = AppColors.categoryTextColor.muted(),
                overflow = TextOverflow.Ellipsis,
            )

            Box(
                modifier = Modifier
                    .clickable { buttonParams.second() }
                    .padding(horizontal = 12.dp)
                    .fillMaxHeight()
            ) {
                Icon(
                    modifier = Modifier.align(Alignment.Center),
                    imageVector = buttonParams.first,
                    contentDescription = "",
                )
            }
        }
    }

    @Composable
    private fun EditElementOverlay() {
        val bringIntoViewRequester = remember { BringIntoViewRequester() }
        val coroutineScope = rememberCoroutineScope()
        val textScrollState = rememberScrollState()

        val paddingVert = 20.dp
        val paddHor = 16.dp
        val editableElement: ElementUI = (state.mode as? PageMode.Edit)?.element ?: return
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(1f).bringIntoViewRequester(bringIntoViewRequester)
            ) {
                when (editableElement) {
                    is ElementUI.Text -> {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Transparent)
                                // .verticalScroll(textScrollState)
                                .focusRequester(focusRequester)
                                .onFocusEvent {
                                    if (it.isFocused) {
                                        coroutineScope.launch {
                                            delay(200)
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            value = editableElement.text,
                            maxLines = Int.MAX_VALUE,
                            onValueChange = {
                                offerIntent(
                                    PageIntent.OnEditableElementChanged(editableElement.copy(text = it))
                                )
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            })
                        Spacer(Modifier.size(floatingToolbarHeight))        // Extra space
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    }

                    is ElementUI.Link -> {
                        val linkTitle = editableElement.relatedPage.run {
                            val relatedPage = this
                            if (relatedPage == null) {
                                "Ссылка не привязана"
                            } else if (relatedPage.title.isNotBlank()) {
                                relatedPage.title
                            } else "Страница без названия"
                        }
                        Row(
                            Modifier
                                .clickable { offerIntent(PageIntent.OnElementClick(editableElement)) }
                                .padding(
                                    horizontal = paddHor,
                                    vertical = paddingVert
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                CompositionLocalProvider(LocalContentColor provides Color.LightGray) {
                                    Text(
                                        text = linkTitle,
                                        fontStyle = FontStyle.Italic,
                                    )
                                }
                            }
                            Icon(
                                modifier = Modifier.padding(start = 10.dp),
                                imageVector = if (editableElement.isBound) Icons.Filled.ArrowForward else Icons.Filled.LinkOff,
                                contentDescription = null
                            )
                        }
                    }
                }
            }

            FloatingToolbar(modifier = Modifier)
        }
    }

    @Composable
    fun FloatingToolbar(modifier: Modifier) {
        when (state.mode) {
            is PageMode.Edit -> {
                val element = (state.mode as PageMode.Edit).element
                val actions = element.actions
                Row(
                    modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer)
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
        private val elementTypes = listOf(ElementType.TEXT, ElementType.LINK)
        private val floatingToolbarHeight = 50.dp
    }

}