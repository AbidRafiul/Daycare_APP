//untuk aplikasi yang versi User (Wali Murid), file ChildListScreen dan ChildItem di comment/disable dulu ya. Soalnya alur buat orang tua itu setelah Login langsung direct ke ChildDetailScreen (Dashboard spesifik 1 anak). Mereka nggak butuh milih-milih nama anak lagi dari list. Nanti file ini tinggal kalian uncomment dan sesuaikan aja di repository Aplikasi Admin, karena guru yang butuh tampilan LazyColumn buat milih data anak mana yang mau di-input nilainya. Kodingan narik data dari Firebase-nya udah ready di ViewModel.


//package com.klmpk5.daycare_app.ui
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.klmpk5.daycare_app.viewModel.ChildViewModel
//import com.klmpk5.daycare_app.utils.UiState
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ChildListScreen(parentUid: String, viewModel: ChildViewModel, onChildClick: (String) -> Unit) {
//    val children by viewModel.children.collectAsState(initial = emptyList())
//    val syncState by viewModel.syncState.collectAsState()
//
//    LaunchedEffect(Unit) { viewModel.loadChildData(parentUid) }
//
//    Scaffold(topBar = { TopAppBar(title = { Text("Dashboard Anak") }) }) { padding ->
//        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
//            if (children.isEmpty() && syncState is UiState.Loading) {
//                CircularProgressIndicator(Modifier.align(Alignment.Center))
//            } else {
//                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
//                    items(children) { child ->
//                        ElevatedCard(onClick = { onChildClick(child.childId) }, modifier = Modifier.fillMaxWidth()) {
//                            Column(Modifier.padding(16.dp)) {
//                                Text(child.fullName, style = MaterialTheme.typography.titleLarge)
//                                Text("Klik untuk detail", style = MaterialTheme.typography.bodySmall)
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}