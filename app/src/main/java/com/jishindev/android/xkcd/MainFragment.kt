package com.jishindev.android.xkcd

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adsnative.ads.ANAdViewBinder
import com.adsnative.ads.ANRecyclerAdapter
import com.jishindev.android.xkcd.adapters.RvAdapter
import com.jishindev.android.xkcd.utils.isConnected
import kotlinx.android.synthetic.main.fragment_main.*
import com.adsnative.ads.ANAdPositions


class MainFragment : Fragment() {

    private var mainVM: MainVM? = null
    private lateinit var rvAdapter: RvAdapter
    //private lateinit var adsRvAdapter :ANRecyclerAdapter
    private var isLoading = false
    private val loadThreshold = 5

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainVM = (activity as? MainActivity)?.viewModel

        if (context?.isConnected() == true) {
            tvStatus?.visibility = View.GONE
            initViews()
            observeForComics()
        } else {
            tvStatus?.visibility = View.VISIBLE
        }
    }

    private fun initViews() {
        rvComics?.setHasFixedSize(false)
        val isGrid = arguments?.get(ARG_IS_GRID) as? Boolean ?: false
        rvAdapter = RvAdapter()

        val lm: RecyclerView.LayoutManager = when {
            isGrid -> {
                GridLayoutManager(context, 3, RecyclerView.VERTICAL, false).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (rvAdapter.getItemViewType(position)) {
                                RvAdapter.TYPE_COMIC -> 1
                                else -> 3
                            }
                        }
                    }
                }
            }
            else -> LinearLayoutManager(context)
        }
        rvComics?.layoutManager = lm

       /* val serverPositions = ANAdPositions.serverPositioning()
        adsRvAdapter = ANRecyclerAdapter(context!!, rvAdapter, "2Pwo1otj1C5T8y6Uuz9v-xbY1aB09x8rWKvsJ-HI",serverPositions)

        val anAdViewBinder = ANAdViewBinder.Builder(R.layout.rv_item_native_ad)
            .bindTitle(R.id.tvTitle)
            .bindSummary(R.id.tvDesc)
            //.bindIconImage(R.id.<iconImage>)
            .bindMainImage(R.id.ivMainImage)
            .bindCallToAction(R.id.llAd)
            //.bindPromotedBy(R.id.<promotedBy>)
            .build()
        adsRvAdapter.registerViewBinder(anAdViewBinder)*/
        rvAdapter.showLoading(true)
        rvComics?.adapter = rvAdapter

        rvComics?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = lm.itemCount
                val lastVisibleItem = (lm as LinearLayoutManager).findLastVisibleItemPosition()
                if (!isLoading && totalItemCount <= (lastVisibleItem + loadThreshold)) {
                    mainVM?.loadComics()
                    isLoading = true
                    rvAdapter.showLoading(true)
                }
            }
        })
        progressBar?.visibility = View.VISIBLE
    }

/*    override fun onResume() {
        super.onResume()
        adsRvAdapter.loadAds()
    }*/

    private fun observeForComics() {
        mainVM?.ldComicsList?.observe(this, Observer {
            Log.d(TAG, "observeForComics: ldComicsList: $it")

            rvAdapter.showLoading(false)
            isLoading = false
            progressBar?.visibility = View.GONE

            if (it != null && it.isNotEmpty())
                rvAdapter.addAllComics(it)
            else
                Toast.makeText(context, "That's all for today.", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    companion object {
        private const val TAG = "MainFragment"
        private const val ARG_IS_GRID = "com.jishindev.android.xkcd.ARG_IS_GRID"

        fun newInstance(isGrid: Boolean): Fragment {
            MainFragment().apply {
                arguments = bundleOf(ARG_IS_GRID to isGrid)
            }.also { return it }
        }
    }
}
