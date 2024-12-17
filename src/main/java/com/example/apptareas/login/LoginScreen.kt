package com.example.apptareas.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.apptareas.ui.theme.AppTareasTheme

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToSignUpPage: () -> Unit,
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.loginError != null
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        if (isError) {
            Text(
                text = loginUiState?.loginError ?: "unknown error",
                color = MaterialTheme.colorScheme.error,
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState?.userName?:"",
            onValueChange ={loginViewModel?.onUserNameChange(it)},
            leadingIcon = {
                Icon(imageVector= Icons.Default.Person,
                    contentDescription = null,
                    )
            },
            label = {
                Text(text = "Correo Electronico")
            },
            isError = isError
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState?.password?:" ",
            onValueChange ={loginViewModel?.onPasswordNameChange(it)},
            leadingIcon = {
                Icon(imageVector= Icons.Default.Lock,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        Button(onClick = {loginViewModel?.loginUser(context)}) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ){
            Text(text = "¿No tienes una Cuenta?")
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = {onNavToSignUpPage.invoke()}) {
                Text(text = "SignUp")
            }
        }
        if(loginUiState?.isLoading == true){
            CircularProgressIndicator()
        }
        LaunchedEffect(key1 = loginViewModel?.hasUser) {
            if (loginViewModel?.hasUser == true){
                onNavToHomePage.invoke()
            }
        }
    }
}

@Composable
fun SignUpScreen(
    loginViewModel: LoginViewModel? = null,
    onNavToHomePage: () -> Unit,
    onNavToLoginPage: () -> Unit,
) {
    val loginUiState = loginViewModel?.loginUiState
    val isError = loginUiState?.signUpError != null
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize()
        .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Sign Up",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        if (isError) {
            Text(
                text = loginUiState?.signUpError ?: "unknown error",
                color = MaterialTheme.colorScheme.error,
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState?.userNameSignUp?:" ",
            onValueChange ={loginViewModel?.onUserNameChangeSignup(it)},
            leadingIcon = {
                Icon(imageVector= Icons.Default.Person,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Correo Electronico")
            },
            isError = isError
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState?.passwordSignUp?:" ",
            onValueChange ={loginViewModel?.onPasswordChangeSignup(it)},
            leadingIcon = {
                Icon(imageVector= Icons.Default.Lock,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            value = loginUiState?.confirmPasswordSignUp?:" ",
            onValueChange ={loginViewModel?.onConfirmPasswordChange(it)},
            leadingIcon = {
                Icon(imageVector= Icons.Default.Lock,
                    contentDescription = null,
                )
            },
            label = {
                Text(text = "Confirmar Contraseña")
            },
            visualTransformation = PasswordVisualTransformation(),
            isError = isError
        )

        Button(onClick = {loginViewModel?.createUser(context)}) {
            Text(text = "Sign In")
        }
        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ){
            Text(text = "¿Ya tienes una Cuenta?")
            Spacer(modifier = Modifier.size(8.dp))
            TextButton(onClick = {onNavToLoginPage.invoke()}) {
                Text(text = "Sign In")
            }
        }
        if(loginUiState?.isLoading == true){
            CircularProgressIndicator()
        }
        LaunchedEffect(key1 = loginViewModel?.hasUser) {
            if (loginViewModel?.hasUser == true){
                onNavToHomePage.invoke()
            }
        }
    }
}




@Preview
@Composable
fun PrevLoginScreen(){
    AppTareasTheme {
        LoginScreen(onNavToHomePage = {}) { }
    }
}

@Preview
@Composable
fun PrevSignUpScreen(){
    AppTareasTheme {
        SignUpScreen(onNavToHomePage = {}) { }
    }
}



