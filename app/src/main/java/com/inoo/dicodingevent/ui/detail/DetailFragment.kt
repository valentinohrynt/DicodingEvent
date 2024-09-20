package com.inoo.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.inoo.dicodingevent.R
import com.inoo.dicodingevent.data.response.ListEventsItem
import com.inoo.dicodingevent.databinding.FragmentDetailBinding
import com.inoo.dicodingevent.util.simpleDateUtil.formatDateTime

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

    }

    private fun updateUI(eventDetail: ListEventsItem?) {

        binding.apply {
            Glide.with(this@DetailFragment)
                .load(eventDetail?.imageLogo)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivDetailEventImageLogo)

            tvDetailEventName.text = eventDetail?.name
            tvDetailEventOwner.text = eventDetail?.ownerName
            tvDetailEventTime.text = "${formatDateTime(eventDetail?.beginTime ?: "")} - ${formatDateTime(eventDetail?.endTime ?: "")}"
            tvDetailEventQuota.text = "Quota: ${eventDetail?.quota?.minus(eventDetail?.registrants!!)}"
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
