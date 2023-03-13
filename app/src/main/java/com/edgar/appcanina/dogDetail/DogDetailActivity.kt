package com.edgar.appcanina.dogDetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.edgar.appcanina.R
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.databinding.ActivityDogDetailBinding
import com.edgar.appcanina.model.Dog

class DogDetailActivity : AppCompatActivity() {

    companion object{
        const val DOG_KEY = "dog"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }

    private val viewModel: DogDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)
        val isRecognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY,false)?: false

        if (dog == null){
            Toast.makeText(this,R.string.dog_not_fund,Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.dogIndex.text = getString(R.string.dog_index_format,dog.index)
        binding.lifeExpectancy.text = getString(R.string.dog_life_expectancy_format,dog.lifeExpectancy)
        binding.dogImage.load(dog.imageUrl)


        viewModel.status.observe(this){status->
            when(status){
                is ApiResponceStatus.Loading -> binding.loadingWheel.visibility = View.VISIBLE

                is ApiResponceStatus.Error -> {
                    binding.loadingWheel.visibility = View.GONE
                    Toast.makeText(this,status.message,Toast.LENGTH_SHORT).show()
                }
                is ApiResponceStatus.Success -> {
                    binding.loadingWheel.visibility = View.GONE
                    finish()
                }
            }
        }

        binding.closeButton.setOnClickListener {
            if (isRecognition){
                viewModel.addDogToUser(dog.id)
            }else{
                finish()
            }

        }
        binding.dog = dog
    }
}