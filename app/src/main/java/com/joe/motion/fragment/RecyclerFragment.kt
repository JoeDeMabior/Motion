package com.joe.motion.fragment

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnLayout
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joe.motion.R
import com.joe.motion.adapter.RecyclerAdapter
import com.joe.motion.common.copyViewImage
import com.joe.motion.common.getToolbarHeight
import com.joe.motion.common.withEndAction
import com.joe.motion.common.withStartAaction
import com.joe.motion.listener.BottomNavigationViewListener
import com.joe.motion.model.DataProvider
import kotlinx.android.synthetic.main.fragment_recycler.*

class RecyclerFragment : BaseFragment(), View.OnClickListener {

    var bottomNavListener: BottomNavigationViewListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        if (savedInstanceState == null)
            root.doOnLayout {
                toolbar.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(400)
                    .start()

                bottomNavListener?.showBottomNavigationView()
            }
        else {
            toolbar.alpha = 1f
            toolbar.translationY = 0f
        }
    }

    private fun setupViews() {
        details_toolbar_transition_helper.translationY =
            -resources.getDimension(R.dimen.details_toolbar_container_height)
        toolbar.translationY = -toolbar.context.getToolbarHeight().toFloat()

        val elevation = -resources.getDimension(R.dimen.toolbar_elevation)

        with(recycler_view) {
            adapter = RecyclerAdapter(DataProvider.getCardData(), this@RecyclerFragment)
            setHasFixedSize(true)

            val layoutManager = layoutManager as LinearLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0)
                        bottomNavListener?.hideBottomNavigationView()
                    if (dy < 0)
                        bottomNavListener?.showBottomNavigationView()

                    if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
                        if (toolbar.cardElevation == 0f)
                            return
                        animateToolbarElevation(true)
                    } else {
                        if (toolbar.cardElevation > 0f)
                            return
                        toolbar.cardElevation = elevation
                    }
                }
            })
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (activity is BottomNavigationViewListener)
            bottomNavListener = activity as BottomNavigationViewListener
        else
            throw ClassCastException("$activity must implement BottomNavigationViewListener")
    }

    override fun onClick(view: View) {
        val fragmentTransaction = initFragmentTransaction(view)
        val copy = view.copyViewImage()
        copy.y += toolbar.height
        root.addView(copy)
        view.visibility = View.INVISIBLE
        startAnimation(copy, fragmentTransaction)
    }

    private fun startAnimation(view: View, fragmentTransaction: FragmentTransaction?) {
        AnimatorInflater.loadAnimator(activity, R.animator.main_list_animator).apply {
            setTarget(recycler_view)
            withStartAaction { if (toolbar.cardElevation > 0) animateToolbarElevation(true) }
            withEndAction {
                recycler_view.visibility = View.INVISIBLE

                val toY =
                    view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f

                view.animate().y(toY).start()

                toolbar.animate()
                    .translationY(-toolbar.height.toFloat())
                    .alpha(0f)
                    .setDuration(600)
                    .withStartAction {
                        bottomNavListener?.hideBottomNavigationView()
                        details_toolbar_transition_helper.animate()
                            .translationY(0f)
                            .setDuration(500)
                            .start()
                    }
                    .withEndAction {
                        fragmentTransaction?.commitAllowingStateLoss()
                    }
                    .start()
            }
            start()
        }
    }

    private fun initFragmentTransaction(view: View): FragmentTransaction? {
        val toY = view.resources.getDimensionPixelOffset(R.dimen.details_toolbar_container_height) - view.height / 2f
        val positions = FloatArray(3)
        positions[0] = view.x
        positions[1] = view.y + toolbar.height
        positions[2] = toY

        val adapterPosition = recycler_view.getChildAdapterPosition(view)
        val detailsFragment = DetailsFragment.newInstance(positions, adapterPosition)

        return fragmentManager?.beginTransaction()
            ?.replace(R.id.fragment_container, detailsFragment, DetailsFragment.TAG)
            ?.addToBackStack(null)
    }

    private fun animateToolbarElevation(animateOut: Boolean) {
        var valueFrom = resources.getDimension(R.dimen.toolbar_elevation)
        var valueTo = 0f

        if (!animateOut) {
            valueTo = valueFrom
            valueFrom = 0f
        }

        ValueAnimator.ofFloat(valueFrom, valueTo).setDuration(250).apply {
            startDelay = 0
            addUpdateListener { toolbar.cardElevation = it.animatedValue as Float }
            start()
        }
    }

    companion object {
        fun newInstance(): RecyclerFragment {
            return RecyclerFragment()
        }
    }

}
