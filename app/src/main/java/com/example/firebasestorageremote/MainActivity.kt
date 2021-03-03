package com.example.firebasestorageremote

import android.content.ContentUris
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.firebasestorageremote.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class MainActivity : AppCompatActivity() {
    lateinit var storage: FirebaseStorage

    companion object {
        const val REQUEST_CODE = 1
        const val UPLOAD_FOLDER = "upload_images/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val auth = Firebase.auth

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }

        if (auth.currentUser == null) {
            startActivity(
                Intent(this, LoginActivity::class.java)
            )
            finish()
        }
        else {

        }
        storage = Firebase.storage

        val storageRef = storage.reference // reference to root
        var imageRef = storage.getReferenceFromUrl(
            "gs://fir-storageremote.appspot.com/spring.jpg"
        )

        binding.button.setOnClickListener {
            // ppt p.30 참고
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        /*
                        Toast.makeText(this, "Fetch and activate succeeded",
                            Toast.LENGTH_SHORT).show()
                         */

                        val season = remoteConfig.getString("season")
                        if (season.toString() == "spring") {
                            imageRef = storage.getReferenceFromUrl(
                                "gs://fir-storageremote.appspot.com/spring.jpg"
                            )
                        }
                        else if (season.toString() == "summer") {
                            imageRef = storage.getReferenceFromUrl(
                                "gs://fir-storageremote.appspot.com/summer.jpg"
                            )
                        }
                        else if (season.toString() == "fall") {
                            imageRef = storage.getReferenceFromUrl(
                                "gs://fir-storageremote.appspot.com/fall.jpg"
                            )
                        }
                        else if (season.toString() == "winter") {
                            imageRef = storage.getReferenceFromUrl(
                                "gs://fir-storageremote.appspot.com/winter.jpg"
                            )
                        }
                        displayImageRef(imageRef, binding.imageView)
                    } else {
                        Toast.makeText(this, "Fetch failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        displayImageRef(imageRef, binding.imageView)

        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

    }

    private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
        imageRef?.getBytes(Long.MAX_VALUE)?.addOnSuccessListener {
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            view.setImageBitmap(bmp)
        }?.addOnFailureListener {
            // Failed to download the image
        }
    }

}