package com.example.amphibians.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.amphibians.R
import com.example.amphibians.model.Amphibian
import com.example.amphibians.ui.theme.shapes

@Composable
fun HomeScreen(
    amphibiansUiState: AmphibiansUiState,
    onRetryButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    when(amphibiansUiState){
        is AmphibiansUiState.Success -> AmphibiansList(
            amphibians = amphibiansUiState.amphibians,
            modifier = modifier.fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
        
        is AmphibiansUiState.Loading -> LoadingScreen(
            modifier = modifier.fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
        
        is AmphibiansUiState.Error -> ErrorScreen(
            onRetryButtonClicked = onRetryButtonClicked,
            modifier = modifier.fillMaxSize()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Composable
fun ErrorScreen(
    onRetryButtonClicked: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), 
            contentDescription = stringResource(R.string.error_detected)
        )
        Text(
            text = stringResource(R.string.failed_to_load),
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
        )
        Button(
            onClick = onRetryButtonClicked
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading),
        modifier = modifier
            .size(200.dp)
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AmphibiansList(
    amphibians: List<Amphibian>,
    modifier: Modifier = Modifier
) {
    val visibleState = remember {
        MutableTransitionState(false).apply {
            // Start the animation immediately
            targetState = true
        }
    }
    AnimatedVisibility(
        visibleState = visibleState,
        enter = fadeIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioNoBouncy
            )
        ),
        exit = fadeOut()
    ) {
        LazyColumn(
            modifier = modifier
        ) {
            items(
                count = amphibians.size,
                key = { index ->
                    amphibians[index].name
                },
            ) { index ->
                AmphibianItem(
                    amphibian = amphibians[index],
                    modifier = Modifier
                        .padding(vertical = dimensionResource(id = R.dimen.padding_small))
                        .animateEnterExit(
                            enter = slideInVertically(
                                animationSpec = spring(
                                    stiffness = Spring.StiffnessVeryLow,
                                    dampingRatio = Spring.DampingRatioLowBouncy
                                ),
                                initialOffsetY = {
                                    it * (index + 1)
                                } // staggered entrance
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun AmphibianItem(
    amphibian: Amphibian,
    modifier: Modifier = Modifier
) {
    var expanded by rememberSaveable{
        mutableStateOf(false)
    }
    Card(
        modifier = modifier
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
            .clickable{
                expanded = !expanded
            },
        shape = shapes.small
    ) {
        Column {
            Text(
                text = amphibian.name + "(" + amphibian.type + ")",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
            )
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(amphibian.imgSrc)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.amphibian_photo),
                error = painterResource(id = R.drawable.ic_broken_image),
                placeholder = painterResource(id = R.drawable.loading_img),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1.5f)
                    .fillMaxWidth()
            )
            if(expanded) {
                Text(
                    text = amphibian.description,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small))
                )
            }
        }
    }
}
