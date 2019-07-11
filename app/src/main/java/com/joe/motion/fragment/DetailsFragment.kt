package com.joe.motion.fragment

import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import com.joe.motion.R
import com.joe.motion.adapter.RecyclerAdapter
import com.joe.motion.common.EXTRA_COORDINATES
import com.joe.motion.common.EXTRA_POSITION
import com.joe.motion.listener.OnBackPressedListener
import com.joe.motion.model.DataProvider
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.item_card.*

class DetailsFragment : BaseFragment(), OnBackPressedListener {

    private lateinit var coordinates: FloatArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { coordinates = it.getFloatArray(EXTRA_COORDINATES) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = if (arguments != null) (arguments as Bundle).getInt(EXTRA_POSITION) else 0

        setupViews(position)

        if (savedInstanceState == null) {
            animateToolbar()

            fab_negative.animate()
                .translationY(0f)
                .setDuration(650)
                .setInterpolator(OvershootInterpolator(4f))
                .start()

            fab_positive.animate()
                .translationY(0f)
                .setStartDelay(100)
                .setDuration(650)
                .setInterpolator(OvershootInterpolator(4f))
                .start()
        } else
            toolbar.alpha = 1f
    }

    private fun setupViews(position: Int) {
        (details_card.layoutParams as ViewGroup.MarginLayoutParams).topMargin = coordinates[2].toInt()

        val data = DataProvider.getCardData()[position]
        item_title.text = data.name
        title_amount.text = data.amount
        title_date.text = data.date
        title_status.text = data.status.code
        image_status.setImageResource(data.status.icondId)
        image_card.setImageResource(data.imageId)

        fab_negative.setOnClickListener { onBackPressed() }

        with(recycler_view) {
            adapter = RecyclerAdapter(DataProvider.getDetailsData())
            setHasFixedSize(true)
            fab_negative.doOnLayout {
                val paddingBottom = (paddingBottom + fab_negative.height * 1.5).toInt()
                updatePadding(bottom = paddingBottom)
            }
        }
    }

    override fun onBackPressed() {
        animateViewsOut()
    }

    private fun animateViewsOut() {
        val translateTo = fab_negative.height * 2f
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(recycler_view)
            start()
        }

        fab_negative.animate()
            .translationY(translateTo)
            .setDuration(350)
            .setInterpolator(AnticipateInterpolator(2f))
            .start()

        fab_positive.animate()
            .translationY(translateTo)
            .setStartDelay(50)
            .setDuration(350)
            .withEndAction { activity?.supportFragmentManager?.popBackStack() }
            .setInterpolator(AnticipateInterpolator(2f))
            .start()

        animateToolbar(0f, 350)
    }

    private fun animateToolbar(alphaTo: Float = 1f, duration: Long = 200) {
        toolbar.animate().alpha(alphaTo).setDuration(duration).start()
    }

    companion object {
        const val TAG = "DetailsFragment"

        fun newInstance(coordinates: FloatArray, adapterPosition: Int): DetailsFragment {
            val bundle = Bundle().apply {
                putFloatArray(EXTRA_COORDINATES, coordinates)
                putInt(EXTRA_POSITION, adapterPosition)
            }

            return DetailsFragment().apply { arguments = bundle }
        }
    }

}
