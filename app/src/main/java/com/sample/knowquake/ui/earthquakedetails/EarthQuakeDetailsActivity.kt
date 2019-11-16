package com.sample.knowquake.ui.earthquakedetails

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.sample.knowquake.R
import com.sample.knowquake.databinding.ActivityEarthquakedeatilsBinding
import com.sample.knowquake.vo.DataStore
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject
import androidx.core.app.NavUtils
import android.view.MenuItem


class EarthQuakeDetailsActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dataStore: DataStore

    private val viewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory)
            .get(EarthquakeDetailsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityEarthquakedeatilsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_earthquakedeatils)
        binding.apply {
            viewModel = this@EarthQuakeDetailsActivity.viewModel
            lifecycleOwner = this@EarthQuakeDetailsActivity
            setSupportActionBar(toolBar)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.title = "EarthQuakeDetails"
        viewModel.setEarthquakeDetails(dataStore.eqFeatures!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}