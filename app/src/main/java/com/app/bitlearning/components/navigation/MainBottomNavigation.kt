/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.components.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Camera
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class BottomNavItem(
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
)

val bottomNavItems = mutableListOf(
    BottomNavItem(
        route = "cameras",
        selectedIcon = Icons.Filled.Camera,
        unselectedIcon = Icons.Outlined.Camera,
        label = "Cameras",
    ),
    BottomNavItem(
        route = "favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        label = "Favorites",
    ),
    BottomNavItem(
        route = "my_profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        label = "Profile",
    ),
    BottomNavItem(
        route = "settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        label = "Settings",
    ),
)

val bottomNavItemsWithFAB = mutableListOf(
    BottomNavItem(
        route = "cameras",
        selectedIcon = Icons.Filled.Camera,
        unselectedIcon = Icons.Outlined.Camera,
        label = "Cameras",
    ),
    BottomNavItem(
        route = "favorites",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder,
        label = "Favorites",
    ),
    BottomNavItem(
        route = "my_profile",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        label = "Profile",
    ),
)

@Composable
fun MainBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    items: List<BottomNavItem> = bottomNavItems,
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        tonalElevation = 8.dp,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = {
                    Text(text = item.label)
                },
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                ),
            )
        }
    }
}

@Composable
fun MainFABBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    items: List<BottomNavItem>,
) {
    val itemCount = items.size
    val middleIndex = if (itemCount % 2 != 0) itemCount / 2 else -1

    Box {
        // Bottom Navigation Bar
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            tonalElevation = 8.dp,
        ) {
            items.forEachIndexed { index, item ->
                val selected = currentRoute == item.route
                val isMiddle = index == middleIndex

                if (isMiddle) {
                    // Empty placeholder so we can draw big icon on top
                    NavigationBarItem(
                        icon = {},
                        label = { Text(text = item.label) },
                        selected = selected,
                        onClick = { /* no-op here */ },
                    )
                } else {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label,
                            )
                        },
                        label = { Text(text = item.label) },
                        selected = selected,
                        onClick = {
                            if (currentRoute != item.route) {
                                onNavigate(item.route)
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ),
                    )
                }
            }
        }

        // Floating Center Icon
        if (middleIndex != -1) {
            val middleItem = items[middleIndex]
            FloatingActionButton(
                onClick = { onNavigate(middleItem.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-28).dp), // pushes button up above nav bar
            ) {
                Icon(
                    imageVector = middleItem.selectedIcon,
                    contentDescription = middleItem.label,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainBottomNavigationPreview() {
    MaterialTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(50.dp),
        ) {
            MainBottomNavigation(
                currentRoute = "cameras",
                onNavigate = {},
            )

            MainFABBottomNavigation(
                currentRoute = "cameras",
                onNavigate = {},
                items = bottomNavItemsWithFAB,
            )
        }
    }
}
