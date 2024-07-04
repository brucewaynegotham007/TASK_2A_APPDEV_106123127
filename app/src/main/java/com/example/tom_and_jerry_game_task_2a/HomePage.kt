package com.example.tom_and_jerry_game_task_2a

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.delay
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
val randomWord = mutableStateOf("")
val complimentOfRandomWord = mutableStateOf("")

//differentiate between the captured and yet to be captured letters in the random word
//third gift reward or punishment implementation to be done ..
//rest is done I guess

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
            val responseForTheme = retrofitServiceForTheme.getTheme(ThemeRequest(date = date , time = time))
            responseForTheme.body()?.let {
                lightMode.value = it.theme=="day"
            }
            gameEnderVal.value = responseForObstacleLimit.limit
        }
        catch (e: Exception) {
            Log.d("obsLim" , "ERROR : ${e.message}")
        }
    }
    LaunchedEffect(Unit) {
        val lengthOfRandomWord = Random.nextInt(4,8)
        val responseForRandomWord = retrofitServiceForRandomWord.getRandomWord(RandomWordRequest(length = lengthOfRandomWord))
        try {
            responseForRandomWord.body()?.let {
                randomWord.value = it.word
                Log.d("randomWord" , randomWord.value)
            }
        }
        catch(e : Exception) {
            Log.d("App might crash" , "Sorry")
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
    randomWordList: SnapshotStateList<LetterChar>
) {
    if(
        randomWordList.isNotEmpty() &&
        randomWordList.first().lane == currentPositionOfChar.value &&
        randomWordList.first().yOffset.value in collisionCheckingYOffset.value - 50f .. collisionCheckingYOffset.value + 50f
    ) {
        randomWordList.remove(randomWordList.first())
        complimentOfRandomWord.value += randomWord.value[0]
        randomWord.value = randomWord.value.drop(1)
        Log.d("randomWordChanged?" , randomWord.value)
        wordChanged.value = true
    }
    else if(
        randomWordList.isNotEmpty() &&
        randomWordList.first().yOffset.value >= collisionCheckingYOffset.value*2
    ) {
        randomWordList.remove(randomWordList.first())
        randomWord.value = randomWord.value.drop(0)
        Log.d("randomWordChanged?" , randomWord.value)
    }
    else {
        //do nothing
    }
}

@Composable
fun LetterOnScreen(randomWordList: SnapshotStateList<LetterChar>) {
    if(randomWordList.isNotEmpty()) {
        val collisionCheckingYOffset = remember { mutableFloatStateOf(1450f) }
        var xOffset = 0f
        val letterBitmap = when (randomWordList.first().letter) {
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
            'z' -> ImageBitmap.imageResource(id = R.drawable.z)
            else -> ImageBitmap.imageResource(id = R.drawable.ak_remove_bg)
        }
        LaunchedEffect(Unit) {
            while (true) {
                delay(10L)
                if(randomWordList.isNotEmpty()) {
                    randomWordList.first().yOffset.value += 9
                }
                checkLetterCollisions(collisionCheckingYOffset, randomWordList)
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent)
            ) {
                collisionCheckingYOffset.value = size.height / 2
                when (randomWordList.first().lane) {
                    1 -> xOffset = size.width / 6 - 80f
                    2 -> xOffset = size.width / 2 - 120f
                    3 -> xOffset = 4 * size.width / 5 - 150f
                    else -> xOffset = 0f
                }
                drawImage(
                    image = letterBitmap,
                    topLeft = Offset(xOffset, randomWordList.first().yOffset.value)
                )
            }
        }
    }
}

@Composable
fun WordHandling() {
    val yOffsetSetter = remember { mutableFloatStateOf(0f) }
    val randomWordList = remember { mutableStateListOf<LetterChar>() }
    val timeFactor = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while(true) {
            delay(10000L)
            timeFactor.value += 10
        }
    }
    LaunchedEffect(timeFactor.value){
        if (randomWord.value.isNotEmpty()) {
            val separateChars = randomWord.value.toList()
            val lane = Random.nextInt(1, 4)
            val letter = separateChars.first()
            yOffsetSetter.value = 0f
            val newLetterChar = LetterChar(letter, lane, yOffsetSetter)
            randomWordList.add(newLetterChar)
        }
    }
    if(randomWordList.isNotEmpty()){
        Log.d("randomWordListOutside" , randomWordList.toString())
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LetterOnScreen(randomWordList)
        }
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
        TomImage()
        JerryImage()
        ObstacleImage()
    }
}

@Composable
fun TomImage() {
    var tomImageData by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(Unit) {
        try {
            val responseForTomImage = retrofitServiceForTomImage.getTomImage("tom")
            tomImageData = responseForTomImage.bytes()
        } catch (e: Exception) {
            Log.d("tomImage", "ERROR: ${e.message}")
        }
    }
    Column(
        modifier = Modifier.size(446.4.dp , 276.8.dp)
    ) {
        tomImageData?.let {imageData ->
            val bitmap = remember(imageData) {
                BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            }
            bitmap?.let {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Tom Image from API",
                    modifier = Modifier
                        .size(558.dp, 346.dp)
                        .scale(0.8f)
                )
                Log.d("Tom Image" , "Image loaded")
            } ?: run {
                Log.d("Tom Image" , "Failed to decode bitmap")
            }
        } ?: run {
            Text(text = "Image Loading ... ")
        }
    }
}

@Composable
fun JerryImage() {
    var jerryImageData by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(Unit) {
        try {
            val responseForJerryImage = retrofitServiceForJerryImage.getJerryImage("jerry")
            jerryImageData = responseForJerryImage.bytes()
        } catch (e: Exception) {
            Log.d("tomImage", "ERROR: ${e.message}")
        }
    }
    Column(
        modifier = Modifier.size(400.dp)
    ) {
        jerryImageData?.let {imageData ->
            val bitmap = remember(imageData) {
                BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            }
            bitmap?.let {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Jerry Image from API",
                    modifier = Modifier
                        .size(357.6.dp, 291.2.dp)
                        .scale(0.8f)
                )
                Log.d("Jerry Image" , "Image loaded")
            } ?: run {
                Log.d("Jerry Image" , "Failed to decode bitmap")
            }
        } ?: run {
            Text(text = "Image Loading ... ")
        }
    }
}

@Composable
fun ObstacleImage() {
    var obstacleImageData by remember { mutableStateOf<ByteArray?>(null) }
    LaunchedEffect(Unit) {
        try {
            val responseForObstacleImage = retrofitServiceForObstacleImage.getObstacleImage("obstacle")
            obstacleImageData = responseForObstacleImage.bytes()
        } catch (e: Exception) {
            Log.d("tomImage", "ERROR: ${e.message}")
        }
    }
    Column(
        modifier = Modifier.size(300.dp , 300.dp)
    ) {
        obstacleImageData?.let {imageData ->
            val bitmap = remember(imageData) {
                BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            }
            bitmap?.let {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Tom Image from API",
                    modifier = Modifier
                        .size(300.dp, 300.dp)
                        .scale(0.8f)
                )
                Log.d("Obstacle Image" , "Image loaded")
            } ?: run {
                Log.d("Obstacle Image" , "Failed to decode bitmap")
            }
        } ?: run {
            Text(text = "Image Loading ... ")
        }
    }
}

fun getSavedText(sharedPreferences: SharedPreferences, key : String) : String {
    return sharedPreferences.getString( key, "0") ?: "0"
}

fun saveText(sharedPreferences: SharedPreferences, key: String, value: String) {
    sharedPreferences.edit().putString(key , value).apply()
}

