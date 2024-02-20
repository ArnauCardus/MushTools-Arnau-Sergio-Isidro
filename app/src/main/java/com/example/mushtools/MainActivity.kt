package com.example.mushtools


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mushtools.components.PantallaPrincipal
import com.example.mushtools.screens.LoginActivity
import com.example.mushtools.ui.theme.MushToolsTheme
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser === null){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        setContent {
            MushToolsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    PantallaPrincipal()
                }
            }
        }
    }
}