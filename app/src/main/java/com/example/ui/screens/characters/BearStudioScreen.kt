package com.example.ui.screens.characters

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BlurOn
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BearPose
import com.example.ui.components.CuteBears3D
import com.example.ui.components.EnvironmentMood
import com.example.ui.components.FloatingParticles
import com.example.ui.components.PostProcessingConfig

data class BearPoseItem(
  val pose: BearPose,
  val name: String,
  val description: String,
  val emoji: String
)

val AllBearStudioPoses = listOf(
  BearPoseItem(BearPose.HOLDING_HEART, "Holding Heart", "Standing together holding a sparkling glowing pink heart", "💖"),
  BearPoseItem(BearPose.HUGGING, "Hugging & Swaying", "Warm tender bear cuddle with gentle rhythmic swaying", "🫂"),
  BearPoseItem(BearPose.WALKING, "Walking Together", "Sweet stroll side-by-side through starry pastel paths", "🐾"),
  BearPoseItem(BearPose.SITTING, "Sitting Cozy", "Relaxing cozily together under soft glowing fairy lights", "✨"),
  BearPoseItem(BearPose.GIFT_EXCHANGE, "Gift Exchange", "Sharing lovely surprise gift boxes with bright smiles", "🎁"),
  BearPoseItem(BearPose.DANCING, "Happy Dance", "Bouncing, jumping, and dancing in celebration", "💃"),
  BearPoseItem(BearPose.HOLDING_BALLOONS, "Birthday Balloons", "Floating with colorful festive birthday helium balloons", "🎈"),
  BearPoseItem(BearPose.PRESENTING_CAKE, "Presenting Cake", "Presenting a delicious 3D frosted birthday cake", "🎂"),
  BearPoseItem(BearPose.BLOWING_CANDLES, "Blowing Candles", "Making a secret wish and puffing out birthday candles", "🕯️"),
  BearPoseItem(BearPose.CUTTING_CAKE, "Cutting Cake", "Guiding the golden utensil to serve the first sweet slice", "🍰"),
  BearPoseItem(BearPose.OPENING_GIFT, "Opening Gift", "Unwrapping ribbons to reveal cute cuddly surprises", "🎀"),
  BearPoseItem(BearPose.PRESENTING_LETTER, "Romantic Letter", "Handing over a sealed parchment love scroll", "💌"),
  BearPoseItem(BearPose.PROPOSAL, "Cute Proposal", "Kneeling down with a glowing heart and red velvet ring box", "💍"),
  BearPoseItem(BearPose.EXPRESSIONS, "Cute Expressions", "Shy rosy blush, sparkling anime eyes, and happy smiles", "🥰"),
  BearPoseItem(BearPose.CELEBRATING, "Game Victory", "Cheering with confetti bursts and golden victory stars", "🏆")
)

@Composable
fun BearStudioScreen(
  onSoundTap: () -> Unit,
  onBack: () -> Unit,
  reducedMotion: Boolean = false
) {
  var selectedPoseItem by remember { mutableStateOf(AllBearStudioPoses[0]) }
  var currentMood by remember { mutableStateOf(EnvironmentMood.PASTEL_SUNSET) }

  // Post-Processing Studio Controls State
  var isPostProcessingMasterEnabled by remember { mutableStateOf(true) }
  var enableAmbientOcclusion by remember { mutableStateOf(true) }
  var enableDynamicSoftShadows by remember { mutableStateOf(true) }
  var enableRefinedBloom by remember { mutableStateOf(true) }
  var enableRimLighting by remember { mutableStateOf(true) }
  var bloomIntensity by remember { mutableFloatStateOf(1.0f) }
  var shadowSoftness by remember { mutableFloatStateOf(1.0f) }
  var showPostProcessingPanel by remember { mutableStateOf(true) }

  // Studio background tinting according to environment mood
  val bgTop by animateColorAsState(
    targetValue = when (currentMood) {
      EnvironmentMood.PASTEL_SUNSET -> Color(0xFFFFF7ED)
      EnvironmentMood.CANDY_DREAM -> Color(0xFFFDF2F8)
      EnvironmentMood.FAIRY_TALE -> Color(0xFFF0FDFA)
      EnvironmentMood.ROMANTIC_NIGHT -> Color(0xFF2A102E)
    },
    animationSpec = tween(600),
    label = "bgTop"
  )
  val bgBottom by animateColorAsState(
    targetValue = when (currentMood) {
      EnvironmentMood.PASTEL_SUNSET -> Color(0xFFFFEDD5)
      EnvironmentMood.CANDY_DREAM -> Color(0xFFFCE7F3)
      EnvironmentMood.FAIRY_TALE -> Color(0xFFFEF9C3)
      EnvironmentMood.ROMANTIC_NIGHT -> Color(0xFF1E0A24)
    },
    animationSpec = tween(600),
    label = "bgBottom"
  )

  val postProcessingConfig = remember(
    isPostProcessingMasterEnabled,
    enableAmbientOcclusion,
    enableDynamicSoftShadows,
    enableRefinedBloom,
    enableRimLighting,
    bloomIntensity,
    shadowSoftness,
    currentMood
  ) {
    if (isPostProcessingMasterEnabled) {
      PostProcessingConfig(
        ambientOcclusion = enableAmbientOcclusion,
        aoIntensity = 1.0f,
        dynamicSoftShadows = enableDynamicSoftShadows,
        shadowSoftness = shadowSoftness,
        refinedBloom = enableRefinedBloom,
        bloomIntensity = bloomIntensity,
        rimLighting = enableRimLighting,
        studioEnvironment = true,
        mood = currentMood
      )
    } else {
      // Raw 3D mode without post-processing
      PostProcessingConfig(
        ambientOcclusion = false,
        dynamicSoftShadows = false,
        refinedBloom = false,
        rimLighting = false,
        studioEnvironment = false,
        mood = currentMood
      )
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.verticalGradient(colors = listOf(bgTop, bgBottom, Color(0xFFFFF0F5))))
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 14.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Navigation Bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = onBack,
          modifier = Modifier
            .size(40.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White)
            .testTag("bear_studio_back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            tint = Color(0xFF9A3412)
          )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
            text = "3D Companion Studio 🐻",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 19.sp,
            color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412)
          )
          Text(
            text = "Physically Based Pastel Post-Processing",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFE9D5FF) else Color(0xFFC2410C)
          )
        }

        // Toggle Post-Processing Master / Compare Button
        IconButton(
          onClick = { isPostProcessingMasterEnabled = !isPostProcessingMasterEnabled },
          modifier = Modifier
            .size(40.dp)
            .shadow(4.dp, CircleShape)
            .clip(CircleShape)
            .background(if (isPostProcessingMasterEnabled) Color(0xFFFFEDD5) else Color.White)
            .testTag("toggle_post_processing_button")
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = "Compare Post-Processing",
            tint = if (isPostProcessingMasterEnabled) Color(0xFFEA580C) else Color(0xFF9CA3AF)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Environment Mood Palette Selector
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Palette,
          contentDescription = null,
          tint = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412),
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "Environment Lighting Mood:",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412)
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(EnvironmentMood.values()) { mood ->
          val isSelected = currentMood == mood
          FilterChip(
            selected = isSelected,
            onClick = { currentMood = mood },
            label = { Text(mood.displayName, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFFF6B9D),
              selectedLabelColor = Color.White,
              containerColor = Color.White.copy(alpha = 0.85f)
            ),
            shape = RoundedCornerShape(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Main Interactive 3D Bear Stage Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .shadow(10.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFF38143F) else Color.White
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Pose Title & A/B Status Badge
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "${selectedPoseItem.emoji} ${selectedPoseItem.name}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412)
            )

            // Post-Processing Status Indicator
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                  if (isPostProcessingMasterEnabled) Color(0xFFDCFCE7) else Color(0xFFF3F4F6)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (isPostProcessingMasterEnabled) "✨ HD Post-FX Active" else "🚫 Raw 3D Mode",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPostProcessingMasterEnabled) Color(0xFF166534) else Color(0xFF6B7280)
              )
            }
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = selectedPoseItem.description,
            fontSize = 12.sp,
            color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFE9D5FF) else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(10.dp))

          // 3D Canvas with Post-Processing Pipeline
          CuteBears3D(
            pose = selectedPoseItem.pose,
            size = 240.dp,
            reducedMotion = reducedMotion,
            postProcessingConfig = postProcessingConfig,
            onClick = {
              onSoundTap()
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "👉 Tap the bears to trigger rosy cheek blush & sound! 💖",
            fontSize = 11.5.sp,
            color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFB3C1) else Color(0xFFEA580C),
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Post-Processing Controls Panel
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFF43184C) else Color(0xFFFFF7ED)
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFEA580C),
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Post-Processing Effects Suite",
                fontWeight = FontWeight.Bold,
                fontSize = 14.5.sp,
                color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412)
              )
            }

            Text(
              text = if (showPostProcessingPanel) "Hide ▲" else "Tune ▼",
              fontSize = 11.5.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFFEA580C),
              modifier = Modifier
                .clickable { showPostProcessingPanel = !showPostProcessingPanel }
                .padding(4.dp)
            )
          }

          AnimatedVisibility(visible = showPostProcessingPanel) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
              // Master Toggle
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Master Post-Processing Pass",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color.White else Color(0xFF451A03)
                  )
                  Text(
                    text = "Enable all cinematic shaders & render passes",
                    fontSize = 10.5.sp,
                    color = Color.Gray
                  )
                }
                Switch(
                  checked = isPostProcessingMasterEnabled,
                  onCheckedChange = { isPostProcessingMasterEnabled = it },
                  colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFF2E7E))
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Individual Pass Toggles
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.BrightnessMedium,
                    contentDescription = null,
                    tint = Color(0xFF7C3AED),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Ambient Occlusion (AO)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFE9D5FF) else Color(0xFF451A03)
                  )
                }
                Switch(
                  checked = enableAmbientOcclusion && isPostProcessingMasterEnabled,
                  onCheckedChange = { enableAmbientOcclusion = it },
                  enabled = isPostProcessingMasterEnabled
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.BlurOn,
                    contentDescription = null,
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Dynamic Soft Shadows",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFE9D5FF) else Color(0xFF451A03)
                  )
                }
                Switch(
                  checked = enableDynamicSoftShadows && isPostProcessingMasterEnabled,
                  onCheckedChange = { enableDynamicSoftShadows = it },
                  enabled = isPostProcessingMasterEnabled
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFE11D48),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Refined Bloom Pass & Light Bounce",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFE9D5FF) else Color(0xFF451A03)
                  )
                }
                Switch(
                  checked = enableRefinedBloom && isPostProcessingMasterEnabled,
                  onCheckedChange = { enableRefinedBloom = it },
                  enabled = isPostProcessingMasterEnabled
                )
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Subsurface Fur Rim Lighting",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFE9D5FF) else Color(0xFF451A03)
                  )
                }
                Switch(
                  checked = enableRimLighting && isPostProcessingMasterEnabled,
                  onCheckedChange = { enableRimLighting = it },
                  enabled = isPostProcessingMasterEnabled
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Bloom Intensity Slider
              Text(
                text = "Bloom Radiance: ${(bloomIntensity * 100).toInt()}%",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412)
              )
              Slider(
                value = bloomIntensity,
                onValueChange = { bloomIntensity = it },
                valueRange = 0.5f..1.8f,
                enabled = isPostProcessingMasterEnabled && enableRefinedBloom,
                colors = SliderDefaults.colors(
                  thumbColor = Color(0xFFFF2E7E),
                  activeTrackColor = Color(0xFFFF6B9D)
                )
              )

              // Shadow Softness Slider
              Text(
                text = "Shadow Penumbra Softness: ${(shadowSoftness * 100).toInt()}%",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412)
              )
              Slider(
                value = shadowSoftness,
                onValueChange = { shadowSoftness = it },
                valueRange = 0.4f..1.6f,
                enabled = isPostProcessingMasterEnabled && enableDynamicSoftShadows,
                colors = SliderDefaults.colors(
                  thumbColor = Color(0xFF0284C7),
                  activeTrackColor = Color(0xFF7DD3FC)
                )
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      Text(
        text = "Select Character Pose (15 Total)",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = if (currentMood == EnvironmentMood.ROMANTIC_NIGHT) Color(0xFFFFD1DC) else Color(0xFF9A3412),
        modifier = Modifier.align(Alignment.Start)
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Horizontal list of all 15 poses
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(AllBearStudioPoses) { item ->
          val isSelected = selectedPoseItem.pose == item.pose

          Card(
            modifier = Modifier
              .size(width = 110.dp, height = 90.dp)
              .clip(RoundedCornerShape(18.dp))
              .clickable { selectedPoseItem = item }
              .testTag("pose_card_${item.name}"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) Color(0xFFFFEDD5) else Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 5.dp else 2.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Text(text = item.emoji, fontSize = 24.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = item.name,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                color = if (isSelected) Color(0xFF9A3412) else Color(0xFF451A03),
                textAlign = TextAlign.Center,
                maxLines = 2
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(28.dp))
    }
  }
}
