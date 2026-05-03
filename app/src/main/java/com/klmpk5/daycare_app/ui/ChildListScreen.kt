package com.klmpk5.daycare_app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.klmpk5.daycare_app.viewModel.ChildViewModel

@Composable
fun ChildListScreen(viewModel: ChildViewModel) {
    // 1. Mengamati aliran data dari Room (Awalnya pasti berupa list kosong)
    val children by viewModel.children.collectAsState(initial = emptyList())

    // 2. Cek apakah datanya kosong
    if (children.isEmpty()) {
        // Tampilkan teks di tengah layar jika Room masih kosong
        // (Ini akan muncul selama 1-2 detik saat data ditarik dari Firebase ke Room)
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Belum ada data anak atau sedang memuat...")
        }
    } else {
        // 3. Jika Room sudah kemasukan data dari Firebase, gambar list-nya!
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(children, key = { it.childId }) { child ->
                ChildItem(child = child)
            }
        }
    }
}