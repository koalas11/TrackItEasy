package com.kotlinenjoyers.trackiteasy.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kotlinenjoyers.trackiteasy.ui.navigation.ParcelNavHost
import com.kotlinenjoyers.trackiteasy.ui.theme.TrackItEasyTheme


class MainActivity : ComponentActivity() {

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            TrackItEasyTheme {
                ParcelNavHost()
            }
        }
    }
}