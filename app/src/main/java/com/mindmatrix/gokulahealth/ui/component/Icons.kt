package com.mindmatrix.gokulahealth.ui.component

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

object GokulaIcons {
    val Cow: ImageVector
        get() = ImageVector.Builder(
            name = "Cow",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {

            // ── Left Horn ─────────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(7.5f, 2f)
                lineTo(5f, 2f)
                curveTo(4f, 2f, 3f, 3f, 3.5f, 4.5f)
                lineTo(5.5f, 7f)
                curveTo(6f, 6f, 6.8f, 5.2f, 7.8f, 4.8f)
                close()
            }

            // ── Right Horn ────────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(16.5f, 2f)
                lineTo(19f, 2f)
                curveTo(20f, 2f, 21f, 3f, 20.5f, 4.5f)
                lineTo(18.5f, 7f)
                curveTo(18f, 6f, 17.2f, 5.2f, 16.2f, 4.8f)
                close()
            }

            // ── Left Ear ──────────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(4f, 9f)
                curveTo(2f, 8.5f, 1.5f, 11f, 3.5f, 11.5f)
                curveTo(4.5f, 11.8f, 5.5f, 11f, 6f, 10f)
                curveTo(5.3f, 9.7f, 4.6f, 9.2f, 4f, 9f)
                close()
            }

            // ── Right Ear ─────────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(20f, 9f)
                curveTo(22f, 8.5f, 22.5f, 11f, 20.5f, 11.5f)
                curveTo(19.5f, 11.8f, 18.5f, 11f, 18f, 10f)
                curveTo(18.7f, 9.7f, 19.4f, 9.2f, 20f, 9f)
                close()
            }

            // ── Head ──────────────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 4f)
                curveTo(8.5f, 4f, 6f, 7f, 6f, 10.5f)
                curveTo(6f, 14f, 8.5f, 17f, 12f, 17f)
                curveTo(15.5f, 17f, 18f, 14f, 18f, 10.5f)
                curveTo(18f, 7f, 15.5f, 4f, 12f, 4f)
                close()
            }

            // ── Muzzle White ──────────────────────
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12f, 12f)
                curveTo(10f, 12f, 8.5f, 13f, 8.5f, 14.5f)
                curveTo(8.5f, 16f, 10f, 17f, 12f, 17f)
                curveTo(14f, 17f, 15.5f, 16f, 15.5f, 14.5f)
                curveTo(15.5f, 13f, 14f, 12f, 12f, 12f)
                close()
            }

            // ── Left Nostril ──────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.5f, 14f)
                curveTo(10.5f, 13.4f, 10.9f, 13f, 11.2f, 13f)
                curveTo(11.5f, 13f, 11.7f, 13.4f, 11.7f, 14f)
                curveTo(11.7f, 14.6f, 11.5f, 15f, 11.2f, 15f)
                curveTo(10.9f, 15f, 10.5f, 14.6f, 10.5f, 14f)
                close()
            }

            // ── Right Nostril ─────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.3f, 14f)
                curveTo(12.3f, 13.4f, 12.5f, 13f, 12.8f, 13f)
                curveTo(13.1f, 13f, 13.5f, 13.4f, 13.5f, 14f)
                curveTo(13.5f, 14.6f, 13.1f, 15f, 12.8f, 15f)
                curveTo(12.5f, 15f, 12.3f, 14.6f, 12.3f, 14f)
                close()
            }

            // ── Left Eye White ────────────────────
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9f, 8.5f)
                curveTo(9f, 7.7f, 9.7f, 7f, 10.3f, 7f)
                curveTo(11f, 7f, 11.5f, 7.7f, 11.5f, 8.5f)
                curveTo(11.5f, 9.3f, 11f, 10f, 10.3f, 10f)
                curveTo(9.7f, 10f, 9f, 9.3f, 9f, 8.5f)
                close()
            }

            // ── Left Pupil ────────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9.8f, 8.5f)
                curveTo(9.8f, 8f, 10f, 7.7f, 10.3f, 7.7f)
                curveTo(10.6f, 7.7f, 10.8f, 8f, 10.8f, 8.5f)
                curveTo(10.8f, 9f, 10.6f, 9.3f, 10.3f, 9.3f)
                curveTo(10f, 9.3f, 9.8f, 9f, 9.8f, 8.5f)
                close()
            }

            // ── Right Eye White ───────────────────
            path(
                fill = SolidColor(Color.White),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(12.5f, 8.5f)
                curveTo(12.5f, 7.7f, 13f, 7f, 13.7f, 7f)
                curveTo(14.3f, 7f, 15f, 7.7f, 15f, 8.5f)
                curveTo(15f, 9.3f, 14.3f, 10f, 13.7f, 10f)
                curveTo(13f, 10f, 12.5f, 9.3f, 12.5f, 8.5f)
                close()
            }

            // ── Right Pupil ───────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(13.2f, 8.5f)
                curveTo(13.2f, 8f, 13.4f, 7.7f, 13.7f, 7.7f)
                curveTo(14f, 7.7f, 14.2f, 8f, 14.2f, 8.5f)
                curveTo(14.2f, 9f, 14f, 9.3f, 13.7f, 9.3f)
                curveTo(13.4f, 9.3f, 13.2f, 9f, 13.2f, 8.5f)
                close()
            }

            // ── Neck/Body hint ────────────────────
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(9.5f, 17f)
                lineTo(8.5f, 22f)
                lineTo(15.5f, 22f)
                lineTo(14.5f, 17f)
                close()
            }

        }.build()
}