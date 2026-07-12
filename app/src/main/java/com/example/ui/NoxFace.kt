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

    LaunchedEffect(Unit) {
        while (true) {
            delay((2000L..6000L).random())
            isBlinking = true
            delay(150L)
            isBlinking = false
            
            if (expression == NoxExpression.Normal && (0..2).random() == 0) {
                lookOffsetX = (-15..15).random().toFloat()
                lookOffsetY = (-10..10).random().toFloat()
                delay((1000L..2000L).random())
                lookOffsetX = 0f
                lookOffsetY = 0f
            }
        }
    }

    val eyeHeightTarget = if (isBlinking) 2f else when (expression) {
        NoxExpression.Happy -> 48f
        NoxExpression.Sad -> 56f
        NoxExpression.Angry -> 48f
        NoxExpression.Surprised -> 72f
        NoxExpression.Sleepy -> 32f
        NoxExpression.Yawning -> 8f
        NoxExpression.Sleep -> 2f
        else -> 64f
    }
    
    val eyeWidthTarget = when (expression) {
        NoxExpression.Surprised -> 64f
        else -> 56f
    }

    val browRotationTarget = when (expression) {
        NoxExpression.Happy -> -10f
        NoxExpression.Sad -> 20f
        NoxExpression.Angry -> -25f
        NoxExpression.Thinking -> 10f
        NoxExpression.Surprised -> -15f
        else -> 0f
    }
    
    val browYOffsetTarget = when(expression) {
        NoxExpression.Happy -> -15f
        NoxExpression.Angry -> 10f
        NoxExpression.Surprised -> -20f
        NoxExpression.Sleepy -> 5f
        NoxExpression.Yawning -> -10f
        NoxExpression.Sleep -> 10f
        else -> 0f
    }

    val glowColorTarget = when (expression) {
        NoxExpression.Angry -> com.example.ui.theme.RedNeon
        NoxExpression.Sad -> com.example.ui.theme.AmberNeon
        else -> com.example.ui.theme.EmeraldNeon
    }
    
    val glowIntensityTarget = when(expression) {
        NoxExpression.Happy -> pulseGlow + 15f
        NoxExpression.Angry -> pulseGlow + 20f
        NoxExpression.Sleepy -> pulseGlow * 0.85f
        NoxExpression.Yawning -> pulseGlow * 0.70f
        NoxExpression.Sleep -> pulseGlow * 0.30f
        else -> pulseGlow
    }

    val eyeHeight by animateFloatAsState(targetValue = eyeHeightTarget, animationSpec = tween(300))
    val eyeWidth by animateFloatAsState(targetValue = eyeWidthTarget, animationSpec = tween(300))
    val browRotation by animateFloatAsState(targetValue = browRotationTarget, animationSpec = tween(300))
    val browYOffset by animateFloatAsState(targetValue = browYOffsetTarget, animationSpec = tween(300))
    val lookX by animateFloatAsState(targetValue = lookOffsetX, animationSpec = tween(500))
    val lookY by animateFloatAsState(targetValue = lookOffsetY, animationSpec = tween(500))
    
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
            val leftEyeCenter = Offset(centerX - eyeSpacing + lookX, centerY + lookY)
            val rightEyeCenter = Offset(centerX + eyeSpacing + lookX, centerY + lookY)
            
            // Draw Left Eye
            drawEyeAndBrow(
                center = leftEyeCenter,
                eyeWidth = eyeWidth,
                eyeHeight = eyeHeight,
                browRotation = browRotation - 3f, // default inward rotation
                browYOffset = browYOffset,
                glowColor = glowColorTarget,
                glowRadius = glowIntensityTarget,
                isLeft = true
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
                isLeft = false
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
    isLeft: Boolean
) {
    // We simulate glow by drawing multiple semi-transparent rounded rectangles behind
    for (i in 3 downTo 1) {
        val alpha = (0.15f / i)
        drawRoundRect(
            color = glowColor.copy(alpha = alpha),
            topLeft = Offset(center.x - eyeWidth / 2 - glowRadius * i, center.y - eyeHeight / 2 - glowRadius * i),
            size = Size(eyeWidth + glowRadius * 2 * i, eyeHeight + glowRadius * 2 * i),
            cornerRadius = CornerRadius(12f, 12f)
        )
    }

    // Core eye
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
