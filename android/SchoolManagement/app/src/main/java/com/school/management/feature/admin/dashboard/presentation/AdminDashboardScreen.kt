package com.school.management.feature.admin.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.school.management.core.ui.components.BottomNavBar
import com.school.management.core.ui.components.ErrorView
import com.school.management.core.ui.components.LoadingIndicator

private val AdminPrimary = Color(0xFF6C63FF)
private val AdminSecondary = Color(0xFF3F51B5)
private val AdminGradientStart = Color(0xFF1A1A2E)
private val AdminGradientEnd = Color(0xFF16213E)
private val CardGlass = Color(0xFF1E293B)
private val SuccessGreen = Color(0xFF4CAF50)
private val WarningAmber = Color(0xFFFF9800)
private val DangerRed = Color(0xFFEF5350)
private val InfoBlue = Color(0xFF42A5F5)
private val AccentCyan = Color(0xFF26C6DA)
private val PurpleLight = Color(0xFFCE93D8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    onNavigateToTeachers: () -> Unit = {},
    onNavigateToStudents: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAttendance: () -> Unit = {},
    onAddTeacher: () -> Unit = {},
    onAddStudent: () -> Unit = {},
    currentRoute: String = "dashboard"
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    Scaffold(
        bottomBar = {
            BottomNavBar(
                currentRoute = currentRoute,
                items = listOf(
                    Triple("dashboard", "Dashboard", Icons.Outlined.Dashboard),
                    Triple("teachers", "Teachers", Icons.Outlined.People),
                    Triple("students", "Students", Icons.Outlined.Person),
                    Triple("settings", "Settings", Icons.Outlined.Settings)
                ),
                onItemSelected = { route ->
                    when (route) {
                        "teachers" -> onNavigateToTeachers()
                        "students" -> onNavigateToStudents()
                        "settings" -> onNavigateToSettings()
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(AdminGradientStart, AdminGradientEnd)
                    )
                )
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && uiState.metrics == null -> {
                    LoadingIndicator()
                }
                uiState.error != null && uiState.metrics == null -> {
                    ErrorView(
                        message = uiState.error ?: "Something went wrong",
                        onRetry = viewModel::loadDashboard
                    )
                }
                else -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = viewModel::refresh
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header
                            item {
                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -it / 2 }
                                ) {
                                    DashboardHeader(
                                        schoolName = uiState.schoolName,
                                        userName = uiState.userName
                                    )
                                }
                            }

                            // Stats Grid
                            item {
                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = fadeIn(tween(600, delayMillis = 200)) + slideInVertically(tween(600, delayMillis = 200)) { it / 3 }
                                ) {
                                    StatsGrid(metrics = uiState.metrics)
                                }
                            }

                            // Quick Actions
                            item {
                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = fadeIn(tween(600, delayMillis = 400)) + slideInVertically(tween(600, delayMillis = 400)) { it / 3 }
                                ) {
                                    QuickActionsRow(
                                        onAddTeacher = onAddTeacher,
                                        onAddStudent = onAddStudent,
                                        onViewAttendance = onNavigateToAttendance
                                    )
                                }
                            }

                            // Performance Overview
                            item {
                                AnimatedVisibility(
                                    visible = isVisible,
                                    enter = fadeIn(tween(600, delayMillis = 600)) + slideInVertically(tween(600, delayMillis = 600)) { it / 3 }
                                ) {
                                    PerformanceOverview(metrics = uiState.metrics)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardHeader(
    schoolName: String,
    userName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f)
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = schoolName,
                    style = MaterialTheme.typography.bodySmall,
                    color = AdminPrimary
                )
            }
        }

        IconButton(
            onClick = { },
            modifier = Modifier
                .size(48.dp)
                .background(
                    CardGlass.copy(alpha = 0.6f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun StatsGrid(metrics: com.school.management.core.model.domain.DashboardMetrics?) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Overview",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                title = "Active Students",
                value = metrics?.activeStudents ?: 0,
                icon = Icons.Default.Groups,
                gradientColors = listOf(Color(0xFF667EEA), Color(0xFF764BA2)),
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "Active Teachers",
                value = metrics?.activeTeachers ?: 0,
                icon = Icons.Default.Person,
                gradientColors = listOf(Color(0xFF11998E), Color(0xFF38EF7D)),
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                title = "Today Absences",
                value = metrics?.todayAbsences ?: 0,
                icon = Icons.Default.PersonOff,
                gradientColors = listOf(Color(0xFFEB3349), Color(0xFFF45C43)),
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "Total Billed",
                value = metrics?.totalBilled?.toInt() ?: 0,
                icon = Icons.Default.Receipt,
                prefix = "$",
                gradientColors = listOf(Color(0xFFFFA726), Color(0xFFFF7043)),
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardStatCard(
                title = "Total Collected",
                value = metrics?.totalCollected?.toInt() ?: 0,
                icon = Icons.Default.CheckCircle,
                prefix = "$",
                gradientColors = listOf(Color(0xFF56AB2F), Color(0xFFA8E063)),
                modifier = Modifier.weight(1f)
            )
            DashboardStatCard(
                title = "Outstanding",
                value = metrics?.outstanding?.toInt() ?: 0,
                icon = Icons.Default.PendingActions,
                prefix = "$",
                gradientColors = listOf(Color(0xFFE53935), Color(0xFFE35D5B)),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DashboardStatCard(
    title: String,
    value: Int,
    icon: ImageVector,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    prefix: String = ""
) {
    val animatedValue by animateIntAsState(
        targetValue = value,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "counter_$title"
    )

    Card(
        modifier = modifier
            .height(120.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = gradientColors,
                        start = Offset.Zero,
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color.White.copy(alpha = 0.2f),
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Column {
                    Text(
                        text = "$prefix${String.format("%,d", animatedValue)}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionsRow(
    onAddTeacher: () -> Unit,
    onAddStudent: () -> Unit,
    onViewAttendance: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                label = "Add Teacher",
                icon = Icons.Default.PersonAdd,
                color = AccentCyan,
                onClick = onAddTeacher,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "Add Student",
                icon = Icons.Default.Add,
                color = SuccessGreen,
                onClick = onAddStudent,
                modifier = Modifier.weight(1f)
            )
            QuickActionButton(
                label = "Attendance",
                icon = Icons.Default.CalendarToday,
                color = WarningAmber,
                onClick = onViewAttendance,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlass)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PerformanceOverview(metrics: com.school.management.core.model.domain.DashboardMetrics?) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardGlass)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Performance",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))

            PerformanceRow(
                label = "Attendance Rate",
                percentage = metrics?.attendanceRate ?: 0.0,
                color = SuccessGreen
            )
            Spacer(modifier = Modifier.height(12.dp))
            PerformanceRow(
                label = "Fee Collection Rate",
                percentage = metrics?.collectionRate ?: 0.0,
                color = InfoBlue
            )
        }
    }
}

@Composable
private fun PerformanceRow(
    label: String,
    percentage: Double,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "${String.format("%.1f", percentage)}%",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = color
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (percentage / 100f).toFloat().coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color, color.copy(alpha = 0.6f))
                        )
                    )
            )
        }
    }
}
