package com.example.runningevents.presentation.login_signup_container.social_login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.runningevents.R
import com.example.runningevents.databinding.FragmentSocialLoginBinding
import com.example.runningevents.presentation.common.ErrorSnackbar.showErrorSnackbar
import com.example.runningevents.presentation.login_signup_container.LoginSignupContainerFragmentDirections
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SocialLoginFragment : Fragment() {

    private val viewModel: SocialLoginViewModel by viewModels()
    private lateinit var binding: FragmentSocialLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleLoginResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSocialLoginBinding.inflate(inflater, container, false)
        googleResultLauncher()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()

        binding.googleLoginButton.setOnClickListener {
            loginWithGoogle()
        }
        binding.loginAnonymousButton.setOnClickListener {
            viewModel.loginAnonymously()
        }
    }

    private fun initObservers() {
        viewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.loading.visibility = View.VISIBLE
            } else {
                binding.loading.visibility = View.GONE
            }
        })
        viewModel.eventShowError.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { error ->
                showErrorSnackbar(view = view, errorMessage = error)
            }
        })
        viewModel.loginSuccess.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                findNavController().navigate(LoginSignupContainerFragmentDirections.actionLoginRegisterFragmentToRacesFragment())
            }
        })
    }

    private fun loginWithGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        activity?.let {
            googleSignInClient = GoogleSignIn.getClient(it, googleSignInOptions)
            val intent = googleSignInClient.signInIntent
            googleLoginResultLauncher.launch(intent)
        }
    }

    private fun googleResultLauncher() {
        googleLoginResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    try {
                        val account = task.getResult(ApiException::class.java)
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        viewModel.loginGoogle(firebaseCredential = credential)
                    } catch (e: Exception) {
                        showErrorSnackbar(view = view, errorMessage = e.message.toString())
                    }
                } else {
                    showErrorSnackbar(
                        view = view,
                        errorMessage = "Google sign in failed. Try again later."
                    )
                }
            }
    }
}