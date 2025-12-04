package com.rexosphere.leoconnect.presentation.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rexosphere.leoconnect.presentation.icons.ChatBubbleOvalLeftEllipsis

object MessagesTabNavigator : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = "Messages"
            val icon = rememberVectorPainter(ChatBubbleOvalLeftEllipsis)

            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(MessagesScreen())
    }
}
