package com.example.ui.screens.gifts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.FloatingParticles

data class VirtualGiftItem(
  val id: Int,
  val name: String,
  val emoji: String,
  val tag: String,
  val gradient: List<Color>,
  val message: String
)

val AvailableVirtualGifts = listOf(
  VirtualGiftItem(
    0,
    "Enchanted Rose Dome",
    "🌹",
    "Eternal Beauty",
    listOf(Color(0xFFFB7185), Color(0xFFE11D48)),
    "An eternal scarlet rose under starry glass, symbolizing timeless affection and tender care for you!"
  ),
  VirtualGiftItem(
    1,
    "Deluxe Choco-Strawberry",
    "🍓",
    "Sweet Indulgence",
    listOf(Color(0xFFF472B6), Color(0xFFDB2777)),
    "Hand-dipped Belgian strawberries wrapped in golden foil — because your sweetness is unmatched!"
  ),
  VirtualGiftItem(
    2,
    "Fluffy Companion Bear",
    "🧸",
    "Warm Cuddles",
    listOf(Color(0xFFFBBF24), Color(0xFFD97706)),
    "A plush, cuddly teddy bear who is always ready to hug you whenever you need extra warmth and comfort!"
  ),
  VirtualGiftItem(
    3,
    "Crystal Heart Pendant",
    "💎",
    "Pure Devotion",
    listOf(Color(0xFF818CF8), Color(0xFF4F46E5)),
    "A shimmering crystal heart capturing magical iridescent light, crafted just for you to wear close to your heart!"
  ),
  VirtualGiftItem(
    4,
    "Helium Heart Bouquet",
    "🎈",
    "Birthday Cheers",
    listOf(Color(0xFFC084FC), Color(0xFF9333EA)),
    "Floating pink and lavender heart balloons dancing in the soft breeze, carrying all our heartfelt wishes!"
  ),
  VirtualGiftItem(
    5,
    "Golden Treasure Box",
    "✨",
    "Special Surprise",
    listOf(Color(0xFFFFD54F), Color(0xFFFFA000)),
    "An ornate golden treasure chest packed with infinite romantic memories, sweet promises, and laughs!"
  )
)

@Composable
fun VirtualGiftsScreen(
  recipientName: String,
  onSoundOpen: () -> Unit,
  onBack: () -> Unit,
  reducedMotion: Boolean = false
) {
  val openedGiftIds = remember { mutableStateListOf<Int>() }
  var activeModalGift by remember { mutableStateOf<VirtualGiftItem?>(null) }

  fun openGift(gift: VirtualGiftItem) {
    if (!openedGiftIds.contains(gift.id)) {
      openedGiftIds.add(gift.id)
    }
    activeModalGift = gift
    onSoundOpen()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFFFFF7ED), Color(0xFFFEF3C7), Color(0xFFFFF0F5))
        )
      )
  ) {
    FloatingParticles(reducedMotion = reducedMotion)

    Column(
      modifier = Modifier
        .fillMaxSize()
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
            text = "Opened: ${openedGiftIds.size} / ${AvailableVirtualGifts.size} 🎁",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFFD97706)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Virtual Gifts for $recipientName 🎁",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp,
        color = Color(0xFF92400E)
      )

      Text(
        text = "Tap any gift box to unwrap the 3D surprise inside!",
        fontSize = 12.sp,
        color = Color(0xFFB45309)
      )

      Spacer(modifier = Modifier.height(18.dp))

      // 2-column grid of gifts
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
        modifier = Modifier.weight(1f)
      ) {
        itemsIndexed(AvailableVirtualGifts) { index, gift ->
          val isOpened = openedGiftIds.contains(gift.id)

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(0.95f)
              .clip(RoundedCornerShape(22.dp))
              .clickable { openGift(gift) }
              .testTag("virtual_gift_card_$index"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
          ) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.SpaceBetween
            ) {
              Box(
                modifier = Modifier
                  .size(62.dp)
                  .clip(RoundedCornerShape(18.dp))
                  .background(Brush.linearGradient(gift.gradient)),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = if (isOpened) gift.emoji else "🎁",
                  fontSize = 32.sp
                )
              }

              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = gift.name,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color(0xFF451A03),
                  textAlign = TextAlign.Center,
                  maxLines = 1
                )
                Text(
                  text = if (isOpened) "Revealed ✨" else "Tap to Open 🎀",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = if (isOpened) Color(0xFF059669) else Color(0xFFD97706)
                )
              }
            }
          }
        }
      }
    }

    // Modal Reveal Popup
    if (activeModalGift != null) {
      val gift = activeModalGift!!

      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.55f))
          .clickable { activeModalGift = null },
        contentAlignment = Alignment.Center
      ) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(28.dp)
            .clickable(enabled = false) {},
          shape = RoundedCornerShape(32.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              IconButton(onClick = { activeModalGift = null }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
              }
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(20.dp))
            ) {
              Image(
                painter = painterResource(id = R.drawable.img_gift_surprise),
                contentDescription = "3D Gift Surprise",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
              )
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .align(Alignment.Center)
                  .shadow(6.dp, CircleShape)
                  .clip(CircleShape)
                  .background(Brush.radialGradient(gift.gradient)),
                contentAlignment = Alignment.Center
              ) {
                Text(text = gift.emoji, fontSize = 28.sp)
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
              text = gift.name,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = Color(0xFF451A03),
              textAlign = TextAlign.Center
            )

            Text(
              text = gift.tag,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFD97706),
              modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = gift.message,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center,
              lineHeight = 22.sp,
              modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
              onClick = { activeModalGift = null },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("gift_modal_collect_button"),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
              shape = RoundedCornerShape(24.dp)
            ) {
              Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Collect Gift 💖", fontWeight = FontWeight.Bold, color = Color.White)
            }
          }
        }
      }
    }
  }
}
