package com.example.runningevents.presentation.login_signup_container.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.runningevents.databinding.FragmentLoginBinding
import com.example.runningevents.presentation.common.ErrorSnackbar.showErrorSnackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInFragment : Fragment() {

    private val viewModel: LogInViewModel by viewModels()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        val emailText = binding.emailEditText
        val passwordText = binding.passwordEditText
        binding.loginButton.setOnClickListener {
            viewModel.loginWithEmailAndPassword(
                email = emailText.text.toString(),
                password = passwordText.text.toString()
            )
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.loading.visibility = View.VISIBLE
                binding.loginButton.isEnabled = false
            } else {
                binding.loading.visibility = View.GONE
                binding.loginButton.isEnabled = true
            }
        })
        viewModel.eventShowError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { error ->
                binding.loading.visibility = View.GONE
                binding.loginButton.isEnabled = true
                showErrorSnackbar(view = view, errorMessage = error)
            }
        })
        viewModel.emailError.observe(viewLifecycleOwner, Observer { error ->
            binding.emailInputLayout.error = error
        })
        viewModel.passwordError.observe(viewLifecycleOwner, Observer { error ->
            binding.passwordInputLayout.error = error
        })
    }

    override fun onResume() {
        super.onResume()
        view?.requestLayout()
    }
}