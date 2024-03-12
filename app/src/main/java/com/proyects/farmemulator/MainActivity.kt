package com.proyects.farmemulator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.drawable.shapes.Shape
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.proyects.farmemulator.ui.theme.FarmEmulatorTheme
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import kotlinx.coroutines.delay

const val stageTime : Long = 3000
var focus : FarmData? = null
var cropsCollected = mutableMapOf(
    PLANT_TYPE.P1 to 0,
    PLANT_TYPE.P2 to 0,
    PLANT_TYPE.P3 to 0,
    PLANT_TYPE.P4 to 0
)

fun collectCrop(farm : FarmData){
    cropsCollected[farm.type] = cropsCollected[farm.type]!! + 1
}

@Composable
fun loadImageResource(id: Int): AsyncImagePainter {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    return rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(data = id)
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


@Composable
fun SpriteHandlerButton(buttonData : FarmData, uiUpdateFlag : MutableState<Boolean>){

    var border = BorderStroke(0.dp, Color.Unspecified)
    if(buttonData ==  focus){ border = BorderStroke(5.dp, Color.Black) }
    val interactionSource = remember { MutableInteractionSource() }

    val lambda = {
        focus = buttonData
        if(buttonData.stateCurrent == STATE.FINAL){
            collectCrop(buttonData)
            buttonData.nextState()
        }
        uiUpdateFlag.value = !uiUpdateFlag.value
    }

    val img = loadImageResource(id = buttonData.sprites[buttonData.stateCurrent]!!)

    val config = LocalConfiguration.current
    return Box(
        modifier = Modifier
            .height(config.screenHeightDp.dp / 1.785f)
            .width(config.screenWidthDp.dp / 2.5f)
            .border(border)
            .clickable(
                indication = null,
                interactionSource = interactionSource,
                onClick = lambda
            )
    )
    {
        Image(
            painter = img, contentDescription = "aaa",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize(),
        )
        Text(text = buttonData.id, modifier = Modifier.absoluteOffset(x = 10.dp, y = 10.dp))
    }
}

fun sow(plant : PLANT_TYPE){
    if(focus?.stateCurrent == STATE.IDLE){
        focus?.changeTypeTo(plant)
        focus?.nextState()
    }
}

@Composable
fun FarmEmulatorGame(){
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    HideSystemBars()

    val uiUpdateFlag = remember { mutableStateOf(true) }

    val farms = remember { listOf(
            FarmData(PLANT_TYPE.P1, "tomatoes"),
            FarmData(PLANT_TYPE.P1, "lettuce"),
            FarmData(PLANT_TYPE.P1, "onions"),
            FarmData(PLANT_TYPE.P1, "carrots")
    ) }
    Row(modifier = Modifier){
        Column(modifier = Modifier) {
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = farms[0], uiUpdateFlag)
            }
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = farms[1], uiUpdateFlag)
            }
        }
        Column(modifier = Modifier) {
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = farms[2], uiUpdateFlag)
            }
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = farms[3], uiUpdateFlag)
            }
        }

        key(uiUpdateFlag.value){
            Column(Modifier
                .fillMaxSize()){
                Text(text = "a state: " + farms[0].stateCurrent.toString())
                Text(text = "b state: " + farms[1].stateCurrent.toString() )
                Text(text = "c state: " + farms[2].stateCurrent.toString())
                Text(text = "d state: " + farms[3].stateCurrent.toString())
                Text(text = "last pressed: " + focus?.id)
                Text(text = "p1 collected: " + cropsCollected[PLANT_TYPE.P1])
                Text(text = "p2 collected: " + cropsCollected[PLANT_TYPE.P2])
                Text(text = "p3 collected: " + cropsCollected[PLANT_TYPE.P3])
                Text(text = "p4 collected: " + cropsCollected[PLANT_TYPE.P4])
                Column {
                    Row {
                        Button(onClick = { sow(PLANT_TYPE.P1) }) { Text(text = "P1") }
                        Button(onClick = { sow(PLANT_TYPE.P2) }) { Text(text = "P2") }
                    }
                }
                Column {
                    Row {
                        Button(onClick = {sow(PLANT_TYPE.P3)}){Text(text = "P3")}
                        Button(onClick = {sow(PLANT_TYPE.P4)}){Text(text = "P4")}
                    }
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
        setContent {
            FarmEmulatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    FarmEmulatorGame()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FarmPreview() {
    FarmEmulatorTheme {
        FarmEmulatorGame()
    }
}