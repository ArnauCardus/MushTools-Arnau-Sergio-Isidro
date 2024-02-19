package com.example.mushtools.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.example.mushtools.navegation.NavScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.UUID
import java.util.concurrent.Executor
import kotlin.coroutines.resume


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Fotos(navController: NavController){
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cameraController = remember {
        LifecycleCameraController(context)
    }
    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val executor = ContextCompat.getMainExecutor(context)
                    scope.launch {
                        val fotoUri = takePicture(cameraController, executor)
                        fotoUri?.let {
                            guardarFotoEnFirebaseStorage(context, it)
                        }
                    }
                }
            ) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Camara")
            }
        }
    ) {
        if (permissionState.status.isGranted) {
            CamaraComposable(cameraController, lifecycle, modifier = Modifier.padding(it))
        } else {
            Text(text = "Permiso Denegado!", modifier = Modifier.padding(it))
        }
    }
}

private suspend fun takePicture(cameraController: LifecycleCameraController, executor: Executor): Uri? {
    val file = File.createTempFile("imagentest", ".jpg")
    val outputDirectory = ImageCapture.OutputFileOptions.Builder(file).build()

    return suspendCancellableCoroutine { continuation ->
        cameraController.takePicture(outputDirectory, executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedFile = File(outputFileResults.savedUri?.path ?: "")
                    println("Imagen guardada en: ${savedFile.absolutePath}")
                    println(outputFileResults.savedUri)
                    continuation.resume(outputFileResults.savedUri ?:  null)
                }

                override fun onError(exception: ImageCaptureException) {
                    println("Error al capturar la imagen: $exception")
                    continuation.resume(null,)
                }
            })
    }
}

@Composable
fun CamaraComposable(
    cameraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    modifier: Modifier = Modifier,
) {
    cameraController.bindToLifecycle(lifecycle)
    AndroidView(modifier = modifier, factory = { context ->
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
        previewView.controller = cameraController

        previewView
    })
}
fun guardarFotoEnFirebaseStorage(context: Context, fotoUri: Uri) {
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val imageRef = storageRef.child("fotos/${UUID.randomUUID()}.jpg")

    val inputStream = context.contentResolver.openInputStream(fotoUri)
    val bytes = inputStream?.readBytes()

    bytes?.let {
        imageRef.putBytes(it)
            .addOnSuccessListener {taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    Log.d("FirebaseStorage", "URL de la imagen: $imageUrl")
                }
                println("Foto guardada exitosamente en Firebase Storage.")
            }
            .addOnFailureListener { exception ->
                println("Error al guardar la foto en Firebase Storage: $exception")
            }
    }
}
