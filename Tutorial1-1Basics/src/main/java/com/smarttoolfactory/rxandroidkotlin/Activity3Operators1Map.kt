package com.smarttoolfactory.rxandroidkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.smarttoolfactory.rxandroidkotlin.databinding.Activity3OperatorsBinding
import com.smarttoolfactory.rxandroidkotlin.model.User
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class Activity3Operators1Map : AppCompatActivity() {

    private lateinit var dataBinding: Activity3OperatorsBinding
    private val TAG = Activity3Operators1Map::class.java.simpleName
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
            .observeOn(AndroidSchedulers.mainThread())
            .map { user ->
                // modifying user object by adding email address
                // turning user name to uppercase
                user.apply {
                    email = "${name}@rxjava.com"
                    name = name!!.toUpperCase()
                }
                user
            }
            .subscribe(object : Observer<User> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(user: User) {
                    println("🔥map() onNext(): " + user.name + ", " + user.gender + ", " + user.address?.address)

                    stringBuilder.append("${user.name}-${user.gender}-${user.address?.address}\n")
                }

                override fun onError(e: Throwable) {
                    println("🔥map() onError(): " + e.message)
                }

                override fun onComplete() {
                    println("🔥map() onComplete()")
                    dataBinding.textUsers.text = stringBuilder.toString()
                }
            })
    }

    /**
     * Assume this method is making a network call and fetching Users
     * an Observable that emits list of users
     * each User has name and email, but missing email id
     */
    private fun getUsersObservable(): Observable<User> {
        val names = arrayOf("mark", "john", "trump", "obama")

        val users = mutableListOf<User>()
        for (name in names) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }
        return Observable.create<User> { emitter ->
            for (user in users) {
                if (!emitter.isDisposed) {
                    emitter.onNext(user)
                }
            }

            if (!emitter.isDisposed) {
                emitter.onComplete()
            }

        }.subscribeOn(Schedulers.io())
    }

    override fun onDestroy() {
        super.onDestroy()
        println("Activity3Operators1Map onDestroy()")

        disposable?.dispose();


    }
}