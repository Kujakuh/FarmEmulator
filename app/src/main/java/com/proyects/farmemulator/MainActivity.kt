package com.proyects.farmemulator

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.drawable.shapes.Shape
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
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

const val stageTime : Long = 3000
var lastPressed : FarmData? = null

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
fun SpriteHandlerButton(buttonData : FarmData) {
    var border = BorderStroke(0.dp, Color.Unspecified)
    if(buttonData ==  lastPressed){ border = BorderStroke(5.dp, Color.Black) }
    val interactionSource = remember { MutableInteractionSource() }

    val lambda = {
        buttonData.nextState()
        lastPressed = buttonData
    }
    val img = painterResource(id = buttonData.sprites[buttonData.stateCurrent]!!)
    val context = LocalConfiguration.current
    return Box(
        modifier = Modifier
            .height(context.screenHeightDp.dp / 2)
            .width(context.screenWidthDp.dp / 2.5f)
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
                .fillMaxSize()
        )
    }
}

@Composable
fun Test(){

    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
    val a = remember { FarmData(PLANT_TYPE.P1) }
    val b = remember { FarmData(PLANT_TYPE.P2) }
    val c = remember { FarmData(PLANT_TYPE.P3) }
    val d = remember { FarmData(PLANT_TYPE.P4) }


    Row(modifier = Modifier){
        Column(modifier = Modifier) {
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = a)
            }
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = b)
            }
        }
        Column(modifier = Modifier) {
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = c)
            }
            Row(modifier = Modifier){
                SpriteHandlerButton(buttonData = d)
            }
        }

        Column(Modifier
            .fillMaxSize()){
            Text(text = "a state: " + a.stateCurrent.toString())
            Text(text = "b state: " + b.stateCurrent.toString() )
            Text(text = "c state: " + c.stateCurrent.toString())
            Text(text = "d state: " + d.stateCurrent.toString())
            Text(text = "last pressed: " + lastPressed?.type)
        }
    }
    StageSwapHandlerOf(a)
    StageSwapHandlerOf(b)
    StageSwapHandlerOf(c)
    StageSwapHandlerOf(d)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FarmEmulatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    Test()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FarmPreview() {
    FarmEmulatorTheme {
        Test()
    }
}