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
import net.deechael.kookdesktop.util.Case
import net.deechael.kookdesktop.util.Controller
import net.deechael.kookdesktop.util.Switcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login() {
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
                    LoginUser()
                }
                Case("1") {
                    LoginBot()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginUser() {
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var buttonEnabled by rememberSaveable { mutableStateOf(true) }

    var tryLogging by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = phoneNumber,
        onValueChange = { phoneNumber = it },
        label = {
            Text("Phone Number")
        },
        shape = RoundedCornerShape(5.dp),
        singleLine = true,
        placeholder = {
            Text("Password")
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
        // Invoke below if login failed
//
//            tryLogging = false
//            buttonEnabled = true
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
fun LoginBot() {
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var buttonEnabled by rememberSaveable { mutableStateOf(true) }

    var tryLogging by rememberSaveable { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = { password = it },
        label = {
            Text("Token")
        },
        shape = RoundedCornerShape(5.dp),
        singleLine = true,
        placeholder = {
            Text("Token")
        },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (passwordVisible) "Hide Token" else "Show Token"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )
    Spacer(modifier = Modifier.height(50.dp))
    Button(onClick = {
        buttonEnabled = false
        tryLogging = true
        // Invoke below if login failed
//
//            tryLogging = false
//            buttonEnabled = true
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