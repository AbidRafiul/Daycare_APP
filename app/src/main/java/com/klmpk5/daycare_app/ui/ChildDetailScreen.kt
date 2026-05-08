package com.klmpk5.daycare_app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.klmpk5.daycare_app.viewModel.ChildViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDetailScreen(
    parentUid: String,
    viewModel: ChildViewModel,
    onBackClick: () -> Unit
) {
    LaunchedEffect(parentUid) {
        viewModel.getFullChildData(parentUid)
    }

    val child = viewModel.childData
    val plan = viewModel.weeklyPlan
    val score = viewModel.dailyScore

    val isLoading = viewModel.isLoading
    val error = viewModel.errorMessage

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard Anak") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
                Text("Menarik data dari server...")
            } else if (error.isNotEmpty()) {
                Text(text = error, color = MaterialTheme.colorScheme.error)
            } else if (child != null) {
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Foto", modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(text = child.fullName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(text = "${child.gender} | Lahir: ${child.birthDate}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "ID: ${child.childId}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Weekly Plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        if (plan != null) {
                            Text(text = "Tema: ${plan.description}", fontWeight = FontWeight.Bold)
                            Text(text = "Periode: ${plan.startDate} s/d ${plan.endDate}", style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("Belum ada rencana mingguan.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Nilai Harian", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        if (score != null) {
                            Text(text = "Tanggal: ${score.date}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Aktivitas: ${score.activityName}", fontWeight = FontWeight.Bold)
                            Text(text = "Nilai: ${score.score}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "Catatan Guru:", fontWeight = FontWeight.Bold)
                            Text(text = score.notes, style = MaterialTheme.typography.bodyMedium)
                        } else {
                            Text("Belum ada nilai harian untuk hari ini.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }
}