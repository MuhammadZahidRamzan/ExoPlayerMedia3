package com.example.sleepmeditation.utils

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.view.View

object Extensions {
    inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key)
                as? T
    }

    inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelable(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelable(key)
                as? T
    }
    fun View.gone(){
        this.visibility = View.GONE
    }

    fun View.visible(){
        this.visibility = View.VISIBLE
    }

    fun View.invisible(){
        this.visibility = View.INVISIBLE
    }
}