package com.edgar.appcanina.dogList

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.databinding.ActivityDogListBinding
import com.edgar.appcanina.dogDetail.DogDetailActivity
import com.edgar.appcanina.dogDetail.DogDetailActivity.Companion.DOG_KEY

private const val SPAN_COUNT = 3

class DogListActivity : AppCompatActivity() {

    private val dogListVM: DogListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recycler = binding.dogRecycler
        recycler.layoutManager = GridLayoutManager(this, SPAN_COUNT)

        val adapter = DogAdapter()

        val progress = binding.progress

        adapter.setOnItemClickListener {dogInfo->
            val intent = Intent(this, DogDetailActivity::class.java)
            intent.putExtra(DOG_KEY,dogInfo)
            startActivity(intent)
        }

        recycler.adapter = adapter

        dogListVM.dogList.observe(this) { listDog ->
            adapter.submitList(listDog)
        }

        dogListVM.status.observe(this){status->
            when(status){
                is ApiResponceStatus.Loading -> progress.visibility = View.VISIBLE

                is ApiResponceStatus.Error -> {
                    progress.visibility = View.GONE
                    Toast.makeText(this,status.message,Toast.LENGTH_SHORT).show()
                }
                is ApiResponceStatus.Success -> {
                    progress.visibility = View.GONE
                }
            }
        }
    }
}