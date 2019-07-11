package com.joe.motion.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.joe.motion.R
import com.joe.motion.common.replaceFragmentInActivity
import com.joe.motion.fragment.RecyclerFragment
import com.joe.motion.listener.BottomNavigationViewListener
import com.joe.motion.listener.OnBackPressedListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationViewListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFragment(RecyclerFragment.newInstance())
    }

    private fun setupFragment(fragment: Fragment) {
        supportFragmentManager.findFragmentById(R.id.fragment_container) ?: replaceFragmentInActivity(
            fragment,
            R.id.fragment_container
        )
    }

    override fun hideBottomNavigationView() {
        if (bottom_navigation.translationY == 0f)
            bottom_navigation.animate()
                .translationY(bottom_navigation.height.toFloat())
                .setDuration(250)
                .start()
    }

    override fun showBottomNavigationView() {
        if (bottom_navigation.translationY >= bottom_navigation.height.toFloat())
            bottom_navigation.animate()
                .translationY(0f)
                .setDuration(400)
                .start()
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments
        var proceedToSuper = true

        for (fragment in fragmentList) {
            if (fragment is OnBackPressedListener) {
                proceedToSuper = false
                (fragment as OnBackPressedListener).onBackPressed()
            }
        }

        if (proceedToSuper)
            super.onBackPressed()
    }

}
