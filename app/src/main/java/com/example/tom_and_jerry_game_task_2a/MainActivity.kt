package com.example.tom_and_jerry_game_task_2a

import android.content.Context
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.getSystemService
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tom_and_jerry_game_task_2a.ui.theme.Tom_and_Jerry_game_task_2aTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Tom_and_Jerry_game_task_2aTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    myApp()
                }
            }
        }
    }
}

@Composable
fun myApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "homePage") {
        composable("gamePageBase") {
            gamePageBase(navController)
        }
        composable("homePage") {
            firstPage(navController)
        }
    }
}

val currentPositionOfChar = mutableIntStateOf(1)
val previousPositionOfChar = mutableIntStateOf(1)
val xOffsetForTom = mutableFloatStateOf(0f)
val xOffsetForJerry = mutableFloatStateOf(0f)
val xOffsetForTomCanvas = mutableFloatStateOf(0f)
val xOffsetForJerryCanvas = mutableFloatStateOf(0f)
val xOffsetForGunCanvas = mutableFloatStateOf(0f)
val xOffsetForImmunityCanvas = mutableFloatStateOf(0f)
val firstBoxVal = mutableStateOf("PLAYER")
val score = mutableIntStateOf(0)
val gameEnded = mutableStateOf(false)
val multiplier = mutableFloatStateOf(1f)
val immunity = mutableStateOf(false)
val scoreOnlyMultiplier = mutableFloatStateOf(1f)
val obsOnlyMultiplier = mutableFloatStateOf(1f)
val gunType = mutableIntStateOf(0)

@Composable
fun powerUps(cheeseCount: MutableState<Int> , count: MutableState<Int>) {
    Column(
        modifier = Modifier,
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            val showExtraBullet = remember { mutableStateOf(true) }
            if(showExtraBullet.value) {
                Card(
                    modifier = Modifier.size(50.dp, 50.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.extra_bullet_icon),
                            contentDescription = "extra bullet",
                            modifier = Modifier.clickable {
                                cheeseCount.value += 4
                                showExtraBullet.value = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
            val showExtraLife = remember { mutableStateOf(true) }
            if(showExtraLife.value) {
                Card(
                    modifier = Modifier.size(50.dp, 50.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.extra_life_icon),
                            contentDescription = "extra life",
                            modifier = Modifier.clickable {
                                if (count.value > 0) count.value--
                                showExtraLife.value = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(horizontal = 5.dp))
            val triggerImmunity = remember { mutableStateOf(false) }
            val showImmunity = remember { mutableStateOf(true) }
            if(showImmunity.value) {
                Card(
                    modifier = Modifier.size(50.dp, 50.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.immunity_icon),
                            contentDescription = "immunity",
                            modifier = Modifier.clickable {
                                triggerImmunity.value = true
                                showImmunity.value = false
                            }
                        )
                        LaunchedEffect(triggerImmunity.value) {
                            if (triggerImmunity.value) {
                                immunity.value = true
                                delay(10000L)
                                immunity.value = false
                                triggerImmunity.value = false
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun checkingPage(obs: Obstacle, cheeseCount: MutableState<Int>, count: MutableState<Int>) {

    Column(
        modifier = Modifier.fillMaxSize()
    ){

        val lionBitmap = ImageBitmap.imageResource(id = R.drawable.lion_obstacle)
        val cheeseBitmap = ImageBitmap.imageResource(id = R.drawable.cheese_no_bg)
        val treeBitmap = ImageBitmap.imageResource(id = R.drawable.tree_obstacle)
        val lakeBitmap = ImageBitmap.imageResource(id = R.drawable.lake_obstacle)
        val giftBitmap = ImageBitmap.imageResource(id = R.drawable.punishment_or_reward)
        val noImage = ImageBitmap.imageResource(id = R.drawable.alternate_tracks)

        val imgBitmap = when (obs.type) {
            "Lake" -> lakeBitmap
            "Lion" -> lionBitmap
            "tree" -> treeBitmap
            "Cheese" -> cheeseBitmap
            "Gift" -> giftBitmap
            else -> noImage
        }

        var xOffset = 0f
        val collisionCheckingYOffset = remember { mutableFloatStateOf(1450f) }

        LaunchedEffect(obs) {
            while (obs.inScreen.value) {
                delay(25L)
                obs.yOffset.value += 15
                if (obs.yOffset.value > 2800) {
                    obstacleList.remove(obs)
                    //changing the visibility of obs.inScreen deemed unnecessary
                }
                checkCollisions(obs,cheeseCount,count,collisionCheckingYOffset)
            }
        }

        AnimatedVisibility(
            visible = obs.inScreen.value,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + slideInVertically(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + slideOutVertically(
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent)
            ) {
                collisionCheckingYOffset.value = size.height/2 - 50f
                when (obs.lane) {
                    1 -> xOffset = size.width/6 - 50f
                    2 -> xOffset = size.width/2 - 85f
                    3 -> xOffset = 4*size.width/5 - 100f
                    else -> xOffset = 0f
                }
                drawImage(
                    image = imgBitmap,
                    topLeft = Offset(xOffset, obs.yOffset.value)
                )
            }
        }
    }
}

@Composable
fun gyroscopeSetup(context : Context) {
    val sensorManager = context.getSystemService(SensorManager::class.java)
    var gyroscopeValues by remember { mutableStateOf(floatArrayOf(0f,0f,0f)) }
    val angularPosition = remember { mutableFloatStateOf(0f) }

    DisposableEffect(sensorManager) {
        val gyroscopeSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.values?.let {values ->
                    gyroscopeValues = values
                    when {
                        values[1] > 3.0 -> if(currentPositionOfChar.value<3) currentPositionOfChar.value++
                        values[1] < -3.0 -> if(currentPositionOfChar.value>1) currentPositionOfChar.value--
                        else -> currentPositionOfChar.value = currentPositionOfChar.value
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                //blank
            }
        }
        sensorManager?.registerListener(sensorEventListener,gyroscopeSensor,SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager?.unregisterListener(sensorEventListener)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun vibration(context : Context) {
    val vibrator = remember(context) {
        val vibrationManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibrationManager.defaultVibrator
    }
    val vibrationEffect = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)
    vibrator.vibrate(vibrationEffect)
}

@Composable
fun bgSound(context : Context) {

    val mediaPlayer = remember {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        MediaPlayer.create(context , R.raw.bg_music_for_cheese_chase).apply {
            isLooping = true
            setAudioAttributes(audioAttributes)
            setOnPreparedListener {
                start()
            }
            setOnErrorListener { _, _, _ ->
                false
            }
        }
    }
    mediaPlayer.setVolume(0.4f,0.4f)

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

@Composable
fun playCollisionSound(context : Context) {
    val mediaPlayer = remember {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        MediaPlayer.create(context , R.raw.collision_sound_trimmed).apply {
            isLooping = false
            setAudioAttributes(audioAttributes)
            setOnPreparedListener {
                start()
            }
            setOnErrorListener { _, _, _ ->
                false
            }
        }
    }
    mediaPlayer.setVolume(1.0f,1.0f)

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(Unit) {
        delay(2000L)
        playCollision.value = false
    }
}

@Composable
fun playWinSound(context : Context) {
    val mediaPlayer = remember {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        MediaPlayer.create(context , R.raw.win_sound).apply {
            isLooping = false
            setAudioAttributes(audioAttributes)
            setOnPreparedListener {
                start()
            }
            setOnErrorListener { _, _, _ ->
                false
            }
        }
    }
    mediaPlayer.setVolume(1.0f,1.0f)

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

@Composable
fun playJumpingSound(context : Context , playJumpingSound : MutableState<Boolean>) {
    val mediaPlayer = remember {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        MediaPlayer.create(context , R.raw.jumping_sound_trimmed).apply {
            isLooping = false
            setAudioAttributes(audioAttributes)
            setOnPreparedListener {
                start()
            }
            setOnErrorListener { _, _, _ ->
                false
            }
        }
    }
    mediaPlayer.setVolume(1.0f,1.0f)

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(Unit) {
        delay(2000L)
        playJumpingSound.value = false
    }
}

@Composable
fun gamePageBase(navController: NavController) {

    var localContext = LocalContext.current

    gyroscopeSetup(
        context = localContext
    )

    if(!gameEnded.value) {
        bgSound(context = localContext)
    }

    if(playCollision.value) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && playerWantsHapticFeedback.value){
            vibration(localContext)
        }
        if(playerWantsSound.value && !gameEnded.value){
            playCollisionSound(localContext)
        }
    }

    val cheeseCount = remember { mutableIntStateOf(0) }
    val count = remember { mutableIntStateOf(0) }
    var actualCheeseCount = cheeseCount.value/4 + cheeseCount.value%4

    AndroidView(
        modifier = Modifier,
        factory = { context ->
            ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setImageResource(R.drawable.track2png2)
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->

        }
    )
    Column(
        modifier = Modifier.animateContentSize()
    ) {
        gamePageObstacles(navController,cheeseCount, count)
    }
    val xOffsetJerryCanvas by animateFloatAsState(
        targetValue = xOffsetForJerryCanvas.value,
        animationSpec = tween(durationMillis = 500),
        label = "Jerry jump animation"
    )
    val xOffsetTomCanvas by animateFloatAsState(
        targetValue = xOffsetForTomCanvas.value,
        animationSpec = tween(durationMillis = 500),
        label = "Tom jump animation"
    )
    val xOffsetGunCanvas by animateFloatAsState(
        targetValue = xOffsetForGunCanvas.value,
        animationSpec = tween(durationMillis = 500),
        label = "Tom jump animation"
    )
    val xOffsetImmunityCanvas by animateFloatAsState(
        targetValue = xOffsetForImmunityCanvas.value,
        animationSpec = tween(durationMillis = 500),
        label = "Tom jump animation"
    )
    val yOffsetForJerry = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(currentPositionOfChar.value) {
        delay(250)
        previousPositionOfChar.value = currentPositionOfChar.value
    }
    yOffsetForJerry.value = when(currentPositionOfChar.value == previousPositionOfChar.value) {
        true -> 0f
        false -> -150f
    }
    val yOffsetJerry by animateFloatAsState(
        targetValue = yOffsetForJerry.value,
        animationSpec = tween(durationMillis = 250),
        label = "Jerry y jump hopefully"
    )
    val jumpHeight = remember { Animatable(0f) }
    val jumpHeightCanvas = remember { Animatable(0f)}
    LaunchedEffect(Unit) {
        launch {
            jumpHeight.animateTo(
                targetValue = -10f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 150, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            jumpHeightCanvas.animateTo(
                targetValue = -100f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 150, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val jerryBitmap = ImageBitmap.imageResource(id = R.drawable.jerry_alternate)
        val tomBitmap = ImageBitmap.imageResource(id = R.drawable.tom_alternate)
        val gunBitmap = when(gunType.value) {
            0 -> ImageBitmap.imageResource(id = R.drawable.gun_no_bg)
            1 -> ImageBitmap.imageResource(id = R.drawable.ak_remove_bg)
            2 -> ImageBitmap.imageResource(id = R.drawable.scar_remove_bg)
            else -> ImageBitmap.imageResource(id = R.drawable.m416_remove_bg)
        }
        val immunityBitmap = ImageBitmap.imageResource(id = R.drawable.immunity_ring)
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            when(currentPositionOfChar.value) {
                1-> {
                    xOffsetForTomCanvas.value = -50f
                    xOffsetForJerryCanvas.value = -350f
                    xOffsetForGunCanvas.value = 200f
                    xOffsetForImmunityCanvas.value = -100f
                }
                2-> {
                    xOffsetForTomCanvas.value = size.width/3
                    xOffsetForJerryCanvas.value = size.width/3
                    xOffsetForGunCanvas.value = size.width/3 + 300f
                    xOffsetForImmunityCanvas.value = size.width/3 + 100f
                }
                3-> {
                    xOffsetForTomCanvas.value = 2 * size.width/3
                    xOffsetForJerryCanvas.value = size.width - 100
                    xOffsetForGunCanvas.value = size.width-100f
                    xOffsetForImmunityCanvas.value = size.width - 100f
                }
            }
//            Log.d("canvas width", size.width.toString())
            withTransform({
                scale(scale = 0.5f)
            }) {
                drawImage(
                    image = jerryBitmap,
                    topLeft = Offset(xOffsetJerryCanvas, 300f + size.height/2 + yOffsetJerry + jumpHeight.value)
                )
            }
            withTransform({
                scale(scale = 0.8f)
            }) {
                if(count.value>0) {
                    drawImage(
                        image = tomBitmap,
                        topLeft = Offset(xOffsetTomCanvas, 3*size.height/4 + yOffsetJerry + jumpHeight.value -100)
                    )
                }
            }
            withTransform({
                scale(scale = 0.8f)
            }) {
                drawImage(
                    image = gunBitmap,
                    topLeft = Offset(xOffsetGunCanvas,size.height/2 + yOffsetJerry + jumpHeight.value - 50)
                )
            }
            withTransform({
                scale(scale = 0.6f)
            }) {
                if(immunity.value){
                    drawImage(
                        image = immunityBitmap,
                        topLeft = Offset(xOffsetImmunityCanvas, size.height/2 + yOffsetJerry+ 200)
                    )
                }
            }
        }
    }
    val shotFired = remember { mutableStateOf(false) }
    val bulletXOffset = remember { mutableFloatStateOf(0f) }
    val bulletYOffset = remember { mutableFloatStateOf(3450f) }

    LaunchedEffect(shotFired.value) {
        when(currentPositionOfChar.value) {
            1 -> bulletXOffset.value = -450f
            2 -> bulletXOffset.value = 500f
            3 -> bulletXOffset.value = 1500f
        }
        while(shotFired.value){
            delay(25L)
            bulletYOffset.value -= 15
            if(bulletYOffset.value<=-2000) {
                shotFired.value = false
                bulletYOffset.value = 3450f
            }
        }
    }

//    Log.d("shotFired",shotFired.value.toString())

    val bulletLane = remember { mutableIntStateOf(1) }
    LaunchedEffect(shotFired.value) {
        bulletLane.value = currentPositionOfChar.value
    }

    LaunchedEffect(bulletYOffset.value) {
        for (i in obstacleList) {
//            if (i.lane == currentPositionOfChar.value) {
//                Log.d("bullet", bulletYOffset.value.toString())
//                Log.d("obstacle", i.yOffset.value.toString())
//            }
            if (i.lane == bulletLane.value
                &&
                bulletYOffset.value in
                2.37931034 * i.yOffset.value -50f..2.37931034 * i.yOffset.value + 50f
                &&
                (i.type != "Cheese" && i.type != "Gift")
            ) {
                obstacleList.remove(i)
                shotFired.value = false
                bulletYOffset.value = 3450f
                break
            }
            else {
                //do nothing
            }
        }
    }

    val bulletBitmap = ImageBitmap.imageResource(id = R.drawable.bullet_no_bg)
    if(shotFired.value){
        Column(
            modifier = Modifier.animateContentSize()
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent)
            ) {
                withTransform({
                    scale(scale = 0.4f)
                    translate(0f,-2100f)
                }) {
                    drawImage(
                        image = bulletBitmap,
                        topLeft = Offset((bulletXOffset.value),bulletYOffset.value)
                    )
                }
            }
        }
    }
    if(!shotFired.value && cheeseCount.value > 0) {
        Column(
            modifier = Modifier.padding(top = 0.dp , start = 35.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Card(
              modifier = Modifier
                  .size(80.dp, 60.dp)
                  .clickable {
                      shotFired.value = true
                      cheeseCount.value--
                  }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shooting_icon),
                        contentDescription = "shooting"
                    )
                }
            }
        }
    }
    val playJumpingSound = remember { mutableStateOf(false) }
    if(playJumpingSound.value && playerWantsSound.value) {
        playJumpingSound(context = localContext , playJumpingSound = playJumpingSound)
    }
    Column(
        modifier = Modifier
    ) {
        Row(
            modifier = Modifier.padding(top = 550.dp , start = 0.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.left_arrow),
                contentDescription = "left arrow",
                modifier = Modifier
                    .size(200.dp, 200.dp)
                    .clickable {
                        if (currentPositionOfChar.value >= 2) {
                            currentPositionOfChar.value--
                            playJumpingSound.value = true
                        }
                    }
            )
            Spacer(modifier = Modifier.padding(0.dp))
            Image(
                painter = painterResource(id = R.drawable.right_arrow),
                contentDescription = "right arrow",
                modifier = Modifier
                    .size(200.dp, 200.dp)
                    .clickable {
                        if (currentPositionOfChar.value <= 2) {
                            currentPositionOfChar.value++
                            playJumpingSound.value = true
                        }
                    }
            )
        }
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End
    ) {
        powerUps(cheeseCount, count)
    }
    Column(
        modifier = Modifier.padding(top = 10.dp , start = 10.dp)
    ) {
        Card(
            modifier = Modifier.size(120.dp,50.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(count.value<2) {
                    Image(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "life1",
                        modifier = Modifier.scale(0.7f)
                    )
                }
                if(count.value<1) {
                    Image(
                        painter = painterResource(id = R.drawable.heart),
                        contentDescription = "life2",
                        modifier = Modifier.scale(0.7f)
                    )
                }
            }
        }
    }
    Column(
        modifier = Modifier.padding(top = 10.dp , start = 140.dp)
    ) {
        Card(
            modifier = Modifier.size(120.dp,50.dp),
            colors = CardDefaults.cardColors(Color(255,215,0))
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.size(100.dp,40.dp),
                    colors = CardDefaults.cardColors(Color.Blue)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "${score.value}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier.padding(top = 10.dp , start = 270.dp)
    ) {
        Card(
            modifier = Modifier.size(140.dp , 60.dp),
            colors = CardDefaults.cardColors(Color.Red)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.cheese_no_bg),
                        contentDescription = "cheese counter"
                    )
                    Spacer(modifier = Modifier.padding(0.dp))
                    Card(
                        modifier = Modifier.size(60.dp,40.dp),
                        colors = CardDefaults.cardColors(Color.Cyan)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "${actualCheeseCount}",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun gamePageObstacles(navController: NavController, cheeseCount : MutableState<Int> , count : MutableState<Int>) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 500)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(durationMillis = 500)
        )
    ) {
        val timeFactor = remember { mutableIntStateOf(180000) }
        val isTimerRunning = remember { mutableStateOf(true) }
        LaunchedEffect(Unit) {
            delay(1000L)
            multiplier.value += 0.1f
        }
        LaunchedEffect(isTimerRunning.value) {
            while(isTimerRunning.value) {
                delay((1000/(multiplier.value * obsOnlyMultiplier.value)).toLong())
                timeFactor.value--
                if(timeFactor.value==0) {
                    isTimerRunning.value = false
                }
            }
        }

        LaunchedEffect(timeFactor.value) {
            if(timeFactor.value%2 == 0) {
                val lane = Random.nextInt(1,4)
                val type = Random.nextInt(0,4)
                obstacleList.add(Obstacle(typesOfObstacles[type],lane , mutableFloatStateOf(0f) , mutableStateOf(true)))
            }
            if(timeFactor.value%11 == 0 && timeFactor.value != 0) {
                val lane = Random.nextInt(1,4)
                obstacleList.add(Obstacle("Gift",lane , mutableFloatStateOf(0f) , mutableStateOf(true)))
            }
            if(!gameEnded.value){
                delay(((100 * obsOnlyMultiplier.value) / (multiplier.value * scoreOnlyMultiplier.value)).toLong())
                score.value += 100
            }
        }

        if(gameEnded.value){
            result(navController,count,cheeseCount)
        }

        for (obs in obstacleList) {
            checkingPage(obs,cheeseCount,count)
        }
    }
}

val obstacleList = mutableListOf<Obstacle>()
val typesOfObstacles = listOf<String>("Lake","Lion","tree","Cheese")
val playCollision = mutableStateOf(false)

suspend fun checkCollisions(obs : Obstacle, cheeseCount: MutableState<Int>, count: MutableState<Int> , collisionCheckingYOffset : MutableState<Float>) {
    if(obs.lane == currentPositionOfChar.value && obs.yOffset.value in collisionCheckingYOffset.value-20f..collisionCheckingYOffset.value+20f) {
        obstacleList.remove(obs)
        if(immunity.value) {
            //do nothing
        }
        else if(obs.type == "Gift") {
            var a = Random.nextInt(0,2)
            //reward = multiplier only for score for some time
            if(a==1) {
                scoreOnlyMultiplier.value = 2f
                delay(5000L)
                scoreOnlyMultiplier.value = 1f
            }
            //punishment = multiplier only for obstacles for some time
            else {
                obsOnlyMultiplier.value = 2f
                delay(5000L)
                obsOnlyMultiplier.value = 1f
            }
        }
        else if(obs.type == "Cheese") {
            cheeseCount.value++
//            Log.d("cheeseCount" , cheeseCount.value.toString())
        }
        else {
            count.value++
            playCollision.value = true
            Log.d("count" , count.value.toString())
        }
    }
    else {
        //do nothing
    }
    if(count.value>=2) {
        gameEnded.value = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun result(navController: NavController , count: MutableState<Int> , cheeseCount: MutableState<Int>) {

    val localContext = LocalContext.current
    val sharedPreferences = localContext.getSharedPreferences("Cheese Chase" , Context.MODE_PRIVATE)

    if(score.value > getSavedText(sharedPreferences , "High Score").toInt()) {
        saveText(sharedPreferences , "High Score" , score.value.toString())
    }

    if(playerWantsSound.value) {
        playWinSound(context = localContext)
    }

    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        modifier = Modifier.size(width = 300.dp, height = 350.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .scale(1.3f),
            colors = CardDefaults.cardColors(containerColor = Color(62, 64, 118)),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.padding(top = 30.dp))
                Card(
                    modifier = Modifier
                        .size(150.dp , 30.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = "SCORE : ${score.value}" , fontSize = 18.sp)
                    }
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Column(modifier = Modifier.size(150.dp,150.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.wholesome),
                        contentDescription = "result",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.padding(top = 10.dp))
                Button(
                    onClick = {
                        count.value = 0
                        cheeseCount.value = 0
                        gameEnded.value = false
                        currentPositionOfChar.value = 1
                        previousPositionOfChar.value = 1
                        score.value = 0
                        multiplier.value = 1f
                        immunity.value = false
                        scoreOnlyMultiplier.value = 1f
                        obsOnlyMultiplier.value = 1f
                        obstacleList.clear()
                        navController.navigate("gamePageBase")
                        firstBoxVal.value = "PLAYER 1"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0, 190, 255),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(width = 150.dp, height = 40.dp)
                ) {
                    Text(text = "Play Again" , fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.padding(top = 15.dp))
                Button(
                    onClick = {
                        count.value = 0
                        cheeseCount.value = 0
                        gameEnded.value = false
                        currentPositionOfChar.value = 1
                        previousPositionOfChar.value = 1
                        score.value = 0
                        multiplier.value = 1f
                        immunity.value = false
                        scoreOnlyMultiplier.value = 1f
                        obsOnlyMultiplier.value = 1f
                        obstacleList.clear()
                        navController.navigate("firstPage")
                        firstBoxVal.value = "PLAYER 1"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.size(width = 150.dp, height = 40.dp)
                ) {
                    Text(text = "Home" , fontSize = 18.sp)
                }
            }
        }
    }
}





