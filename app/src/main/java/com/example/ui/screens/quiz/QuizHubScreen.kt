package com.example.ui.screens.quiz

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.ui.components.BearPose
import com.example.ui.components.CuteBears3D
import com.example.ui.components.FloatingParticles

val QuizCategories = listOf(
  "Birthday Trivia",
  "Cute Couple Quiz",
  "How Well Do You Know Me?"
)

@Composable
fun QuizHubScreen(
  recipientName: String,
  onLoadQuestions: (category: String, callback: (List<QuizQuestion>) -> Unit) -> Unit,
  onAddCustomQuestion: (category: String, q: String, a: String, b: String, c: String, d: String, correctIdx: Int, exp: String) -> Unit,
  onSoundCorrect: () -> Unit,
  onSoundWrong: () -> Unit,
  onBack: () -> Unit,
  reducedMotion: Boolean = false
) {
  var selectedCategory by remember { mutableStateOf(QuizCategories[0]) }
  var questions by remember { mutableStateOf<List<QuizQuestion>>(emptyList()) }
  var currentQuestionIndex by remember { mutableIntStateOf(0) }
  var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
  var score by remember { mutableIntStateOf(0) }
  var isQuizComplete by remember { mutableStateOf(false) }

  // Custom Question Modal
  var showAddDialog by remember { mutableStateOf(false) }
  var customQ by remember { mutableStateOf("") }
  var customOptA by remember { mutableStateOf("") }
  var customOptB by remember { mutableStateOf("") }
  var customOptC by remember { mutableStateOf("") }
  var customOptD by remember { mutableStateOf("") }
  var customCorrectIdx by remember { mutableIntStateOf(0) }

  fun reloadCategory(category: String) {
    selectedCategory = category
    currentQuestionIndex = 0
    selectedOptionIndex = null
    score = 0
    isQuizComplete = false
    onLoadQuestions(category) { fetched ->
      questions = fetched
    }
  }

  LaunchedEffect(selectedCategory) {
    reloadCategory(selectedCategory)
  }

  fun onOptionSelected(optIdx: Int) {
    if (selectedOptionIndex != null || questions.isEmpty()) return
    selectedOptionIndex = optIdx

    val q = questions[currentQuestionIndex]
    if (optIdx == q.correctOptionIndex) {
      score += 100
      onSoundCorrect()
    } else {
      onSoundWrong()
    }
  }

  fun nextQuestion() {
    selectedOptionIndex = null
    if (currentQuestionIndex + 1 < questions.size) {
      currentQuestionIndex++
    } else {
      isQuizComplete = true
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFFAF5FF), Color(0xFFF3E8FF), Color(0xFFFFF0F5))
        )
      )
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
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

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "Score: $score pts 💖",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF7E22CE)
          )
        }

        IconButton(
          onClick = { showAddDialog = true },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("add_custom_quiz_button")
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = "Add Question", tint = Color(0xFF7E22CE))
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Category Selector Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
      ) {
        QuizCategories.forEach { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { reloadCategory(cat) },
            label = {
              Text(
                text = when (cat) {
                  "Birthday Trivia" -> "Birthday"
                  "Cute Couple Quiz" -> "Couple"
                  else -> "Know Me?"
                },
                fontSize = 11.sp
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFF9333EA),
              selectedLabelColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (!isQuizComplete && questions.isNotEmpty()) {
        val q = questions[currentQuestionIndex]
        val options = q.getOptions()

        // Progress Bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF7E22CE)
          )
          Text(
            text = selectedCategory,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
          progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = Color(0xFF9333EA),
          trackColor = Color(0xFFE9D5FF)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Cute Bear reaction
        val reactionPose = when {
          selectedOptionIndex == null -> BearPose.HOLDING_HEART
          selectedOptionIndex == q.correctOptionIndex -> BearPose.CELEBRATING
          else -> BearPose.HUGGING
        }

        CuteBears3D(
          pose = reactionPose,
          size = 140.dp,
          reducedMotion = reducedMotion
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Question Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = q.questionText,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color(0xFF3B0764),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4 Options
            options.forEachIndexed { optIdx, optText ->
              val isSelected = selectedOptionIndex == optIdx
              val isCorrect = optIdx == q.correctOptionIndex
              val hasAnswered = selectedOptionIndex != null

              val containerColor = when {
                !hasAnswered -> Color(0xFFFAF5FF)
                isSelected && isCorrect -> Color(0xFFDCFCE7)
                isSelected && !isCorrect -> Color(0xFFFEE2E2)
                isCorrect -> Color(0xFFDCFCE7)
                else -> Color(0xFFFAF5FF)
              }

              val borderColor = when {
                !hasAnswered -> Color(0xFFE9D5FF)
                isCorrect -> Color(0xFF16A34A)
                isSelected -> Color(0xFFDC2626)
                else -> Color(0xFFE9D5FF)
              }

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 5.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(containerColor)
                  .clickable(enabled = !hasAnswered) { onOptionSelected(optIdx) }
                  .padding(horizontal = 14.dp, vertical = 12.dp)
                  .testTag("quiz_option_$optIdx")
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "${('A' + optIdx)}.  $optText",
                    fontSize = 13.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = Color(0xFF3B0764),
                    modifier = Modifier.weight(1f)
                  )

                  if (hasAnswered && isCorrect) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = "Correct",
                      tint = Color(0xFF16A34A),
                      modifier = Modifier.size(18.dp)
                    )
                  } else if (hasAnswered && isSelected) {
                    Icon(
                      imageVector = Icons.Default.Close,
                      contentDescription = "Incorrect",
                      tint = Color(0xFFDC2626),
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
              }
            }

            // Explanation
            if (selectedOptionIndex != null && q.explanation.isNotBlank()) {
              Spacer(modifier = Modifier.height(12.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color(0xFFF3E8FF))
                  .padding(10.dp)
              ) {
                Text(
                  text = "💡 ${q.explanation}",
                  fontSize = 12.sp,
                  color = Color(0xFF6B21A8)
                )
              }
            }

            // Next Question Button
            if (selectedOptionIndex != null) {
              Spacer(modifier = Modifier.height(16.dp))
              Button(
                onClick = { nextQuestion() },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(46.dp)
                  .testTag("quiz_next_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
                shape = RoundedCornerShape(23.dp)
              ) {
                Text(
                  text = if (currentQuestionIndex + 1 < questions.size) "Next Question ➡️" else "View Results 🏆",
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
              }
            }
          }
        }
      } else if (isQuizComplete) {
        // Quiz Complete Card
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(26.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = null,
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(54.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Quiz Mastered! 🌟",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = Color(0xFF581C87)
            )

            Text(
              text = "Final Score: $score / ${questions.size * 100} pts",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = Color(0xFF9333EA),
              modifier = Modifier.padding(vertical = 6.dp)
            )

            Text(
              text = "You and $recipientName share an extraordinary bond! Every quiz answer brings you closer together! 💖",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
              onClick = { reloadCategory(selectedCategory) },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("quiz_replay_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA)),
              shape = RoundedCornerShape(24.dp)
            ) {
              Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Replay Quiz", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
              onClick = onBack,
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
              shape = RoundedCornerShape(24.dp)
            ) {
              Text("Back to Home")
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }

    // Modal to create Custom Question
    if (showAddDialog) {
      AlertDialog(
        onDismissRequest = { showAddDialog = false },
        title = {
          Text("Add Custom Question ✍️", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
          Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
              text = "Category: $selectedCategory",
              fontSize = 12.sp,
              color = Color(0xFF9333EA),
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = customQ,
              onValueChange = { customQ = it },
              label = { Text("Your Question") },
              placeholder = { Text("e.g. What is my favorite movie?") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = customOptA,
              onValueChange = { customOptA = it },
              label = { Text("Option A (Correct)") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = customOptB,
              onValueChange = { customOptB = it },
              label = { Text("Option B") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = customOptC,
              onValueChange = { customOptC = it },
              label = { Text("Option C") },
              modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
              value = customOptD,
              onValueChange = { customOptD = it },
              label = { Text("Option D") },
              modifier = Modifier.fillMaxWidth()
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (customQ.isNotBlank() && customOptA.isNotBlank() && customOptB.isNotBlank()) {
                onAddCustomQuestion(
                  selectedCategory,
                  customQ,
                  customOptA,
                  customOptB,
                  customOptC.ifBlank { "Option C" },
                  customOptD.ifBlank { "Option D" },
                  0, // Option A is correct
                  "Personalized answer created by you!"
                )
                showAddDialog = false
                customQ = ""
                customOptA = ""
                customOptB = ""
                customOptC = ""
                customOptD = ""
                reloadCategory(selectedCategory)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9333EA))
          ) {
            Text("Save Question")
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
