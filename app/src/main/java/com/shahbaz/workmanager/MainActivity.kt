package com.shahbaz.workmanager

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.shahbaz.workmanager.databinding.ActivityMainBinding
import com.shahbaz.workmanager.worker.ImageUploaderWorker
import java.time.Duration
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private var fileUri = ""
    private lateinit var binding: ActivityMainBinding
    private val workManager by lazy { WorkManager.getInstance(this) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.slectImage.setOnClickListener {
            imageLauncher.launch("image/*")
        }

        binding.uploadImage.setOnClickListener {
            val data = workDataOf("fileUri" to fileUri)
            val workRequest = OneTimeWorkRequestBuilder<ImageUploaderWorker>()
                .setInputData(data)
                .setConstraints(Constraints(NetworkType.CONNECTED))
                .setBackoffCriteria(BackoffPolicy.LINEAR, 30, TimeUnit.SECONDS)
                .build()
            workManager.enqueue(workRequest)

            workManager.getWorkInfoByIdLiveData(workRequest.id).observe(this) { workInfo ->

                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> {}
                        WorkInfo.State.RUNNING -> {
                            Toast.makeText(this, "Running", Toast.LENGTH_SHORT).show()
                        }

                        WorkInfo.State.SUCCEEDED -> {
                            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                        }

                        WorkInfo.State.FAILED -> {
                            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                        }

                        WorkInfo.State.BLOCKED -> {}
                        WorkInfo.State.CANCELLED -> {

                        }
                    }
                }
            }


        }

    }

    val imageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            fileUri = uri.toString()
            binding.imageUri.text = fileUri
            binding.imageView.setImageURI(uri)
        }
    }
}