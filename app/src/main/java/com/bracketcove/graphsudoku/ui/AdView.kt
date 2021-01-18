package com.bracketcove.graphsudoku.ui

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import com.bracketcove.graphsudoku.R
import com.google.android.gms.ads.*

@Composable
fun BannerAd() {
//    var showAd by remember {
//        mutableStateOf(true)
//    }
//
//    val context = AmbientContext.current
//    val adUnitId = stringResource(R.string.ad_unit_id)
//    val adView = remember {
//        AdView(context).apply {
//            this.adSize = AdSize.SMART_BANNER
//            this.adUnitId = adUnitId
//            this.adListener = object : AdListener() {
//                override fun onAdLoaded() {
//                    super.onAdLoaded()
//                    showAd = true
//                }
//
//                override fun onAdFailedToLoad(p0: LoadAdError?) {
//                    super.onAdFailedToLoad(p0)
//                    showAd = false
//                }
//            }
//            this.loadAd(AdRequest.Builder().build())
//
//        }
//    }
//
//    AndroidView(modifier = Modifier
//        .alpha(if (showAd) 1f else 0f)
//        .background(MaterialTheme.colors.primary),
//        viewBlock = { adView }
//    )
}
