package com.ellie.switchmapapp

import android.graphics.Bitmap
import com.ellie.image.ImageRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.subjects.BehaviorSubject


class MainFlatMapViewModel(private val imageRepository: ImageRepository) {

    private val clickEvents = BehaviorSubject.create<String>()

    fun images(): Observable<Bitmap> =
            clickEvents
                    .flatMapSingle { imageRepository.image(it) }
                    .observeOn(AndroidSchedulers.mainThread())


    fun onButtonClick(text: String) {
        clickEvents.onNext(text)
    }
}
