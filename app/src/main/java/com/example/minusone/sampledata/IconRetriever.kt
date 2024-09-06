package com.example.minusone.sampledata

import android.graphics.drawable.Drawable
import com.example.minusone.R


class IconRetriever() {
    fun getAppIcon(packageName: String): Int? {
        when (packageName) {
            "com.google.android.youtube" -> return R.drawable.youtube
            "com.zhiliaoapp.musically" -> return R.drawable.tiktok
            "com.facebook.katana" -> return R.drawable.facebook
            "com.reddit.frontpage" -> return R.drawable.redit
            "com.instagram.android" -> return R.drawable.instagram
            "com.twitter.androidTwitter" -> return R.drawable.twitter
            "com.snapchat.android" -> return R.drawable.snapchat
            "com.pinterest" -> return R.drawable.pinterest
            "com.linkedin.android" -> return R.drawable.linkedin
            "tv.twitch.android.app" -> return R.drawable.twitch
            "com.netflix.mediaclient" -> return R.drawable.netflix
        }
        return null
    }
}