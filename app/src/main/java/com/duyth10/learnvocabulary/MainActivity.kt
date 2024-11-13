package com.duyth10.learnvocabulary

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val intentData = Bundle()


        val intent = intent

        intent?.let {

            val wordSend = intent.getStringExtra("wordSend")
            val phoneticsTextSend = intent.getStringExtra("phoneticsTextSend")
            val phoneticsAudioSend = intent.getStringExtra("phoneticsAudioSend")
            val meaningsBasic = intent.getStringExtra("meaningsBasic")

            Log.d("ReceiverIfm", "wordSend: $wordSend")
            Log.d("ReceiverIfm", "phoneticsTextSend: $phoneticsTextSend")
            Log.d("ReceiverIfm", "phoneticsAudioSend: $phoneticsAudioSend")
            Log.d("ReceiverIfm", "meaningsBasic: $meaningsBasic")

            if (wordSend != null || phoneticsTextSend != null || phoneticsAudioSend != null || meaningsBasic != null) {
                intentData.putString("wordSend", wordSend)
                intentData.putString("phoneticsTextSend", phoneticsTextSend)
                intentData.putString("phoneticsAudioSend", phoneticsAudioSend)
                intentData.putString("meaningsBasic", meaningsBasic)
                intentData.putBoolean("isFromExternalApp", true)

            }
        }
        if (savedInstanceState == null) {

            val mainFragment = MainFragment()
            if (!intentData.isEmpty) {
                mainFragment.arguments = intentData
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mainFragment)
                .commit()
        }
    }
}