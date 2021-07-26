package com.ellie.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

open class ImageRepository(private val context: Context) {

    open fun image(id: String): Single<Bitmap> {
        val single = Single.fromCallable {
            val inputStream = context.assets.open("$id.jpg")
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            bitmap
        }.subscribeOn(Schedulers.io())

        return if (id == "apple") single.delay(3, TimeUnit.SECONDS) else single
    }
}
