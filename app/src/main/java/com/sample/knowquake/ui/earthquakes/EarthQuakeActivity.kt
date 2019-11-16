package com.sample.knowquake.ui.earthquakes

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.sample.knowquake.R
import com.sample.knowquake.databinding.ActivityEarthquakeBinding
import com.sample.knowquake.network.NoNetworkException
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import com.sample.knowquake.customs.EndlessRecyclerOnScrollListener
import com.sample.knowquake.result.EventObserver
import com.sample.knowquake.ui.OnItemClickListener
import com.sample.knowquake.ui.earthquakedetails.EarthQuakeDetailsActivity
import com.sample.knowquake.vo.DataStore
import com.sample.knowquake.vo.EqFeatures
import androidx.work.*
import com.sample.knowquake.job.NewQuakeUpdateJob
import java.util.concurrent.TimeUnit


class EarthQuakeActivity : DaggerAppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, OnItemClickListener {

    companion object {
        private const val LIMIT = 20
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dataStore: DataStore

    @Inject
    lateinit var workerManager: WorkManager

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(EarthQuakeViewModel::class.java)
    }

    private lateinit var binding: ActivityEarthquakeBinding

    private lateinit var earthQuakeAdapter: EarthQuakeAdapter

    private var lastUpdatedList: ArrayList<Any>? = null

    private var currentList: ArrayList<Any>? = null

    private var endlessScroll: EndlessScroll? = null

    private var offset = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_earthquake)
        binding.apply {
            viewModel = this.viewModel
            lifecycleOwner = this@EarthQuakeActivity
            setSupportActionBar(toolBar)
            eqRecyclerView.apply {
                earthQuakeAdapter = EarthQuakeAdapter(this@EarthQuakeActivity)
                setItemViewCacheSize(20)
                adapter = earthQuakeAdapter
                layoutManager = LinearLayoutManager(
                    this@EarthQuakeActivity,
                    RecyclerView.VERTICAL,
                    false
                ).apply { recycleChildrenOnDetach = true }
                addItemDecoration(DividerItemDecoration(this@EarthQuakeActivity, RecyclerView.VERTICAL))
                itemAnimator = null
                endlessScroll = EndlessScroll()
                addOnScrollListener(endlessScroll!!)
            }
            swipeRefreshLayout.setOnRefreshListener(this@EarthQuakeActivity)
        }
        lastUpdatedList = ArrayList()

        viewModel.apply {

            isProgressShown.observe(this@EarthQuakeActivity, EventObserver {
                if (it) {
                    binding.eqProgressBar.visibility = View.VISIBLE
                } else {
                    binding.eqProgressBar.visibility = View.GONE
                }
            })

            earthQuakeError.observe(this@EarthQuakeActivity, Observer {
                binding.eqProgressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
                when (it) {
                    is NoNetworkException -> {
                        Snackbar.make(binding.root, "No Network. Please check your connection", Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            })

            earthQuake.observe(this@EarthQuakeActivity, Observer {
                val size = it.features.size
                if (size > 0) {
                    val newList = it.features
                    lastUpdatedList?.addAll(newList)
                    if (lastUpdatedList?.size!! > 0) {
                        lastUpdatedList?.remove(EarthQuakeAdapter.PROGRESSLOADER)
                    }
                    lastUpdatedList?.add(EarthQuakeAdapter.PROGRESSLOADER)
                    currentList = ArrayList(lastUpdatedList!!)
                    earthQuakeAdapter.setData(currentList!!)
                    binding.eqRecyclerView.visibility = View.VISIBLE
                } else {
                    removeProgressItem(currentList!!)
                }
                binding.swipeRefreshLayout.isRefreshing = false
            })
        }

        val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val orderSyncRequest = PeriodicWorkRequest.Builder(NewQuakeUpdateJob::class.java, 15, TimeUnit.MINUTES)
            .setInputData(Data.Builder().putString("macId", "Testing").build())
            .setConstraints(constraints)
            .addTag("GET_NEW_EARTHQUAKE")
            .build()
        workerManager.enqueue(orderSyncRequest)

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.earthQuakeFeatures(LIMIT, offset, false)
    }

    override fun onRefresh() {
        lastUpdatedList?.clear()
        currentList.let { it?.clear() }
        earthQuakeAdapter.setData(currentList!!)
        offset = 1
        resetEndLessScroll()
        binding.eqRecyclerView.visibility = View.GONE
        viewModel.earthQuakeFeatures(LIMIT, offset, true)
    }

    override fun onItemClick(position: Int) {
        when (currentList!![position]) {
            is EqFeatures -> {
                dataStore.eqFeatures = currentList!![position] as EqFeatures
                startActivity(Intent(this, EarthQuakeDetailsActivity::class.java))
            }
        }
    }

    private fun removeProgressItem(lists: ArrayList<Any>) {
        if (lists.size > 0 && lists.contains(EarthQuakeAdapter.PROGRESSLOADER)) {
            val position = lists.size
            lists.removeAt(position - 1)
            earthQuakeAdapter.notifyItemRemoved(position - 1)
        }
    }

    private fun resetEndLessScroll() {
        if (endlessScroll != null) {
            binding.eqRecyclerView.removeOnScrollListener(endlessScroll!!)
            endlessScroll = EndlessScroll()
            binding.eqRecyclerView.addOnScrollListener(endlessScroll!!)
        }
    }


    private inner class EndlessScroll : EndlessRecyclerOnScrollListener() {
        override fun onLoadMore() {
            offset += LIMIT
            viewModel.earthQuakeFeatures(LIMIT, offset, true)
        }
    }

}