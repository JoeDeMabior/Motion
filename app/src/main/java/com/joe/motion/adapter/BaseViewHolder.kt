package com.joe.motion.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joe.motion.R
import com.joe.motion.common.TRANSITION_CARD
import com.joe.motion.common.inflate
import com.joe.motion.model.DataProvider
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_card.*
import kotlinx.android.synthetic.main.item_details.*

abstract class BaseViewHolder<T : DataProvider.BaseData>(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {

    override val containerView: View?
        get() = itemView

    abstract fun bind(data: T, listener: View.OnClickListener? = null)

    class CardViewHolder(parent: ViewGroup) : BaseViewHolder<DataProvider.Card>(parent.inflate(R.layout.item_card)) {

        override fun bind(data: DataProvider.Card, listener: View.OnClickListener?) {
            containerView?.setOnClickListener(listener)
            containerView?.transitionName = TRANSITION_CARD + adapterPosition

            item_title.text = data.name
            title_amount.text = data.amount
            title_date.text = data.date
            title_status.text = data.status.code
            image_status.setImageResource(data.status.icondId)
            image_card.setImageResource(data.imageId)
        }

    }

    class DetailsViewHolder(parent: ViewGroup) :
        BaseViewHolder<DataProvider.Details>(parent.inflate(R.layout.item_details)) {

        override fun bind(data: DataProvider.Details, listener: View.OnClickListener?) {
            details_title.text = data.title
            details_subtitle.text = data.subtitle
            details_amount.text = data.amount
        }

    }

}
