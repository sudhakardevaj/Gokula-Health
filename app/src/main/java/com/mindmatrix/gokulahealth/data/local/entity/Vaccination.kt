package com.mindmatrix.gokulahealth.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vaccination",
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
data class Vaccination(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cattleId: Int,
    val vaccineName: String,
    val dateGiven: String,
    val nextDueDate: String,
    val notes: String
)
