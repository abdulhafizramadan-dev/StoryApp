package com.ahr.storyapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ahr.storyapp.R
import com.ahr.storyapp.databinding.FragmentRegisterBinding
import com.ahr.storyapp.domain.Response
import com.ahr.storyapp.helper.isEmailFormatValid
import com.ahr.storyapp.helper.isLengthPasswordGreaterThan6
import com.ahr.storyapp.helper.isNotEmpty
import com.ahr.storyapp.helper.validateInput
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupButtons()
        setupValidationForm()
    }

    private fun setupButtons() {
        arrayOf(binding.btnRegister, binding.btnLogin)
            .forEach { button -> button.setOnClickListener(this) }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_register -> register()
            R.id.btn_login -> navigateToLoginScreen()
        }
    }

    private fun setupValidationForm() {
        binding.edtName.validateInput(R.string.validation_error_name)
        binding.edtEmail.validateInput(R.string.validation_error_email)
    }


    private fun navigateToLoginScreen() {
        parentFragmentManager.popBackStack()
    }

    private fun register() {

        if (!validateFormRegister()) return

        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        authViewModel.register(name = name, email = email, password = password)
            .observe(viewLifecycleOwner) { response ->
                when (response) {
                    is Response.Loading -> toggleLoading(true)
                    is Response.Empty -> toggleLoading(false)
                    is Response.Success -> {
                        toggleLoading(false)
                        Snackbar.make(binding.root, response.data.message, Snackbar.LENGTH_LONG).show()
                        navigateToLoginScreen()
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

    private fun validateFormRegister(): Boolean {

        if (!binding.edtName.isNotEmpty()) {
            binding.tilName.error = getString(R.string.validation_error_name)
            return false
        }
        if (!binding.edtEmail.isNotEmpty()) {
            binding.tilEmail.error = getString(R.string.validation_error_email)
            return false
        }
        if (!binding.edtPassword.isNotEmpty()) {
            binding.tilPassword.error = getString(R.string.validation_error_password)
            return false
        }

        val isNameValid = binding.edtName.isNotEmpty()
        val isEmailValid = binding.edtEmail.isNotEmpty() && binding.edtEmail.isEmailFormatValid()
        val isPasswordValid = binding.edtPassword.isNotEmpty() && binding.edtPassword.isLengthPasswordGreaterThan6()

        return isNameValid && isEmailValid && isPasswordValid
    }

    private fun toggleLoading(state: Boolean) {
        binding.btnRegister.isEnabled = !state
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}