package com.proyects.farmemulator

import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


@SuppressLint("MutableCollectionMutableState")
class FarmData(pType : PLANT_TYPE) {
    var collected : MutableMap<PLANT_TYPE, Int> by mutableStateOf(mutableMapOf(
        PLANT_TYPE.P1 to 0,
        PLANT_TYPE.P2 to 0,
        PLANT_TYPE.P3 to 0,
        PLANT_TYPE.P4 to 0
    ))
    var stateCurrent : STATE by mutableStateOf(STATE.IDLE)
    var type : PLANT_TYPE by mutableStateOf(pType)
    var sprites = updateSprites()
    fun nextState(){
        if(stateCurrent.ordinal == STATE.entries.size - 1) {
            stateCurrent = STATE.entries[0]
            collected[type] = collected[type]!! + 1
        }
        else stateCurrent = STATE.entries[stateCurrent.ordinal + 1]
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

fun collectFarmData(farmData: List<FarmData>) : Map<PLANT_TYPE, Int> {
    val totalCollected = mutableMapOf<PLANT_TYPE, Int>()
    farmData.forEach {
        totalCollected[it.type] =
            totalCollected[it.type]?.plus(it.collected[it.type]!!) ?:
            it.collected[it.type]!!
    }
    return totalCollected
}