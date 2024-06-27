package com.example.tom_and_jerry_game_task_2a

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

val leckerlioneFont = FontFamily(Font(R.font.leckerlione_regular))

val gameEnderVal = mutableIntStateOf(2)

val playerWantsSound = mutableStateOf(true)
val playerWantsHapticFeedback = mutableStateOf(true)

val showSettings = mutableStateOf(false)

val lightMode = mutableStateOf(true)

@Composable
fun ApiData() {
    LaunchedEffect(Unit) {
        try {
            val responseForObstacleLimit = retrofitServiceForObstacleLimit.getObstacleLimit()
            Log.d("obsLim" , responseForObstacleLimit.limit.toString())
            gameEnderVal.value = responseForObstacleLimit.limit
        }
        catch (e: Exception) {
            Log.d("obsLim" , "ERROR : ${e.message}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun settings() {
    AlertDialog(onDismissRequest = { showSettings.value = false }) {
        Card(
            modifier = Modifier.size(350.dp, 450.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SETTINGS",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Haptic Feedback",
                        modifier = Modifier.padding(top = 10.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(start = 10.dp))
                    Switch(
                        checked = playerWantsHapticFeedback.value,
                        onCheckedChange = {
                            playerWantsHapticFeedback.value = !playerWantsHapticFeedback.value
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(top = 0.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sound Effects",
                        modifier = Modifier.padding(top = 10.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(start = 10.dp))
                    Switch(
                        checked = playerWantsSound.value,
                        onCheckedChange = {
                            playerWantsSound.value = !playerWantsSound.value
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select gun type : ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row() {
                        Spacer(modifier = Modifier.padding(start = 30.dp))
                        RadioButton(
                            selected = gunType.value == 0,
                            onClick = {
                                gunType.value = 0
                            }
                        )
                        Card(
                            modifier = Modifier.size(40.dp, 40.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.gun_no_bg),
                                    contentDescription = "Pistol"
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RadioButton(
                            selected = gunType.value == 1,
                            onClick = {
                                gunType.value = 1
                            }
                        )
                        Card(
                            modifier = Modifier.size(50.dp,50.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ak_remove_bg),
                                    contentDescription = "AK"
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ){
                    Spacer(modifier = Modifier.padding(start = 30.dp))
                    Row() {
                        RadioButton(
                            selected = gunType.value == 2,
                            onClick = {
                                gunType.value = 2
                            }
                        )
                        Card(
                            modifier = Modifier.size(50.dp,50.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.scar_remove_bg),
                                    contentDescription = "Scar"
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        RadioButton(
                            selected = gunType.value == 3,
                            onClick = {
                                gunType.value = 3
                            }
                        )
                        Card(
                            modifier = Modifier.size(50.dp,50.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.m416_remove_bg),
                                    contentDescription = "M416"
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.padding(top =20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Light Mode",
                        modifier = Modifier.padding(top = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.padding(start = 10.dp))
                    Switch(
                        checked = !lightMode.value,
                        onCheckedChange = {
                            lightMode.value = !lightMode.value
                        }
                    )
                    Spacer(modifier = Modifier.padding(start = 10.dp))
                    Text(
                        text = "Dark Mode",
                        modifier = Modifier.padding(top = 10.dp),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun firstPage(navController: NavController) {
    ApiData()
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier,
        factory = { context ->
            ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setImageResource(R.drawable.homepage_bg)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { /* No update needed */ }
    )
    if(showSettings.value) {
        settings()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, end = 20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.End
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_settings_24),
            contentDescription = "Settings",
            modifier = Modifier
                .scale(2f)
                .clickable {
                    showSettings.value = true
                }
        )
    }
    Column(
        modifier = Modifier
            .padding(top = 100.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHEESE",
            fontFamily = leckerlioneFont,
            fontSize = 70.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            color = Color(130,50,250)
        )
        Text(
            text = "CHASE",
            fontFamily = leckerlioneFont,
            fontSize = 70.sp,
            fontWeight = FontWeight.ExtraBold,
            fontStyle = FontStyle.Italic,
            color = Color(130,50,250)
        )
    }
    val sharedPreferences = context.getSharedPreferences("Cheese Chase" , Context.MODE_PRIVATE)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 300.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Highest Score : ${getSavedText(sharedPreferences,"High Score")}",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Red
        )
    }
    Column(
        modifier = Modifier.padding(top = 700.dp , start = 100.dp)
    ) {
        Button(
            onClick = { navController.navigate("gamePageBase") },
            modifier = Modifier.size(180.dp,60.dp),
            colors = ButtonDefaults.buttonColors(Color.Cyan)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PLAY",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

fun getSavedText(sharedPreferences: SharedPreferences, key : String) : String {
    return sharedPreferences.getString( key, "0") ?: "0"
}

fun saveText(sharedPreferences: SharedPreferences, key: String, value: String) {
    sharedPreferences.edit().putString(key , value).apply()
}

