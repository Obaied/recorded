# Recorded
Simple voice-notes recording app, made with MVP and Kotlin. The idea is for this to be used as a boilerplate project for client-server apps and a reference on how to use the below libraries with Kotlin. Inspiration is taken from the [ribot's android-boilerplate](https://github.com/ribot/android-boilerplate/) project.
Libraries and tools included:

## Libraries
- RecyclerView
- Reactive extensions by [RxKotlin 2](https://github.com/ReactiveX/RxKotlin)
- Dependency injection by [Dagger 2](http://google.github.io/dagger/)
- Logging by [Timber](https://github.com/JakeWharton/timber) with native Kotlin extensions
- complimentary and randomized colour algorithms by [Colours](https://github.com/Obaied/colours)
- Mocking with [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin)
- [Checkstyle](http://checkstyle.sourceforge.net/), [PMD](https://pmd.github.io/) and [Findbugs](http://findbugs.sourceforge.net/) for code analysis

## Architecture
The architecture is mostly based on ribot's Android architecture [here](https://github.com/ribot/android-boilerplate#architecture): 

![](https://github.com/ribot/android-guidelines/raw/master/architecture_guidelines/architecture_diagram.png)

The idea is to be able to compartmentalize every aspect of the project to small, chunkable pieces that can be unit-tested easily and without pain. Also, extending the project and introducing newbies to it should not be a chaotic task. The architecture does contain a fair amount of nuts and bolts but a competent learner would find that the architecture has a solid and stable structure that is easy to wrap one's head around after consulting this README, running the unit tests and trying to fork and extend the code.

The following should be familiar to those familiar with MVVM, MVC, or MVP architectures.

### UI: Activities, Presenters and MvpViews
The UI is a very straight-forward implementation of MVP architecture.

* _Activities_ is the one responsible for handling pretty much all initializations and UI interactions.
* There exists an object, called a _presenter_, for each activity instance and is specific to that activity. The  _presenter's_ main job is to simply handle all the non-UI related tasks, like fetching data from databases or calling APIs.
* Whatever comes data or callbacks that come back from whatever the _presenter_ did is piped back to the _activity_ through simple interface callbacks that that activity would implement. These interface callbacks are more-or-less a *contract* of what interactions that _activity_ can do. That interface is called in the project as _MvpViews_

To give an example:

* An _activity_ starts (`onCreate()` is called)
* a _presenter_ is initialized and called to go fetch stuff from an API or a database (it doesn't matter which in this level of analysis)
* The _presenter_ plays nice and talks to what would be explained later as a _DataManager_ to fetch the data from an API or a database. 
* The asyncronuous callback to that call would be handled by the _presenter_ which would do any necessary non-UI changes.
* If there are any UI changes, The _presenter_ would talk to the _activity_ through an _MvpView_ interface callback. Those UI changes could actually be many. In the case of a simple fetching of one quote and displaying it on the screen, there are at least 3 scenarios in which UI would be affected:
  * The quote shows up normally
  * The quote doesn't show up (server is not working or no connection is available)
  * The fetching process is faced with a big fat exception

Those are just simple scenarios. There could also be UI changes when the fetch process starts and ends. For example, it would display a progress loader when the fetch starts, and hides it when the fetch ends. As you can see, there are a lot of scenarios that a _presenter_ would need to take to the governing _activity_. All these cases constitute a *contract* between the _presenter_ and _activity_ which is detailed inside an _MvpView_ interface.

That's pretty much it. We have some input and output without worrying too much about who's doing what in the kitchen. It makes handling data and separating the tasks of UI and data much easier. 

The rest of the architecture would deal with how to handle the data using the _DataManager_

P.S: I primarily follow a *no-fragment all-activity* approach so I wouldn't have to worry about where to put the _presenter_ instance if I have one activity and five fragments, each requiring their own API calls.


### Data: The Overarching *DataManager*
_DataManager_ is a singleton class that handles all the necessary data operations whether its local or remote. 
By _Local_, I mean relating to an already existing database on the device. 
By _Remote_, I mean relating to an API or something that would require a network call

### Data: Local, Remote, and Models
I follow `ribot's` original separation of data and data handling to _Local_, _Remote_ and _Model_.

_Local_ refers to all the classes and things needed to handle an SQLite database. This project uses *SQLBrite* for handling databases with reactive extensions. There exists three classes in this category:

* _DatabaseHelper_ which handles any data-related tasks coming from _DataManager_
* _Db_ which simplifies the process of creating queries by having constant names and static functions to deal with cursors.
* _DbOpenHelper_ which is a boilerplate class that's used to create the database initially. I doubt there's a lot of innovation to be made in this class. Its a plain-old boilerplate.

The _Remote_ category contains Retrofit2 Services. There's nothing too special about making these particular Retrofit2 services that's any different from what the *Getting Started* page of RetroFit2 describes.

One interesting class which I add to this _Remote_ category is called _ServicesHelper_. The problem with RetroFit2 Services is that they are interfaces. I don't like to include logic in interfaces, even if Kotlin or Java 8 allows it to an extent. Sometimes, its helpful to handle the data coming from the API in a certain way and it would be necessary to have that behaviour abstracted to a class that can be mocked with Mockito. In the case of my project, I had a quotes API that gets a single quote only. I needed to a list of quotes to have an infinite scroller behaviour. Ideally, I'd ask the backend to make an extra endpoint that serves my needs, but that won't happen with a public API, so I had to make the API call multiple times and concatenate the result. Luckily, I'm using Rx which makes the job a lot easier. I used _ServicesHelper_ to write a piece of unit-tested and mockable logic that would serve multiple quotes instead of one.

### Data: Chaining observables and handling errors
So what I need to do to get quotes to show up on the screen is simple:
- Fetch quotes from database
- If database is empty (first launch), fetch quotes from API
- set the fetched quotes from API to the database

Here's how it looks like:

```kotlin
    //Inside StartPresenter.kt
    fun subscribeToDbToFetchQuotes() {
        d { "subscribeToDbToFetchQuotes(): " }

        checkViewAttached()
        mvpView?.showProgress()

        mCompositeDisposable.add(mDataManager.fetchQuotesFromDb()
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(Consumer<List<Quote>> {
                    d { "subscribeToDbToFetchQuotes(): Received quotes: ${it.size}" }
                    mvpView?.hideProgress()

                    if (it.isEmpty()) {
                        mvpView?.showEmpty()
                        return@Consumer
                    }

                    mvpView?.showQuotes(it)

                }, Consumer<Throwable> {
                    e(it, { "subscribeToDbToFetchQuotes(): Received error" })

                    mvpView?.hideProgress()
                    mvpView?.showError(it.message!!)
                }
                )
        )
    }

    //Inside StartPresenter.kt
    fun fetchQuotesFromApi(limit: Int) {
        //since subscribeToDbToFetchQuotes is subscribed to SqlBrite's SELECT statement,
        // whatever I push here would be updated there
        mDataManager.fetchQuotesFromApi(limit)
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui())
                .onErrorResumeNext(Function { Observable.error<List<Quote>>(it) }) //OnErrorResumeNext and Observable.error() would propagate the error to the next level. So, whatever error occurs here, would get passed to onError() on the UI side
                .flatMap { t: List<Quote> ->
                    //Chain observable as such
                    mDataManager.setQuotesToDb(t).subscribe({}, { e { "setQuotesToDb() error occurred: ${it.localizedMessage}" } }, { d { "Done server set" } })
                    Observable.just(t)
                }
                .subscribeBy(
                        onNext = {},
                        onError = { mvpView?.showError("No internet connection") },
                        onComplete = { d { "onComplete(): done with fetching quotes from api" } }
                )
    }

    //Inside StartActivity.kt
    override fun showEmpty() {
        mPresenter.fetchQuotesFromApi(QUOTE_LIMIT_PER_PAGE)
    }
```
#### Quick explanation
* `SubscribeToDbToFetchQuotes()` handled subscription to Db. If the data coming back from the database is empty, then we would call `fetchQuotesFromApi()` from `showEmpty()` method.
* Inside `fetchQuotesFromApi()`, we try to fetch some data (quotes in this example) from an api with `mDataManager.fetchQuotesFromApi()`
* We subscribe the observable to do stuff on `.io()` thread and show results on `.ui()` thread.
* `onErrorResumeNext()` makes sure that whatever error we encounter from fetching data is caught in this method. I wanna terminate the entire chain when there is an error there, so I return an `Observable.error()`
* `.flatmap()` is the chaining part. I wanna be able to set whatever data I get from the API to my database. I'm not transforming the data I received using `.map()`, I'm simply doing _something else_ with that data **without** transforming it.
* I subscribe to the last chain of observables. If an error occurred with fetching data (first observable), it would be handled (in this case, propagated to the subscribed `onError()`) with `onErrorResumeNext()`
* I am very conscious that I'm subscribing to the DB observable (inside `flatmap()`). Any error that occurs through this observable will **NOT** be propagated to the last `subscribeBy()` methods, since it is handled inside the `subscribe()` method inside the `.flatmap()` chain.

### Handling Unit Tests
Whenever there was a need for `context`, I'd use *RobolectricTestRunner* as a jUnit test runner and supply `RuntimeEnvironment.application` as context.
The rest of the classes that don't require context wouldn't use a special runner.

In the case of an MVP structure, you'd need to unit test your _presenters_ and your _DataManager_ in terms of API and database fetching.

The process of testing is pretty straight-forward and nothing ground-breaking: for each test, *Prepare*, *Run* and *Assert*.

The *Preparation* process is mostly knowing what to mock and how to mock it. If it can't be mocked or you're finding this too hard, it might be a good idea to have another look in your architecture, maybe there's something you can abstract to make mocking easier. Testing always comes first.
Also very important to remember, NEVER create a mock of the class you're testing. You'll be defeating the entire point of it. If you're testing _DataManager_, create an instance of _DataManager_ with all the nuts and bolts it requires to be fully-functional just like it would be in the wild.

The *Run* process would mandate running the function to be tested.

The *Assert* process would check if the output matches what should happen to the input after running it through the function.

The *Preparation* step is mostly handled by Mockito. Mockito can even serve _RuntimeExceptions_ when accessing a specific function to test for failure. Its really great at that. 

The *Run* and *Assert* processes are beautifully handled by RxKotlin2 when data is involved. RxKotlin2 allows supplying a `TestObserver` instance that can be checked in the *Assert* step to verify if the *Run* step succeeded or failed. Here's an example test class from _DataManagerTest_ that tests fetching quotes

```
  @Test
  fun fetchQuotesFromDb() {
      //Prepare
      val quotes = DummyDataFactory.makeQuotes(2)
      whenever(mockDatabaseHelper.fetchQuotesFromDb(any<Int>()))
              .thenReturn(Observable.just(quotes))

      //Run
      val testObserver = TestObserver<List<Quote>>()
      dataManager.fetchQuotesFromDb(0).subscribe(testObserver)

      //Assert
      testObserver.assertNoErrors()
      testObserver.assertValue(quotes)
  }
```

### Handling Concurrency When Unit Testing
Unit testing would require running sequentially in an essentially blocking and single-threaded manner. One can argue that using a mechanism like *CountDownLatch* can allow multi-threaded unit-tests but I argue that simplicity is key and having all the unit tests run in a blocking and single-threaded manner is much easier and straight-forward.

RxKotlin2 allows the user to specify which thread to subscribe and observe on. I've made a small abstraction to this logic using the following two classes:

```
interface SchedulerProvider {
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun trampoline(): Scheduler
    fun newThread(): Scheduler
    fun io(): Scheduler
}

class AppSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun trampoline(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun newThread(): Scheduler {
        return Schedulers.newThread()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }
}

class TestSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun computation(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun trampoline(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun newThread(): Scheduler {
        return Schedulers.trampoline()
    }

    override fun io(): Scheduler {
        return Schedulers.trampoline()
    }
}
```

I'll inject whatever instance of `SchedulerProvider` I'll need based on the situation. In the case of unit-tests, I'll inject an instance of `TestSchedulerProvider` and have all the threads lead to `trampoline()` which means its blocking and single-threaded and runs on the current thread. In the case of running real code, I'll inject an instance of `AppSchedulerProvider` which would run the threads based on which one is chosen without any changes. 
This abstraction effectively solves the concurrency problem of unit tests.

## Code Quality
> TODO

### Tests
To run **unit** tests on your machine:

``` 
./gradlew test
``` 


## License
```
Copyright (c) 2017 Abdullah Obaied

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

![adjust-logo](https://www.adjust.com/assets/mediahub/adjust_standard.png)

# Privacy Policy
Abdullah Obaied built the Recorded app as an Open Source app. This SERVICE is provided by Abdullah Obaied at no cost and is intended for use as is.

This page is used to inform website visitors regarding my policies with the collection, use, and disclosure of Personal Information if anyone decided to use my Service.

If you choose to use my Service, then you agree to the collection and use of information in relation to this policy. The Personal Information that I collect is used for providing and improving the Service. I will not use or share your information with anyone except as described in this Privacy Policy.

The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions, which is accessible at Recorded unless otherwise defined in this Privacy Policy.

- Information Collection and Use

For a better experience, while using our Service, I may require you to provide us with certain personally identifiable information. The information that I request is retained on your device and is not collected by me in any way

The app does use third party services that may collect information used to identify you.

Link to privacy policy of third party service providers used by the app

[Google Play Services](https://www.google.com/policies/privacy/)

[AdMob](https://support.google.com/admob/answer/6128543?hl=en)

- Log Data

I want to inform you that whenever you use my Service, in a case of an error in the app I collect data and information (through third party products) on your phone called Log Data. This Log Data may include information such as your device Internet Protocol (“IP”) address, device name, operating system version, the configuration of the app when utilizing my Service, the time and date of your use of the Service, and other statistics.

- Cookies

Cookies are files with small amount of data that is commonly used an anonymous unique identifier. These are sent to your browser from the website that you visit and are stored on your device internal memory.

This Service does not use these “cookies” explicitly. However, the app may use third party code and libraries that use “cookies” to collection information and to improve their services. You have the option to either accept or refuse these cookies and know when a cookie is being sent to your device. If you choose to refuse our cookies, you may not be able to use some portions of this Service.

- Service Providers

I may employ third-party companies and individuals due to the following reasons:

To facilitate our Service;
To provide the Service on our behalf;
To perform Service-related services; or
To assist us in analyzing how our Service is used.
I want to inform users of this Service that these third parties have access to your Personal Information. The reason is to perform the tasks assigned to them on our behalf. However, they are obligated not to disclose or use the information for any other purpose.

- Security

I value your trust in providing us your Personal Information, thus we are striving to use commercially acceptable means of protecting it. But remember that no method of transmission over the internet, or method of electronic storage is 100% secure and reliable, and I cannot guarantee its absolute security.

- Links to Other Sites

This Service may contain links to other sites. If you click on a third-party link, you will be directed to that site. Note that these external sites are not operated by me. Therefore, I strongly advise you to review the Privacy Policy of these websites. I have no control over and assume no responsibility for the content, privacy policies, or practices of any third-party sites or services.

- Children’s Privacy

These Services do not address anyone under the age of 13. I do not knowingly collect personally identifiable information from children under 13. In the case I discover that a child under 13 has provided me with personal information, I immediately delete this from our servers. If you are a parent or guardian and you are aware that your child has provided us with personal information, please contact me so that I will be able to do necessary actions.

- Changes to This Privacy Policy

I may update our Privacy Policy from time to time. Thus, you are advised to review this page periodically for any changes. I will notify you of any changes by posting the new Privacy Policy on this page. These changes are effective immediately after they are posted on this page.

- Contact Us

If you have any questions or suggestions about my Privacy Policy, do not hesitate to contact me.

![adjust-logo](https://www.adjust.com/assets/mediahub/adjust_standard.png)
