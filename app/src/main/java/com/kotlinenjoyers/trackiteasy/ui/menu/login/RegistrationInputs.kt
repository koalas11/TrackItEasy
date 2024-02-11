package com.kotlinenjoyers.trackiteasy.ui.menu.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kotlinenjoyers.trackiteasy.R
import com.kotlinenjoyers.trackiteasy.repository.firebase.FirebaseAuthRepository
import com.kotlinenjoyers.trackiteasy.ui.MainActivity
import com.kotlinenjoyers.trackiteasy.util.ConnectivityObserver

@Composable
fun RegistrationInputs(
    modifier: Modifier = Modifier,
    changeState: (AccountState) -> Unit,
    networkStatus: ConnectivityObserver.Status,
) {
    val scope = rememberCoroutineScope()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var pwconfirm by rememberSaveable { mutableStateOf("") }
    var error : String? by rememberSaveable {
        mutableStateOf(null)
    }
    val auth = (LocalContext.current as MainActivity).auth
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        value = email,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        label = { Text("Mail") },
        onValueChange = {email = it},
        singleLine = true,
    )

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        value = password,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        label = { Text("Password") },
        onValueChange = {password = it},
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    )

    TextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        value = pwconfirm,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        ),
        label = { Text("${stringResource(id = R.string.confirm)} Password") },
        onValueChange = {pwconfirm = it},
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    )

    if (error != null)
        Text(
            modifier = modifier,
            text = error!!,
            textAlign = TextAlign.Center,
        )

    Button(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth(),
        onClick = {
                if (email.trim() == "") {
                    error = "Email empty"
                    return@Button
                }
                if (password.trim() == "") {
                    error = "Password empty"
                    return@Button
                }
                if (password != pwconfirm) {
                    error = "Password is not equal to Confirm Password"
                    return@Button
                }
                val setError: (String) -> Unit = {
                    error = it
                }

                FirebaseAuthRepository.register(auth, email.trim(), password.trim(), scope, changeState, setError)
            },
        shape = MaterialTheme.shapes.small,
        enabled = networkStatus == ConnectivityObserver.Status.Available,
    ) {
        Text(
            modifier = modifier,
            text = stringResource(id = R.string.register),
        )
    }
}