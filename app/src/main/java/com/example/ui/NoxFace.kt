package com.example.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlinx.coroutines.delay

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.alpha
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.EaseInOutSine

data class ZParticle(
    val id: String,
    val offsetX: Float
)

@Composable
fun ZParticleAnim(particle: ZParticle, onRemove: (String) -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
        delay(2500)
        isVisible = false
        delay(1000)
        onRemove(particle.id)
    }

    val yOffset by animateFloatAsState(
        targetValue = if (isVisible) -150f else -250f,
        animationSpec = tween(3500, easing = LinearOutSlowInEasing)
    )
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(1000)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
    ) {
        Text(
            text = "Z",
            color = com.example.ui.theme.EmeraldNeon,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(
                    x = particle.offsetX.dp,
                    y = yOffset.dp
                )
        )
    }
}

@Composable
fun NoxFace(
    expression: NoxExpression,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 15f,
        targetValue = 25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    var isBlinking by remember { mutableStateOf(false) }
    var lookOffsetX by remember { mutableStateOf(0f) }
    var lookOffsetY by remember { mutableStateOf(0f) }
    var bounceY by remember { mutableStateOf(0f) }
    
    val textMeasurer = rememberTextMeasurer()

    LaunchedEffect(Unit) {
        while (true) {
            delay((4000L..8000L).random())
            if (expression != NoxExpression.Warning && expression != NoxExpression.Thinking) {
                isBlinking = true
                delay(150L)
                isBlinking = false
            }
        }
    }
    
    LaunchedEffect(expression) {
        when (expression) {
            NoxExpression.Normal -> {
                while (true) {
                    lookOffsetX = (-15..15).random().toFloat()
                    lookOffsetY = (-10..10).random().toFloat()
                    delay((1000L..2000L).random())
                    lookOffsetX = 0f
                    lookOffsetY = 0f
                    delay((1000L..2000L).random())
                }
            }
            NoxExpression.IncomeSuccess, NoxExpression.TargetAchieved -> {
                bounceY = -20f
                delay(150)
                bounceY = 0f
            }
            NoxExpression.Warning -> {
                isBlinking = true
                delay(150)
                isBlinking = false
                delay(150)
                isBlinking = true
                delay(150)
                isBlinking = false
            }
            NoxExpression.Thinking -> {
                lookOffsetX = -20f
                delay(300)
                isBlinking = true
                delay(150)
                isBlinking = false
                lookOffsetX = 20f
                delay(300)
                lookOffsetX = -20f
                delay(300)
                lookOffsetX = 0f
            }
            else -> {}
        }
    }

    val eyeHeightTarget = if (isBlinking) 2f else when (expression) {
        NoxExpression.Happy, NoxExpression.IncomeSuccess, NoxExpression.TargetAchieved, NoxExpression.Warning -> 64f
        NoxExpression.Sad -> 56f
        NoxExpression.Angry -> 48f
        NoxExpression.Danger -> 16f
        NoxExpression.Surprised -> 72f
        NoxExpression.Sleepy -> 32f
        NoxExpression.Yawning -> 8f
        NoxExpression.Sleep -> 4f
        else -> 64f
    }
    
    val eyeWidthTarget = when (expression) {
        NoxExpression.Surprised -> 64f
        NoxExpression.Sleep -> 32f
        else -> 56f
    }

    val browRotationTarget = when (expression) {
        NoxExpression.Happy, NoxExpression.IncomeSuccess, NoxExpression.TargetAchieved -> -10f
        NoxExpression.Sad -> 20f
        NoxExpression.Angry, NoxExpression.Warning -> -25f
        NoxExpression.Danger -> -35f
        NoxExpression.Thinking -> 10f
        NoxExpression.Surprised -> -15f
        else -> 0f
    }
    
    val browYOffsetTarget = when(expression) {
        NoxExpression.Happy, NoxExpression.IncomeSuccess, NoxExpression.TargetAchieved -> -15f
        NoxExpression.Angry, NoxExpression.Warning -> 10f
        NoxExpression.Danger -> 15f
        NoxExpression.ExpenseSuccess -> 5f
        NoxExpression.Surprised -> -20f
        NoxExpression.Sleepy -> 5f
        NoxExpression.Yawning -> -10f
        NoxExpression.Sleep -> 10f
        else -> 0f
    }

    val glowColorTarget = when (expression) {
        NoxExpression.Angry, NoxExpression.Warning, NoxExpression.Danger -> com.example.ui.theme.RedNeon
        NoxExpression.Sad -> com.example.ui.theme.AmberNeon
        else -> com.example.ui.theme.EmeraldNeon
    }
    
    val glowIntensityTarget = when(expression) {
        NoxExpression.Happy, NoxExpression.IncomeSuccess -> pulseGlow + 15f
        NoxExpression.TargetAchieved -> pulseGlow + 25f
        NoxExpression.Angry, NoxExpression.Warning -> pulseGlow + 20f
        NoxExpression.Danger -> pulseGlow + 30f
        NoxExpression.ExpenseSuccess -> pulseGlow * 0.5f
        NoxExpression.Sleepy -> pulseGlow * 0.85f
        NoxExpression.Yawning -> pulseGlow * 0.70f
        NoxExpression.Sleep -> pulseGlow * 0.30f
        else -> pulseGlow
    }

    val eyeSymbol = when (expression) {
        NoxExpression.IncomeSuccess -> "$"
        NoxExpression.ExpenseSuccess -> "-"
        NoxExpression.TargetAchieved -> "★"
        NoxExpression.Warning -> "!"
        else -> null
    }

    val eyeHeight by animateFloatAsState(targetValue = eyeHeightTarget, animationSpec = tween(300))
    val eyeWidth by animateFloatAsState(targetValue = eyeWidthTarget, animationSpec = tween(300))
    val browRotation by animateFloatAsState(targetValue = browRotationTarget, animationSpec = tween(300))
    val browYOffset by animateFloatAsState(targetValue = browYOffsetTarget, animationSpec = tween(300))
    val lookX by animateFloatAsState(targetValue = lookOffsetX, animationSpec = tween(500))
    val lookY by animateFloatAsState(targetValue = lookOffsetY, animationSpec = tween(500))
    val bounceAnim by animateFloatAsState(targetValue = bounceY, animationSpec = tween(150, easing = FastOutSlowInEasing))
    
    val idleBounce by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )

    val sleepAnimProgress by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    var zParticles by remember { mutableStateOf(listOf<ZParticle>()) }
    LaunchedEffect(expression) {
        if (expression == NoxExpression.Sleep) {
            while (true) {
                delay((4000L..6000L).random())
                zParticles = zParticles + ZParticle(
                    id = java.util.UUID.randomUUID().toString(),
                    offsetX = (-40..40).random().toFloat()
                )
            }
        } else {
            zParticles = emptyList()
        }
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            val eyeSpacing = 56f
            
            val animLookX = lookX + if (expression == NoxExpression.Sleep) sleepAnimProgress else 0f
            val animLookY = lookY + if (expression != NoxExpression.Sleep) idleBounce else 0f + bounceAnim
            
            val leftEyeCenter = Offset(centerX - eyeSpacing + animLookX, centerY + animLookY)
            val rightEyeCenter = Offset(centerX + eyeSpacing + animLookX, centerY + animLookY)
            
            // Draw Left Eye
            drawEyeAndBrow(
                center = leftEyeCenter,
                eyeWidth = eyeWidth,
                eyeHeight = eyeHeight,
                browRotation = browRotation - 3f, // default inward rotation
                browYOffset = browYOffset,
                glowColor = glowColorTarget,
                glowRadius = glowIntensityTarget,
                isLeft = true,
                eyeSymbol = eyeSymbol,
                textMeasurer = textMeasurer
            )
            
            // Draw Right Eye
            drawEyeAndBrow(
                center = rightEyeCenter,
                eyeWidth = eyeWidth,
                eyeHeight = eyeHeight,
                browRotation = -browRotation + 3f, // Mirrored rotation for right brow
                browYOffset = browYOffset,
                glowColor = glowColorTarget,
                glowRadius = glowIntensityTarget,
                isLeft = false,
                eyeSymbol = eyeSymbol,
                textMeasurer = textMeasurer
            )
        }
        
        zParticles.forEach { particle ->
            ZParticleAnim(particle = particle, onRemove = { id ->
                zParticles = zParticles.filter { it.id != id }
            })
        }
    }
}

fun DrawScope.drawEyeAndBrow(
    center: Offset,
    eyeWidth: Float,
    eyeHeight: Float,
    browRotation: Float,
    browYOffset: Float,
    glowColor: Color,
    glowRadius: Float,
    isLeft: Boolean,
    eyeSymbol: String?,
    textMeasurer: androidx.compose.ui.text.TextMeasurer
) {
    // We simulate glow by drawing multiple semi-transparent rounded rectangles behind
    for (i in 3 downTo 1) {
        val alpha = (0.15f / i)
        val radius = glowRadius * i
        drawRoundRect(
            color = glowColor.copy(alpha = alpha),
            topLeft = Offset(center.x - eyeWidth / 2 - radius, center.y - eyeHeight / 2 - radius),
            size = Size(eyeWidth + radius * 2, eyeHeight + radius * 2),
            cornerRadius = CornerRadius(16f + radius / 2, 16f + radius / 2)
        )
    }

    if (eyeSymbol != null) {
        val style = TextStyle(
            color = glowColor,
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        val measuredText = textMeasurer.measure(
            text = eyeSymbol,
            style = style
        )
        drawText(
            textLayoutResult = measuredText,
            topLeft = Offset(center.x - measuredText.size.width / 2f, center.y - measuredText.size.height / 2f)
        )
    } else {
        // Draw the core solid eye
        drawRoundRect(
            color = glowColor, // It's #00FF9C in design, not white! But design HTML says bg-[#00FF9C] and white inside.
            topLeft = Offset(center.x - eyeWidth / 2, center.y - eyeHeight / 2),
            size = Size(eyeWidth, eyeHeight),
            cornerRadius = CornerRadius(12f, 12f)
        )
        
        // Eye inner white blur spot
        drawRoundRect(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(center.x - eyeWidth / 2 + 8f, center.y - eyeHeight / 2 + 8f),
            size = Size(16f, 16f),
            cornerRadius = CornerRadius(8f, 8f)
        )
    }

    // Brow
    val browWidth = 40f
    val browHeight = 3f
    val browBaseY = center.y - eyeHeight / 2 - 16f + browYOffset
    
    withTransform({
        rotate(browRotation, pivot = Offset(center.x, browBaseY))
    }) {
        // Brow glow
        for (i in 2 downTo 1) {
            drawRoundRect(
                color = glowColor.copy(alpha = 0.2f / i),
                topLeft = Offset(center.x - browWidth / 2 - glowRadius * i / 2, browBaseY - browHeight / 2 - glowRadius * i / 2),
                size = Size(browWidth + glowRadius * i, browHeight + glowRadius * i),
                cornerRadius = CornerRadius(1.5f, 1.5f)
            )
        }
        
        drawRoundRect(
            color = glowColor,
            topLeft = Offset(center.x - browWidth / 2, browBaseY - browHeight / 2),
            size = Size(browWidth, browHeight),
            cornerRadius = CornerRadius(1.5f, 1.5f)
        )
    }
}
