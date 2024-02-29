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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.mushtools.navegation.NavScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.UUID
import java.util.concurrent.Executor
import kotlin.coroutines.resume


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Fotos(
          onOk: (String) -> Unit,
){
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
                        takePicture(cameraController, executor)?.let { fotoUri ->
                            guardarFotoEnFirebaseStorage(context, fotoUri) { rutaImagen ->
                                onOk(rutaImagen)
                            }
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
    return suspendCancellableCoroutine { continuation ->
        val outputOptions = ImageCapture.OutputFileOptions.Builder(File.createTempFile("imagentest", ".jpg"))
            .build()
        cameraController.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                continuation.resume(outputFileResults.savedUri ?: null)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("takePicture", "Error al capturar la imagen: $exception")
                continuation.resume(null)
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
fun guardarFotoEnFirebaseStorage(context: Context, fotoUri: Uri, onOk: (String) -> Unit){
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    val nombreImagen = "${UUID.randomUUID()}.jpg"
    val rutaImagen =  "fotos/$nombreImagen"
    val imageRef = storageRef.child(rutaImagen)
    val inputStream = context.contentResolver.openInputStream(fotoUri)
    val bytes = inputStream?.readBytes()
    bytes?.let {
        imageRef.putBytes(it)
            .addOnSuccessListener {
                onOk (rutaImagen)
            }
        println("Foto guardada exitosamente en Firebase Storage.")
    }
}