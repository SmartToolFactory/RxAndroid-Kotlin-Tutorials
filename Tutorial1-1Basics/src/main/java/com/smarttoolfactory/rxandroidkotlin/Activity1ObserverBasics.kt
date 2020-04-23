package com.smarttoolfactory.rxandroidkotlin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.rxandroidkotlin.databinding.Activity1BasicObserversBinding
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import java.util.concurrent.TimeUnit


/**
 * Class for testing Observer and Observable properties.
 *
 *
 *
 * **Important:**  observer.onNext() calls [Observer.onNext] even
 * if [Observer] is unsubscribed and [Observer.onComplete] invoked
 *
 *
 * If you unsubscribe from interval, you cannot expect a completion emission.
 *
 *
 */
class Activity1ObserverBasics : AppCompatActivity() {

    private lateinit var dataBinding: Activity1BasicObserversBinding

    private lateinit var observable: Observable<Long>

    private lateinit var observer: Observer<Long>

    private var disposableTest: Disposable? = null

    private var isObserving = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity1_basic_observers)


//        // 1- Create with rangeLong operator
//        observable = Observable.rangeLong(0, 1000)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext { aLong ->
//                // TODO 🔥🔥 WARNING: This is called only if there is at least 1 subscriber
//                System.out.println("🚗 observable doOnNext(): " + aLong + ", thread: " + Thread.currentThread().getName());
//            }
//
//
//        // 1- Create with just Operator
//        observable = Observable.just(1L, 2L, 3L, 4L, 5L)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .doOnNext { aLong ->
//                //                    // TODO 🔥🔥 WARNING: This is called only if there is at least 1 subscriber
//                println("🚗 observable doOnNext(): " + aLong + ", thread: " + Thread.currentThread().getName());
//            }


        observable = Observable.interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { aLong ->
                // TODO 🔥🔥 WARNING: This is called only if there is at least 1 subscriber
                println("🚗 observable doOnNext(): " + aLong + ", thread: " + Thread.currentThread().name)
            }
            .doOnSubscribe { disposable ->
                println("🔧 Observable doOnSubscribe() disposable: $disposable")
            }

        //  ⚠️⚠️️ TODO to unsubscribe from this disposable should be disposed
        var disposable: Disposable? = null
        observable
            .filter {
                it > 0L && it % 3 == 0L
            }
            .doOnSubscribe {
                disposable = it
            }
            .doOnComplete{
                Toast.makeText(
                    this,
                    "🎃 observable subscribe() -> doOnComplete()",
                    Toast.LENGTH_SHORT
                )
            }
            .subscribe {

                println("🎃 observable subscribe() -> onNext(): $it")
                Toast.makeText(
                    this,
                    "🎃 observable subscribe() -> onNext(): $it",
                    Toast.LENGTH_SHORT
                )
                    .show()


                disposable?.dispose()

            }


        bindViews()

        setObserver()

        observable.subscribe(observer)

        isObserving = true

    }


    private fun bindViews() {

        dataBinding.button1.setOnClickListener { v ->

            isObserving = !isObserving

            if (isObserving) {

                dataBinding.button1.text = "Remove Obs1"

                observable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer)


            } else {
                if (disposableTest != null && !disposableTest!!.isDisposed) {

                    dataBinding.button1.text = "Add Obs1"

                    Toast.makeText(
                        this,
                        "disposableTest: " + disposableTest + ", isDisposed: " + disposableTest?.isDisposed,
                        Toast.LENGTH_SHORT
                    ).show()


                    // ⚠️ TODO Calling onComplete does NOT stop interval, it only calls onComplete method
                    observer.onComplete()

                    // ⚠️ TODO dispose stops the interval but does not call onComplete method of observer
                    disposableTest?.dispose()


                }
            }
        }


    }


    private fun setObserver() {

        observer = object : Observer<Long> {
            override fun onSubscribe(disposable: Disposable) {
                println("🔥 observer onSubscribe() d: $disposable")
                dataBinding.text1.text = "onSubscribe() disposable: $disposable"
                disposableTest = disposable
            }

            override fun onNext(value: Long) {
                println("🔥 observer onNext() int: $value")
                dataBinding.text1.text = "onNext() int: $value"

            }

            override fun onError(e: Throwable) {
                dataBinding.text1.text = "onError() e:  $e.message"

            }

            override fun onComplete() {
                dataBinding.text1.text = "onComplete()"

                println("🏪 observer onComplete()")

                Toast.makeText(
                    this@Activity1ObserverBasics,
                    "observer onComplete() disposableTest.isDisposed(): " + disposableTest?.isDisposed,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        if (disposableTest != null) {


            disposableTest?.dispose()

            Toast.makeText(
                this,
                "onDestroy() disposable: " + disposableTest + ", isDisposed(): " + disposableTest?.isDisposed,
                Toast.LENGTH_SHORT
            ).show()
        }

        val obs = Observable.just(12)
    }
}
