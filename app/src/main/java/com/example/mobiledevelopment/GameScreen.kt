package com.example.mobiledevelopment
import androidx.compose.foundation.layout.*
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.mobiledevelopment.authors.AuthorsScreen
import com.example.mobiledevelopment.model.GameSettings
import com.example.mobiledevelopment.registration.RegistrationConfig
import com.example.mobiledevelopment.registration.RegistrationScreen
import com.example.mobiledevelopment.rules.RulesScreen
import com.example.mobiledevelopment.settings.GameSettingsScreen
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
private enum class GameTab(val titleRes: Int) {
    PLAYER(R.string.tab_player),
    RULES(R.string.tab_rules),
    AUTHORS(R.string.tab_authors),
    SETTINGS(R.string.tab_settings)
}
@Composable
fun GameScreen() {
    var selectedTab by rememberSaveable {
        mutableStateOf(GameTab.PLAYER)
    }
    var settings by rememberSaveable {
        mutableStateOf(GameSettings())
    }
    val stateHolder = rememberSaveableStateHolder()
    val config = remember { RegistrationConfig() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        PrimaryTabRow(selectedTabIndex = selectedTab.ordinal) {
            GameTab.entries.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
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
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            stateHolder.SaveableStateProvider(selectedTab.name) {
                when (selectedTab) {
                    GameTab.PLAYER -> RegistrationScreen(config)
                    GameTab.RULES -> RulesScreen()
                    GameTab.AUTHORS -> AuthorsScreen()
                    GameTab.SETTINGS -> GameSettingsScreen(
                        settings = settings,
                        onSettingsChange = { settings = it }
                    )
                }
            }
        }
    }
}