package com.rnett.spellbook.components

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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

@Composable
fun <T> rememberDragSetState(): DragSetState<T> {
    val scope = rememberCoroutineScope()
    return remember { DragSetState<T>(scope) }
}

interface DragContainerState<T> {
    interface ContainerKey {
        fun remove()
    }

    class ContainerKeyImpl(val container: DragContainerState<*>) : ContainerKey {
        override fun remove() {
            container.removeContainer(this)
        }
    }

    fun removeContainer(key: ContainerKey)
    fun registerContainer(handle: DragHandler<T>): ContainerKey

    data class DragHandler<T>(
        val bounds: Rect,
        val accepts: State<(T) -> Boolean>,
        val onEnter: (T) -> Unit,
        val onLeave: (T) -> Unit,
        val onIn: (T, Offset) -> Unit,
        val onDrop: (T) -> Boolean
    )

    operator fun plus(other: DragContainerState<T>): CompositeDragContainerState<T> = compositeContainerOf(this, other)
}

fun <T> compositeContainerOf(vararg containers: DragContainerState<T>) =
    CompositeDragContainerState(containers.flatMap {
        if (it is CompositeDragContainerState)
            it.containers
        else
            listOf(it)
    })

class CompositeDragContainerState<T>(internal val containers: List<DragContainerState<T>>) : DragContainerState<T> {
    private val handlers = WeakHashMap<DragContainerState.ContainerKey, List<DragContainerState.ContainerKey>>()
    override fun removeContainer(key: DragContainerState.ContainerKey) {
        handlers[key]?.let { it.forEach { it.remove() } }

    }

    override fun registerContainer(handle: DragContainerState.DragHandler<T>): DragContainerState.ContainerKey {
        val key = DragContainerState.ContainerKeyImpl(this)
        handlers[key] = containers.map { it.registerContainer(handle) }
        return key
    }
}

interface DragItemState<T> {
    val enabled: Boolean
    fun endDrag(): Boolean
    fun startDrag(coords: Offset, item: T)
    fun cancelDrag()
    fun updateDrag(change: Offset)
}

class DragSetState<T>(val scope: CoroutineScope) : DragContainerState<T>, DragItemState<T> {
    override var enabled by mutableStateOf(true)

    var item: T? by mutableStateOf(null)
        private set
    var windowPosition by mutableStateOf<Offset?>(null)
        private set

    val isDragging by derivedStateOf { windowPosition != null }

    override fun startDrag(coords: Offset, item: T) {
        this.item = item
        windowPosition = coords
    }

    /**
     * @return true if the drag ended in an accepting container
     */
    @OptIn(InternalComposeApi::class)
    override fun endDrag(): Boolean {
        if (!isDragging) return false
        val handler = containers.values
            .firstOrNull { it.bounds.contains(windowPosition!!) && it.accepts.value(item!!) }
        val result = handler?.onDrop?.invoke(item!!)
        cancelDrag()

        return result == true
    }

    override fun cancelDrag() {
        if (!isDragging) return
        containers.values.forEach {
            val inBounds = it.bounds.contains(windowPosition!!)
            val accepts by lazy { it.accepts.value(item!!) }

            if (inBounds && accepts)
                it.onLeave(item!!)
        }
        item = null
        windowPosition = null
    }

    override fun updateDrag(change: Offset) {
        if (!isDragging) return
        val old = windowPosition!!
        val new = old + change
        windowPosition = new

        containers.values.forEach {
            val inNew = it.bounds.contains(new)
            val inOld = it.bounds.contains(old)
            val accepts by lazy { it.accepts.value(item!!) }

            if (inNew && !inOld && accepts)
                it.onEnter(item!!)

            if (!inNew && inOld) {
                it.onLeave(item!!)
            }

            if (inNew && accepts)
                it.onIn(item!!, new - it.bounds.topLeft)
        }
    }

    private val containers = WeakHashMap<DragContainerState.ContainerKey, DragContainerState.DragHandler<T>>()

    override fun registerContainer(handle: DragContainerState.DragHandler<T>): DragContainerState.ContainerKey {
        val key = DragContainerState.ContainerKeyImpl(this)
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

    override fun removeContainer(key: DragContainerState.ContainerKey) {
        containers.remove(key)
    }
}


@Composable
inline fun SideEffectHandler(buffer: Int): (() -> Unit) -> Unit {

    val eventFlow = remember { MutableSharedFlow<() -> Unit>(extraBufferCapacity = buffer) }
    val event by eventFlow.collectAsState(null)

    val scope = rememberCoroutineScope { Dispatchers.Default }

    event?.let {
        SideEffect {
            it()
        }
    }

    return {
        scope.launch { eventFlow.emit(it) }
    }
}

fun <T> Modifier.draggableItem(
    set: DragItemState<T>,
    item: T,
    onDragStart: () -> Unit = {},
    onDragCancel: () -> Unit = {},
    onDrugOut: () -> Unit = {}
) = composed {

    var coords by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val mod = onGloballyPositioned {
        coords = it
    }

    val dragStart = rememberUpdatedState(onDragStart)
    val dragCancel = rememberUpdatedState(onDragCancel)
    val drugOut = rememberUpdatedState(onDrugOut)

    val eventHandler = SideEffectHandler(2)

    if (coords != null && set.enabled) {
        mod.pointerInput(coords, set, item) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    set.startDrag(coords!!.localToWindow(it), item)
                    eventHandler { dragStart.value() }
                },
                onDragEnd = {
                    if (set.endDrag())
                        eventHandler { drugOut.value() }
                    else
                        eventHandler { dragCancel.value() }
                },
                onDragCancel = {
                    set.cancelDrag()
                    eventHandler { dragCancel.value() }
                },
                onDrag = { _, delta ->
                    set.updateDrag(delta)
                }
            )
        }
    } else mod
}

/**
 * @param onDrop returns whether to call onDrugOut on the item (calls onDragCancel otherwise)
 */
fun <T> Modifier.draggableContainer(
    set: DragContainerState<T>,
    onEnter: (T) -> Unit = {},
    onLeave: (T) -> Unit = {},
    onIn: (T, Offset) -> Unit = { _, _ -> },
    accepts: (T) -> Boolean = { true },
    onDrop: (T) -> Boolean
) = composed {

    val onEnter = rememberUpdatedState(onEnter)
    val onLeave = rememberUpdatedState(onLeave)
    val onIn = rememberUpdatedState(onIn)
    val accepts = rememberUpdatedState(accepts)
    val onDrop = rememberUpdatedState(onDrop)

    var bounds by remember { mutableStateOf<Rect>(Rect.Zero) }
    val mod = onGloballyPositioned {
        bounds = it.boundsInWindow()
    }

    val handler = DragContainerState.DragHandler(
        bounds,
        accepts,
        { onEnter.value(it) },
        { onLeave.value(it) },
        { it, offset -> onIn.value(it, offset) },
        { onDrop.value(it) }
    )

    DisposableEffect(bounds, set) {
        val key = set.registerContainer(handler)

        onDispose {
            key.remove()
        }
    }

    mod
}