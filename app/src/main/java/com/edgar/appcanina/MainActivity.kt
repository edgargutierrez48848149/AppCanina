package com.edgar.appcanina

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.edgar.appcanina.api.ApiResponceStatus
import com.edgar.appcanina.api.ApiServiceInterceptor
import com.edgar.appcanina.auth.Loginactivity
import com.edgar.appcanina.databinding.ActivityMainBinding
import com.edgar.appcanina.dogDetail.DogDetailActivity
import com.edgar.appcanina.dogDetail.DogDetailActivity.Companion.DOG_KEY
import com.edgar.appcanina.dogDetail.DogDetailActivity.Companion.IS_RECOGNITION_KEY
import com.edgar.appcanina.dogList.DogListActivity
import com.edgar.appcanina.machineLerning.Classifier
import com.edgar.appcanina.machineLerning.DogRecognition
import com.edgar.appcanina.main.MainViewModel
import com.edgar.appcanina.model.Dog
import com.edgar.appcanina.model.User
import com.edgar.appcanina.settings.SettingsActivity
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setupCamera()
            } else {
                Toast.makeText(this,"Necesitas permisos de camara",Toast.LENGTH_SHORT).show()
            }
        }

    private lateinit var binding:ActivityMainBinding
    private lateinit var imageCapture :ImageCapture
    private lateinit var cameraExecutor:ExecutorService
    private lateinit var clasifier :Classifier
    private val viewModel: MainViewModel by viewModels()
    private var isCameraReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = User.getLoggedUser(this)
        if (user == null){
            openLoginActivity()
            return
        }else{
            ApiServiceInterceptor.setSessionToken(user.autenticationToken)
        }

        binding.settingsFab.setOnClickListener {
            openSettingsActivity()
        }

        binding.dogListFab.setOnClickListener {
            openDogListActivity()
        }

        viewModel.status.observe(this){status->
            when(status){
                is ApiResponceStatus.Loading -> binding.progress.visibility = View.VISIBLE

                is ApiResponceStatus.Error -> {
                    binding.progress.visibility = View.GONE
                    Toast.makeText(this,status.message,Toast.LENGTH_SHORT).show()
                }
                is ApiResponceStatus.Success -> {
                    binding.progress.visibility = View.GONE
                }
            }
        }

        viewModel.dog.observe(this){dog->
            if (dog != null){
                openDogDetailactivity(dog)
            }
        }

        viewModel.dogRecognition.observe(this){dogRecognition->
            enabledTakePhotoButton(dogRecognition)
        }

        requestCameraPermision()
    }

    private fun openDogDetailactivity(dog: Dog) {
        val intent = Intent(this, DogDetailActivity::class.java)
        intent.putExtra(DOG_KEY,dog)
        intent.putExtra(IS_RECOGNITION_KEY,true)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        viewModel.setupClassifier(
            FileUtil.loadMappedFile(this@MainActivity,MODEL_PATH),
            FileUtil.loadLabels(this@MainActivity, LABEL_PATH)
        )
    }

    private fun requestCameraPermision(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    setupCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    AlertDialog.Builder(this)
                        .setTitle("Acepta permisos")
                        .setMessage("Acepta los permisos de camara para continuar")
                        .setPositiveButton(android.R.string.ok){_,_ ->
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                        .setNegativeButton(android.R.string.cancel){_,_-> }
                        .show()
            }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }else{
            setupCamera()
        }
    }

    private fun setupCamera(){
        binding.cameraPreview.post {
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(binding.cameraPreview.display.rotation)
                .build()
            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
            isCameraReady = true
        }
    }

    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(cameraExecutor, { imageProxy ->
                viewModel.recognizeImage(imageProxy)
            })

            cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture,imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun enabledTakePhotoButton(dogRecognition: DogRecognition) {
        if (dogRecognition.confidence > 70.0){
            binding.takePhoto.alpha = 1f
            binding.takePhoto.setOnClickListener {
                viewModel.getRecognizedDog(dogRecognition.id)
            }
        }else{
            binding.takePhoto.alpha = 0.2f
            binding.takePhoto.setOnClickListener {
                null
            }
        }
    }



    private fun openDogListActivity() {
        startActivity(Intent(this,DogListActivity::class.java))
    }

    private fun openSettingsActivity() {
        startActivity(Intent(this,SettingsActivity::class.java))
    }

    private fun openLoginActivity() {
        startActivity(Intent(this,Loginactivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::cameraExecutor.isInitialized){
            cameraExecutor.shutdown()
        }
    }
}