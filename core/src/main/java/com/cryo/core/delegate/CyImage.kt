package com.cryo.core.delegate

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cryo.core.COMMON
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

class CyImage(parent: CyView<*>): CyView<CyImage>(parent) {
    override fun newView(): View {
        return ImageView(context)
    }

    @SuppressLint("CheckResult")
    fun setSrc(src: String, placeHolder: Int? = null, errorPlaceHolder: Int? = null): CyImage {
        val request = Glide.with(context).load(src)
        if(placeHolder != null) {
            request.placeholder(placeHolder)
        }
        if(errorPlaceHolder != null) {
            request.error(errorPlaceHolder)
        }
        request.diskCacheStrategy(DiskCacheStrategy.ALL).into(view as ImageView)
        return this
    }

    @SuppressLint("CheckResult")
    fun setSrc(src: Path, placeHolder: Int? = null, errorPlaceHolder: Int? = null): CyImage {
        val realSrc = File(COMMON.get("#").toString(), src.pathString)
        val request = Glide.with(context).load(realSrc)
        if(placeHolder != null) {
            request.placeholder(placeHolder)
        }
        if(errorPlaceHolder != null) {
            request.error(errorPlaceHolder)
        }
        request.diskCacheStrategy(DiskCacheStrategy.ALL).into(view as ImageView)
        return this
    }

    fun setSrc(resId: Int): CyImage {
        (view as ImageView).setImageResource(resId)
        return this
    }
}