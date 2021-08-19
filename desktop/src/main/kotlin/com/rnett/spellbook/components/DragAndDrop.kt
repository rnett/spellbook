package com.rnett.spellbook.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composer
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.util.WeakHashMap
import kotlin.random.Random

@Composable
fun <T> rememberDragSetState(): DragSetState<T> {
    val composer = currentComposer
    val scope = rememberCoroutineScope()
    return remember { DragSetState<T>(composer, scope) }
}

class DragSetState<T>(val composer: Composer, val scope: CoroutineScope) {
    var item: T? by mutableStateOf(null)
        private set
    var windowPosition by mutableStateOf<Offset?>(null)
        private set

    val isDragging by derivedStateOf { windowPosition != null }

    internal fun startDrag(coords: Offset, item: T) {
        this.item = item
        windowPosition = coords
    }

    @OptIn(InternalComposeApi::class)
    internal fun endDrag(): Boolean {
        if (!isDragging) return false
        val handler = containers.values.map { it.value }
            .firstOrNull { it.bounds.contains(windowPosition!!) && it.accepts(item!!) }
        handler?.onDrop?.invoke(item!!)
        cancelDrag()
        return true
    }

    internal fun cancelDrag() {
        item = null
        windowPosition = null
        //TODO onLeave events
    }

    internal fun updateDrag(change: Offset) {
        if (!isDragging) return
        val old = windowPosition!!
        val new = old + change
        windowPosition = new

        containers.values.map { it.value }.forEach {
            val inNew = it.bounds.contains(new)
            val inOld = it.bounds.contains(old)
            val accepts by lazy { it.accepts(item!!) }

            if (inNew && !inOld && accepts)
                it.onEnter(item!!)

            if (!inNew && inOld) {
                it.onLeave(item!!)
            }

            if (inNew && accepts)
                it.onIn(item!!, new - it.bounds.topLeft)
        }
    }

    internal inner class ContainerKey {
        fun remove() {
            containers.remove(this)
        }
    }

    data class DragHandler<T>(
        val bounds: Rect,
        val accepts: (T) -> Boolean,
        val onEnter: (T) -> Unit,
        val onLeave: (T) -> Unit,
        val onIn: (T, Offset) -> Unit,
        val onDrop: (T) -> Unit
    )

    private val containers = WeakHashMap<ContainerKey, State<DragHandler<T>>>()

    internal fun registerContainer(handle: State<DragHandler<T>>): ContainerKey {
        val key = ContainerKey()
        containers[key] = handle
        return key
    }

    @Composable
    fun display(content: @Composable (T) -> Unit) {
        windowPosition?.let { position ->
            Popup(AbsolutePopupPositionProvider(IntOffset(0, 0))) {
                Box(Modifier.absoluteOffset { IntOffset(position.x.toInt(), position.y.toInt()) }) {
                    item?.let {
                        content(it)
                    }
                }
            }
        }
    }
}

class Event<R>(val handler: State<() -> R>, val name: String = "ID ${Random.nextInt()}") {
    operator fun invoke() = handler.value()
    override fun toString(): String {
        return "Event($name)"
    }
}

@Composable
fun <R> rememberUpdatedEventHandler(handler: () -> R, name: String = remember { "ID ${Random.nextInt()}" }) = Event(
    rememberUpdatedState(handler), name
)

@Composable
fun EventHandler(buffer: Int): (Event<*>) -> Unit {

    val eventFlow = remember { MutableSharedFlow<Event<*>>(extraBufferCapacity = buffer) }
    val event by eventFlow.collectAsState(null)

    val scope = rememberCoroutineScope { Dispatchers.Default }

    event?.let {
        SideEffect {
            println(it)
            it()
        }
    }

    return {
        scope.launch { eventFlow.emit(it) }
    }
}

fun <T> Modifier.draggableItem(
    set: DragSetState<T>,
    item: T,
    onDragStart: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    onDrugOut: () -> Unit = {}
) = composed {

    var coords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val mod = onGloballyPositioned {
        coords = it
    }

    val dragStart = rememberUpdatedEventHandler(onDragStart, "Drag Start")
    val dragCancel = rememberUpdatedEventHandler(onDragCancel, "Drag Cancel")
    val drugOut = rememberUpdatedEventHandler(onDrugOut, "Drug Out")

    val eventHandler = EventHandler(10)

    if (coords != null) {
        mod.pointerInput(coords, set, item) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    set.startDrag(coords!!.localToWindow(it), item)
                    eventHandler(dragStart)
                },
                onDragEnd = {
                    if (set.endDrag())
                        eventHandler(drugOut)
                },
                onDragCancel = {
                    set.cancelDrag()
                    eventHandler(dragCancel)
                },
                onDrag = { _, delta ->
                    set.updateDrag(delta)
                }
            )
        }
    } else mod
}

fun <T> Modifier.draggableContainer(
    set: DragSetState<T>,
    onEnter: (T) -> Unit = {},
    onLeave: (T) -> Unit = {},
    onIn: (T, Offset) -> Unit = { _, _ -> },
    accepts: (T) -> Boolean = { true },
    onDrop: (T) -> Unit
) = composed {

    val eventHandler = EventHandler(10)

    var coords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val mod = onGloballyPositioned {
        coords = it
    }
    key(coords) {
        if (coords != null) {
            val handler = DragSetState.DragHandler(
                coords!!.boundsInWindow(),
                accepts,
                onEnter,
                onLeave,
                onIn,
                onDrop
            )

            val handlerState = rememberUpdatedState(handler)

            DisposableEffect(coords, set) {
                val key = set.registerContainer(handlerState)

                onDispose {
                    key.remove()
                }
            }
        }
    }

    mod
}