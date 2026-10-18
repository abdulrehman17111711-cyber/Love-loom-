package com.example.ui.screens.countdown

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.viewmodel.BirthdayCountdownState

@Composable
fun BirthdayCountdownScreen(
  recipientName: String,
  birthDay: Int,
  birthMonth: Int,
  birthYear: Int,
  countdown: BirthdayCountdownState,
  onGoToCelebration: () -> Unit,
  onBack: () -> Unit,
  reducedMotion: Boolean = false
) {
  val monthNames = listOf(
    "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5), Color(0xFFFFF0F5))
        )
      )
  ) {
    FloatingParticles(
      particleCount = if (countdown.isToday) 45 else 25,
      reducedMotion = reducedMotion
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp, vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
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
          text = "Birthday Countdown ⏳",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp,
          color = Color(0xFF065F46)
        )

        Spacer(modifier = Modifier.size(38.dp))
      }

      // Middle Countdown Centerpiece
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CuteBears3D(
          pose = if (countdown.isToday) BearPose.CELEBRATING else BearPose.HOLDING_BALLOONS,
          size = 180.dp,
          reducedMotion = reducedMotion
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (countdown.isToday) "🎉 IT'S YOUR BIRTHDAY TODAY! 🎉" else "Next Birthday in",
          fontWeight = FontWeight.ExtraBold,
          fontSize = if (countdown.isToday) 18.sp else 16.sp,
          color = Color(0xFF047857),
          textAlign = TextAlign.Center
        )

        Text(
          text = "$birthDay ${monthNames[(birthMonth - 1).coerceIn(0, 11)]} • Turning ${countdown.turnsAge} Years Old 💖",
          fontSize = 13.sp,
          color = Color(0xFF059669),
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 4 Live Digits: Days, Hours, Minutes, Seconds
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          CountdownDigitCard(value = countdown.days.toString(), label = "Days")
          CountdownDigitCard(value = String.format("%02d", countdown.hours), label = "Hours")
          CountdownDigitCard(value = String.format("%02d", countdown.minutes), label = "Minutes")
          CountdownDigitCard(value = String.format("%02d", countdown.seconds), label = "Seconds")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Motivational Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (countdown.isToday) {
                "Today is all about you, $recipientName! The whole universe is celebrating your presence! 🎂✨"
              } else {
                "Every second brings us closer to celebrating the most wonderful day of the year: $recipientName's birthday! 🎈"
              },
              fontSize = 13.sp,
              color = Color(0xFF064E3B),
              textAlign = TextAlign.Center,
              lineHeight = 20.sp
            )
          }
        }
      }

      // Action Button
      Button(
        onClick = onGoToCelebration,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("countdown_celebrate_button"),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
        shape = RoundedCornerShape(25.dp)
      ) {
        Icon(imageVector = Icons.Default.Cake, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (countdown.isToday) "Launch Birthday Party! 🎂" else "Preview 3D Cake & Party 🍰",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = Color.White
        )
      }
    }
  }
}

@Composable
private fun CountdownDigitCard(value: String, label: String) {
  Card(
    modifier = Modifier
      .width(72.dp)
      .clip(RoundedCornerShape(16.dp)),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = value,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        color = Color(0xFF047857)
      )
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF059669)
      )
    }
  }
}
