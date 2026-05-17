package com.klmpk5.daycare_app.ui.dashboard

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_app.ui.theme.DaycarePrimary
import com.klmpk5.daycare_app.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_app.ui.theme.DaycareTextMuted

enum class DashboardTab(
    val icon: String,
    val label: String
) {
    Home("H", "Home"),
    Raport("R", "Raport"),
    Chat("C", "Chat"),
    Class("CL", "Class"),
    Profile("P", "Profile")
}

@Composable
fun DashboardBottomNavigation(
    selectedTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
    ) {
        DashboardTab.values().forEach { item ->
            DashboardNavItem(
                icon = item.icon,
                label = item.label,
                selected = selectedTab == item,
                onClick = { onTabSelected(item) }
            )
        }
    }
}

@Composable
private fun RowScope.DashboardNavItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            Text(
                text = icon,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        },
        label = {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1
            )
        },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = DaycarePrimary,
            selectedTextColor = DaycarePrimary,
            indicatorColor = DaycarePrimaryLight,
            unselectedIconColor = DaycareTextMuted,
            unselectedTextColor = DaycareTextMuted
        )
    )
}
