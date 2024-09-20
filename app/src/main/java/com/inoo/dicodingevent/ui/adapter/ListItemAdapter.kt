package com.inoo.dicodingevent.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inoo.dicodingevent.R
import com.inoo.dicodingevent.data.response.ListEventsItem
import com.inoo.dicodingevent.util.simpleDateUtil.formatDateTime

class ListItemAdapter(
    private val onClickedItem: (Int?) -> Unit,
    private val viewType: Int
) : RecyclerView.Adapter<ListItemAdapter.ListEventViewHolder>() {

    private val events1 = mutableListOf<ListEventsItem>()
    private val events2 = mutableListOf<ListEventsItem>()

    fun setEvents1(newEvents: List<ListEventsItem>) {
        events1.clear()
        events1.addAll(newEvents)
        notifyDataSetChanged()
    }

    // Set data for the second RecyclerView
    fun setEvents2(newEvents: List<ListEventsItem>) {
        events2.clear()
        events2.addAll(newEvents)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListEventViewHolder, position: Int) {
        val event: ListEventsItem = when (viewType) {
            1 -> events1[position] // Handle first RecyclerView's data
            2 -> events2[position] // Handle second RecyclerView's data
            else -> throw IllegalArgumentException("Invalid viewType")
        }
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return when (viewType) {
            1 -> events1.size
            2 -> events2.size
            else -> 0
        }
    }

    inner class ListEventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val eventName: TextView = view.findViewById(R.id.tv_item_name)
        private val eventOwner: TextView = view.findViewById(R.id.tv_item_ownerName)
        private val eventBeginTime: TextView = view.findViewById(R.id.tv_item_beginTime)
        private val eventQuota: TextView = view.findViewById(R.id.tv_item_quota)
        private val eventImage: ImageView = view.findViewById(R.id.iv_image_logo)

        fun bind(event: ListEventsItem) {
            eventName.text = event.name
            eventOwner.text = event.ownerName
            eventBeginTime.text = event.beginTime?.let { formatDateTime(it) }
            eventQuota.text = "${event.registrants}/${event.quota}"

            Glide.with(view.context)
                .load(event.imageLogo)
                .into(eventImage)

            view.setOnClickListener {
                onClickedItem(event.id)
            }
        }
    }
}

