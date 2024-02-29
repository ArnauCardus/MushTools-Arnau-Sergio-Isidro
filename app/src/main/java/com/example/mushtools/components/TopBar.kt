
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.mushtools.screens.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    drawerState: DrawerState
){
    val scope = rememberCoroutineScope()
    val firebaseAuth = FirebaseAuth.getInstance()

    CenterAlignedTopAppBar(
        title = { Text(text = "MushTool")},
        navigationIcon = {
            IconButton(onClick = {
                scope.launch{
                    drawerState.open()
                }
            }) {
                Icon(Icons.Outlined.Menu,"Open Menu")
            }
        },
        actions = {
            // Botón de log out
            LogoutButton {
                firebaseAuth.signOut()
            }
        }
    )
}
private val LocalContext = compositionLocalOf<Context> { error("No Context provided") }

@Composable
private fun RowScope.LogoutButton(onClick: () -> Unit) {
    val context = LocalContext.current
    IconButton(
        onClick = {
            onClick()
            // Redirigir a la pantalla de inicio de sesión después de cerrar sesión
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    ) {
        Icon(Icons.AutoMirrored.Outlined.ExitToApp, contentDescription = "Log out")
    }
}


