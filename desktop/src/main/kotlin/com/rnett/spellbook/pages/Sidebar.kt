package com.rnett.spellbook.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconToggleButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainState
import com.rnett.spellbook.ShoppingCart
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.InfoSidebarState
import com.rnett.spellbook.components.SidebarInfoDisplay
import com.rnett.spellbook.components.handPointer
import com.rnett.spellbook.components.sidebar.ShoppingCartDisplay
import com.rnett.spellbook.ifLet

enum class SidebarPage(val humanName: String) {
    Info("Info"), Cart("Card");
}

@Composable
fun MainState.SidebarToggle(
    page: SidebarPage,
    openedIcon: ImageVector,
    closedIcon: ImageVector,
    enabled: Boolean = true
) {
    IconToggleButton(
        sidebarPage == page,
        {
            if (sidebarPage == page)
                sidebarPage = null
            else
                sidebarPage = page
        },
        Modifier.padding(top = 3.dp).handPointer()
            .ifLet(sidebarPage == page) {
                it.background(
                    Color.White.copy(alpha = 0.3f),
                    RoundedCornerShape(40, 40, 0, 0)
                )
            },
        enabled = enabled
    ) {
        IconWithTooltip(
            if (sidebarPage == page) openedIcon else closedIcon,
            page.humanName
        )
    }
}

@Composable
fun MainState.CloseSidebarButton() {
    IconButtonHand(
        {
            sidebarPage = null
        },
        Modifier.padding(top = 3.dp).handPointer(),
        enabled = sidebarPage != null
    ) {
        IconWithTooltip(
            Icons.Default.Close,
            "Close Sidebar"
        )
    }
}

data class SidebarState(val info: InfoSidebarState, val cart: ShoppingCart) {
}

//TODO use.  going to want to make spell search in page form spellbooks
@Composable
fun Sidebar(state: SidebarState, page: SidebarPage) {
    when (page) {
        SidebarPage.Info -> {
            SidebarInfoDisplay(state.info)
        }
        SidebarPage.Cart -> {
            ShoppingCartDisplay(state.cart)
        }
    }
}