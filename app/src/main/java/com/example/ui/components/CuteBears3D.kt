package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

enum class BearPose {
  HOLDING_HEART,
  HUGGING,
  WALKING,
  SITTING,
  GIFT_EXCHANGE,
  DANCING,
  HOLDING_BALLOONS,
  PRESENTING_CAKE,
  BLOWING_CANDLES,
  CUTTING_CAKE,
  OPENING_GIFT,
  PRESENTING_LETTER,
  PROPOSAL,
  EXPRESSIONS,
  CELEBRATING
}

enum class EnvironmentMood(
  val displayName: String,
  val horizonColors: List<Color>,
  val floorGloss: Color,
  val ambientTint: Color,
  val rimColorLeft: Color,
  val rimColorRight: Color
) {
  PASTEL_SUNSET(
    displayName = "Pastel Sunset 🌅",
    horizonColors = listOf(Color(0x33FFD1DC), Color(0x22FFE4E6), Color.Transparent),
    floorGloss = Color(0x26FFE4D6),
    ambientTint = Color(0x1AFFF1E6),
    rimColorLeft = Color(0xCCFFF7ED),
    rimColorRight = Color(0xCCFDE68A)
  ),
  CANDY_DREAM(
    displayName = "Candy Dream 🍭",
    horizonColors = listOf(Color(0x33F472B6), Color(0x22C084FC), Color.Transparent),
    floorGloss = Color(0x26FCE7F3),
    ambientTint = Color(0x18FDF2F8),
    rimColorLeft = Color(0xCCFBCFE8),
    rimColorRight = Color(0xCCE9D5FF)
  ),
  FAIRY_TALE(
    displayName = "Fairy Tale ✨",
    horizonColors = listOf(Color(0x3367E8F9), Color(0x22FEF08A), Color.Transparent),
    floorGloss = Color(0x26E0F2FE),
    ambientTint = Color(0x18F0FDF4),
    rimColorLeft = Color(0xCCCFFAFE),
    rimColorRight = Color(0xCCFEF9C3)
  ),
  ROMANTIC_NIGHT(
    displayName = "Romantic Night 🌙",
    horizonColors = listOf(Color(0x44831843), Color(0x264C1D95), Color.Transparent),
    floorGloss = Color(0x33FBCFE8),
    ambientTint = Color(0x222E1065),
    rimColorLeft = Color(0xCCF472B6),
    rimColorRight = Color(0xCCA78BFA)
  )
}

data class PostProcessingConfig(
  val ambientOcclusion: Boolean = true,
  val aoIntensity: Float = 1.0f,
  val dynamicSoftShadows: Boolean = true,
  val shadowSoftness: Float = 1.0f,
  val refinedBloom: Boolean = true,
  val bloomIntensity: Float = 1.0f,
  val rimLighting: Boolean = true,
  val studioEnvironment: Boolean = true,
  val mood: EnvironmentMood = EnvironmentMood.PASTEL_SUNSET
)

@Composable
fun CuteBears3D(
  modifier: Modifier = Modifier,
  pose: BearPose = BearPose.HOLDING_HEART,
  size: Dp = 260.dp,
  reducedMotion: Boolean = false,
  showHeartGlow: Boolean = true,
  ambientOcclusion: Boolean = true,
  dynamicSoftShadows: Boolean = true,
  refinedBloom: Boolean = true,
  rimLighting: Boolean = true,
  environmentMood: EnvironmentMood = EnvironmentMood.PASTEL_SUNSET,
  postProcessingConfig: PostProcessingConfig? = null,
  onClick: (() -> Unit)? = null
) {
  val config = postProcessingConfig ?: remember(
    ambientOcclusion, dynamicSoftShadows, refinedBloom, rimLighting, environmentMood
  ) {
    PostProcessingConfig(
      ambientOcclusion = ambientOcclusion,
      dynamicSoftShadows = dynamicSoftShadows,
      refinedBloom = refinedBloom,
      rimLighting = rimLighting,
      mood = environmentMood
    )
  }

  val infiniteTransition = rememberInfiniteTransition(label = "BearsAnimation")

  val breathOffset by infiniteTransition.animateFloat(
    initialValue = if (reducedMotion) 0f else -4f,
    targetValue = if (reducedMotion) 0f else 4f,
    animationSpec = infiniteRepeatable(
      animation = tween(1600, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath"
  )

  val swayAngle by infiniteTransition.animateFloat(
    initialValue = if (reducedMotion) 0f else -3f,
    targetValue = if (reducedMotion) 0f else 3f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "sway"
  )

  val heartPulse by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = if (reducedMotion) 1f else 1.14f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "heartPulse"
  )

  val celebrationJump by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = if (pose == BearPose.CELEBRATING || pose == BearPose.DANCING) -18f else 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "jump"
  )

  val bloomFlicker by infiniteTransition.animateFloat(
    initialValue = 0.92f,
    targetValue = if (reducedMotion) 1.0f else 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "bloomFlicker"
  )

  var tapCheekBlush by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .size(size)
      .clickable(enabled = onClick != null) {
        tapCheekBlush = !tapCheekBlush
        onClick?.invoke()
      },
    contentAlignment = Alignment.Center
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = this.size.width
      val h = this.size.height

      val dynamicYOffset = breathOffset + celebrationJump
      val jumpRatio = (dynamicYOffset / -18f).coerceIn(0f, 1f)

      // ===================================================================
      // PASS 0: 3D PASTEL STUDIO ENVIRONMENT & GROUND PEDESTAL
      // ===================================================================
      if (config.studioEnvironment) {
        drawPastelStudioEnvironment(w, h, config.mood)
      }

      // ===================================================================
      // PASS 1: SOPHISTICATED DYNAMIC SOFT SHADOWS
      // ===================================================================
      if (config.dynamicSoftShadows) {
        drawDynamicSoftShadows(
          w = w,
          h = h,
          jumpRatio = jumpRatio,
          pose = pose,
          softness = config.shadowSoftness
        )
      } else {
        // Fallback basic shadow
        drawOval(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x334A1525), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.93f),
            radius = w * 0.42f
          ),
          topLeft = Offset(w * 0.12f, h * 0.86f),
          size = Size(w * 0.76f, h * 0.12f)
        )
      }

      // Bear Center Coordinates
      val leftBearCenter = Offset(
        x = when (pose) {
          BearPose.HUGGING -> w * 0.42f
          BearPose.PROPOSAL -> w * 0.35f
          else -> w * 0.36f
        },
        y = h * 0.54f + dynamicYOffset
      )
      val leftBearScale = when (pose) {
        BearPose.PROPOSAL -> 0.9f
        else -> 1.0f
      }

      val rightBearCenter = Offset(
        x = when (pose) {
          BearPose.HUGGING -> w * 0.58f
          BearPose.PROPOSAL -> w * 0.65f
          else -> w * 0.64f
        },
        y = h * 0.54f + dynamicYOffset * 0.9f
      )

      // Ground glossy reflection (subtle pastel bounce)
      if (config.studioEnvironment) {
        drawStudioFloorReflection(
          w = w,
          h = h,
          leftCenter = leftBearCenter,
          rightCenter = rightBearCenter,
          mood = config.mood
        )
      }

      // ===================================================================
      // PASS 2: 3D COMPANION BEARS WITH AMBIENT OCCLUSION & RIM LIGHTING
      // ===================================================================
      // Draw Left Bear (Sweet White/Cream Fluffy Bear - Bubu)
      drawFluffyBear(
        center = leftBearCenter,
        scale = leftBearScale,
        sway = if (pose == BearPose.HUGGING) swayAngle * 0.5f else swayAngle,
        isBrown = false,
        pose = pose,
        isLeft = true,
        extraBlush = tapCheekBlush,
        enableAO = config.ambientOcclusion,
        aoIntensity = config.aoIntensity,
        enableRimLighting = config.rimLighting,
        rimColor = config.mood.rimColorLeft
      )

      // Inter-Bear Proximity Ambient Occlusion
      if (config.ambientOcclusion && pose == BearPose.HUGGING) {
        drawBearContactAmbientOcclusion(
          contactCenter = Offset(w * 0.50f, h * 0.54f + dynamicYOffset),
          radius = w * 0.16f,
          intensity = config.aoIntensity
        )
      }

      // Draw Right Bear (Caramel Brown Bear - Dudu)
      drawFluffyBear(
        center = rightBearCenter,
        scale = 1.0f,
        sway = if (pose == BearPose.HUGGING) -swayAngle * 0.5f else -swayAngle,
        isBrown = true,
        pose = pose,
        isLeft = false,
        extraBlush = tapCheekBlush,
        enableAO = config.ambientOcclusion,
        aoIntensity = config.aoIntensity,
        enableRimLighting = config.rimLighting,
        rimColor = config.mood.rimColorRight
      )

      // ===================================================================
      // PASS 3: ACCESSORIES (HEART, CAKE, BALLOONS, RING, LETTER, GIFTS)
      // ===================================================================
      when (pose) {
        BearPose.HOLDING_HEART, BearPose.HUGGING, BearPose.SITTING, BearPose.WALKING -> {
          val heartCenter = Offset(w * 0.50f, h * 0.62f + dynamicYOffset)
          drawGlowingHeart(
            center = heartCenter,
            size = w * 0.28f * heartPulse,
            showGlow = showHeartGlow,
            enableBloom = config.refinedBloom,
            bloomMultiplier = config.bloomIntensity * bloomFlicker,
            enableAO = config.ambientOcclusion
          )

          // Diffuse pink light bounce onto bears
          if (config.refinedBloom) {
            drawBouncedLightReflection(
              heartCenter = heartCenter,
              leftCenter = leftBearCenter,
              rightCenter = rightBearCenter,
              radius = w * 0.35f,
              intensity = config.bloomIntensity * heartPulse
            )
          }
        }
        BearPose.HOLDING_BALLOONS -> {
          drawPartyBalloons(w, h, dynamicYOffset, config.refinedBloom)
        }
        BearPose.PRESENTING_CAKE, BearPose.BLOWING_CANDLES, BearPose.CUTTING_CAKE -> {
          drawMiniCelebrationCake(
            center = Offset(w * 0.5f, h * 0.72f + dynamicYOffset),
            width = w * 0.32f,
            pose = pose,
            enableBloom = config.refinedBloom,
            enableAO = config.ambientOcclusion
          )
        }
        BearPose.PROPOSAL -> {
          drawProposalRingBox(
            center = Offset(w * 0.58f, h * 0.68f + dynamicYOffset),
            size = w * 0.16f,
            enableBloom = config.refinedBloom
          )
        }
        BearPose.PRESENTING_LETTER -> {
          drawMiniLoveEnvelope(
            center = Offset(w * 0.5f, h * 0.65f + dynamicYOffset),
            size = w * 0.22f,
            enableBloom = config.refinedBloom
          )
        }
        BearPose.GIFT_EXCHANGE, BearPose.OPENING_GIFT -> {
          drawMiniGiftBox(
            center = Offset(w * 0.5f, h * 0.68f + dynamicYOffset),
            size = w * 0.24f,
            enableBloom = config.refinedBloom,
            enableAO = config.ambientOcclusion
          )
        }
        BearPose.CELEBRATING, BearPose.DANCING -> {
          val heartCenter = Offset(w * 0.50f, h * 0.50f + dynamicYOffset)
          drawGlowingHeart(
            center = heartCenter,
            size = w * 0.24f * heartPulse,
            showGlow = showHeartGlow,
            enableBloom = config.refinedBloom,
            bloomMultiplier = config.bloomIntensity * bloomFlicker,
            enableAO = config.ambientOcclusion
          )
          drawMiniConfetti(w, h, config.refinedBloom)
        }
        else -> {
          val heartCenter = Offset(w * 0.50f, h * 0.62f + dynamicYOffset)
          drawGlowingHeart(
            center = heartCenter,
            size = w * 0.24f * heartPulse,
            showGlow = showHeartGlow,
            enableBloom = config.refinedBloom,
            bloomMultiplier = config.bloomIntensity * bloomFlicker,
            enableAO = config.ambientOcclusion
          )
        }
      }

      // ===================================================================
      // PASS 4: FINAL POST-PROCESSING COMPOSITING (ATMOSPHERIC VIGNETTE & BLOOM GLINTS)
      // ===================================================================
      if (config.refinedBloom) {
        drawAtmosphericBloomVeil(w, h, config.mood, config.bloomIntensity)
      }
    }
  }
}

// -----------------------------------------------------------------------------
// POST-PROCESSING SUBROUTINES: ENVIRONMENT, SHADOWS, AO & BLOOM
// -----------------------------------------------------------------------------

private fun DrawScope.drawPastelStudioEnvironment(w: Float, h: Float, mood: EnvironmentMood) {
  // Soft studio cyclorama backdrop (curved horizon gradient)
  drawRect(
    brush = Brush.verticalGradient(
      colors = mood.horizonColors,
      startY = h * 0.05f,
      endY = h * 0.90f
    ),
    topLeft = Offset.Zero,
    size = Size(w, h)
  )

  // Top-left warm Key Light vignette
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(mood.ambientTint, Color.Transparent),
      center = Offset(w * 0.25f, h * 0.20f),
      radius = w * 0.65f
    ),
    radius = w * 0.65f,
    center = Offset(w * 0.25f, h * 0.20f)
  )

  // Studio Ground Pedestal Horizon Curve
  val horizonPath = Path().apply {
    moveTo(0f, h * 0.88f)
    cubicTo(
      w * 0.25f, h * 0.86f,
      w * 0.75f, h * 0.86f,
      w, h * 0.88f
    )
    lineTo(w, h)
    lineTo(0f, h)
    close()
  }
  drawPath(
    path = horizonPath,
    brush = Brush.verticalGradient(
      colors = listOf(Color.Transparent, mood.floorGloss, Color(0x33FFFFFF)),
      startY = h * 0.86f,
      endY = h
    )
  )
}

private fun DrawScope.drawDynamicSoftShadows(
  w: Float,
  h: Float,
  jumpRatio: Float,
  pose: BearPose,
  softness: Float
) {
  // Dynamic scale and attenuation: when jumping or breathing up,
  // the shadow spreads wider, diffuses more, and decreases in opacity
  val shadowExpansion = 1.0f + jumpRatio * 0.18f
  val baseAlpha = (1.0f - jumpRatio * 0.45f).coerceIn(0.2f, 1.0f) * softness

  val shadowCenterY = h * 0.93f
  val shadowHeight = h * 0.13f * shadowExpansion

  // Layer 1: Atmospheric Ambient Diffuse Halo (large, ultra-soft falloff)
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x384E1A2C).copy(alpha = 0.18f * baseAlpha),
        Color(0x183D1422).copy(alpha = 0.08f * baseAlpha),
        Color.Transparent
      ),
      center = Offset(w * 0.50f, shadowCenterY),
      radius = w * 0.48f * shadowExpansion
    ),
    topLeft = Offset(w * 0.05f, shadowCenterY - shadowHeight * 0.65f),
    size = Size(w * 0.90f * shadowExpansion, shadowHeight * 1.3f)
  )

  // Layer 2: Soft Body Penumbra (linked main shadow)
  val penumbraWidth = when (pose) {
    BearPose.HUGGING -> w * 0.68f
    BearPose.PROPOSAL -> w * 0.75f
    else -> w * 0.78f
  } * shadowExpansion

  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x604A1525).copy(alpha = 0.38f * baseAlpha),
        Color(0x28300E19).copy(alpha = 0.15f * baseAlpha),
        Color.Transparent
      ),
      center = Offset(w * 0.50f, shadowCenterY),
      radius = penumbraWidth * 0.5f
    ),
    topLeft = Offset(w * 0.50f - penumbraWidth * 0.5f, shadowCenterY - shadowHeight * 0.5f),
    size = Size(penumbraWidth, shadowHeight)
  )

  // Layer 3: Left & Right Contact Umbras (darker, tight contact shadows directly beneath paw contacts)
  val contactY = h * 0.90f + jumpRatio * 2f
  val umbraAlpha = (0.55f * baseAlpha * (1f - jumpRatio * 0.8f)).coerceAtLeast(0f)

  if (umbraAlpha > 0.02f) {
    // Left Bear foot contact umbra
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF2B0B17).copy(alpha = umbraAlpha), Color.Transparent),
        center = Offset(w * 0.36f, contactY),
        radius = w * 0.14f
      ),
      topLeft = Offset(w * 0.24f, contactY - h * 0.025f),
      size = Size(w * 0.24f, h * 0.055f)
    )

    // Right Bear foot contact umbra
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0xFF2B0B17).copy(alpha = umbraAlpha), Color.Transparent),
        center = Offset(w * 0.64f, contactY),
        radius = w * 0.14f
      ),
      topLeft = Offset(w * 0.52f, contactY - h * 0.025f),
      size = Size(w * 0.24f, h * 0.055f)
    )
  }

  // Floating Accessory Drop Shadow (Projected on floor with parallax)
  when (pose) {
    BearPose.HOLDING_HEART, BearPose.CELEBRATING, BearPose.DANCING -> {
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color(0x35FF2E7E).copy(alpha = 0.25f * baseAlpha), Color.Transparent),
          center = Offset(w * 0.50f, shadowCenterY - 4f),
          radius = w * 0.16f
        ),
        topLeft = Offset(w * 0.40f, shadowCenterY - h * 0.02f),
        size = Size(w * 0.20f, h * 0.04f)
      )
    }
    BearPose.PRESENTING_CAKE, BearPose.CUTTING_CAKE -> {
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color(0x40331018).copy(alpha = 0.30f * baseAlpha), Color.Transparent),
          center = Offset(w * 0.50f, shadowCenterY - 2f),
          radius = w * 0.20f
        ),
        topLeft = Offset(w * 0.34f, shadowCenterY - h * 0.03f),
        size = Size(w * 0.32f, h * 0.06f)
      )
    }
    else -> Unit
  }
}

private fun DrawScope.drawStudioFloorReflection(
  w: Float,
  h: Float,
  leftCenter: Offset,
  rightCenter: Offset,
  mood: EnvironmentMood
) {
  // Soft blurred floor specular bounce of the bears' warm pastel colors
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(mood.floorGloss, Color.Transparent),
      center = Offset(leftCenter.x, h * 0.94f),
      radius = w * 0.24f
    ),
    topLeft = Offset(leftCenter.x - w * 0.22f, h * 0.88f),
    size = Size(w * 0.44f, h * 0.10f)
  )

  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(mood.floorGloss, Color.Transparent),
      center = Offset(rightCenter.x, h * 0.94f),
      radius = w * 0.24f
    ),
    topLeft = Offset(rightCenter.x - w * 0.22f, h * 0.88f),
    size = Size(w * 0.44f, h * 0.10f)
  )
}

private fun DrawScope.drawBearContactAmbientOcclusion(
  contactCenter: Offset,
  radius: Float,
  intensity: Float
) {
  // Proximity Ambient Occlusion between bodies in close cuddle/hug
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x45220C14).copy(alpha = 0.28f * intensity),
        Color.Transparent
      ),
      center = contactCenter,
      radius = radius
    ),
    radius = radius,
    center = contactCenter,
    blendMode = BlendMode.Multiply
  )
}

// -----------------------------------------------------------------------------
// BEAR MESH RENDERING WITH OCCLUSION CREVICES & SUB-SURFACE RIM LIGHT
// -----------------------------------------------------------------------------

private fun DrawScope.drawFluffyBear(
  center: Offset,
  scale: Float,
  sway: Float,
  isBrown: Boolean,
  pose: BearPose,
  isLeft: Boolean,
  extraBlush: Boolean,
  enableAO: Boolean,
  aoIntensity: Float,
  enableRimLighting: Boolean,
  rimColor: Color
) {
  val baseFur = if (isBrown) Color(0xFFC68A57) else Color(0xFFFAF3EC)
  val highlightFur = if (isBrown) Color(0xFFE2B083) else Color(0xFFFFFFFF)
  val shadowFur = if (isBrown) Color(0xFFA56938) else Color(0xFFE2D6CA)
  val deepShadowFur = if (isBrown) Color(0xFF7A451D) else Color(0xFFC7B7A6)

  val earInner = if (isBrown) Color(0xFFE6A77E) else Color(0xFFFFC9D5)
  val earInnerShadow = if (isBrown) Color(0xFFB8784E) else Color(0xFFEAA1B1)
  val snoutColor = if (isBrown) Color(0xFFE8C8A9) else Color(0xFFFFFFFF)
  val noseColor = Color(0xFF332018)
  val eyeColor = Color(0xFF1E130E)
  val blushColor = if (extraBlush) Color(0xFFFF5288) else Color(0xFFFF85A1)

  val bearRadius = 48f * scale

  rotate(degrees = sway, pivot = center) {
    scale(scale = scale, pivot = center) {
      val leftEarOffset = Offset(center.x - bearRadius * 0.72f, center.y - bearRadius * 0.85f)
      val rightEarOffset = Offset(center.x + bearRadius * 0.72f, center.y - bearRadius * 0.85f)
      val earRadius = bearRadius * 0.38f

      // -----------------------------------------------------------
      // EARS WITH AMBIENT OCCLUSION CREVICES & TRANSLUCENT LIGHTING
      // -----------------------------------------------------------
      // Left Ear Outer Spherical Fur
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(highlightFur, baseFur, shadowFur),
          center = Offset(leftEarOffset.x - earRadius * 0.2f, leftEarOffset.y - earRadius * 0.2f),
          radius = earRadius * 1.1f
        ),
        radius = earRadius,
        center = leftEarOffset
      )
      // Left Ear AO Fold Crevice
      if (enableAO) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0x35281310).copy(alpha = 0.25f * aoIntensity)),
            center = leftEarOffset,
            radius = earRadius
          ),
          radius = earRadius,
          center = leftEarOffset,
          blendMode = BlendMode.Multiply
        )
      }
      // Left Ear Inner (Subsurface cartilage pink)
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(earInner, earInnerShadow),
          center = Offset(leftEarOffset.x, leftEarOffset.y - earRadius * 0.1f),
          radius = earRadius * 0.65f
        ),
        radius = earRadius * 0.6f,
        center = leftEarOffset
      )

      // Right Ear Outer Spherical Fur
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(highlightFur, baseFur, shadowFur),
          center = Offset(rightEarOffset.x - earRadius * 0.2f, rightEarOffset.y - earRadius * 0.2f),
          radius = earRadius * 1.1f
        ),
        radius = earRadius,
        center = rightEarOffset
      )
      // Right Ear AO Fold Crevice
      if (enableAO) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, Color(0x35281310).copy(alpha = 0.25f * aoIntensity)),
            center = rightEarOffset,
            radius = earRadius
          ),
          radius = earRadius,
          center = rightEarOffset,
          blendMode = BlendMode.Multiply
        )
      }
      // Right Ear Inner
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(earInner, earInnerShadow),
          center = Offset(rightEarOffset.x, rightEarOffset.y - earRadius * 0.1f),
          radius = earRadius * 0.65f
        ),
        radius = earRadius * 0.6f,
        center = rightEarOffset
      )

      // -----------------------------------------------------------
      // BEAR BODY WITH AMBIENT OCCLUSION & VOLUME
      // -----------------------------------------------------------
      val bodyCenter = Offset(center.x, center.y + bearRadius * 0.78f)
      val bodyWidth = bearRadius * 1.55f
      val bodyHeight = bearRadius * 1.35f

      // Body 3D Volume Gradient (Key light from upper-left)
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(highlightFur, baseFur, shadowFur, deepShadowFur),
          center = Offset(bodyCenter.x - bodyWidth * 0.15f, bodyCenter.y - bodyHeight * 0.25f),
          radius = bodyWidth * 0.85f
        ),
        topLeft = Offset(bodyCenter.x - bodyWidth * 0.5f, bodyCenter.y - bodyHeight * 0.5f),
        size = Size(bodyWidth, bodyHeight)
      )

      // Subsurface Rim Glow along the outer flank of the body
      if (enableRimLighting) {
        drawOval(
          brush = Brush.radialGradient(
            colors = listOf(rimColor.copy(alpha = 0.45f), Color.Transparent),
            center = Offset(
              if (isLeft) bodyCenter.x - bodyWidth * 0.45f else bodyCenter.x + bodyWidth * 0.45f,
              bodyCenter.y
            ),
            radius = bodyWidth * 0.45f
          ),
          topLeft = Offset(bodyCenter.x - bodyWidth * 0.5f, bodyCenter.y - bodyHeight * 0.5f),
          size = Size(bodyWidth, bodyHeight),
          blendMode = BlendMode.Screen
        )
      }

      // -----------------------------------------------------------
      // NECK & CHIN CREVICE AMBIENT OCCLUSION
      // Deep contact shadow between head sphere and torso
      // -----------------------------------------------------------
      if (enableAO) {
        val neckAoCenter = Offset(center.x, center.y + bearRadius * 0.72f)
        drawOval(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x551E0E12).copy(alpha = 0.45f * aoIntensity),
              Color(0x201E0E12).copy(alpha = 0.18f * aoIntensity),
              Color.Transparent
            ),
            center = neckAoCenter,
            radius = bearRadius * 0.75f
          ),
          topLeft = Offset(neckAoCenter.x - bearRadius * 0.70f, neckAoCenter.y - bearRadius * 0.22f),
          size = Size(bearRadius * 1.40f, bearRadius * 0.44f),
          blendMode = BlendMode.Multiply
        )
      }

      // -----------------------------------------------------------
      // BEAR HEAD (3D CHIBI SPHERE WITH AMBIENT SHADOWING)
      // -----------------------------------------------------------
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(highlightFur, baseFur, shadowFur),
          center = Offset(center.x - bearRadius * 0.18f, center.y - bearRadius * 0.25f),
          radius = bearRadius * 1.15f
        ),
        radius = bearRadius,
        center = center
      )

      // Head Rim Lighting / Fluffy Silhouette Halo
      if (enableRimLighting) {
        val rimOffset = if (isLeft) {
          Offset(center.x - bearRadius * 0.4f, center.y - bearRadius * 0.3f)
        } else {
          Offset(center.x + bearRadius * 0.4f, center.y - bearRadius * 0.3f)
        }
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(rimColor.copy(alpha = 0.40f), Color.Transparent),
            center = rimOffset,
            radius = bearRadius * 0.65f
          ),
          radius = bearRadius,
          center = center,
          blendMode = BlendMode.Screen
        )
      }

      // -----------------------------------------------------------
      // SNOUT WITH CONTACT OCCLUSION & HIGHLIGHT
      // -----------------------------------------------------------
      val snoutCenter = Offset(center.x, center.y + bearRadius * 0.22f)
      val snoutW = bearRadius * 0.74f
      val snoutH = bearRadius * 0.54f

      // Snout Under-Rim Contact AO (Separates 3D muzzle from face)
      if (enableAO) {
        drawOval(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x4025110E).copy(alpha = 0.35f * aoIntensity),
              Color.Transparent
            ),
            center = Offset(snoutCenter.x, snoutCenter.y + snoutH * 0.15f),
            radius = snoutW * 0.62f
          ),
          topLeft = Offset(snoutCenter.x - snoutW * 0.52f, snoutCenter.y - snoutH * 0.48f),
          size = Size(snoutW * 1.04f, snoutH * 1.12f),
          blendMode = BlendMode.Multiply
        )
      }

      // Snout Dome
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color.White, snoutColor, if (isBrown) Color(0xFFD6B290) else Color(0xFFEBE0D5)),
          center = Offset(snoutCenter.x, snoutCenter.y - snoutH * 0.15f),
          radius = snoutW * 0.6f
        ),
        topLeft = Offset(snoutCenter.x - snoutW * 0.5f, snoutCenter.y - snoutH * 0.5f),
        size = Size(snoutW, snoutH)
      )

      // Cute 3D Nose
      val noseCenter = Offset(snoutCenter.x, snoutCenter.y - snoutH * 0.12f)
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFF4A3025), noseColor),
          center = Offset(noseCenter.x, noseCenter.y - bearRadius * 0.03f),
          radius = bearRadius * 0.14f
        ),
        topLeft = Offset(noseCenter.x - bearRadius * 0.12f, noseCenter.y - bearRadius * 0.08f),
        size = Size(bearRadius * 0.24f, bearRadius * 0.16f)
      )
      // Nose specular highlight dot (Bloom catchlight)
      drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = bearRadius * 0.035f,
        center = Offset(noseCenter.x - bearRadius * 0.04f, noseCenter.y - bearRadius * 0.03f)
      )

      // Cute Chibi Mouth
      val mouthPath = Path().apply {
        moveTo(noseCenter.x, noseCenter.y + bearRadius * 0.08f)
        lineTo(noseCenter.x, noseCenter.y + bearRadius * 0.15f)
        // Left smile curve
        cubicTo(
          noseCenter.x - bearRadius * 0.08f, noseCenter.y + bearRadius * 0.22f,
          noseCenter.x - bearRadius * 0.14f, noseCenter.y + bearRadius * 0.20f,
          noseCenter.x - bearRadius * 0.16f, noseCenter.y + bearRadius * 0.14f
        )
        moveTo(noseCenter.x, noseCenter.y + bearRadius * 0.15f)
        // Right smile curve
        cubicTo(
          noseCenter.x + bearRadius * 0.08f, noseCenter.y + bearRadius * 0.22f,
          noseCenter.x + bearRadius * 0.14f, noseCenter.y + bearRadius * 0.20f,
          noseCenter.x + bearRadius * 0.16f, noseCenter.y + bearRadius * 0.14f
        )
      }
      drawPath(
        path = mouthPath,
        color = noseColor,
        style = Stroke(width = bearRadius * 0.05f)
      )

      // -----------------------------------------------------------
      // SPARKLING BIG ANIME EYES WITH SPECULAR BLOOM CATCHLIGHTS
      // -----------------------------------------------------------
      val eyeY = center.y + bearRadius * 0.02f
      val eyeSpacing = bearRadius * 0.38f
      val leftEyeCenter = Offset(center.x - eyeSpacing, eyeY)
      val rightEyeCenter = Offset(center.x + eyeSpacing, eyeY)
      val eyeRadius = bearRadius * 0.13f

      if (pose == BearPose.BLOWING_CANDLES && isLeft) {
        // Winking eye for blowing candles
        drawArc(
          color = eyeColor,
          startAngle = 180f,
          sweepAngle = 180f,
          useCenter = false,
          topLeft = Offset(leftEyeCenter.x - eyeRadius, leftEyeCenter.y - eyeRadius * 0.5f),
          size = Size(eyeRadius * 2f, eyeRadius),
          style = Stroke(width = bearRadius * 0.06f)
        )
        drawEyeWithCatchlights(rightEyeCenter, eyeRadius, eyeColor)
      } else {
        drawEyeWithCatchlights(leftEyeCenter, eyeRadius, eyeColor)
        drawEyeWithCatchlights(rightEyeCenter, eyeRadius, eyeColor)
      }

      // -----------------------------------------------------------
      // ROSY BLUSH CHEEKS WITH SOFT DIFFUSE GLOW
      // -----------------------------------------------------------
      val blushY = center.y + bearRadius * 0.25f
      val blushSpacing = bearRadius * 0.54f
      val blushW = bearRadius * 0.28f
      val blushH = bearRadius * 0.18f

      val blushAlpha = if (extraBlush) 0.88f else 0.58f

      // Left Cheek
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(blushColor.copy(alpha = blushAlpha), Color.Transparent),
          center = Offset(center.x - blushSpacing, blushY),
          radius = blushW * 0.55f
        ),
        topLeft = Offset(center.x - blushSpacing - blushW * 0.5f, blushY - blushH * 0.5f),
        size = Size(blushW, blushH)
      )
      // Right Cheek
      drawOval(
        brush = Brush.radialGradient(
          colors = listOf(blushColor.copy(alpha = blushAlpha), Color.Transparent),
          center = Offset(center.x + blushSpacing, blushY),
          radius = blushW * 0.55f
        ),
        topLeft = Offset(center.x + blushSpacing - blushW * 0.5f, blushY - blushH * 0.5f),
        size = Size(blushW, blushH)
      )

      // -----------------------------------------------------------
      // BEAR PAWS/ARMS WITH CONTACT AMBIENT OCCLUSION
      // -----------------------------------------------------------
      val pawRadius = bearRadius * 0.26f
      val pawY = bodyCenter.y - bodyHeight * 0.05f
      val leftPawCenter = Offset(center.x - bearRadius * 0.55f, pawY)
      val rightPawCenter = Offset(center.x + bearRadius * 0.55f, pawY)

      // Left Paw AO Wedge
      if (enableAO) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x351F0D11).copy(alpha = 0.32f * aoIntensity), Color.Transparent),
            center = leftPawCenter,
            radius = pawRadius * 1.3f
          ),
          radius = pawRadius * 1.3f,
          center = leftPawCenter,
          blendMode = BlendMode.Multiply
        )
      }
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(highlightFur, baseFur, shadowFur),
          center = Offset(leftPawCenter.x - pawRadius * 0.2f, leftPawCenter.y - pawRadius * 0.2f),
          radius = pawRadius * 1.1f
        ),
        radius = pawRadius,
        center = leftPawCenter
      )

      // Right Paw AO Wedge
      if (enableAO) {
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(Color(0x351F0D11).copy(alpha = 0.32f * aoIntensity), Color.Transparent),
            center = rightPawCenter,
            radius = pawRadius * 1.3f
          ),
          radius = pawRadius * 1.3f,
          center = rightPawCenter,
          blendMode = BlendMode.Multiply
        )
      }
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(highlightFur, baseFur, shadowFur),
          center = Offset(rightPawCenter.x - pawRadius * 0.2f, rightPawCenter.y - pawRadius * 0.2f),
          radius = pawRadius * 1.1f
        ),
        radius = pawRadius,
        center = rightPawCenter
      )
    }
  }
}

private fun DrawScope.drawEyeWithCatchlights(center: Offset, radius: Float, color: Color) {
  // Eyeball base
  drawCircle(color = color, radius = radius, center = center)

  // Primary Catchlight Sparkle (Bloom Specular Core)
  val primaryCatchlightCenter = Offset(center.x + radius * 0.25f, center.y - radius * 0.28f)
  drawCircle(
    color = Color.White,
    radius = radius * 0.44f,
    center = primaryCatchlightCenter
  )

  // Secondary Catchlight Sparkle
  drawCircle(
    color = Color.White.copy(alpha = 0.85f),
    radius = radius * 0.20f,
    center = Offset(center.x - radius * 0.30f, center.y + radius * 0.32f)
  )

  // Starburst Glint cross on primary catchlight
  val glintLen = radius * 0.35f
  drawLine(
    color = Color.White.copy(alpha = 0.75f),
    start = Offset(primaryCatchlightCenter.x - glintLen, primaryCatchlightCenter.y),
    end = Offset(primaryCatchlightCenter.x + glintLen, primaryCatchlightCenter.y),
    strokeWidth = 1.8f
  )
  drawLine(
    color = Color.White.copy(alpha = 0.75f),
    start = Offset(primaryCatchlightCenter.x, primaryCatchlightCenter.y - glintLen),
    end = Offset(primaryCatchlightCenter.x, primaryCatchlightCenter.y + glintLen),
    strokeWidth = 1.8f
  )
}

// -----------------------------------------------------------------------------
// REFINED MULTI-TIER BLOOM PASS FOR EMISSIVE ASSETS
// -----------------------------------------------------------------------------

private fun DrawScope.drawGlowingHeart(
  center: Offset,
  size: Float,
  showGlow: Boolean,
  enableBloom: Boolean,
  bloomMultiplier: Float,
  enableAO: Boolean
) {
  // Heart Ambient Occlusion crevice (contact behind the heart where paws press)
  if (enableAO) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x402B0F18), Color.Transparent),
        center = Offset(center.x, center.y + size * 0.2f),
        radius = size * 0.55f
      ),
      topLeft = Offset(center.x - size * 0.45f, center.y - size * 0.1f),
      size = Size(size * 0.90f, size * 0.65f),
      blendMode = BlendMode.Multiply
    )
  }

  // Refined Multi-Stage Bloom Pass (Screen / Plus Blending)
  if (showGlow && enableBloom) {
    val bScale = bloomMultiplier.coerceIn(0.5f, 2.0f)

    // Stage 3: Wide Atmospheric Aura (soft pink/peach diffuse haze)
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0x44FF2E7E).copy(alpha = 0.25f * bScale),
          Color(0x22FF85A1).copy(alpha = 0.12f * bScale),
          Color.Transparent
        ),
        center = center,
        radius = size * 2.2f
      ),
      radius = size * 2.2f,
      center = center,
      blendMode = BlendMode.Screen
    )

    // Stage 2: Saturated Corona Flare
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0x99FF2E7E).copy(alpha = 0.55f * bScale),
          Color(0x44FF6B9D).copy(alpha = 0.25f * bScale),
          Color.Transparent
        ),
        center = center,
        radius = size * 1.4f
      ),
      radius = size * 1.4f,
      center = center,
      blendMode = BlendMode.Screen
    )

    // Stage 1: Specular Core Bloom
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(
          Color(0xDDFFFFFF).copy(alpha = 0.65f * bScale),
          Color(0x88FF2E7E).copy(alpha = 0.35f * bScale),
          Color.Transparent
        ),
        center = Offset(center.x - size * 0.10f, center.y - size * 0.15f),
        radius = size * 0.70f
      ),
      radius = size * 0.70f,
      center = Offset(center.x - size * 0.10f, center.y - size * 0.15f),
      blendMode = BlendMode.Screen
    )
  }

  // 3D Heart Mesh with Volumetric Specular Spherical Gradient
  val heartPath = createHeartPath(center, size)
  drawPath(
    path = heartPath,
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0xFFFFB3C1),
        Color(0xFFFF4D80),
        Color(0xFFE91E63),
        Color(0xFF9C0D38)
      ),
      center = Offset(center.x - size * 0.15f, center.y - size * 0.20f),
      radius = size * 0.88f
    )
  )

  // Glossy Specular Lobe Highlight
  val highlightPath = Path().apply {
    val hx = center.x - size * 0.22f
    val hy = center.y - size * 0.22f
    moveTo(hx, hy)
    cubicTo(
      hx + size * 0.1f, hy - size * 0.08f,
      hx + size * 0.2f, hy,
      hx + size * 0.15f, hy + size * 0.12f
    )
  }
  drawPath(
    path = highlightPath,
    color = Color.White.copy(alpha = 0.65f),
    style = Stroke(width = size * 0.09f)
  )

  // Secondary Apex Specular Ping with Bloom Star
  if (enableBloom) {
    val apexCenter = Offset(center.x - size * 0.20f, center.y - size * 0.25f)
    drawCircle(
      color = Color.White.copy(alpha = 0.85f),
      radius = size * 0.045f,
      center = apexCenter
    )
    val flare = size * 0.08f
    drawLine(
      color = Color.White.copy(alpha = 0.75f),
      start = Offset(apexCenter.x - flare, apexCenter.y),
      end = Offset(apexCenter.x + flare, apexCenter.y),
      strokeWidth = 1.6f
    )
    drawLine(
      color = Color.White.copy(alpha = 0.75f),
      start = Offset(apexCenter.x, apexCenter.y - flare),
      end = Offset(apexCenter.x, apexCenter.y + flare),
      strokeWidth = 1.6f
    )
  }
}

private fun DrawScope.drawBouncedLightReflection(
  heartCenter: Offset,
  leftCenter: Offset,
  rightCenter: Offset,
  radius: Float,
  intensity: Float
) {
  // Soft pink light bounce illuminating the chest and paws of the white bear
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x35FF6B9D).copy(alpha = 0.20f * intensity),
        Color.Transparent
      ),
      center = Offset(leftCenter.x + 12f, leftCenter.y + 14f),
      radius = radius * 0.6f
    ),
    radius = radius * 0.6f,
    center = Offset(leftCenter.x + 12f, leftCenter.y + 14f),
    blendMode = BlendMode.Screen
  )

  // Warm amber/pink light bounce on the brown bear
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x35FF85A1).copy(alpha = 0.18f * intensity),
        Color.Transparent
      ),
      center = Offset(rightCenter.x - 12f, rightCenter.y + 14f),
      radius = radius * 0.6f
    ),
    radius = radius * 0.6f,
    center = Offset(rightCenter.x - 12f, rightCenter.y + 14f),
    blendMode = BlendMode.Screen
  )
}

fun createHeartPath(center: Offset, size: Float): Path {
  return Path().apply {
    val top = center.y - size * 0.42f
    val bottom = center.y + size * 0.52f
    val left = center.x - size * 0.5f
    val right = center.x + size * 0.5f

    moveTo(center.x, center.y - size * 0.15f)
    cubicTo(
      center.x - size * 0.25f, top - size * 0.1f,
      left, center.y - size * 0.35f,
      left, center.y
    )
    cubicTo(
      left, center.y + size * 0.25f,
      center.x - size * 0.2f, bottom - size * 0.1f,
      center.x, bottom
    )
    cubicTo(
      center.x + size * 0.2f, bottom - size * 0.1f,
      right, center.y + size * 0.25f,
      right, center.y
    )
    cubicTo(
      right, center.y - size * 0.35f,
      center.x + size * 0.25f, top - size * 0.1f,
      center.x, center.y - size * 0.15f
    )
    close()
  }
}

private fun DrawScope.drawPartyBalloons(w: Float, h: Float, dy: Float, enableBloom: Boolean) {
  val balloonColors = listOf(Color(0xFFFF6B9D), Color(0xFFC084FC), Color(0xFF67E8F9), Color(0xFFFDE047))
  val positions = listOf(
    Offset(w * 0.25f, h * 0.22f + dy),
    Offset(w * 0.42f, h * 0.15f + dy * 0.8f),
    Offset(w * 0.62f, h * 0.16f + dy * 1.1f),
    Offset(w * 0.78f, h * 0.24f + dy * 0.9f)
  )
  positions.forEachIndexed { idx, pos ->
    // String with smooth gradient
    drawLine(
      brush = Brush.verticalGradient(
        colors = listOf(Color(0x99FFFFFF), Color(0x33FFFFFF)),
        startY = pos.y,
        endY = h * 0.65f
      ),
      start = pos,
      end = Offset(w * 0.5f, h * 0.65f),
      strokeWidth = 2.2f
    )

    // Soft Bloom behind the balloons
    if (enableBloom) {
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(balloonColors[idx % balloonColors.size].copy(alpha = 0.35f), Color.Transparent),
          center = pos,
          radius = 50f
        ),
        radius = 50f,
        center = pos,
        blendMode = BlendMode.Screen
      )
    }

    // Balloon 3D Sphere with Specular Highlight
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color.White.copy(alpha = 0.95f), balloonColors[idx % balloonColors.size], Color(0xFF331522)),
        center = Offset(pos.x - 10f, pos.y - 12f),
        radius = 38f
      ),
      topLeft = Offset(pos.x - 24f, pos.y - 32f),
      size = Size(48f, 62f)
    )
  }
}

private fun DrawScope.drawMiniCelebrationCake(
  center: Offset,
  width: Float,
  pose: BearPose,
  enableBloom: Boolean,
  enableAO: Boolean
) {
  val cakeH = width * 0.65f

  // Cake base contact AO crease
  if (enableAO) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x44260D15), Color.Transparent),
        center = Offset(center.x, center.y + cakeH * 0.72f),
        radius = width * 0.55f
      ),
      topLeft = Offset(center.x - width * 0.52f, center.y + cakeH * 0.62f),
      size = Size(width * 1.04f, 22f),
      blendMode = BlendMode.Multiply
    )
  }

  // Cake Base Tier (3D Round gradient)
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFFF0F5), Color(0xFFFFB6C1), Color(0xFFFF85A1), Color(0xFFC24168))
    ),
    topLeft = Offset(center.x - width * 0.5f, center.y),
    size = Size(width, cakeH * 0.7f),
    cornerRadius = CornerRadius(16f, 16f)
  )

  // Frosting drips with AO drop shadow
  if (enableAO) {
    drawRoundRect(
      color = Color(0x25000000),
      topLeft = Offset(center.x - width * 0.5f, center.y + 12f),
      size = Size(width, 10f),
      cornerRadius = CornerRadius(8f, 8f)
    )
  }
  drawRoundRect(
    color = Color.White,
    topLeft = Offset(center.x - width * 0.5f, center.y - 8f),
    size = Size(width, 24f),
    cornerRadius = CornerRadius(12f, 12f)
  )

  // Candle
  val candleX = center.x
  val candleY = center.y - 28f
  drawLine(
    color = Color(0xFFFFD54F),
    start = Offset(candleX, candleY + 20f),
    end = Offset(candleX, candleY),
    strokeWidth = 10f
  )

  // Candle Flame with Refined Bloom Pass
  if (pose != BearPose.CUTTING_CAKE) {
    if (enableBloom) {
      // Atmospheric flame bloom aura
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0x88FFD54F), Color(0x33FF6B00), Color.Transparent),
          center = Offset(candleX, candleY - 10f),
          radius = 35f
        ),
        radius = 35f,
        center = Offset(candleX, candleY - 10f),
        blendMode = BlendMode.Screen
      )
    }

    // Flame Core
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color.White, Color(0xFFFFF9C4), Color(0xFFFFB300), Color(0xFFFF6D00)),
        center = Offset(candleX, candleY - 12f),
        radius = 16f
      ),
      topLeft = Offset(candleX - 10f, candleY - 20f),
      size = Size(20f, 26f)
    )
  }
}

private fun DrawScope.drawProposalRingBox(center: Offset, size: Float, enableBloom: Boolean) {
  // Velvet box with rounded bevels
  drawRoundRect(
    brush = Brush.radialGradient(
      colors = listOf(Color(0xFFFF4081), Color(0xFFC2185B), Color(0xFF880E4F)),
      center = center,
      radius = size * 0.8f
    ),
    topLeft = Offset(center.x - size * 0.5f, center.y - size * 0.5f),
    size = Size(size, size * 0.9f),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Sparkling Diamond with Bloom Pass
  val diamondCenter = Offset(center.x, center.y - size * 0.1f)
  if (enableBloom) {
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x99E0F7FA), Color(0x4480D8FF), Color.Transparent),
        center = diamondCenter,
        radius = size * 0.55f
      ),
      radius = size * 0.55f,
      center = diamondCenter,
      blendMode = BlendMode.Screen
    )
    // Diamond anamorphic cross glint
    val glint = size * 0.45f
    drawLine(
      color = Color.White.copy(alpha = 0.9f),
      start = Offset(diamondCenter.x - glint, diamondCenter.y),
      end = Offset(diamondCenter.x + glint, diamondCenter.y),
      strokeWidth = 2f
    )
    drawLine(
      color = Color.White.copy(alpha = 0.9f),
      start = Offset(diamondCenter.x, diamondCenter.y - glint),
      end = Offset(diamondCenter.x, diamondCenter.y + glint),
      strokeWidth = 2f
    )
  }

  drawCircle(
    color = Color.White,
    radius = size * 0.22f,
    center = diamondCenter
  )
  drawCircle(
    color = Color(0xFF80D8FF),
    radius = size * 0.12f,
    center = diamondCenter
  )
}

private fun DrawScope.drawMiniLoveEnvelope(center: Offset, size: Float, enableBloom: Boolean) {
  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFFFDF9), Color(0xFFFFE4EC), Color(0xFFF8BBD0))
    ),
    topLeft = Offset(center.x - size * 0.5f, center.y - size * 0.35f),
    size = Size(size, size * 0.7f),
    cornerRadius = CornerRadius(12f, 12f)
  )
  drawGlowingHeart(
    center = center,
    size = size * 0.35f,
    showGlow = enableBloom,
    enableBloom = enableBloom,
    bloomMultiplier = 1.0f,
    enableAO = false
  )
}

private fun DrawScope.drawMiniGiftBox(
  center: Offset,
  size: Float,
  enableBloom: Boolean,
  enableAO: Boolean
) {
  if (enableAO) {
    drawOval(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x40331018), Color.Transparent),
        center = Offset(center.x, center.y + size * 0.52f),
        radius = size * 0.6f
      ),
      topLeft = Offset(center.x - size * 0.55f, center.y + size * 0.45f),
      size = Size(size * 1.1f, 18f),
      blendMode = BlendMode.Multiply
    )
  }

  drawRoundRect(
    brush = Brush.verticalGradient(
      colors = listOf(Color(0xFFFF9EB5), Color(0xFFFF4081), Color(0xFFD81B60))
    ),
    topLeft = Offset(center.x - size * 0.5f, center.y - size * 0.5f),
    size = Size(size, size),
    cornerRadius = CornerRadius(14f, 14f)
  )

  // Golden ribbon
  drawLine(
    color = Color(0xFFFFD54F),
    start = Offset(center.x, center.y - size * 0.5f),
    end = Offset(center.x, center.y + size * 0.5f),
    strokeWidth = 14f
  )
  drawLine(
    color = Color(0xFFFFD54F),
    start = Offset(center.x - size * 0.5f, center.y),
    end = Offset(center.x + size * 0.5f, center.y),
    strokeWidth = 14f
  )

  if (enableBloom) {
    // Golden center knot bloom
    drawCircle(
      brush = Brush.radialGradient(
        colors = listOf(Color(0x99FFF9C4), Color(0x33FFD54F), Color.Transparent),
        center = center,
        radius = 24f
      ),
      radius = 24f,
      center = center,
      blendMode = BlendMode.Screen
    )
  }
}

private fun DrawScope.drawMiniConfetti(w: Float, h: Float, enableBloom: Boolean) {
  val confettiColors = listOf(Color(0xFFFF4081), Color(0xFFFFD54F), Color(0xFF69F0AE), Color(0xFF40C4FF), Color(0xFFE040FB))
  for (i in 0..20) {
    val cx = (w * (0.08f + (i * 0.046f) % 0.84f))
    val cy = (h * (0.12f + ((i * 37) % 70) * 0.01f))
    val rad = 5f + (i % 4)

    if (enableBloom && i % 3 == 0) {
      drawCircle(
        color = confettiColors[i % confettiColors.size].copy(alpha = 0.4f),
        radius = rad * 2.2f,
        center = Offset(cx, cy),
        blendMode = BlendMode.Screen
      )
    }

    drawCircle(
      color = confettiColors[i % confettiColors.size],
      radius = rad,
      center = Offset(cx, cy)
    )
  }
}

private fun DrawScope.drawAtmosphericBloomVeil(
  w: Float,
  h: Float,
  mood: EnvironmentMood,
  bloomIntensity: Float
) {
  // Soft camera lens vignette and gentle pastel color grade
  drawOval(
    brush = Brush.radialGradient(
      colors = listOf(
        Color.Transparent,
        Color.Transparent,
        Color(0x184A1C28).copy(alpha = 0.12f * bloomIntensity)
      ),
      center = Offset(w * 0.5f, h * 0.5f),
      radius = w * 0.72f
    ),
    topLeft = Offset.Zero,
    size = Size(w, h)
  )
}
