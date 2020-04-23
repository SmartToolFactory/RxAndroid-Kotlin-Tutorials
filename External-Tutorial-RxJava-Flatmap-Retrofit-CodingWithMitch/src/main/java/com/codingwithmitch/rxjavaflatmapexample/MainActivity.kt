package com.codingwithmitch.rxjavaflatmapexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codingwithmitch.rxjavaflatmapexample.models.Post
import com.codingwithmitch.rxjavaflatmapexample.requests.ServiceGenerator
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainActivity : AppCompatActivity() {

    //ui
    private var recyclerView: RecyclerView? = null

    // vars
    private val disposables = CompositeDisposable()
    private var adapter: RecyclerAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)

        initRecyclerView()

        postsObservable()
            .subscribeOn(Schedulers.io())
            .flatMap { post ->
                getCommentsObservable(post)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Post> {
                override fun onSubscribe(d: Disposable) {
                    disposables.add(d)
                }

                override fun onNext(post: Post) {
                    updatePost(post)
                }

                override fun onError(e: Throwable) {
                    Log.e(TAG, "onError: ", e)
                }

                override fun onComplete() {}
            })
    }

    private fun postsObservable(): Observable<Post> {
        return ServiceGenerator.requestApi
            .posts
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap { posts ->
                adapter?.setPosts(posts)
                Observable.fromIterable(posts)
                    .subscribeOn(Schedulers.io())
            }
    }

    private fun getCommentsObservable(post: Post): Observable<Post> {
        return ServiceGenerator.requestApi
            .getComments(post.id)
            .map { comments ->
                val delay = (Random().nextInt(5) + 1) * 1000 // sleep thread for x ms
                Thread.sleep(delay.toLong())
                Log.d(TAG, "apply: sleeping thread " + Thread.currentThread().name + " for " + delay.toString() + "ms")

                post.comments = comments
                post
            }
            .subscribeOn(Schedulers.io())

    }

    private fun initRecyclerView() {
        adapter = RecyclerAdapter()
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    companion object {

        private val TAG = "MainActivity"
    }

    private fun updatePost(post: Post) {
        adapter!!.updatePost(post)
    }
}