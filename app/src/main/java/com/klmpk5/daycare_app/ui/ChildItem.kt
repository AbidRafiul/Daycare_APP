//untuk aplikasi yang versi User (Wali Murid), file ChildListScreen dan ChildItem di comment/disable dulu ya. Soalnya alur buat orang tua itu setelah Login langsung direct ke ChildDetailScreen (Dashboard spesifik 1 anak). Mereka nggak butuh milih-milih nama anak lagi dari list. Nanti file ini tinggal kalian uncomment dan sesuaikan aja di repository Aplikasi Admin, karena guru yang butuh tampilan LazyColumn buat milih data anak mana yang mau di-input nilainya. Kodingan narik data dari Firebase-nya udah ready di ViewModel.


//package com.klmpk5.daycare_app.ui
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Card
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.klmpk5.daycare_app.data.local.entities.Child
//
//@Composable
//fun ChildItem(child: Child) {
//    Card(modifier = Modifier.fillMaxWidth()) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(text = child.fullName, style = MaterialTheme.typography.titleMedium)
//            Text(text = "Tanggal lahir: ${child.birthDate}", style = MaterialTheme.typography.bodyMedium)
//            Text(text = "Gender: ${child.gender}", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}