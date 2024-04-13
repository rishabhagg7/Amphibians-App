package com.example.amphibians.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Amphibian(
    val description: String,
    @SerialName("img_src")
    val imgSrc: String,
    val name: String,
    val type: String
)