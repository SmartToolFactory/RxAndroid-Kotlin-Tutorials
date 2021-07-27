package com.smarttoolfactory.rxandroidkotlin

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.rxandroidkotlin.databinding.Activity2ObservableClickListenerBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Class for testing Observer and Observable properties.
 *
 * **Important:**  mObserver.onNext((long) mCounter) calls [Observer.onNext] even
 * if [Observer] is unsubscribed and [Observer.onComplete] invoked
 *
 */
class Activity2ObservableClickListener : AppCompatActivity() {

    private lateinit var dataBinding: Activity2ObservableClickListenerBinding

    private lateinit var observer: Observer<Long>

    private val compositeDisposable = CompositeDisposable()

    private var isObserving = false

    private var mCounter: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding =
            DataBindingUtil.setContentView(this, R.layout.activity2_observable_click_listener)

        getObserver()

        observe()

        dataBinding.button1.setOnClickListener { v ->

            if (!isObserving) {

                observe()
                dataBinding.button1.text = "Remove Observer"

            } else {

                isObserving = false

                observer.onComplete()

                dataBinding.button1.text = "Add Observer"

                // ⚠️ Using clear will clear all, but can accept new disposable
                compositeDisposable.clear()
                // ⚠️ Using dispose will clear all and set isDisposed = true, so it will not accept any new disposable
                //  compositeDisposable.dispose()

            }
        }

    }


    private fun observe() {

        isObserving = true

        //  TODO ALTERNATIVE 1- Observer with Button
        val viewObservablePlus = Observable.create<View> { emitter ->

            emitter.setCancellable {
                println("🥶 dataBinding.buttonPlus emitter.setCancellable()")
                dataBinding.buttonPlus.setOnClickListener(null)
            }

            dataBinding.buttonPlus.setOnClickListener { emitter.onNext(it) }

        }

        viewObservablePlus.map { view -> (++mCounter).toLong() }.subscribe(observer)


        val viewObservableMinus = Observable.create<View> { emitter ->

            emitter.setCancellable {
                println("🥶 dataBinding.buttonMinus emitter.setCancellable()")
                dataBinding.buttonMinus.setOnClickListener(null)
            }

            dataBinding.buttonMinus.setOnClickListener { emitter.onNext(it) }

        }

        viewObservableMinus.map { view -> (--mCounter).toLong() }.subscribe(observer)


        //  TODO ALTERNATIVE 2- Observer with Button via RxBinding

//        RxView.clicks(dataBinding.buttonMinus)
//            .map { view ->
//                -- mCounter
//            }
//            .doOnSubscribe { disposable ->
//                println("🗿dataBinding.buttonMinus onSubscribe() disposable: $disposable");
//            }
//            .subscribe(observer);

//        RxView.clicks(dataBinding.buttonPlus)
//                .map(view -> (long) ++mCounter)
//                .doOnSubscribe(disposable -> {
//                    System.out.println("🗿dataBinding.buttonPlus onSubscribe() disposable(): " + disposable);
//                })
//                .subscribe(observer);

    }


    private fun getObserver() {

        observer = object : Observer<Long> {
            override fun onSubscribe(disposable: Disposable) {
                println("🔥 Observer onSubscribe() d: $disposable")
                dataBinding.text1.text = "Observer onSubscribe() d: $disposable"
                compositeDisposable.add(disposable)
            }

            override fun onNext(value: Long) {
                dataBinding.text1.text = "Observer onNext() int: $value"
            }

            override fun onError(e: Throwable) {
                dataBinding.text1.text = "Observer onError() e: " + e.message
            }

            override fun onComplete() {
                println("Observer onComplete()")
                dataBinding.text1.text = "Observer onComplete()"
            }
        }
    }
}
