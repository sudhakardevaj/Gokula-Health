package com.mindmatrix.gokulahealth.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "milk_log",
    foreignKeys = [
        ForeignKey(
            entity = Cattle::class,
            parentColumns = ["id"],
            childColumns = ["cattleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cattleId"])]
)
data class MilkLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cattleId: Int,
    val date: String,
    val morningLitres: Float,
    val eveningLitres: Float,
    val totalLitres: Float = morningLitres + eveningLitres
)
