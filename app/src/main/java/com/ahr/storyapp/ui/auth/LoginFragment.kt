package com.ahr.storyapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import com.ahr.storyapp.R
import com.ahr.storyapp.databinding.FragmentLoginBinding
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.ui.MainActivity
import com.ahr.storyapp.helper.isEmailFormatValid
import com.ahr.storyapp.helper.isLengthPasswordGreaterThan6
import com.ahr.storyapp.helper.isNotEmpty
import com.ahr.storyapp.helper.validateInput
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()
        setupValidationForm()
    }

    private fun setupButtons() {
        arrayOf(
            binding.btnLogin,
            binding.btnRegister
        ).forEach { button -> button.setOnClickListener(this) }
    }

    private fun setupValidationForm() {
        binding.edtEmail.validateInput(R.string.validation_error_email)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_login -> login()
            R.id.btn_register -> navigateToRegisterScreen()
        }
    }

    private fun navigateToRegisterScreen() {
        parentFragmentManager.commit {
            replace<RegisterFragment>(R.id.fragment_container_view)
            addToBackStack("register")
        }
    }

    private fun navigateToHomeScreen() {
        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    private fun login() {

        if (!validateFormLogin()) return

        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        authViewModel.login(email = email, password = password)
            .observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Response.Loading -> toggleLoading(true)
                    is Response.Empty -> toggleLoading(false)
                    is Response.Success -> {
                        toggleLoading(false)
                        authViewModel.updateAuthToken(response.data.token)
                        navigateToHomeScreen()
                    }
                    is Response.Error -> {
                        toggleLoading(false)
                        response.message?.let {
                            Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
    }

    private fun validateFormLogin(): Boolean {

        if (!binding.edtEmail.isNotEmpty()) {
            binding.tilEmail.error = getString(R.string.validation_error_email)
            return false
        }
        if (!binding.edtPassword.isNotEmpty()) {
            binding.tilPassword.error = getString(R.string.validation_error_password)
            return false
        }

        val isEmailValid = (binding.edtEmail.isNotEmpty() && binding.edtEmail.isEmailFormatValid())
        val isPasswordValid = (binding.edtPassword.isNotEmpty() && binding.edtPassword.isLengthPasswordGreaterThan6())
        return isEmailValid && isPasswordValid
    }

    private fun toggleLoading(state: Boolean) {
        binding.btnLogin.isEnabled = !state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}