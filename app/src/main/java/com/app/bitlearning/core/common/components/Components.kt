/**
 * Copyright (c) 2026 Bit Learning. All rights reserved.
 * This software is the confidential and proprietary information of hcmurs.
 * You shall not disclose such confidential information and shall use it only in
 * accordance with the terms of the license agreement you entered into with hcmurs.
 */
package com.app.bitlearning.core.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.bitlearning.core.common.theme.*

// ─────────────────────────────────────────────
//  Primary Button
// ─────────────────────────────────────────────
@Composable
fun BLPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = OnPrimary,
            disabledContainerColor = OnSurfaceMuted.copy(alpha = 0.3f),
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
        )
    }
}

// ─────────────────────────────────────────────
//  Outlined / Secondary Button
// ─────────────────────────────────────────────
@Composable
fun BLOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Divider),
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 16.sp),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

// ─────────────────────────────────────────────
//  Section Header (Title + "See All" link)
// ─────────────────────────────────────────────
@Composable
fun BLSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
        )
        if (actionLabel != null && onAction != null) {
            Text(
                text = actionLabel,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Primary,
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.clickable { onAction() },
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Avatar / Profile Picture
// ─────────────────────────────────────────────
@Composable
fun BLAvatar(
    imageUrl: String?,
    size: Dp = 48.dp,
    modifier: Modifier = Modifier,
    showBadge: Boolean = false,
) {
    Box(modifier = modifier.size(size)) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(PrimaryContainer),
        )
        if (showBadge) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Primary)
                    .align(Alignment.BottomEnd),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Filled.Verified,
                    contentDescription = "Verified",
                    tint = Color.White,
                    modifier = Modifier.size(11.dp),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Loading Indicator
// ─────────────────────────────────────────────
@Composable
fun BLLoadingIndicator(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Primary)
    }
}

// ─────────────────────────────────────────────
//  Progress Bar with Label
// ─────────────────────────────────────────────
@Composable
fun BLProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false,
    label: String? = null,
) {
    Column(modifier = modifier) {
        if (showLabel) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = label ?: "Tiến độ",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            Spacer(Modifier.height(4.dp))
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Primary,
            trackColor = PrimaryContainer,
        )
    }
}

// ─────────────────────────────────────────────
//  Category / Tag Chip
// ─────────────────────────────────────────────
@Composable
fun BLCategoryChip(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = TagDesign,
    onClick: (() -> Unit)? = null,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
            ),
        )
    }
}

// ─────────────────────────────────────────────
//  Star Rating Display
// ─────────────────────────────────────────────
@Composable
fun BLRatingRow(
    rating: Double,
    reviewCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Icon(
            imageVector = androidx.compose.material.icons.Icons.Filled.Star,
            contentDescription = null,
            tint = Warning,
            modifier = Modifier.size(13.dp),
        )
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelSmall.copy(
                color = OnSurface,
                fontWeight = FontWeight.SemiBold,
            ),
        )
        Text(
            text = "(${formatCount(reviewCount)})",
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

// ─────────────────────────────────────────────
//  Course Thumbnail Card (used in grid/list)
// ─────────────────────────────────────────────
@Composable
fun BLCourseCard(
    title: String,
    instructor: String,
    rating: Double,
    reviewCount: Int,
    imageUrl: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier
            .height(230.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(SurfaceVariant),
            )
            Column(
                modifier = Modifier.padding(10.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = instructor,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.weight(1f))

                BLRatingRow(rating = rating, reviewCount = reviewCount)
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Menu Item Row (used in Profile screen)
// ─────────────────────────────────────────────
@Composable
fun BLMenuItemRow(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = OnSurfaceMuted,
            modifier = Modifier.size(20.dp),
        )
    }
}

// ─────────────────────────────────────────────
//  Text Field
// ─────────────────────────────────────────────
@Composable
fun BLTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge.copy(color = OnSurfaceMuted),
            )
        },
        leadingIcon = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = OnSurfaceMuted,
                    modifier = Modifier.size(20.dp),
                )
            }
        } else {
            null
        },
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Divider,
            focusedBorderColor = Primary,
            unfocusedContainerColor = SurfaceVariant,
            focusedContainerColor = Surface,
        ),
        visualTransformation = if (isPassword) {
            androidx.compose.ui.text.input.PasswordVisualTransformation()
        } else {
            androidx.compose.ui.text.input.VisualTransformation.None
        },
    )
}

// ─────────────────────────────────────────────
//  Bottom Navigation
// ─────────────────────────────────────────────
@Composable
fun BLBottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLaunchPythonCompiler: () -> Unit = {},
) {
    val items = listOf(
        BottomNavItem("home", "Trang chủ", androidx.compose.material.icons.Icons.Filled.Home),
        BottomNavItem("search", "Tìm kiếm", androidx.compose.material.icons.Icons.Filled.Search),
        BottomNavItem("python", "Python IDE", androidx.compose.material.icons.Icons.Filled.Computer),
        BottomNavItem("courses", "Khóa học", androidx.compose.material.icons.Icons.Filled.MenuBook),
        BottomNavItem("profile", "Hồ sơ", androidx.compose.material.icons.Icons.Filled.Person),
    )
    NavigationBar(
        containerColor = Surface,
        tonalElevation = 0.dp,
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == "python") {
                        onLaunchPythonCompiler()
                    } else {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = OnSurfaceMuted,
                    unselectedTextColor = OnSurfaceMuted,
                    indicatorColor = PrimaryContainer,
                ),
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

// ─────────────────────────────────────────────
//  Divider
// ─────────────────────────────────────────────
@Composable
fun BLDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = Divider,
    )
}

// Helpers
private fun formatCount(count: Int): String = when {
    count >= 1000 -> "${count / 1000}.${(count % 1000) / 100}k"
    else -> count.toString()
}
