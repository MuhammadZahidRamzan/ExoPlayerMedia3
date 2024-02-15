package com.example.sleepmeditation.features.audio.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Audio(
    val id: String?,
    val title: String?,
    val album: String?,
    val artist: String?,
    val duration: Long? = 0,
    val size: String?,
    val path: String?,
    val artUri: Uri?
):Parcelable