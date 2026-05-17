package com.klmpk5.daycare_app.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.data.local.entities.DailyScore
import com.klmpk5.daycare_app.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_app.ui.theme.DaycareAccent
import com.klmpk5.daycare_app.ui.theme.DaycareAccentLight
import com.klmpk5.daycare_app.ui.theme.DaycareBackground
import com.klmpk5.daycare_app.ui.theme.DaycareBorder
import com.klmpk5.daycare_app.ui.theme.DaycareCard
import com.klmpk5.daycare_app.ui.theme.DaycarePrimary
import com.klmpk5.daycare_app.ui.theme.DaycarePrimaryLight
import com.klmpk5.daycare_app.ui.theme.DaycareTextMuted
import com.klmpk5.daycare_app.ui.theme.DaycareTextPrimary
import com.klmpk5.daycare_app.ui.theme.DaycareTextSecondary

private data class ChatPreview(
    val sender: String,
    val message: String,
    val time: String
)

private data class ClassActivity(
    val time: String,
    val title: String,
    val note: String
)

@Composable
fun DashboardScreen(
    parentEmail: String,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(DashboardTab.Home) }
    val child = remember { dummyChild(parentEmail) }
    val weeklyPlans = remember { dummyWeeklyPlans(child.childId) }
    val scores = remember { dummyDailyScores(child.childId) }
    val chats = remember { dummyChats() }
    val activities = remember { dummyClassActivities() }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = DaycareBackground,
        bottomBar = {
            DashboardBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        when (selectedTab) {
            DashboardTab.Home -> HomeDashboardContent(
                child = child,
                weeklyPlans = weeklyPlans,
                scores = scores,
                chats = chats,
                contentPadding = innerPadding,
                onOpenRaport = { selectedTab = DashboardTab.Raport },
                onOpenClass = { selectedTab = DashboardTab.Class },
                onOpenChat = { selectedTab = DashboardTab.Chat }
            )

            DashboardTab.Raport -> RaportContent(
                child = child,
                scores = scores,
                contentPadding = innerPadding
            )

            DashboardTab.Chat -> ChatContent(
                chats = chats,
                contentPadding = innerPadding
            )

            DashboardTab.Class -> ClassContent(
                child = child,
                weeklyPlans = weeklyPlans,
                activities = activities,
                contentPadding = innerPadding
            )

            DashboardTab.Profile -> ProfileContent(
                parentEmail = parentEmail,
                child = child,
                contentPadding = innerPadding,
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun HomeDashboardContent(
    child: Child,
    weeklyPlans: List<WeeklyPlan>,
    scores: List<DailyScore>,
    chats: List<ChatPreview>,
    contentPadding: PaddingValues,
    onOpenRaport: () -> Unit,
    onOpenClass: () -> Unit,
    onOpenChat: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DaycareBackground)
            .padding(contentPadding),
        contentPadding = PaddingValues(bottom = 22.dp)
    ) {
        item {
            DashboardHeader(
                parentName = "Bunda",
                childName = child.fullName,
                summary = "Aktivitas hari ini sudah diperbarui"
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ChildSummaryCard(child = child)
                QuickActionsCard(
                    onOpenRaport = onOpenRaport,
                    onOpenClass = onOpenClass,
                    onOpenChat = onOpenChat
                )
                WeeklyPlanPreviewCard(plan = weeklyPlans.first(), onOpenClass = onOpenClass)
                TodayRaportPreviewCard(scores = scores, onOpenRaport = onOpenRaport)
                ChatPreviewCard(chat = chats.first(), onOpenChat = onOpenChat)
            }
        }
    }
}

@Composable
private fun ChildSummaryCard(child: Child) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(DaycarePrimaryLight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = child.fullName.take(1).uppercase(),
                    color = DaycarePrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = child.fullName,
                    color = DaycareTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${child.gender} - Lahir ${child.birthDate}",
                    color = DaycareTextSecondary,
                    fontSize = 13.sp
                )
            }

            StatusPill(text = "Aktif")
        }
    }
}

@Composable
private fun QuickActionsCard(
    onOpenRaport: () -> Unit,
    onOpenClass: () -> Unit,
    onOpenChat: () -> Unit
) {
    DashboardCard {
        Text(
            text = "Akses Cepat",
            color = DaycareTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickActionButton("Raport", "Nilai", Modifier.weight(1f), onOpenRaport)
            QuickActionButton("Class", "Kelas", Modifier.weight(1f), onOpenClass)
            QuickActionButton("Chat", "Guru", Modifier.weight(1f), onOpenChat)
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    subtitle: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(82.dp),
        color = DaycarePrimaryLight.copy(alpha = 0.68f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DaycarePrimary.copy(alpha = 0.16f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, color = DaycarePrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            Text(subtitle, color = DaycareTextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun WeeklyPlanPreviewCard(
    plan: WeeklyPlan,
    onOpenClass: () -> Unit
) {
    SectionCard(
        title = "Class",
        actionText = "Lihat",
        onActionClick = onOpenClass
    ) {
        Text(
            text = plan.description,
            color = DaycareTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${plan.startDate} sampai ${plan.endDate}",
            color = DaycareTextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun TodayRaportPreviewCard(
    scores: List<DailyScore>,
    onOpenRaport: () -> Unit
) {
    SectionCard(
        title = "Raport Hari Ini",
        actionText = "Detail",
        onActionClick = onOpenRaport
    ) {
        scores.take(2).forEachIndexed { index, score ->
            ScoreRow(score = score)
            if (index < scores.take(2).lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = DaycareBorder.copy(alpha = 0.55f)
                )
            }
        }
    }
}

@Composable
private fun ChatPreviewCard(
    chat: ChatPreview,
    onOpenChat: () -> Unit
) {
    SectionCard(
        title = "Chat Guru",
        actionText = "Balas",
        onActionClick = onOpenChat
    ) {
        Text(
            text = chat.sender,
            color = DaycareTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = chat.message,
            color = DaycareTextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun RaportContent(
    child: Child,
    scores: List<DailyScore>,
    contentPadding: PaddingValues
) {
    SimplePage(
        title = "Raport",
        subtitle = "Catatan perkembangan ${child.fullName}",
        contentPadding = contentPadding
    ) {
        scores.forEach { score ->
            DashboardCard {
                ScoreRow(score = score)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = score.notes,
                    color = DaycareTextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ChatContent(
    chats: List<ChatPreview>,
    contentPadding: PaddingValues
) {
    SimplePage(
        title = "Chat",
        subtitle = "Pesan sementara dari guru daycare",
        contentPadding = contentPadding
    ) {
        chats.forEach { chat ->
            DashboardCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(DaycareAccentLight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("G", color = DaycareAccent, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(chat.sender, color = DaycareTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(chat.message, color = DaycareTextSecondary, fontSize = 13.sp)
                    }
                    Text(chat.time, color = DaycareTextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun ClassContent(
    child: Child,
    weeklyPlans: List<WeeklyPlan>,
    activities: List<ClassActivity>,
    contentPadding: PaddingValues
) {
    SimplePage(
        title = "Class",
        subtitle = "Jadwal dan aktivitas ${child.fullName}",
        contentPadding = contentPadding
    ) {
        weeklyPlans.forEach { plan ->
            DashboardCard {
                Text("Weekly Plan", color = DaycarePrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(plan.description, color = DaycareTextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("${plan.startDate} - ${plan.endDate}", color = DaycareTextSecondary, fontSize = 13.sp)
            }
        }

        activities.forEach { activity ->
            DashboardCard {
                Row {
                    Text(activity.time, color = DaycarePrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(activity.title, color = DaycareTextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(activity.note, color = DaycareTextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    parentEmail: String,
    child: Child,
    contentPadding: PaddingValues,
    onLogout: () -> Unit
) {
    SimplePage(
        title = "Profile",
        subtitle = "Informasi akun wali murid",
        contentPadding = contentPadding
    ) {
        DashboardCard {
            ProfileRow(label = "Email", value = parentEmail.ifBlank { "parent@email.com" })
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DaycareBorder)
            ProfileRow(label = "Role", value = "Parent")
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DaycareBorder)
            ProfileRow(label = "Anak", value = child.fullName)
        }

        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DaycarePrimary)
        ) {
            Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SimplePage(
    title: String,
    subtitle: String,
    contentPadding: PaddingValues,
    content: @Composable () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DaycareBackground)
            .padding(contentPadding),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 22.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = title,
                    color = DaycareTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = DaycareTextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    actionText: String,
    onActionClick: () -> Unit,
    content: @Composable () -> Unit
) {
    DashboardCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = DaycareTextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onActionClick) {
                Text(actionText, color = DaycarePrimary, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun DashboardCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DaycareCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, DaycareBorder.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}

@Composable
private fun ScoreRow(score: DailyScore) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(DaycarePrimaryLight, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(score.score, color = DaycarePrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = score.activityName,
                color = DaycareTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(score.date, color = DaycareTextSecondary, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProfileRow(
    label: String,
    value: String
) {
    Row {
        Text(label, color = DaycareTextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, color = DaycareTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatusPill(text: String) {
    Surface(
        color = DaycarePrimaryLight,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, DaycarePrimary.copy(alpha = 0.18f))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            color = DaycarePrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun dummyChild(parentEmail: String): Child {
    return Child(
        childId = "dummy-child-1",
        fullName = "Alya Putri",
        nickName = "Alya",
        birthDate = "2021-08-14",
        gender = "Perempuan",
        parentUserId = "dummy-parent",
        parentEmail = parentEmail,
        isActive = true
    )
}

private fun dummyWeeklyPlans(childId: String): List<WeeklyPlan> {
    return listOf(
        WeeklyPlan(
            planId = "wp-1",
            startDate = "2026-05-18",
            endDate = "2026-05-22",
            description = "Tema minggu ini: mengenal warna, bentuk, dan kebiasaan mandiri."
        ),
        WeeklyPlan(
            planId = "wp-2",
            startDate = "2026-05-25",
            endDate = "2026-05-29",
            description = "Eksplorasi cerita, motorik halus, dan kegiatan sensorik."
        )
    )
}

private fun dummyDailyScores(childId: String): List<DailyScore> {
    return listOf(
        DailyScore(
            scoreId = "score-1",
            childId = childId,
            date = "2026-05-17",
            activityName = "Motorik Halus",
            score = "A",
            notes = "Alya mampu menyusun balok warna dan mengikuti instruksi sederhana."
        ),
        DailyScore(
            scoreId = "score-2",
            childId = childId,
            date = "2026-05-17",
            activityName = "Sosial Emosional",
            score = "B+",
            notes = "Mulai nyaman bermain bersama teman dan berbagi alat main."
        )
    )
}

private fun dummyChats(): List<ChatPreview> {
    return listOf(
        ChatPreview(
            sender = "Bu Rani",
            message = "Hari ini Alya makan cukup lahap dan tidur siang sekitar 1 jam.",
            time = "13.40"
        ),
        ChatPreview(
            sender = "Admin Daykids",
            message = "Jangan lupa membawa baju ganti tambahan untuk kegiatan besok.",
            time = "09.15"
        )
    )
}

private fun dummyClassActivities(): List<ClassActivity> {
    return listOf(
        ClassActivity(
            time = "08.00",
            title = "Circle Time",
            note = "Bernyanyi, doa pagi, dan mengenal tema warna."
        ),
        ClassActivity(
            time = "10.00",
            title = "Creative Play",
            note = "Mewarnai bentuk sederhana dan bermain balok."
        ),
        ClassActivity(
            time = "12.30",
            title = "Rest Time",
            note = "Tidur siang dan transisi pulang."
        )
    )
}
