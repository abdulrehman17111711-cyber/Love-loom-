package com.example.ui.screens.letters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoveLetter
import com.example.ui.components.FloatingParticles

@Composable
fun LoveLettersScreen(
  recipientName: String,
  letters: List<LoveLetter>,
  onAddLetter: (title: String, content: String, category: String) -> Unit,
  onDeleteLetter: (LoveLetter) -> Unit,
  onBack: () -> Unit,
  reducedMotion: Boolean = false
) {
  val context = LocalContext.current
  var selectedLetter by remember { mutableStateOf<LoveLetter?>(null) }
  var showAddDialog by remember { mutableStateOf(false) }

  var newTitle by remember { mutableStateOf("") }
  var newContent by remember { mutableStateOf("") }
  var newCategory by remember { mutableStateOf("Romantic") }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFFFF0F5), Color(0xFFFFE4EC), Color(0xFFFCE7F3))
        )
      )
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
      // Top Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
        ) {
          Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Text(
          text = "Love Letters 💌",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = MaterialTheme.colorScheme.primary
        )

        IconButton(
          onClick = { showAddDialog = true },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("add_custom_letter_button")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "New Letter", tint = MaterialTheme.colorScheme.primary)
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
      ) {
        items(letters) { letter ->
          val isExpanded = selectedLetter?.id == letter.id

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(22.dp))
              .clickable {
                selectedLetter = if (isExpanded) null else letter
              }
              .testTag("letter_card_${letter.id}"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isExpanded) Color(0xFFFFFDF8) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = letter.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }

                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                  Text(
                    text = letter.category,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Text(
                text = if (isExpanded) letter.content else letter.content.take(110) + "...",
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                fontFamily = FontFamily.Serif
              )

              if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.End
                ) {
                  // Copy Text Button
                  IconButton(
                    onClick = {
                      val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                      val clip = ClipData.newPlainText("Love Letter", letter.content)
                      clipboard.setPrimaryClip(clip)
                      Toast.makeText(context, "Letter copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.testTag("copy_letter_button")
                  ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                  }

                  // Share Intent Button
                  IconButton(
                    onClick = {
                      val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "${letter.title}\n\n${letter.content}\n\nShared with LoveLoom 3D 💖")
                        type = "text/plain"
                      }
                      context.startActivity(Intent.createChooser(sendIntent, "Share Love Letter"))
                    },
                    modifier = Modifier.testTag("share_letter_button")
                  ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                  }

                  if (letter.isCustom) {
                    IconButton(
                      onClick = { onDeleteLetter(letter) },
                      modifier = Modifier.testTag("delete_letter_button")
                    ) {
                      Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Modal to write a custom love letter
    if (showAddDialog) {
      AlertDialog(
        onDismissRequest = { showAddDialog = false },
        title = {
          Text("Write a Letter 💌", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
          Column {
            OutlinedTextField(
              value = newTitle,
              onValueChange = { newTitle = it },
              label = { Text("Letter Title") },
              placeholder = { Text("e.g. To My Forever Love") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = newContent,
              onValueChange = { newContent = it },
              label = { Text("Your Heartfelt Message") },
              placeholder = { Text("Dear $recipientName,\nWrite your sweet words here...") },
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (newContent.isNotBlank()) {
                onAddLetter(newTitle.ifBlank { "Sweet Note" }, newContent, newCategory)
                showAddDialog = false
                newTitle = ""
                newContent = ""
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Text("Seal & Save 💖")
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddDialog = false }) {
            Text("Cancel")
          }
        }
      )
    }
  }
}
