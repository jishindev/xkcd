package com.jishindev.android.xkcd

import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.signature.ObjectKey
import com.jishindev.android.xkcd.models.Comic
import com.jishindev.android.xkcd.utils.GlideApp
import kotlinx.android.synthetic.main.activity_comic_detail.*


class ComicDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comic_detail)

        window.sharedElementEnterTransition.duration = 200
        window.sharedElementReturnTransition.setDuration(200).interpolator = DecelerateInterpolator()

        val comic = intent.getParcelableExtra<Comic>(ARG_COMIC)
        initView(comic)
    }

    private fun initView(comic: Comic?) {
        comic?.run {
            toolbar?.title = title
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            GlideApp.with(this@ComicDetailActivity)
                .load(comic.img)
                .signature(ObjectKey(comic.img))
                .transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(ivComic)
                .waitForLayout()

            tvAltText?.text = comic.alt
            tvDate?.text = "Posted on ${comic.date}"

            ivComic?.setOnClickListener {
                tvAltText?.visibility = View.VISIBLE
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        supportFinishAfterTransition()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val ARG_COMIC = "comic"
    }
}
