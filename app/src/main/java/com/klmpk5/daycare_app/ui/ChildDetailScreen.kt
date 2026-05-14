package com.klmpk5.daycare_app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.klmpk5.daycare_app.viewModel.ChildProfileViewModel
import com.klmpk5.daycare_app.viewModel.DailyScoreViewModel
import com.klmpk5.daycare_app.viewModel.WeeklyPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDetailScreen(
    parentUid: String,
    onBackClick: () -> Unit,
    profileViewModel: ChildProfileViewModel = viewModel(),
    weeklyViewModel: WeeklyPlanViewModel = viewModel(),
    scoreViewModel: DailyScoreViewModel = viewModel()
) {
    val childData by profileViewModel.childData
    val isProfileLoading by profileViewModel.isLoading

    val weeklyPlans by weeklyViewModel.weeklyPlans
    val isWeeklyLoading by weeklyViewModel.isLoading

    val dailyScores by scoreViewModel.dailyScores
    val isScoreLoading by scoreViewModel.isLoading

    LaunchedEffect(parentUid) {
        profileViewModel.loadProfileByParent(parentUid)
    }

    LaunchedEffect(childData?.childId) {
        childData?.childId?.let { id ->
            if (id.isNotEmpty()) {
                weeklyViewModel.loadWeeklyPlans(id)
                scoreViewModel.loadDailyScores(id)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Anak", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        },
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        if (isProfileLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- KARTU PROFIL ANAK ---
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(childData?.fullName ?: "Nama tidak ditemukan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("${childData?.gender ?: "-"} | Lahir: ${childData?.birthDate ?: "-"}", color = Color.LightGray, fontSize = 12.sp)
                                Text("ID: ${childData?.childId ?: "-"}", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                    }
                }

                // --- SECTION WEEKLY PLAN ---
                item {
                    SectionCard(title = "Weekly Plan") {
                        if (isWeeklyLoading) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color.White)
                        } else if (weeklyPlans.isEmpty()) {
                            Text("Belum ada rencana mingguan.", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            weeklyPlans.forEachIndexed { index, plan ->
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text("Tema: ${plan.description}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Periode: ${plan.startDate} s/d ${plan.endDate}", color = Color.LightGray, fontSize = 14.sp)

                                    // Beri garis pembatas kalau datanya lebih dari 1
                                    if (index < weeklyPlans.size - 1) {
                                        Divider(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp), color = Color(0xFF333333))
                                    }
                                }
                            }
                        }
                    }
                }

                // --- SECTION NILAI HARIAN ---
                item {
                    SectionCard(title = "Nilai Harian") {
                        if (isScoreLoading) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color.White)
                        } else if (dailyScores.isEmpty()) {
                            Text("Belum ada nilai harian untuk hari ini.", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            dailyScores.forEachIndexed { index, score ->
                                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Text("Tanggal: ${score.date}", color = Color.Gray, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Aktivitas: ${score.activityName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    // Warna ungu untuk Nilai sesuai screenshot
                                    Text("Nilai: ${score.score}", color = Color(0xFFB39DDB), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Catatan Guru:", color = Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(score.notes, color = Color.Gray, fontSize = 14.sp)

                                    // Beri garis pembatas kalau datanya lebih dari 1
                                    if (index < dailyScores.size - 1) {
                                        Divider(modifier = Modifier.padding(top = 12.dp, bottom = 4.dp), color = Color(0xFF333333))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF333333))
            content()
        }
    }
}