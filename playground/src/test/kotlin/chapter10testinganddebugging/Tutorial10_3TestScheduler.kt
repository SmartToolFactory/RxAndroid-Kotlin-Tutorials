package chapter10testinganddebugging

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.TestObserver
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit


class Tutorial10_3TestScheduler {


    @Test
    fun usingTestScheduler() {

        //Declare TestScheduler
        val testScheduler = TestScheduler()

        //Declare TestObserver
        val testObserver: TestObserver<Long> = TestObserver()

        //Declare Observable emitting every 1 minute
        val minuteTicker = Observable.interval(
            1, TimeUnit.MINUTES,
            testScheduler
        )

        //Subscribe to TestObserver
        minuteTicker.subscribe(testObserver)

        //⏱ Fast forward by 30 seconds
        testScheduler.advanceTimeBy(30, TimeUnit.SECONDS)

        testScheduler.now(TimeUnit.MINUTES)

        //Assert no emissions have occurred yet
        testObserver.assertValueCount(0)

        //⏱ Fast forward to 70 seconds after subscription
        testScheduler.advanceTimeTo(70, TimeUnit.SECONDS)

        //Assert the first emission has occurred
        testObserver.assertValueCount(1)

        //⏱ Fast Forward to 90 minutes after subscription
        testScheduler.advanceTimeTo(90, TimeUnit.MINUTES)

        //Assert 90 emissions have occurred
        testObserver.assertValueCount(90)


        testObserver.dispose()

    }


}