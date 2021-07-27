package com.smarttoolfactory.rxandroidkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.rxandroidkotlin.databinding.Activity3OperatorsBinding
import com.smarttoolfactory.rxandroidkotlin.model.Address
import com.smarttoolfactory.rxandroidkotlin.model.User
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit


class Activity3Operators2FlatMap : AppCompatActivity() {

    private lateinit var dataBinding: Activity3OperatorsBinding
    private val TAG = Activity3Operators2FlatMap::class.java.simpleName
    private var disposable: Disposable? = null

    private val stringBuilder = StringBuilder()

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity3_operators)

        dataBinding.buttonGetUsers.setOnClickListener {

            subscribeUI()
        }
    }

    private fun subscribeUI() {

        stringBuilder.clear()

        getUsersObservable()
            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread()) // 🔥 Causes crash
            .doOnNext {
                // This is thread: RxCachedThreadScheduler-X thread
                println("🗿 doOnNext() ${it.name}, addresss: ${it.address?.address}, " +
                        "thread: ${Thread.currentThread().name}")

            }

            //  .concatMap { user ->
            // TODO FlatMap does not care the order or wait for delay to emit values
            .flatMap { user ->

                // getting each user address by making another network call
                getAddressObservable(user)

                    .doOnNext {
                        // This is RxComputationThreadPool-X thread
                        println("🥶flatMap(): doOnNext() ${it.name}, address: ${it.address?.address}, " +
                                "thread: ${Thread.currentThread().name}")

                    }
            }

            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<User> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(user: User) {
                    // This is main thread
                    println("🔥onNext() ${user.name}, addresss: ${user.address?.address}," +
                            " thread: ${Thread.currentThread().name}")

                    stringBuilder.append("${user.name}-${user.gender}-${Thread.currentThread().name}\n")
                    dataBinding.textUsers.text = stringBuilder.toString()

                }

                override fun onError(e: Throwable) {
                    println("🔥onError(): " + e.message)
                }

                override fun onComplete() {
                    println("onComplete()")
                    dataBinding.textUsers.text = stringBuilder.toString()

                }
            })
    }


    /**
     * Assume this as a network call
     * returns Users with address filed added
     */
    private fun getAddressObservable(user: User): Observable<User> {

        val addresses = arrayOf(
            "1600 Amphitheatre Parkway, Mountain View, CA 94043",
            "2300 Traverwood Dr. Ann Arbor, MI 48105",
            "500 W 2nd St Suite 2900 Austin, TX 78701",
            "355 Main Street Cambridge, MA 02142"
        )
        val delay = Random().nextInt(700).toLong()

        return Observable
            .create(ObservableOnSubscribe<User> { emitter ->

                // This is RxCachedThreadScheduler-X thread
                println("getAddressObservable() create() ${Thread.currentThread().name}")
                val address = Address(addresses[Random().nextInt(2) + 0])

                if (!emitter.isDisposed) {
                    user.address = address

                    // Generate network latency of random duration
//                    val sleepTime = Random().nextInt(1000) + 500
//
//                    sleep(sleepTime.toLong())
                    emitter.onNext(user)
                    emitter.onComplete()
                }

            })
            .subscribeOn(Schedulers.io())
            .delay(delay * 3, TimeUnit.MILLISECONDS)


    }

    /**
     * Assume this is a network call to fetch users
     * returns Users with name and gender but missing address
     */
    private fun getUsersObservable(): Observable<User> {
        val maleUsers = arrayOf("Ace", "Buck", "Chase", "Danny")

        val users = mutableListOf<User>()

        for (name in maleUsers) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }

        return Observable
            .create(ObservableOnSubscribe<User> { emitter ->
                for (user in users) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(user)
                    }
                }

                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            }).subscribeOn(Schedulers.io())
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Activity3Operators1Map onDestroy()")

        disposable?.dispose()


    }

    override fun onPause() {
        super.onPause()
        println("Activity3Operators2FlatMap onPause()")
    }

    override fun onStop() {
        super.onStop()
        println("Activity3Operators2FlatMap onStop()")


    }
}