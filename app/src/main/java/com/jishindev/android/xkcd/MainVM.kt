package com.jishindev.android.xkcd

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jishindev.android.xkcd.adapters.RvAdapter
import com.jishindev.android.xkcd.models.Comic
import com.jishindev.android.xkcd.network.XkcdApi
import kotlinx.coroutines.*

class MainVM : ViewModel() {

    private val api = XkcdApi.getApi()
    private var currentIndex = 1
    var ldComicsList = MutableLiveData<ArrayList<Comic>>()
    var job: Job? = null

    fun loadComics(nextCount: Int = 10){
        Log.d(TAG, "loadComics() called with: nextCount = [$nextCount]")
        job?.cancel()
        job = GlobalScope.launch {
            val comics = (0 until nextCount).map {
                async(Dispatchers.Default) { api.getComic(currentIndex++).await() }
            }.mapNotNull {
                it.await().body()?.apply {
                    visible=true
                    viewType=RvAdapter.TYPE_COMIC
                }
            }.toList() as? ArrayList<Comic>

            Log.d(TAG, "loadComics: comics: $comics")
            ldComicsList.postValue(comics)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared() called")
        job?.cancel()
    }

    companion object {
        private const val TAG = "MainVM"
    }
}
