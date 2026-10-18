package com.example.ui.screens.onboarding

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BearPose
import com.example.ui.components.CuteBears3D
import com.example.ui.components.FloatingParticles

@Composable
fun UnlockPinScreen(
  recipientName: String,
  expectedPin: String,
  birthDay: Int,
  birthMonth: Int,
  onUnlocked: () -> Unit,
  onForgotPin: () -> Unit,
  reducedMotion: Boolean = false
) {
  var enteredCode by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf("") }
  var isUnlocked by remember { mutableStateOf(false) }

  // Allow unlock via PIN OR recipient's birth date (e.g. "1112" for 11 Dec)
  val birthDateCode = String.format("%02d%02d", birthDay, birthMonth)

  fun checkCode(code: String) {
    if (code == expectedPin || code == birthDateCode || expectedPin.isBlank()) {
      isUnlocked = true
      onUnlocked()
    } else {
      errorMessage = "Incorrect code. Enter your PIN or Birthday (DDMM) 🥺"
      enteredCode = ""
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.background
          )
        )
      )
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 24.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
          modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Welcome Back, $recipientName!",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        )

        Text(
          text = "Enter your PIN or Date of Birth (DDMM) to unlock",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          ),
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dot indicators
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          repeat(4) { idx ->
            Box(
              modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(
                  if (idx < enteredCode.length) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.outlineVariant
                )
            )
          }
        }

        if (errorMessage.isNotBlank()) {
          Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp)
          )
        }
      }

      // Plush Numeric Keypad
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        val keys = listOf(
          listOf("1", "2", "3"),
          listOf("4", "5", "6"),
          listOf("7", "8", "9"),
          listOf("Reset", "0", "DEL")
        )

        keys.forEach { row ->
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            row.forEach { key ->
              Box(
                modifier = Modifier
                  .size(72.dp)
                  .clip(CircleShape)
                  .background(
                    when (key) {
                      "DEL", "Reset" -> MaterialTheme.colorScheme.surfaceVariant
                      else -> MaterialTheme.colorScheme.surface
                    }
                  )
                  .clickable {
                    when (key) {
                      "DEL" -> {
                        if (enteredCode.isNotEmpty()) {
                          enteredCode = enteredCode.dropLast(1)
                          errorMessage = ""
                        }
                      }
                      "Reset" -> onForgotPin()
                      else -> {
                        if (enteredCode.length < 6) {
                          enteredCode += key
                          errorMessage = ""
                          if (enteredCode.length >= 4) {
                            checkCode(enteredCode)
                          }
                        }
                      }
                    }
                  }
                  .testTag("keypad_$key"),
                contentAlignment = Alignment.Center
              ) {
                if (key == "DEL") {
                  Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                  )
                } else {
                  Text(
                    text = key,
                    fontSize = if (key == "Reset") 12.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }

        TextButton(
          onClick = { onUnlocked() },
          modifier = Modifier.testTag("skip_unlock_button")
        ) {
          Text(
            text = "Bypass Unlock / Enter Directly ✨",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp
          )
        }
      }
    }
  }
}
