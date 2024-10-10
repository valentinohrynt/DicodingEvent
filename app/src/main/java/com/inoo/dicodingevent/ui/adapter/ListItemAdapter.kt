package com.inoo.dicodingevent.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.inoo.dicodingevent.R
import com.inoo.dicodingevent.data.local.entity.EventEntity
import com.inoo.dicodingevent.ui.MainViewModel
import com.inoo.dicodingevent.util.SimpleDateUtil.formatDateTime

class ListItemAdapter(
    private val onClickedItem: (EventEntity) -> Unit,
    private val viewModel: MainViewModel
) : RecyclerView.Adapter<ListItemAdapter.ListEventViewHolder>() {

    private val events = mutableListOf<EventEntity>()

//    private val inactiveEvents = mutableListOf<EventEntity>()
//    private val activeEvents = mutableListOf<EventEntity>()
//    private val favoritedEvents = mutableListOf<EventEntity>()

    fun setEvents(newEvents: List<EventEntity>) {
        val diffCallback = EventDiffCallback(events, newEvents)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        events.clear()
        events.addAll(newEvents)
        diffResult.dispatchUpdatesTo(this)
    }

//    fun setInactiveEvents(newEvents: List<EventEntity>) {
//        val diffCallback = EventDiffCallback(inactiveEvents, newEvents)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        inactiveEvents.clear()
//        inactiveEvents.addAll(newEvents)
//        diffResult.dispatchUpdatesTo(this)
//    }
//
//    fun setActiveEvents(newEvents: List<EventEntity>) {
//        val diffCallback = EventDiffCallback(activeEvents, newEvents)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        activeEvents.clear()
//        activeEvents.addAll(newEvents)
//        diffResult.dispatchUpdatesTo(this)
//    }
//
//    fun setFavoritedEvents(newEvents: List<EventEntity>) {
//        val diffCallback = EventDiffCallback(favoritedEvents, newEvents)
//        val diffResult = DiffUtil.calculateDiff(diffCallback)
//        favoritedEvents.clear()
//        favoritedEvents.addAll(newEvents)
//        diffResult.dispatchUpdatesTo(this)
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListEventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ListEventViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListEventViewHolder, position: Int) {
        val event: EventEntity = events[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int {
        return events.size
    }

    inner class ListEventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val eventName: TextView = view.findViewById(R.id.tv_item_name)
        private val eventOwner: TextView = view.findViewById(R.id.tv_item_ownerName)
        private val eventBeginTime: TextView = view.findViewById(R.id.tv_item_beginTime)
        private val eventQuota: TextView = view.findViewById(R.id.tv_item_quota)
        private val eventImage: ImageView = view.findViewById(R.id.iv_image_logo)
        private val btnFavorite: Button = view.findViewById(R.id.btn_favorite)

        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(event: EventEntity) {
            eventName.text = event.name
            eventOwner.text = event.ownerName
            eventBeginTime.text = formatDateTime(event.beginTime)
            "${event.registrants}/${event.quota}".also { eventQuota.text = it }

            Glide.with(view.context)
                .load(event.imageLogo)
                .into(eventImage)

            view.setOnClickListener {
                onClickedItem(event)
            }

            if (event.isFavorited) {
                btnFavorite.background = view.context.getDrawable(R.drawable.ic_favorite_black_24dp)
            } else {
                btnFavorite.background = view.context.getDrawable(R.drawable.ic_unfavorite_black_24dp)
            }

            btnFavorite.setOnClickListener {
                event.isFavorited = !event.isFavorited
                btnFavorite.background = if (event.isFavorited) {
                    view.context.getDrawable(R.drawable.ic_favorite_black_24dp)
                } else {
                    view.context.getDrawable(R.drawable.ic_unfavorite_black_24dp)
                }

                viewModel.updateFavoriteStatus(event, event.isFavorited)

                val message = if (event.isFavorited) "Added to favorites" else "Removed from favorites"
                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private class EventDiffCallback(
        private val oldList: List<EventEntity>,
        private val newList: List<EventEntity>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id // Compare unique IDs
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition] // Check for content equality
        }
    }
}

