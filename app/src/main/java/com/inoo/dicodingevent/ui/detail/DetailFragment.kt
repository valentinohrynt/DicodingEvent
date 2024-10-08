package com.inoo.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.inoo.dicodingevent.data.response.ListEventsItem
import com.inoo.dicodingevent.databinding.FragmentDetailBinding
import com.inoo.dicodingevent.util.SimpleDateUtil.formatDateTime

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()

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

        val args = DetailFragmentArgs.fromBundle(requireArguments())
        val eventId = args.eventId.toString()

        viewModel.fetchEventDetail(eventId)

        viewModel.eventDetail.observe(viewLifecycleOwner){
            updateUI(it)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun updateUI(eventDetail: ListEventsItem?) {

        binding.apply {
            Glide.with(this@DetailFragment)
                .load(eventDetail?.imageLogo)
                .into(ivDetailEventImageLogo)

            tvDetailEventName.text = eventDetail?.name
            tvDetailEventOwner.text = eventDetail?.ownerName
            "${formatDateTime(eventDetail?.beginTime ?: "")} - ${formatDateTime(eventDetail?.endTime ?: "")}".also { tvDetailEventTime.text = it }
            "Available Quota: ${eventDetail?.quota?.minus(eventDetail.registrants!!)}".also { tvDetailEventQuota.text = it }
            tvDetailEventDescription.text = HtmlCompat.fromHtml(eventDetail?.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)

            btnDetailEventOpenLink.setOnClickListener {
                eventDetail?.link.let { link ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }
                if (eventDetail?.link == null) {
                    Toast.makeText(requireContext(), "No link found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
