package com.example.tom_and_jerry_game_task_2a

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
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
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

val leckerlioneFont = FontFamily(Font(R.font.leckerlione_regular))

val gameEnderVal = mutableIntStateOf(2)

val playerWantsSound = mutableStateOf(true)
val playerWantsHapticFeedback = mutableStateOf(true)

val showSettings = mutableStateOf(false)

val lightMode = mutableStateOf(true)

val tomImageData = mutableStateOf<ByteArray?>(null)

val randomWord = mutableStateOf("")

//letters ain't getting displayed on screen .. fix it mate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ApiData() {
    val current = LocalDateTime.now()
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val date = current.format(dateFormatter)
    val time = current.format(timeFormatter)
    LaunchedEffect(Unit) {
        try {
            val responseForObstacleLimit = retrofitServiceForObstacleLimit.getObstacleLimit()
            val responseForTomImage = retrofitServiceForTomImage.getTomImage("tom")
            val responseForRandomWord = retrofitServiceForRandomWord.getRandomWord(RandomWordRequest(length = 5))
            val responseForTheme = retrofitServiceForTheme.getTheme(ThemeRequest(date = date , time = time))
            responseForTheme.body()?.let {
                lightMode.value = it.theme=="day"
            }
            responseForRandomWord.body()?.let {
                randomWord.value = it.word
            }
            tomImageData.value = responseForTomImage.bytes()
            Log.d("response" , tomImageData.value.toString())
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

data class LetterChar(
    val letter: Char,
    val lane : Int,
    val yOffset : MutableState<Float>
)

fun checkLetterCollisions(
    collisionCheckingYOffset: MutableState<Float>,
    randomWordList: MutableList<LetterChar>,
    letterChar: LetterChar
) {
    if(
        letterChar.lane == currentPositionOfChar.value &&
        letterChar.yOffset.value in collisionCheckingYOffset.value - 20 .. collisionCheckingYOffset.value + 20
    ) {
        randomWordList.remove(letterChar)
    }
}

@Composable
fun LetterOnScreen(letterChar: LetterChar , randomWordList: MutableList<LetterChar>) {
    val collisionCheckingYOffset = remember { mutableFloatStateOf(1450f) }
    var xOffset = 0f
    val letterBitmap = when(letterChar.letter) {
        'a' -> ImageBitmap.imageResource(id = R.drawable.a)
        'b' -> ImageBitmap.imageResource(id = R.drawable.b)
        'c' -> ImageBitmap.imageResource(id = R.drawable.c)
        'd' -> ImageBitmap.imageResource(id = R.drawable.d)
        'e' -> ImageBitmap.imageResource(id = R.drawable.e)
        'f' -> ImageBitmap.imageResource(id = R.drawable.f)
        'g' -> ImageBitmap.imageResource(id = R.drawable.g)
        'h' -> ImageBitmap.imageResource(id = R.drawable.h)
        'i' -> ImageBitmap.imageResource(id = R.drawable.i)
        'j' -> ImageBitmap.imageResource(id = R.drawable.j)
        'k' -> ImageBitmap.imageResource(id = R.drawable.k)
        'l' -> ImageBitmap.imageResource(id = R.drawable.l)
        'm' -> ImageBitmap.imageResource(id = R.drawable.m)
        'n' -> ImageBitmap.imageResource(id = R.drawable.n)
        'o' -> ImageBitmap.imageResource(id = R.drawable.o)
        'p' -> ImageBitmap.imageResource(id = R.drawable.p)
        'q' -> ImageBitmap.imageResource(id = R.drawable.q)
        'r' -> ImageBitmap.imageResource(id = R.drawable.r)
        's' -> ImageBitmap.imageResource(id = R.drawable.s)
        't' -> ImageBitmap.imageResource(id = R.drawable.t)
        'u' -> ImageBitmap.imageResource(id = R.drawable.u)
        'v' -> ImageBitmap.imageResource(id = R.drawable.v)
        'w' -> ImageBitmap.imageResource(id = R.drawable.w)
        'x' -> ImageBitmap.imageResource(id = R.drawable.x)
        'y' -> ImageBitmap.imageResource(id = R.drawable.y)
        else -> ImageBitmap.imageResource(id = R.drawable.z)
    }
    LaunchedEffect(Unit) {
        delay(10L)
        letterChar.yOffset.value += 5
        checkLetterCollisions(collisionCheckingYOffset,randomWordList,letterChar)
    }
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Transparent)
    ) {
        collisionCheckingYOffset.value = size.height/2 - 50f
        when (letterChar.lane) {
            1 -> xOffset = size.width/6 - 50f
            2 -> xOffset = size.width/2 - 85f
            3 -> xOffset = 4*size.width/5 - 100f
            else -> xOffset = 0f
        }
        drawImage(
            image = letterBitmap,
            topLeft = Offset(xOffset, letterChar.yOffset.value)
        )
    }
}

@Composable
fun WordHandling(randomWordList: MutableList<LetterChar>) {
    val timeFactor = remember { mutableIntStateOf(0) }
    val displayOnScreen = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(1000L)
        timeFactor.value++
    }
    LaunchedEffect(timeFactor.value) {
        if(timeFactor.value%10==0 && timeFactor.value!=0) {
            if(randomWordList.isNotEmpty()) {
                displayOnScreen.value = true
                delay(4000L)
                displayOnScreen.value = false
            }
        }
    }
    if(displayOnScreen.value) {
        LetterOnScreen(randomWordList.first() , randomWordList)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun firstPage(navController: NavController) {
    val context = LocalContext.current
    ApiData()
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
    Column(
        modifier = Modifier.padding(top = 720.dp , start = 300.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.twotone_help_24),
            contentDescription = "rules page",
            modifier = Modifier
                .scale(2.5f)
                .clickable {
                    navController.navigate("rulesPage")
                }
        )
    }
}

@Composable
fun rulesPage() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val imageRequest = ImageRequest.Builder(LocalContext.current)
            .data(tomImageData.value)
            .build()
        val imagePainter = rememberAsyncImagePainter(imageRequest)
        Image(
            painter = imagePainter,
            contentDescription = "Tom Image from API",
            modifier = Modifier.fillMaxSize()
        )
        Log.d("tom image" , "I am here")
    }
}

fun getSavedText(sharedPreferences: SharedPreferences, key : String) : String {
    return sharedPreferences.getString( key, "0") ?: "0"
}

fun saveText(sharedPreferences: SharedPreferences, key: String, value: String) {
    sharedPreferences.edit().putString(key , value).apply()
}

