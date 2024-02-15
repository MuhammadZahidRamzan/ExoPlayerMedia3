package com.example.sleepmeditation.utils.managers

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.sleepmeditation.features.audio.domain.model.Audio
import com.za.filemanagerapp.utils.managers.FileManager
import java.io.File
import javax.inject.Inject


class FileManagerImpl @Inject constructor(private val context: Context): FileManager {
    @SuppressLint("Range")
    override fun getAudiosFiles(): List<Audio> {
        val tempList = ArrayList<Audio>()
        val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC"
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL
                )
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
        val cursor = context.contentResolver.query(
            collection,projection,selection
            ,null, sortOrder)
        if (cursor != null){
            if (cursor.moveToNext())
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val sizeC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    try {
                        val file = File(pathC)
                        val artUriC = Uri.fromFile(file)
                        val music = Audio(
                            id = idC,
                            title = titleC,
                            album = albumC,
                            artist = artistC,
                            path = pathC,
                            duration = durationC,
                            artUri = artUriC,
                            size = sizeC
                        )
                        if (file.exists()) tempList.add(music)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }while (cursor.moveToNext())
            cursor.close()
        }
        return tempList
    }
}
