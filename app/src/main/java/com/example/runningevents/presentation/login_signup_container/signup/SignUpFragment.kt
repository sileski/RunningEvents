package com.example.runningevents.presentation.login_signup_container.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runningevents.databinding.FragmentSignUpBinding
import com.example.runningevents.presentation.common.ErrorSnackbar.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        binding.signUpButton.setOnClickListener {
            val emailText = binding.emailEditText.text.toString()
            val passwordText = binding.passwordEditText.text.toString()
            val confirmPasswordText = binding.confirmPasswordEditText.text.toString()
            val fullNameText = binding.fullNameEditText.text.toString()
            viewModel.signUpWithEmailAndPassword(
                email = emailText,
                password = passwordText,
                confirmPassword = confirmPasswordText,
                fullName = fullNameText
            )
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.loading.visibility = View.VISIBLE
                binding.signUpButton.isEnabled = false
            } else {
                binding.loading.visibility = View.GONE
                binding.signUpButton.isEnabled = true
            }
        })
        viewModel.eventShowError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { error ->
                binding.loading.visibility = View.GONE
                binding.signUpButton.isEnabled = true
                showErrorSnackbar(view = view, errorMessage = error)
            }
        })
        viewModel.emailError.observe(viewLifecycleOwner, Observer {
            binding.emailInputLayout.error = it
        })
        viewModel.passwordError.observe(viewLifecycleOwner, Observer {
            binding.passwordInputLayout.error = it
        })
        viewModel.confirmPasswordError.observe(viewLifecycleOwner, Observer {
            binding.confirmPasswordInputLayout.error = it
        })
        viewModel.fullNameError.observe(viewLifecycleOwner, Observer {
            binding.fullNameInputLayout.error = it
        })
    }

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
    }
}