package net.deechael.kookdesktop.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.Indicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Popup
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import net.deechael.kook.exception.LoginFailedException
import net.deechael.kook.util.KookLoginer
import net.deechael.kookdesktop.KOOK_CLIENT
import net.deechael.kookdesktop.KOOK_SCOPE
import net.deechael.kookdesktop.LOGGER
import net.deechael.kookdesktop.util.Case
import net.deechael.kookdesktop.util.Controller
import net.deechael.kookdesktop.util.Dialog
import net.deechael.kookdesktop.util.Switcher
import okhttp3.Request
import snw.jkook.JKook
import snw.kookbc.impl.CoreImpl
import snw.kookbc.impl.KBCClient
import snw.kookbc.impl.network.HttpAPIRoute
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(success: () -> Unit) {
    var current by rememberSaveable {
        mutableStateOf("0")
    }
    val loginerController = Controller()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = current == "0",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = "user"
                        )
                    },
                    label = {
                        Text(
                            text = "User"
                        )
                    },
                    onClick = {
                        current = "0"
                        loginerController.goto("0")
                    }
                )
                NavigationBarItem(
                    selected = current == "1",
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.SmartToy,
                            contentDescription = "bot"
                        )
                    },
                    label = {
                        Text(
                            text = "Bot"
                        )
                    },
                    onClick = {
                        current = "1"
                        loginerController.goto("1")
                    }
                )
            }
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Kook Desktop",
                modifier = Modifier.padding(12.dp),
                fontSize = 4.em
            )
            Text(
                text = "Powered by Kotlin Compose Multiplatform",
                modifier = Modifier.padding(
                    top = 6.dp,
                    bottom = 12.dp
                ),
                fontSize = 1.em
            )
            Switcher(current, loginerController) {
                Case("0") {
                    LoginUser(success)
                }
                Case("1") {
                    LoginBot(success)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUser(success: () -> Unit) {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var buttonEnabled by rememberSaveable { mutableStateOf(true) }

    var tryLogging by rememberSaveable { mutableStateOf(false) }

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showDialog) {
        Dialog(onCloseRequest = {
            showDialog = false
        }) {
            Text(
                text = "Failed to login",
                modifier = Modifier.padding(12.dp),
                fontSize = 1.em
            )
        }
    }

    TextField(
        value = phoneNumber,
        onValueChange = { phoneNumber = it },
        label = {
            Text("Phone Number")
        },
        shape = RoundedCornerShape(5.dp),
        singleLine = true,
        placeholder = {
            Text("Phone Number")
        },
    )
    Spacer(modifier = Modifier.height(10.dp))
    TextField(
        value = password,
        onValueChange = { password = it },
        label = {
            Text("Password")
        },
        shape = RoundedCornerShape(5.dp),
        singleLine = true,
        placeholder = {
            Text("Password")
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (passwordVisible) "Hide Password" else "Show Password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )
    Spacer(modifier = Modifier.height(50.dp))
    Button(onClick = {
        buttonEnabled = false
        tryLogging = true
        try {
            val token = KookLoginer.login(phoneNumber, password)
            KOOK_CLIENT = KBCClient(JKook.getCore() as CoreImpl, File("plugins"), token)
        } catch (e: LoginFailedException) {
            showDialog = true
            KOOK_CLIENT = null
            tryLogging = false
            buttonEnabled = true
        }
        if (KOOK_CLIENT != null) {
            Runtime.getRuntime().addShutdownHook(Thread(KOOK_CLIENT!!::shutdown))
            KOOK_SCOPE.launch {
                try {
                    KOOK_CLIENT!!.start()
                } catch (e: Exception) {
                    LOGGER.error("Failed to start client", e)
                    KOOK_CLIENT!!.shutdown()
                    return@launch
                }
                KOOK_CLIENT!!.loop()
                KOOK_CLIENT!!.shutdown()
            }
            success()
        }
    },
        enabled = buttonEnabled,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.clickable { buttonEnabled }
    ) {
        Text("Login")
    }
    if (tryLogging) {
        Popup {
            Indicator()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBot(success: () -> Unit) {
    var token by rememberSaveable { mutableStateOf("") }
    var tokenVisible by rememberSaveable { mutableStateOf(false) }
    var buttonEnabled by rememberSaveable { mutableStateOf(true) }

    var tryLogging by rememberSaveable { mutableStateOf(false) }

    var showDialog by rememberSaveable {
        mutableStateOf(false)
    }

    if (showDialog) {
        Dialog(onCloseRequest = {
            showDialog = false
        }) {
            Text(
                text = "Failed to login",
                modifier = Modifier.padding(12.dp),
                fontSize = 1.em
            )
        }
    }

    TextField(
        value = token,
        onValueChange = { token = it },
        label = {
            Text("Token")
        },
        shape = RoundedCornerShape(5.dp),
        singleLine = true,
        placeholder = {
            Text("Token")
        },
        visualTransformation = if (tokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (tokenVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (tokenVisible) "Hide Token" else "Show Token"

            IconButton(onClick = { tokenVisible = !tokenVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )
    Spacer(modifier = Modifier.height(50.dp))
    Button(onClick = {
        buttonEnabled = false
        tryLogging = true

        KOOK_CLIENT = KBCClient(JKook.getCore() as CoreImpl, File("plugins"), "Bot $token")

        JKook.getCore()

        LOGGER.debug("Trying to login as bot")

        LOGGER.debug("Checking if the bot token is valid")
        val response = KOOK_CLIENT!!.networkClient.client.newCall(
            Request.Builder()
                .url(HttpAPIRoute.USER_ME.toFullURL())
                .get()
                .build()
        ).execute()

        if (!response.isSuccessful) {
            LOGGER.debug("Login failed because the token is not correct")
            showDialog = true
            KOOK_CLIENT = null
            tryLogging = false
            buttonEnabled = true
            response.close()
            return@Button
        }

        val body = JsonParser.parseString(response.body!!.string()).asJsonObject

        response.close()

        if (body["code"].asInt != 0) {
            LOGGER.debug("Login failed because the token is not correct")
            showDialog = true
            KOOK_CLIENT = null
            tryLogging = false
            buttonEnabled = true
            return@Button
        }

        val userInfo = body["data"].asJsonObject

        LOGGER.info("Login successfully")
        LOGGER.info("Login as ${userInfo["username"].asString}#${userInfo["identify_num"].asString}")

        Runtime.getRuntime().addShutdownHook(Thread(KOOK_CLIENT!!::shutdown))
        KOOK_SCOPE.launch {
            try {
                KOOK_CLIENT!!.start()
            } catch (e: Exception) {
                LOGGER.error("Failed to start client", e)
                KOOK_CLIENT!!.shutdown()
                return@launch
            }
            KOOK_CLIENT!!.loop()
            KOOK_CLIENT!!.shutdown()
        }
        success()
    },
        enabled = buttonEnabled,
        shape = RoundedCornerShape(5.dp),
        modifier = Modifier.clickable { buttonEnabled }
    ) {
        Text("Login")
    }
    if (tryLogging) {
        Popup {
            Indicator()
        }
    }
}