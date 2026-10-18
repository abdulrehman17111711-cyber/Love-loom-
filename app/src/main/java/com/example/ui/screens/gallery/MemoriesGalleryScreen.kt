package com.example.ui.screens.gallery

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.MemoryItem
import com.example.ui.components.FloatingParticles

@Composable
fun MemoriesGalleryScreen(
  recipientName: String,
  memories: List<MemoryItem>,
  onAddMemory: (uri: String, title: String, dateText: String, caption: String) -> Unit,
  onDeleteMemory: (MemoryItem) -> Unit,
  onBack: () -> Unit,
  reducedMotion: Boolean = false
) {
  var selectedMemoryForViewer by remember { mutableStateOf<MemoryItem?>(null) }
  var pendingImageUri by remember { mutableStateOf<String?>(null) }
  var pendingTitle by remember { mutableStateOf("") }
  var pendingCaption by remember { mutableStateOf("") }
  var pendingDate by remember { mutableStateOf("Today") }

  // Zero-permission Android Photo Picker!
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      pendingImageUri = uri.toString()
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFEEF2FF), Color(0xFFE0E7FF), Color(0xFFFFF0F5))
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
          text = "Memories Gallery 📸",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color(0xFF3730A3)
        )

        IconButton(
          onClick = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
          },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("add_photo_button")
        ) {
          Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = "Add Photo", tint = Color(0xFF4F46E5))
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = "Private romantic polaroids and memories of $recipientName ✨",
        fontSize = 12.sp,
        color = Color(0xFF4338CA),
        modifier = Modifier.padding(horizontal = 4.dp)
      )

      Spacer(modifier = Modifier.height(14.dp))

      if (memories.isEmpty()) {
        // Empty State with Sample polaroids & Add button
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_bear_couple_hero),
                contentDescription = null,
                modifier = Modifier
                  .size(160.dp)
                  .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Crop
              )

              Spacer(modifier = Modifier.height(14.dp))

              Text(
                text = "Capture Your First Memory",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF3730A3)
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Add photos together to create a timeless private polaroid album!",
                fontSize = 12.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(18.dp))

              Button(
                onClick = {
                  photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                shape = RoundedCornerShape(24.dp)
              ) {
                Icon(imageVector = Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Select Photo from Device", fontWeight = FontWeight.Bold, color = Color.White)
              }
            }
          }
        }
      } else {
        // Memories Grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          contentPadding = PaddingValues(bottom = 24.dp),
          modifier = Modifier.weight(1f)
        ) {
          items(memories) { item ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable { selectedMemoryForViewer = item }
                .testTag("memory_item_${item.id}"),
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(8.dp)
              ) {
                // Polaroid Photo Frame
                AsyncImage(
                  model = item.imageUri,
                  contentDescription = item.title,
                  modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp)),
                  contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                  text = item.title,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFF312E81),
                  maxLines = 1
                )

                Text(
                  text = item.dateText,
                  fontSize = 10.5.sp,
                  color = Color(0xFF6366F1),
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    }

    // Add Memory Details Dialog
    if (pendingImageUri != null) {
      AlertDialog(
        onDismissRequest = { pendingImageUri = null },
        title = {
          Text("Add to Memories 🖼️", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
          Column {
            OutlinedTextField(
              value = pendingTitle,
              onValueChange = { pendingTitle = it },
              label = { Text("Memory Title") },
              placeholder = { Text("e.g. Birthday Picnic") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = pendingCaption,
              onValueChange = { pendingCaption = it },
              label = { Text("Sweet Note / Caption") },
              placeholder = { Text("The happiest day together!") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = pendingDate,
              onValueChange = { pendingDate = it },
              label = { Text("Date") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              onAddMemory(
                pendingImageUri!!,
                pendingTitle.ifBlank { "Sweet Moment" },
                pendingDate.ifBlank { "Special Day" },
                pendingCaption
              )
              pendingImageUri = null
              pendingTitle = ""
              pendingCaption = ""
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
          ) {
            Text("Save Polaroid 📸")
          }
        },
        dismissButton = {
          TextButton(onClick = { pendingImageUri = null }) {
            Text("Cancel")
          }
        }
      )
    }

    // Full-screen Polaroid Viewer
    if (selectedMemoryForViewer != null) {
      val item = selectedMemoryForViewer!!

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.75f))
          .clickable { selectedMemoryForViewer = null },
        contentAlignment = Alignment.Center
      ) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clickable(enabled = false) {},
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF312E81)
              )
              IconButton(onClick = {
                onDeleteMemory(item)
                selectedMemoryForViewer = null
              }) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AsyncImage(
              model = item.imageUri,
              contentDescription = item.title,
              modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp)),
              contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (item.caption.isNotBlank()) {
              Text(
                text = item.caption,
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
              )
            }

            Text(
              text = "Recorded: ${item.dateText}",
              fontSize = 11.sp,
              color = Color(0xFF6366F1),
              modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = { selectedMemoryForViewer = null },
              modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
              shape = RoundedCornerShape(22.dp)
            ) {
              Text("Close", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
