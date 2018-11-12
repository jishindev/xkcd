package com.jishindev.android.xkcd.models

import android.os.Parcelable
import com.adsnative.ads.NativeAdUnit
import com.google.gson.Gson
import com.jishindev.android.xkcd.adapters.RvAdapter
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Comic(
    val num: Int,
    val title: String,
    val day: String,
    val month: String,
    val year: String,
    val img: String,
    val alt: String,
    var width: Int = 0,
    var height: Int = 0
) : Parcelable {

    // For empty object - loading view
    companion object {
        fun getEmptyComic() = Comic(
            -1,
            "Lorem Ipsum",
            "31",
            "12",
            "1999",
            "https://imgs.xkcd.com/comics/understocked.png",
            "Lorem ipsum Lorem ipsum"
        ).apply {
            viewType = RvAdapter.TYPE_LOADING
            visible = false
        }

        fun getAdComic(): Comic = getEmptyComic().apply { viewType = RvAdapter.TYPE_AD }
    }


    @IgnoredOnParcel
    var viewType: Int = RvAdapter.TYPE_COMIC
    @IgnoredOnParcel
    var visible: Boolean = true


    private val calMonth get() = Calendar.getInstance().apply { set(Calendar.MONTH, month.toInt()) }
    private val sdf get() = SimpleDateFormat("MMM", Locale.US)
    val date: String get() = "$day ${sdf.format(calMonth.time)} $year"

    override fun toString(): String {
        return Gson().toJson(this)
    }
}