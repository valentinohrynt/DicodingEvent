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

class GridItemAdapter(private val onClickedItem: (Int?) -> Unit) : RecyclerView.Adapter<GridItemAdapter.GridEventViewHolder>() {

    private val events = mutableListOf<ListEventsItem>()

    fun setEvents(newEvents: List<ListEventsItem>) {
        events.clear()
        events.addAll(newEvents)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return GridEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: GridEventViewHolder, position: Int) {
        val event = events[position]
        holder.bind(event)
    }

    override fun getItemCount() = events.size

    inner class GridEventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
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