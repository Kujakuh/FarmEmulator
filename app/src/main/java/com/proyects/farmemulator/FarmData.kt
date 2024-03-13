package com.proyects.farmemulator

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@SuppressLint("MutableCollectionMutableState")
class FarmData(pType : PLANT_TYPE) {
    var stateCurrent : STATE by mutableStateOf(STATE.IDLE)
    var type : PLANT_TYPE by mutableStateOf(pType)
    var sprites = updateSprites()
    fun nextState(){
        stateCurrent =
            if(stateCurrent.ordinal == STATE.entries.size - 1) STATE.entries[0]
            else STATE.entries[stateCurrent.ordinal + 1]
    }
    private fun updateSprites(): Map<STATE, Int> {
        return when (type){
            PLANT_TYPE.P1 -> plant1
            PLANT_TYPE.P2 -> plant2
            PLANT_TYPE.P3 -> plant3
            PLANT_TYPE.P4 -> plant4
        }
    }
    fun changeTypeTo(t : PLANT_TYPE){
        type = t
        sprites = updateSprites()
    }
}
