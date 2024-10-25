package com.inoo.dicodingevent.ui.detail

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.inoo.dicodingevent.R
import com.inoo.dicodingevent.data.Result
import com.inoo.dicodingevent.databinding.FragmentDetailBinding
import com.inoo.dicodingevent.ui.MainActivity
import com.inoo.dicodingevent.ui.viewmodel.MainViewModel
import com.inoo.dicodingevent.ui.setting.SettingPreferences
import com.inoo.dicodingevent.ui.viewmodel.ViewModelFactory
import com.inoo.dicodingevent.ui.setting.dataStore
import com.inoo.dicodingevent.util.SimpleDateUtil.formatDateTime
import com.inoo.dicodingevent.data.local.entity.EventEntity

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), SettingPreferences.getInstance(requireContext().applicationContext.dataStore))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActionBar()
        hideBottomNavigation()

        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val eventId = args.eventId.toString()

        progressBar = binding.progressBar
        observeEventDetail(eventId)
    }

    private fun observeEventDetail(eventId: String) {
        viewModel.getDetailEvent(eventId).observe(viewLifecycleOwner) { result ->
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
                    Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupActionBar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = "Detail Event"
        }
    }

    private fun hideBottomNavigation() {
        (activity as? MainActivity)?.hideBottomNavigation()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun updateUI(eventDetail: EventEntity) {

        binding.apply {
            Glide.with(this@DetailFragment)
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
                    Toast.makeText(requireContext(), "No link found", Toast.LENGTH_SHORT).show()
                }
            }

            if (eventDetail.isFavorited) {
                btnFavorite.background = view?.context?.getDrawable(R.drawable.ic_favorite_black_24dp)
            } else {
                btnFavorite.background = view?.context?.getDrawable(R.drawable.ic_unfavorite_black_24dp)
            }

            btnFavorite.setOnClickListener {
                eventDetail.isFavorited = !eventDetail.isFavorited
                binding.btnFavorite.background = if (eventDetail.isFavorited) {
                    view?.context?.getDrawable(R.drawable.ic_favorite_black_24dp)
                } else {
                    view?.context?.getDrawable(R.drawable.ic_unfavorite_black_24dp)
                }

                viewModel.updateFavoriteStatus(eventDetail, eventDetail.isFavorited)

                val message = if (eventDetail.isFavorited) "Added to favorites" else "Removed from favorites"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
