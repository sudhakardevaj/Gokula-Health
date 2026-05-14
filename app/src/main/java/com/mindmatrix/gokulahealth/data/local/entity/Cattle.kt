package com.mindmatrix.gokulahealth.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cattle")
data class Cattle(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val earTagId: String,
    val name: String,
    val breed: String,
    val photoUri: String,
    val age: Int,
    val notes: String,
    val gender: String = "Female" // ✅ FIXED: Was collected in UI but never saved to DB
)