package com.jishindev.android.xkcd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jishindev.android.xkcd.adapters.VpAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProviders.of(this).get(MainVM::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        toolbar?.title = "XKCD Reader"
        setSupportActionBar(toolbar)

        initViews()
        viewModel.loadComics()
    }

    private fun initViews() {
        val fragmentsMap = mapOf(
            "List" to MainFragment.newInstance(false),
            "Grid" to MainFragment.newInstance(true)
        )
        viewPager?.adapter = VpAdapter(supportFragmentManager, fragmentsMap)
        tabLayout?.setupWithViewPager(viewPager)
    }
}
