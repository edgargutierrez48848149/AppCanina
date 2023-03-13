package com.edgar.appcanina.auth

import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.edgar.appcanina.MainActivity
import com.edgar.appcanina.R
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.databinding.ActivityLoginactivityBinding
import com.edgar.appcanina.model.User

class Loginactivity : AppCompatActivity(), LoginFragment.LoginFragmentActions,
    SignUpFragment.SignUpFragmentActions {

    private val viewModel:AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.status.observe(this){status->
            when(status){
                is ApiResponceStatus.Loading -> binding.progress.visibility = View.VISIBLE

                is ApiResponceStatus.Error -> {
                    binding.progress.visibility = View.GONE
                    showerrorDialog(status.message)
                }
                is ApiResponceStatus.Success -> {
                    binding.progress.visibility = View.GONE
                }
            }
        }

        viewModel.user.observe(this){user->
            if(user != null){
                User.setLoggedUser(this,user)
                starMainActivity()
            }
        }
    }

    private fun starMainActivity() {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    fun showerrorDialog(message: String){
        AlertDialog.Builder(this)
            .setTitle(R.string.app_name)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok){_,_ -> //Dismis
            }
            .create()
            .show()

    }

    override fun onRegistreButtonClick() {
        findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    override fun onLoginFieldsValidated(email: String, password: String) {
        viewModel.login(email,password)
    }

    override fun onSignUpFieldsValidated(email: String, password: String) {
       viewModel.signUp(email,password)
    }
}