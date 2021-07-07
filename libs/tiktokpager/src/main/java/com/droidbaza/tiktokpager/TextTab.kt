package com.droidbaza.tiktokpager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


sealed class TabItem(var position:Int,var title:String){
    object Popular:TabItem(0,"Popular")
    object Recommended:TabItem(1,"Recommended")
}
@Composable
fun TabRow(modifier: Modifier = Modifier,
           selected:(position:Int)->Unit){

    val selectedPosition = remember{
        mutableStateOf(0)
    }

    val tabs = remember {
        listOf(
            TabItem.Popular,
            TabItem.Recommended
        )
    }

    Row(
        modifier = Modifier.wrapContentHeight()
            .padding(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, tabItem ->
            TextTab(
                textLabel = tabItem.title,
                selected = index==selectedPosition.value,
                withEndShape =index!=tabs.size-1,
                onClick = {
                    selectedPosition.value = tabItem.position
                    selected(tabItem.position)
                })
        }

    }

}
@Composable
fun TextTab(
            textLabel:String,
            selected: Boolean,
            onClick: () -> Unit,
            icon: @Composable (() -> Unit)? = null,
            modifier: Modifier = Modifier,
            withEndShape:Boolean = false,
            enabled: Boolean = true){
    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
            ),
        contentAlignment = Alignment.Center
    ) {
        val txtSize = if(selected){
            18.sp
        }else{
            15.sp
        }
        Row(verticalAlignment = Alignment.CenterVertically){
            Text(text = textLabel,
                fontWeight = FontWeight.Bold,
                fontSize = txtSize,
                modifier = Modifier.padding(horizontal = 6.dp))

            if(withEndShape){
                Box(
                    modifier = Modifier
                        .size(2.dp, 6.dp)
                        .clip(RectangleShape)
                        .background(color = Color.LightGray)
                )
            }
        }

    }

}