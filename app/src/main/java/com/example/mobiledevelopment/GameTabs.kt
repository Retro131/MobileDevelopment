package com.example.mobiledevelopment

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class GameTab(val titleRes: Int) {
    PLAYER(R.string.tab_player),
    RULES(R.string.tab_rules),
    AUTHORS(R.string.tab_authors),
    SETTINGS(R.string.tab_settings)
}

@Composable
fun GameTabs(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    PrimaryTabRow(selectedTabIndex = selectedIndex) {
        GameTab.entries.forEachIndexed { index, tab ->
            Tab(
                selected = selectedIndex == index,
                onClick = { onTabSelected(index) },
                modifier = Modifier.height(48.dp)
            ) {
                if (tab == GameTab.SETTINGS) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(tab.titleRes),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(tab.titleRes),
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}