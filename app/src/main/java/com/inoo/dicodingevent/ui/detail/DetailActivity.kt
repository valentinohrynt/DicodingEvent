package com.inoo.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.inoo.dicodingevent.R
import com.inoo.dicodingevent.data.Result
import com.inoo.dicodingevent.data.local.entity.EventEntity
import com.inoo.dicodingevent.databinding.ActivityDetailBinding
import com.inoo.dicodingevent.ui.setting.SettingPreferences
import com.inoo.dicodingevent.ui.setting.dataStore
import com.inoo.dicodingevent.ui.viewmodel.MainViewModel
import com.inoo.dicodingevent.ui.viewmodel.ViewModelFactory
import com.inoo.dicodingevent.util.SimpleDateUtil.formatDateTime

class DetailActivity : AppCompatActivity() {

    private var _binding: ActivityDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: SettingPreferences
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this, pref)
    }
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
        progressBar = binding.progressBar
        pref = SettingPreferences.getInstance(this.applicationContext.dataStore)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.detail_event)

        if (eventId != null) {
            observeEventDetail(eventId)
        }
    }

    private fun observeEventDetail(eventId: String) {
        viewModel.getDetailEvent(eventId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    updateUI(result.data)
                }
                is Result.Error -> {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUI(eventDetail: EventEntity) {

        binding.apply {
            Glide.with(this@DetailActivity)
                .load(eventDetail.imageLogo)
                .into(ivDetailEventImageLogo)

            tvDetailEventName.text = eventDetail.name
            tvDetailEventOwner.text = eventDetail.ownerName
            "${formatDateTime(eventDetail.beginTime)} - ${formatDateTime(eventDetail.endTime)}".also { tvDetailEventTime.text = it }
            "Available Quota: ${eventDetail.quota.minus(eventDetail.registrants)}".also { tvDetailEventQuota.text = it }
            tvDetailEventDescription.text = HtmlCompat.fromHtml(eventDetail.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            btnDetailEventOpenLink.setOnClickListener {
                eventDetail.link.let { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }
                if (eventDetail.link == null) {
                    Toast.makeText(this@DetailActivity,
                        getString(R.string.no_link_found), Toast.LENGTH_SHORT).show()
                }
            }

            if (eventDetail.isFavorited) {
                btnFavorite.background = AppCompatResources.getDrawable(this@DetailActivity, R.drawable.ic_favorite_black_24dp)
            } else {
                btnFavorite.background = AppCompatResources.getDrawable(this@DetailActivity, R.drawable.ic_unfavorite_black_24dp)
            }

            btnFavorite.setOnClickListener {
                eventDetail.isFavorited = !eventDetail.isFavorited
                binding.btnFavorite.background = if (eventDetail.isFavorited) {
                    AppCompatResources.getDrawable(this@DetailActivity, R.drawable.ic_favorite_black_24dp)
                } else {
                    AppCompatResources.getDrawable(this@DetailActivity, R.drawable.ic_unfavorite_black_24dp)
                }

                viewModel.updateFavoriteStatus(eventDetail, eventDetail.isFavorited)

                val message = if (eventDetail.isFavorited) getString(R.string.added_to_favorites) else getString(
                    R.string.removed_from_favorites
                )
                Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "extra_event_id"
    }

}