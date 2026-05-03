package com.klmpk5.daycare_app.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.klmpk5.daycare_app.data.local.entities.Child

@Composable
fun ChildItem(child: Child) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = child.fullName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Tanggal lahir: ${child.birthDate}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Gender: ${child.gender}", style = MaterialTheme.typography.bodySmall)
        }
    }
}