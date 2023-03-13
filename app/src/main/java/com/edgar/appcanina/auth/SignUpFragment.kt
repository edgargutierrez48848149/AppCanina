package com.edgar.appcanina.auth

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.edgar.appcanina.R
import com.edgar.appcanina.databinding.FragmentSignUpBinding
import com.edgar.appcanina.isValidEmail

class SignUpFragment : Fragment() {


    interface SignUpFragmentActions{
        fun onSignUpFieldsValidated(email: String,password:String)
    }

    private lateinit var signUpFragmentActions: SignUpFragmentActions

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signUpFragmentActions = try {
            context as SignUpFragmentActions
        }catch (e:java.lang.ClassCastException){
            throw ClassCastException("$context must implement LoginFragmentActions")
        }
    }


    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        setupSignUpButton()
        return binding.root
    }

    private fun setupSignUpButton() {
        binding.signUpButton.setOnClickListener {
            validateFields()
        }
    }

    private fun validateFields() {
        binding.emailInput.error = ""
        binding.passwordInput.error = ""
        binding.confirmPasswordInput.error = ""

        val email = binding.emailEdit.text.toString()
        if (!isValidEmail(email)){
            binding.emailInput.error = getString(R.string.email_no_valid)
            return
        }

        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()){
            binding.passwordInput.error = getString(R.string.password_empty)
            return
        }

        val passwordConfirm = binding.confirmPasswordEdit.text.toString()
        if (passwordConfirm.isEmpty() || passwordConfirm != password){
            binding.confirmPasswordInput.error = getString(R.string.password_diferent_or_empty)
            return
        }

        signUpFragmentActions.onSignUpFieldsValidated(email,password)

    }
}