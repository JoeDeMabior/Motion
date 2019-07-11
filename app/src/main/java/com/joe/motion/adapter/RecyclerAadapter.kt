package com.joe.motion.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joe.motion.model.DataProvider

class RecyclerAadapter<T : DataProvider.BaseData>(
    private var dataSet: List<T>,
    private var listener: View.OnClickListener? = null
) : RecyclerView.Adapter<BaseViewHolder<T>>() {

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        return when (viewType) {
            0 -> BaseViewHolder.DetailsViewHolder(parent) as BaseViewHolder<T>
            else -> BaseViewHolder.CardViewHolder(parent) as BaseViewHolder<T>
        }
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) = holder.bind(dataSet[position], listener)

    override fun getItemViewType(position: Int): Int {
        return when (dataSet[0].javaClass) {
            DataProvider.Details::class.java -> 0
            else -> 1
        }
    }

}
