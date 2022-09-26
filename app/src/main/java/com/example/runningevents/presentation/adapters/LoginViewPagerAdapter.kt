package com.example.runningevents.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.runningevents.presentation.login_signup_container.login.LogInFragment
import com.example.runningevents.presentation.login_signup_container.signup.SignUpFragment

class LoginViewPagerAdapter(
    private val totalTabs: Int,
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return totalTabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LogInFragment()
            1 -> SignUpFragment()
            else -> LogInFragment()
        }
    }
}