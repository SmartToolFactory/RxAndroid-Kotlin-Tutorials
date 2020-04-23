package com.smarttoolfactory.rxandroidkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.rxandroidkotlin.databinding.Activity1TimerWithStartStopBinding
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class Activity1TimerWithStartStop : AppCompatActivity() {


    private lateinit var dataBinding: Activity1TimerWithStartStopBinding


    private val compositeDisposable = CompositeDisposable()
    private var isTimerOn = false

    // ⚠️ This is a hot observable
    private val timer = Observable.interval(1, TimeUnit.SECONDS)
        .replay(1)
        .autoConnect()


    private val timer2 = Observable.interval(1, TimeUnit.SECONDS)

    private val subject = PublishSubject.create<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity1_timer_with_start_stop)

        timer2
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                println("🎃 Timer2 doOnSubscribe()")
                compositeDisposable.add(it)
            }
            .doOnNext {
                println("🎃 Timer2 doOnNext() $it")

            }
            .subscribe(subject)
    }


    override fun onResume() {
        super.onResume()
        subscribeContinousTimer()

    }

    override fun onPause() {
        super.onPause()
        compositeDisposable.clear()
    }

    private fun subscribeContinousTimer() {


        // TODO Works but does not stop timer onPause called
//        compositeDisposable.add(timer
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                //                Toast.makeText(this, "Tick: $it", Toast.LENGTH_SHORT).show()
//                println("🥺 Tick: $it")
//
//                dataBinding.text1.text = "Tick $it"
//            })


        // TODO Alternative with Subject but NOT WORKING


        subject
            .doOnSubscribe {
                println("🥶 subject doOnSubscribe()")
            }

            .subscribe {
                println("🥶 subject onNext() $it")
                dataBinding.text1.text = "Tick $it"
            }

//     compositeDisposable.add(
//         subject.subscribe {
//             dataBinding.text1.text = "Tick $it"
//         }
//     )

    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

    }

}