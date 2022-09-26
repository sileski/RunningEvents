package com.example.runningevents.presentation.login_signup_container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.runningevents.R
import com.example.runningevents.databinding.FragmentLoginRegisterPagerBinding
import com.example.runningevents.presentation.adapters.LoginViewPagerAdapter
import com.google.android.material.tabs.TabLayout

class LoginSignupContainerFragment : Fragment() {

    private lateinit var binding: FragmentLoginRegisterPagerBinding
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginRegisterPagerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTabLayout()
        setUpViewPager()
    }

    private fun setUpTabLayout() {
        tabLayout = binding.tabLayout
        tabLayout.addTab(tabLayout.newTab().setText(R.string.log_in))
        tabLayout.addTab(tabLayout.newTab().setText(R.string.sign_up))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tabLayout.selectedTabPosition
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    private fun setUpViewPager() {
        viewPager = binding.viewPager
        val loginViewPagerAdapter = LoginViewPagerAdapter(
            tabLayout.tabCount,
            fragmentManager = parentFragmentManager,
            lifecycle = lifecycle
        )
        viewPager.adapter = loginViewPagerAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })
    }

}