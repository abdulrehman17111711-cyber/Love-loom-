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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BearPose
import com.example.ui.components.CuteBears3D
import com.example.ui.components.FloatingParticles

private val MonthsList = listOf(
  "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December"
)

@Composable
fun BirthdayDateScreen(
  recipientName: String,
  initialDay: Int = 11,
  initialMonth: Int = 12,
  initialYear: Int = 2008,
  onDateConfirmed: (day: Int, month: Int, year: Int, pin: String, lockEnabled: Boolean) -> Unit,
  reducedMotion: Boolean = false
) {
  var selectedDay by remember { mutableIntStateOf(initialDay.coerceIn(1, 31)) }
  var selectedMonth by remember { mutableIntStateOf(initialMonth.coerceIn(1, 12)) }
  var selectedYear by remember { mutableIntStateOf(initialYear.coerceIn(1920, 2026)) }

  var enablePinLock by remember { mutableStateOf(false) }
  var pinCode by remember { mutableStateOf("") }

  val daysInMonth = when (selectedMonth) {
    2 -> if (selectedYear % 4 == 0 && (selectedYear % 100 != 0 || selectedYear % 400 == 0)) 29 else 28
    4, 6, 9, 11 -> 30
    else -> 31
  }

  if (selectedDay > daysInMonth) {
    selectedDay = daysInMonth
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
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp, vertical = 28.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(10.dp))

      CuteBears3D(
        pose = BearPose.HOLDING_BALLOONS,
        size = 180.dp,
        reducedMotion = reducedMotion
      )

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "When is your birthday?",
        style = MaterialTheme.typography.headlineSmall.copy(
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.primary
        ),
        textAlign = TextAlign.Center
      )

      Text(
        text = "Hello $recipientName! Let's set up your special day countdown and surprises! 🎂",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Date Picker Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Date selection badges
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            // Day selector
            DateUnitBox(
              label = "Day",
              value = selectedDay.toString(),
              onIncrement = { if (selectedDay < daysInMonth) selectedDay++ else selectedDay = 1 },
              onDecrement = { if (selectedDay > 1) selectedDay-- else selectedDay = daysInMonth },
              modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Month selector
            DateUnitBox(
              label = "Month",
              value = MonthsList[selectedMonth - 1],
              onIncrement = { if (selectedMonth < 12) selectedMonth++ else selectedMonth = 1 },
              onDecrement = { if (selectedMonth > 1) selectedMonth-- else selectedMonth = 12 },
              modifier = Modifier.weight(1.4f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Year selector
            DateUnitBox(
              label = "Year",
              value = selectedYear.toString(),
              onIncrement = { if (selectedYear < 2026) selectedYear++ },
              onDecrement = { if (selectedYear > 1920) selectedYear-- },
              modifier = Modifier.weight(1.1f)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Preview badge
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
              .padding(horizontal = 16.dp, vertical = 10.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Cake,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "$selectedDay ${MonthsList[selectedMonth - 1]} $selectedYear",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 15.sp
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Optional Security Lock Setup Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Birthday Privacy Lock",
                  fontWeight = FontWeight.SemiBold,
                  fontSize = 14.sp
                )
                Text(
                  text = "Protect romantic memories with a PIN or date unlock",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = enablePinLock,
              onCheckedChange = { enablePinLock = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
              ),
              modifier = Modifier.testTag("pin_lock_switch")
            )
          }

          if (enablePinLock) {
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
              value = pinCode,
              onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) pinCode = it },
              label = { Text("Set 4-6 Digit Security PIN") },
              placeholder = { Text("e.g., 1234") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
              singleLine = true,
              modifier = Modifier
                .fillMaxWidth()
                .testTag("pin_input_field"),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))

      Button(
        onClick = {
          onDateConfirmed(selectedDay, selectedMonth, selectedYear, pinCode, enablePinLock)
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("date_next_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(26.dp)
      ) {
        Text(
          text = "Enter LoveLoom 💖",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}

@Composable
private fun DateUnitBox(
  label: String,
  value: String,
  onIncrement: () -> Unit,
  onDecrement: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Up Arrow Button
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .clickable { onIncrement() },
      contentAlignment = Alignment.Center
    ) {
      Text("▲", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
    }

    Spacer(modifier = Modifier.height(4.dp))

    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
        .padding(vertical = 10.dp),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = value,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // Down Arrow Button
    Box(
      modifier = Modifier
        .size(32.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .clickable { onDecrement() },
      contentAlignment = Alignment.Center
    ) {
      Text("▼", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
    }
  }
}
