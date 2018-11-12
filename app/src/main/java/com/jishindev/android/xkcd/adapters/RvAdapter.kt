package com.jishindev.android.xkcd.adapters

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.signature.ObjectKey
import com.jishindev.android.xkcd.ComicDetailActivity
import com.jishindev.android.xkcd.R
import com.jishindev.android.xkcd.models.Comic
import com.jishindev.android.xkcd.utils.GlideApp
import com.jishindev.android.xkcd.utils.getSingleNativeAd
import kotlinx.android.synthetic.main.rv_item.view.*
import kotlinx.android.synthetic.main.rv_item_native_ad.view.*
import kotlinx.android.synthetic.main.rv_item_progress_footer.view.*


class RvAdapter(private val comicsList: ArrayList<Comic> = arrayListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val loadingComic by lazy { Comic.getEmptyComic() }

    fun addAllComics(cl: ArrayList<Comic>) {
        Log.d(TAG, "addAllComics() called with: cl = ${cl.map { it.title }}")

        Log.i(
            TAG,
            "addAllComics: itemCount: $itemCount, lastComic viewType:${if (itemCount > 0) "${comicsList.last().viewType}" else "empty list"}"
        )
        // remove loading view if its present
        if (itemCount > 0 && comicsList.last().viewType == TYPE_LOADING) {
            comicsList.removeAt(itemCount - 1)
            Log.i(TAG, "addAllComics: removed loading view")
        }

        val start = itemCount
        comicsList.addAll(cl)

        // add the ad
        comicsList.add(Comic.getAdComic())

        // add the loading view back
        comicsList.add(loadingComic)

        val end = comicsList.size - 1
        Log.i(TAG, "addAllComics: start: $start, end: $end")
        notifyItemRangeInserted(start, end)
    }

    fun showLoading(show: Boolean) {
        if (itemCount > 0 && comicsList.last().viewType == TYPE_LOADING) {
            comicsList.last().visible = show
            notifyItemChanged(comicsList.size - 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_COMIC -> ItemVH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.rv_item,
                    parent,
                    false
                )
            )
            TYPE_LOADING -> LoadingVH(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.rv_item_progress_footer,
                    parent,
                    false
                )
            )
            else -> AdVH(LayoutInflater.from(parent.context).inflate(R.layout.rv_item_native_ad, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return comicsList.size
    }

    override fun getItemViewType(position: Int): Int {
        return comicsList[position].viewType
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemVH -> holder.bind(comicsList[position])
            is LoadingVH -> holder.bind(comicsList[position])
            is AdVH -> holder.bind()
        }
    }

    companion object {
        private const val TAG = "RvAdapter"
        const val TYPE_COMIC = 1
        const val TYPE_LOADING = 2
        const val TYPE_AD = 3
    }

    class ItemVH(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(comic: Comic) {
            // Log.d(TAG, "bind() called with: comic = [$comic]")
            with(itemView) {
                tvTitle?.text = comic.title
                tvDate.text = comic.date
                GlideApp.with(itemView)
                    .load(comic.img)
                    .signature(ObjectKey(comic.img))
                    .transition(withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(ivComic)
                    .waitForLayout()
                    .getSize { width, height ->
                        comic.width = width
                        comic.height = height
                    }

                setOnClickListener {
                    val intent = Intent(context, ComicDetailActivity::class.java)
                    intent.putExtra(ComicDetailActivity.ARG_COMIC, comic)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        context as Activity,
                        ivComic as View,
                        "comic"
                    )
                    context.startActivity(intent, options.toBundle())
                }
            }
        }
    }

    class LoadingVH(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(comic: Comic) {
            with(itemView) {
                progressBar?.visibility = if (comic.visible) View.VISIBLE else View.GONE
            }
        }
    }

    class AdVH(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.context.getSingleNativeAd(itemView) { nativeAdUnit ->
                with(itemView) {
                    llAd?.visibility = View.VISIBLE
                    tvAdTitle?.text = nativeAdUnit.title
                    tvDesc?.text = nativeAdUnit.summary
                    GlideApp.with(itemView)
                        .load(nativeAdUnit.mainImage)
                        .signature(ObjectKey(nativeAdUnit.mainImage!!))
                        .transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(ivMainImage)
                        .waitForLayout()
                }
            }
        }
    }
}