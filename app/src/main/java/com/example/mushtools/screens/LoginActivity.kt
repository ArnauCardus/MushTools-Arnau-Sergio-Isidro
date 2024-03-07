package com.example.mushtools.screens

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mushtools.MainActivity
import com.example.mushtools.R
import com.example.mushtools.screens.ui.theme.MushToolsTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class LoginActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        setContent {
            MushToolsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var showRegisterScreen by remember { mutableStateOf(false) }

                    if (showRegisterScreen) {
                        RegisterScreen(
                            onRegisterClick = { email, password, username ->
                                register(email, password, username)
                            },
                            onLoginClick = { email, password ->
                                login(email, password)
                            }
                        )
                    } else {
                        LoginScreen(
                            onLoginClick = { email, password ->
                                login(email, password)
                            },
                            onRegisterClick = { showRegisterScreen = true }
                        )
                    }
                }
            }
        }
    }
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        finishAffinity()
    }
    private fun login(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Cierra la actividad de inicio de sesión
                } else {
                    Toast.makeText(this, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun register(email: String, password: String, username: String) {
        // Verificar si el correo electrónico ya está registrado
        checkEmailExists(email) { emailExists ->
            if (emailExists) {
                Toast.makeText(this, "El correo electrónico ya está registrado", Toast.LENGTH_SHORT).show()
            } else {
                // Verificar si el nombre de usuario ya está registrado
                checkUsernameExists(username) { usernameExists ->
                    if (usernameExists) {
                        Toast.makeText(this, "El nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si ni el correo electrónico ni el nombre de usuario están registrados, procedemos con el registro
                        val auth = Firebase.auth
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    // Obtener ID del usuario actual
                                    val userId = user?.uid ?: ""
                                    val userData = hashMapOf(
                                        "email" to email,
                                        "username" to username
                                    )

                                    val db = Firebase.firestore
                                    db.collection("Usuarios").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                            // Cambiar a la pantalla de inicio de sesión después del registro
                                            showLoginScreen()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                }
            }
        }
    }

    private fun showLoginScreen() {
        setContent {
            LoginScreen(
                onLoginClick = { email, password ->
                    login(email, password)
                },
                onRegisterClick = { showRegisterScreen() }
            )
        }
    }

    private fun showRegisterScreen() {
        setContent {
            RegisterScreen(
                onRegisterClick = { email, password, username ->
                    register(email, password, username)
                },
                onLoginClick = { email, password ->
                    login(email, password)
                }
            )
        }
    }

    private fun checkEmailExists(email: String, onEmailChecked: (Boolean) -> Unit) {
        val db = Firebase.firestore
        db.collection("Usuarios")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                // Si hay algún documento con el correo electrónico dado, entonces el correo ya está registrado
                onEmailChecked(!documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                // Manejar cualquier error que ocurra durante la consulta
                Log.w(TAG, "Error al comprobar el correo electrónico", exception)
                onEmailChecked(false) // Por precaución, asumimos que el correo no está registrado
            }
    }

    private fun checkUsernameExists(username: String, onUsernameChecked: (Boolean) -> Unit) {
        val db = Firebase.firestore
        db.collection("Usuarios")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                // Si hay algún documento con el nombre de usuario dado, entonces el nombre de usuario ya está registrado
                onUsernameChecked(!documents.isEmpty)
            }
            .addOnFailureListener { exception ->
                // Manejar cualquier error que ocurra durante la consulta
                Log.w(TAG, "Error al comprobar el nombre de usuario", exception)
                onUsernameChecked(false) // Por precaución, asumimos que el nombre de usuario no está registrado
            }
    }
}

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = { onLoginClick(email, password) }) {
                Text("Iniciar sesión")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onRegisterClick) {
                Text("Registrarse", color = Color.Gray)
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: (String, String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") } // Nuevo estado para mensajes de error

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Campo de nombre de usuario
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo electrónico
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))


            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )


            Button(onClick = {
                if (email.isNotBlank() && password.isNotBlank() && username.isNotBlank()) {

                    onRegisterClick(email, password, username)
                } else {
                    // Mostrar mensaje de error si algún campo está vacío
                    errorMessage = "Por favor, rellena todos los campos"
                }
            }) {
                Text("Registrarse")
            }
        }
    }
}

