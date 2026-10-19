package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.BearPose
import com.example.ui.components.CuteBears3D
import com.example.ui.components.FloatingParticles

@Composable
fun WelcomeScreen(
  initialName: String,
  onNameConfirmed: (String) -> Unit,
  reducedMotion: Boolean = false
) {
  var name by remember { mutableStateOf(if (initialName.isNotBlank() && initialName != "Ayesha") initialName else "") }
  var errorText by remember { mutableStateOf("") }

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
        .padding(horizontal = 24.dp, vertical = 32.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "LoveLoom 3D",
        style = MaterialTheme.typography.headlineMedium.copy(
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.primary,
          letterSpacing = 1.sp
        ),
        textAlign = TextAlign.Center
      )

      Text(
        text = "More Than Just a Birthday App",
        style = MaterialTheme.typography.bodyMedium.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        ),
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(24.dp))

      // 3D Bear companions holding glowing heart card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(32.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
          contentAlignment = Alignment.Center
        ) {
          // Render generated hero artwork or interactive 3D bears
          Image(
            painter = painterResource(id = R.drawable.img_bear_couple_hero),
            contentDescription = "LoveLoom 3D Bear Companions",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )

          // Soft overlay badge
          Box(
            modifier = Modifier
              .align(Alignment.BottomCenter)
              .padding(bottom = 12.dp)
              .clip(RoundedCornerShape(20.dp))
              .background(Color.Black.copy(alpha = 0.45f))
              .padding(horizontal = 16.dp, vertical = 6.dp)
          ) {
            Text(
              text = "💖 A Special Celebration For You",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(32.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "What is your name?",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            ),
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Enter recipient's name to personalize every screen, cake & letter!",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = name,
            onValueChange = {
              name = it
              if (it.isNotBlank()) errorText = ""
            },
            placeholder = { Text("e.g., Ayesha") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
              )
            },
            singleLine = true,
            isError = errorText.isNotEmpty(),
            supportingText = if (errorText.isNotEmpty()) {
              { Text(errorText, color = MaterialTheme.colorScheme.error) }
            } else null,
            keyboardOptions = KeyboardOptions(
              capitalization = KeyboardCapitalization.Words,
              imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
              onDone = {
                if (name.isBlank()) {
                  errorText = "Please enter a name to continue"
                } else {
                  onNameConfirmed(name.trim())
                }
              }
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("name_input_field"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
          )

          Spacer(modifier = Modifier.height(24.dp))

          Button(
            onClick = {
              if (name.isBlank()) {
                errorText = "Please enter a name to continue"
              } else {
                onNameConfirmed(name.trim())
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("welcome_next_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(26.dp)
          ) {
            Text(
              text = "Next 💖",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
