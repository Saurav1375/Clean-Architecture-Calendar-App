package com.example.litecalendar.presentation

import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.litecalendar.data.auth.AuthRepositoryImpl
import com.example.litecalendar.domain.model.Event
import com.example.litecalendar.domain.model.EventParcel
import com.example.litecalendar.presentation.home_screen.HomeScreen
import com.example.litecalendar.presentation.home_screen.HomeScreenViewModel
import com.example.litecalendar.presentation.home_screen.Screen
import com.example.litecalendar.presentation.auth_screen.AuthScreen
import com.example.litecalendar.presentation.auth_screen.AuthViewModel
import com.example.litecalendar.presentation.home_screen.components.day_view.DayView
import com.example.litecalendar.presentation.home_screen.components.events_view.MainViewScreen
import kotlinx.coroutines.launch
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    applicationContext : Context,
    navController: NavHostController,
    authUiClient : AuthRepositoryImpl
) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = viewModel()
    val state by authViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        if (authUiClient.getSignedInUser() != null) {
            navController.navigate(Screen.MonthViewScreen.route)
        }
    }
    NavHost(
        navController = navController,
        startDestination = Screen.AuthScreen.route,
        route = "parent"
    ) {

        composable(Screen.AuthScreen.route) {
            val context = LocalContext.current as Activity
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        scope.launch {
                            val signInResult = authUiClient.SignInWithIntent(
                                intent = result.data ?: return@launch
                            )
                            authViewModel.onSignInResult(signInResult)

                        }

                    }

                }
            )

            LaunchedEffect(key1 = (state.isSignInSuccessful && authUiClient.getSignedInUser() != null)) {
                if (state.isSignInSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Sign In Successful",
                        Toast.LENGTH_LONG
                    ).show()

                    navController.navigate(Screen.MonthViewScreen.route)
                    authViewModel.resetState()
                }

            }
            AuthScreen {
                scope.launch {
                    val signInIntentSender = authUiClient.SignIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )

                }

            }
        }

        composable(Screen.MonthViewScreen.route) {
            HomeScreen(viewModel = viewModel, navController =  navController, user = authUiClient.getSignedInUser()) { date ->
                navController.navigate(Screen.DayViewScreen.route + "/$date")
            }
        }

        composable(Screen.DayViewScreen.route + "/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            DayView(dateParam = date, navController = navController, viewModel = viewModel, userData = authUiClient.getSignedInUser())
        }

        composable(Screen.MainItemViewScreen.route + "/{itemParcel}") { backStackEntry ->
            val item = navController.previousBackStackEntry?.savedStateHandle?.get<Any>("itemParcel")
                ?: EventParcel(
                    "",
                    "",
                    "",
                    "",
                    type = "EVENT",
                    LocalDate.now().toString(),
                    LocalDate.now().plusDays(1).toString(),
                    "02:20",
                    "04:30",
                    "",
                    false,
                    5,
                    true,
                    "EVERYDAY"
                )
            MainViewScreen(navController = navController,item = item, viewModel = viewModel, user = authUiClient.getSignedInUser())
        }

        composable(Screen.SearchScreen.route) {
            SearchScreen(navController = navController, viewModel = viewModel )
        }
    }
}
