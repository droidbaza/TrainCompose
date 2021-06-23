package com.droidbaza.traincompose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun RowSample() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Text(
            text = "Netflix",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .weight(2f)
                .padding(16.dp)
        )
        Text(
            text = "$10",
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Right,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        )
    }
}

@Composable
fun ColumnSample() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {

        Text(
            text = "Netflix",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        Text(
            text = "$10",
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

    }
}

@Composable
fun BoxSample() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
    ) {
        Text(
            text = "Netflix",
            style = MaterialTheme.typography.h6,
        )

        Text(
            text = "$10",
            style = MaterialTheme.typography.body2,
        )
    }
}

@Composable
@Preview
fun ConstraintLayoutSample() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp)),
        elevation = 0.dp,
        backgroundColor = MaterialTheme.colors.onSurface,
    ) {

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            val (nameLayout, priceLayout) = createRefs()

            /* Column to display name and next billing status */
            Column(modifier = Modifier.constrainAs(nameLayout) {
                start.linkTo(parent.start)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            }) {

                Text(
                    text = "Netflix",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .align(Alignment.Start)
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Next payment : Apr 30",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier.padding(end = 16.dp)
                )

            }

            /* Column to display amount and per month status */
            Column(modifier = Modifier.constrainAs(priceLayout) {
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }) {

                Text(
                    text = "$4.99",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.surface,
                    textAlign = TextAlign.Right
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "/month",
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.surface,
                    textAlign = TextAlign.Right
                )
            }

        }
    }

}