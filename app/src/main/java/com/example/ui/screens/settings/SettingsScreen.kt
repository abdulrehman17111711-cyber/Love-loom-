package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile

@Composable
fun SettingsScreen(
  userProfile: UserProfile,
  onUpdateProfile: (name: String, day: Int, month: Int, year: Int) -> Unit,
  onUpdateTheme: (themeIndex: Int) -> Unit,
  onUpdateLock: (enabled: Boolean, pin: String) -> Unit,
  onToggleReducedMotion: () -> Unit,
  onResetData: () -> Unit,
  onBack: () -> Unit
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showResetConfirmDialog by remember { mutableStateOf(false) }

  var editName by remember { mutableStateOf(userProfile.recipientName) }
  var editDay by remember { mutableIntStateOf(userProfile.birthDay) }
  var editMonth by remember { mutableIntStateOf(userProfile.birthMonth) }
  var editYear by remember { mutableIntStateOf(userProfile.birthYear) }

  val themes = listOf(
    Pair("Romantic Rose", Color(0xFFFF5288)),
    Pair("Lavender Dream", Color(0xFF9D65C9)),
    Pair("Golden Champagne", Color(0xFFD49B26)),
    Pair("Sweet Midnight", Color(0xFF382256))
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
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
          .background(MaterialTheme.colorScheme.surface)
      ) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
      }

      Text(
        text = "Settings & Personalization ⚙️",
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.onBackground
      )

      IconButton(
        onClick = { showAboutDialog = true },
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(MaterialTheme.colorScheme.surface)
          .testTag("about_app_button")
      ) {
        Icon(imageVector = Icons.Default.Info, contentDescription = "About", tint = MaterialTheme.colorScheme.primary)
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Profile Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Recipient: ${userProfile.recipientName}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Birthday: ${userProfile.birthDay}/${userProfile.birthMonth}/${userProfile.birthYear}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          IconButton(
            onClick = {
              editName = userProfile.recipientName
              editDay = userProfile.birthDay
              editMonth = userProfile.birthMonth
              editYear = userProfile.birthYear
              showEditProfileDialog = true
            },
            modifier = Modifier.testTag("edit_profile_button")
          ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = MaterialTheme.colorScheme.primary)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Theme Selector Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Palette,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Visual Themes",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        themes.forEachIndexed { index, pair ->
          val isSelected = userProfile.themeIndex == index

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
              )
              .clickable { onUpdateTheme(index) }
              .padding(horizontal = 12.dp, vertical = 10.dp)
              .testTag("theme_option_$index"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(22.dp)
                  .clip(CircleShape)
                  .background(pair.second)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = pair.first,
                fontSize = 13.5.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            if (isSelected) {
              Text(
                text = "Active ✨",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Privacy & Security Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Birthday & PIN Lock",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (userProfile.isLockEnabled) "App is protected on launch" else "App opens directly without lock",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Switch(
            checked = userProfile.isLockEnabled,
            onCheckedChange = { enabled ->
              onUpdateLock(enabled, userProfile.pinCode.ifBlank { "1234" })
            },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("settings_lock_switch")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Accessibility Card
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(22.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Accessibility,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Reduced Motion",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Gentle transitions for battery & performance",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Switch(
            checked = userProfile.reducedMotion,
            onCheckedChange = { onToggleReducedMotion() },
            colors = SwitchDefaults.colors(
              checkedThumbColor = Color.White,
              checkedTrackColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.testTag("reduced_motion_switch")
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Reset Data Button
    OutlinedButton(
      onClick = { showResetConfirmDialog = true },
      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .testTag("reset_data_button"),
      shape = RoundedCornerShape(24.dp)
    ) {
      Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.Red)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Reset App & Recipient Profile", color = Color.Red, fontWeight = FontWeight.SemiBold)
    }

    Spacer(modifier = Modifier.height(24.dp))
  }

  // Edit Profile Dialog
  if (showEditProfileDialog) {
    AlertDialog(
      onDismissRequest = { showEditProfileDialog = false },
      title = { Text("Edit Recipient Details", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          OutlinedTextField(
            value = editName,
            onValueChange = { editName = it },
            label = { Text("Recipient Name") },
            modifier = Modifier.fillMaxWidth()
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = editDay.toString(),
              onValueChange = { it.toIntOrNull()?.let { d -> if (d in 1..31) editDay = d } },
              label = { Text("Day") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = editMonth.toString(),
              onValueChange = { it.toIntOrNull()?.let { m -> if (m in 1..12) editMonth = m } },
              label = { Text("Month") },
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = editYear.toString(),
              onValueChange = { it.toIntOrNull()?.let { y -> if (y in 1900..2026) editYear = y } },
              label = { Text("Year") },
              modifier = Modifier.weight(1.2f)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onUpdateProfile(editName, editDay, editMonth, editYear)
            showEditProfileDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("Save Changes")
        }
      },
      dismissButton = {
        TextButton(onClick = { showEditProfileDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Reset Confirmation Dialog
  if (showResetConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showResetConfirmDialog = false },
      title = { Text("Reset LoveLoom 3D?") },
      text = {
        Text("This will clear the profile and restart the onboarding experience so you can customize it for a new recipient.")
      },
      confirmButton = {
        Button(
          onClick = {
            showResetConfirmDialog = false
            onResetData()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("Confirm Reset")
        }
      },
      dismissButton = {
        TextButton(onClick = { showResetConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // About LoveLoom 3D Dialog
  if (showAboutDialog) {
    AlertDialog(
      onDismissRequest = { showAboutDialog = false },
      title = { Text("About LoveLoom 3D 💖", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text(
            text = "LoveLoom 3D — More Than Just a Birthday App\nVersion 1.0.0",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• 100% Native Kotlin & Jetpack Compose\n• Offline-first local Room database\n• Original 3D Bear companions (inspired by Dudu & Bubu style)\n• Synthesized real-time audio tracks & sound fx\n• Multi-recipient commercial customization ready\n• Zero third-party copyright risks",
            fontSize = 12.5.sp,
            lineHeight = 18.sp
          )
        }
      },
      confirmButton = {
        Button(
          onClick = { showAboutDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("Awesome!")
        }
      }
    )
  }
}
