package com.proyects.farmemulator

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.proyects.farmemulator.ui.theme.FarmEmulatorTheme
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.delay

const val stageTime : Long = 3500
var focus : FarmData? = null
var cropsCollected = mutableMapOf(
    PLANT_TYPE.P1 to 0,
    PLANT_TYPE.P2 to 0,
    PLANT_TYPE.P3 to 0,
    PLANT_TYPE.P4 to 0
)
val grassColor = Color(0xFFABC270)
val uiColor = Color(0xFF856456)

val pixelFont = FontFamily((Font(R.font.pixelart, FontWeight.Normal)))

@Composable
fun loadImageResource(id: Int, context : Context,fade: Boolean = false): AsyncImagePainter {
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    return rememberAsyncImagePainter(ImageRequest.Builder(context)
        .data(data = id)
        .crossfade(fade)
        .apply(block = {size(Size.ORIGINAL)})
        .build(), imageLoader = imageLoader)
}

@Composable
fun StageSwapHandlerOf(data: FarmData){
    LaunchedEffect(key1 = data.stateCurrent) {
        if( data.stateCurrent == STATE.TIME1 ||
            data.stateCurrent == STATE.TIME2 ||
            data.stateCurrent == STATE.TIME3  )
        {
            delay(stageTime)
            data.nextState()
        }
    }
}

fun plant(plant : PLANT_TYPE, context: Context){
    if(focus?.stateCurrent == STATE.IDLE){
        focus?.changeTypeTo(plant)
        focus?.nextState()
        val plantSound = android.media.MediaPlayer.create(context, R.raw.plant)
        plantSound.start()
    }
}

@Composable
fun SeedButton(iconPressed : Int, iconRelease: Int, plant: PLANT_TYPE, contentDescription : String,
                modifier: Modifier, context: Context){
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    return Box(
        modifier = Modifier
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = { plant(plant, context) }
            )
    ){
        Image(painter = painterResource(id =
        if (isPressed) iconPressed
        else iconRelease),
            contentDescription = contentDescription,
            modifier = modifier)
    }
}

@Composable
fun SpriteHandlerButton(buttonData: FarmData,
    uiUpdateFlag: MutableState<Boolean>,
    finalStages: Map<PLANT_TYPE, AsyncImagePainter>, context: Context){

    var border = R.drawable.blank
    if(buttonData ==  focus){ border = R.drawable.finalselectframe }
    val interactionSource = remember { MutableInteractionSource() }

    val lambda = {
        focus = buttonData
        if(buttonData.stateCurrent == STATE.FINAL){
            cropsCollected[buttonData.type] = cropsCollected[buttonData.type]!! + 1
            buttonData.nextState()
            val harvestSound = android.media.MediaPlayer.create(context, R.raw.harvest)
            harvestSound.start()
        }
        uiUpdateFlag.value = !uiUpdateFlag.value
    }

    val img = if (buttonData.stateCurrent != STATE.FINAL)
                painterResource(id = buttonData.sprites[buttonData.stateCurrent]!!)
              else finalStages[buttonData.type]!!

    val config = LocalConfiguration.current
    return Box(
        modifier = Modifier
            .height(config.screenHeightDp.dp / 1.75f)
            .width(config.screenWidthDp.dp / 2.5f)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = lambda
            )
    )
    {
        Image(
            painter = img, contentDescription = "Huerta de ${buttonData.type}",
            modifier = Modifier
                .fillMaxSize(),
        )
        Image(painter = loadImageResource(id = border, context, true),
            contentDescription = "Marco de seleccion",
            modifier = Modifier.fillMaxSize())
    }
}

@Composable
fun FarmEmulatorGame(finalStages : Map<PLANT_TYPE, AsyncImagePainter>, context: Context){
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    HideSystemBars()

    val uiUpdateFlag = remember { mutableStateOf(true) }

    val farms = remember { listOf(
            FarmData(PLANT_TYPE.P1),
            FarmData(PLANT_TYPE.P1),
            FarmData(PLANT_TYPE.P1),
            FarmData(PLANT_TYPE.P1)
    ) }
    Row(modifier = Modifier.background(uiColor)){
        Column(modifier = Modifier
            .fillMaxHeight()
            .background(grassColor)) {
            Row(modifier = Modifier.fillMaxHeight(0.5f)){
                SpriteHandlerButton(buttonData = farms[0], uiUpdateFlag, finalStages, context)
            }
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = farms[1], uiUpdateFlag, finalStages, context)
            }
        }
        Column(modifier = Modifier
            .fillMaxHeight()
            .background(grassColor)) {
            Row(modifier = Modifier.fillMaxHeight(0.5f)){
                SpriteHandlerButton(buttonData = farms[2], uiUpdateFlag, finalStages, context)
            }
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = farms[3], uiUpdateFlag, finalStages, context)
            }
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .absoluteOffset()
            .background(uiColor))
        {
            key(uiUpdateFlag.value){
                Row(
                    modifier = Modifier
                        .size(150.dp)
                        .fillMaxHeight(0.7f)
                        .fillMaxWidth()
                        .absoluteOffset(5.dp, 15.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth(0.5f)) {
                        SeedButton(
                            iconPressed = R.drawable.botontomatepressed,
                            iconRelease = R.drawable.botontomate,
                            plant = PLANT_TYPE.P1,
                            contentDescription = "Plantar tomate",
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .fillMaxWidth(),
                            context = context
                        )
                        SeedButton(
                            iconPressed = R.drawable.botoncebollapressed,
                            iconRelease = R.drawable.botoncebolla,
                            plant = PLANT_TYPE.P2,
                            contentDescription = "Plantar cebolla",
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(),
                            context = context
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SeedButton(
                            iconPressed = R.drawable.botoncalabazapressed,
                            iconRelease = R.drawable.botoncalabaza,
                            plant = PLANT_TYPE.P3,
                            contentDescription = "Plantar calabaza",
                            modifier = Modifier
                                .fillMaxHeight(0.5f)
                                .fillMaxWidth(),
                            context = context
                        )
                        SeedButton(
                            iconPressed = R.drawable.botonberenjenapressed,
                            iconRelease = R.drawable.botonberenjena,
                            plant = PLANT_TYPE.P4,
                            contentDescription = "Plantar berenjena",
                            modifier = Modifier
                                .fillMaxSize(),
                            context = context
                        )
                    }
                }
            }
            Column (modifier = Modifier.absoluteOffset(0.dp, 60.dp)){
                Box(modifier = Modifier){
                    Image(painter = painterResource(id = R.drawable.listtomate),
                        contentDescription = "Numero total de tomates recolectados")
                    Text(text = cropsCollected[PLANT_TYPE.P1].toString(),
                        fontFamily = pixelFont, fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .absoluteOffset(140.dp, 13.dp),
                        color = Color.White)
                }
                Box(modifier = Modifier){
                    Image(painter = painterResource(id = R.drawable.listcebolla),
                        contentDescription = "Numero total de cebollas recolectadas")
                    Text(text = cropsCollected[PLANT_TYPE.P2].toString(),
                        fontFamily = pixelFont, fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .absoluteOffset(140.dp, 13.dp),
                        color = Color.White)
                }
                Box(modifier = Modifier){
                    Image(painter = painterResource(id = R.drawable.listcalabaza),
                        contentDescription = "Numero total de calabazas recolectadas")
                    Text(text = cropsCollected[PLANT_TYPE.P3].toString(),
                        fontFamily = pixelFont, fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .absoluteOffset(140.dp, 13.dp),
                        color = Color.White)
                }
                Box(modifier = Modifier){
                    Image(painter = painterResource(id = R.drawable.listberenjena),
                        contentDescription = "Numero total de berenjenas recolectadas")
                    Text(text = cropsCollected[PLANT_TYPE.P4].toString(),
                        fontFamily = pixelFont, fontWeight = FontWeight.Normal,
                        modifier = Modifier
                            .absoluteOffset(140.dp, 13.dp),
                        color = Color.White)
                }
            }
        }
    }
    StageSwapHandlerOf(farms[0])
    StageSwapHandlerOf(farms[1])
    StageSwapHandlerOf(farms[2])
    StageSwapHandlerOf(farms[3])
}



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropsCollected[PLANT_TYPE.P1] = 0
        cropsCollected[PLANT_TYPE.P2] = 0
        cropsCollected[PLANT_TYPE.P3] = 0
        cropsCollected[PLANT_TYPE.P4] = 0
        setContent {
            FarmEmulatorTheme {
                val context = LocalContext.current
                val finalStages = mapOf(
                    PLANT_TYPE.P1 to loadImageResource(id = R.drawable.utomatoesfinal, context),
                    PLANT_TYPE.P2 to loadImageResource(id = R.drawable.ucebollasfinal, context),
                    PLANT_TYPE.P3 to loadImageResource(id = R.drawable.ucalabazasfinal, context),
                    PLANT_TYPE.P4 to loadImageResource(id = R.drawable.uberenjenasfinal, context)
                )
                Surface(color = MaterialTheme.colorScheme.background) {
                    FarmEmulatorGame(finalStages, context)
                }
            }
        }
    }
}