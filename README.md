# About
Open-source, voice notes recording app.

This project is to be used as a boilerplate project for client-server apps and a reference on how to use the below libraries with Kotlin. Inspiration is taken from the [ribot's android-boilerplate](https://github.com/ribot/android-boilerplate/) project.

## Libraries
- Reactive extensions by [RxKotlin 2](https://github.com/ReactiveX/RxKotlin)
- Dependency injection by [Dagger 2](http://google.github.io/dagger/)
- Logging by [Timber](https://github.com/JakeWharton/timber) with native Kotlin extensions
- Complimentary and randomized colour algorithms by [Colours](https://github.com/Obaied/colours)
- Mocking with [Mockito-Kotlin](https://github.com/nhaarman/mockito-kotlin)
- [Checkstyle](http://checkstyle.sourceforge.net/), [PMD](https://pmd.github.io/) and [Findbugs](http://findbugs.sourceforge.net/) for code analysis

## Architecture
The architecture is mostly based on a modified version of ribot's Android architecture [here](https://github.com/ribot/android-boilerplate#architecture): 

![](https://github.com/ribot/android-guidelines/raw/master/architecture_guidelines/architecture_diagram.png)

The idea is to be able to compartmentalize every aspect of the project to testable pieces. Also, extending the project and introducing newbies to it should not be a chaotic task. The architecture does contain a fair amount of nuts and bolts, but its nothing too alien to [Clean Architecture](https://www.youtube.com/watch?v=Nsjsiz2A9mg). Feel free to consult this [fantastic boilerplate by Android10](https://github.com/android10/Android-CleanArchitecture) as another source of knowledge

### Dependency Injection
I made an extensive write-up [over here](https://github.com/Obaied/BareBonesAndroidDagger) relating how I approach dependency injection. I'd love to get any feedback on it. Drop an issue and we'll talk. It would be good to list down what I've written over there with a couple of related examples to Recorded.

[Dagger 2.12 released proper support for Android](https://google.github.io/dagger/android). 

> P.S: My apologies if Google did an uber-transformation to the posted approach in a latter release and I didn't update it here. Please drop me an issue and I'll definitely take a look at it, unless my taco cryptocurrency kicks off, then you're on your own, dude :)

Support for Android is pretty amazing. It allows the developer to basically make a BaseActivity/BaseFragment and have Dagger take care of the injection process for you. The BareBonesAndroidDagger approach I took was basically trying to separate the dependencies I need based on the level of the view hierarchy.

The hierarchy goes as follows:
- **Application Level**: Dependencies for all views and for the custom Application class.
- **Activity Level**: Dependencies for a specific activity.
- **Fragment Level**: Dependencies for a specific fragment.

In the [BareBonesAndroidDagger](https://github.com/Obaied/BareBonesAndroidDagger), I provide a detailed class breakdown. For here, I'll just write about a couple of Dagger modules.

#### Class Breakdown: `ApplicationModule`

```
@Module
class ApplicationModule {
    @Provides
    @Singleton
    fun provideNavigator(): Navigator = Navigator()

    @Provides
    fun providesCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Provides
    fun providesSchedulerProvider(): SchedulerProvider = AppSchedulerProvider()

    @Provides
    fun providesPrefManager(): PrefManager = PrefManager()

    @Provides
    @Singleton
    fun providesDataManagerHelper(): DataManagerHelper = DataManagerHelper()

    @Provides
    @Singleton
    fun providesDataManager(dataManagerHelper: DataManagerHelper): DataManager
            = DataManager(dataManagerHelper)
}
```

This level is the **Application Level**. Whatever is here, is shared with all activities, fragments, and the custom application class. I understand that somethings would not make sense for say the application class to have an activity navigator. I try to keep things as simple as possible since dependency injection graphs is one of those things that can get too chaotic too fast. Three levels of isolation served me in the project. If there needs to be more levels, that's easily achievable as well.

#### Class Breakdown: `NotesFragmentModule`

```
@Module
class NotesFragmentModule {
    @Provides
    fun providesNotesAdapter() = NotesAdapter()

    @Provides
    fun providesNotesPresenter(dataManager: DataManager,
                               compositeDisposable: CompositeDisposable,
                               schedulerProvider: SchedulerProvider)
            = NotesPresenter(dataManager,
            compositeDisposable,
            schedulerProvider)
}
```

This module injects things specifically and only into `NotesFragment`. The dependencies necessary for calling `providesNotesPresenter` are all injected from our `ApplicationModule` automagically!

### UI: Activities, Presenters and MvpViews
The UI is a very straight-forward implementation of MVP architecture.

* _Activities_ is the one responsible for handling pretty much all initializations and UI interactions.
* There exists an object, called a _presenter_, for each activity instance and is specific to that activity. The  _presenter's_ main job is to simply handle all the non-UI related tasks, like fetching data from databases or calling APIs.
* Whatever comes data or callbacks that come back from whatever the _presenter_ did is piped back to the _activity_ through simple interface callbacks that that activity would implement. These interface callbacks are more-or-less a *contract* of what interactions that _activity_ can do. That interface is called in the project as _MvpViews_

To give an example:

* A view starts up (`onCreate()` is called). This could be an activity or a fragment. This project follows the use of fragments for their delicious `setRetainInstance(true)`
* a _presenter_ is initialized and called to go fetch stuff from an API or a database (it doesn't matter which in this level of analysis)
* The _presenter_ plays nice and talks to what would be explained later as a _DataManager_ to fetch the data from an API or a database. 
* The asyncronuous callback to that call would be handled by the _presenter_ which would do any necessary non-UI changes.
* If there are any UI changes, The _presenter_ would talk to the responsible view through an _MvpView_ interface callback. Those UI changes could actually be many. In the case of a simple fetching of one quote and displaying it on the screen, there are at least 3 scenarios in which UI would be affected in a simple client-server app:
  * On Completed: The fetched data shows up normally
  * On Empty: The fetched data doesn't show up (server is not working or no connection is available)
  * On Error: The fetching process is faced with a big fat exception

Those are just simple scenarios. There could also be UI changes when the fetch process starts and ends. For example, it would display a progress loader when the fetch starts, and hides it when the fetch ends. As you can see, there are a lot of scenarios that a _presenter_ would need to take to the governing view. All these cases constitute a *contract* between the _presenter_ and view which is detailed inside an _MvpView_ interface.

That's pretty much it. We have some input and output without worrying too much about who's doing what in the kitchen. It makes testing easy. 

### Data: Rationale and Testing
So, what needs to be tested from this business side? Based on Clean Architecture standards, this is the core level of the hierarchy.

This level can be broken down into the following: Local, Models, Remote, Managers, and Helpers

#### Locals
Basically, everything relating to an existing database. You'll put your **Room**, DAO or SQLite wrappers here.

#### Models
Any POJOs that are related to the database. Kotlin calls those _Data Classes_ but that naming is a bit confusing in here (We put Data Classes in Data category!!)

#### Remote
RetroFit Services or things relating to an API call or networking library.

P.S: This project doesn't use a database since the voice notes are already saved in an external directory on the device. The separation of concerns between unit-testable and non-unit-testable helpers is still the same.

#### Managers
This is just our _DataManager_. There could be many if the data section was too big to contain. This class must be 100% unit-testable. By that, I mean no imports to any Android library since JUnit can't mock that. Last thing I want to use is Robolectric for unit tests. Anything relating to Android, Java IO, or anything that can't be directly mocked is relayed to our Helpers category

#### Helpers
As mentioned before. These are native calls to Android or Java libraries that cannot be mocked.

An example to this interaction in the project would be fetching files from the device's external downloads directory.
The method to fetch that is `fetchRecordingFiles(path)` in `DataManager` class. Problem is that I can't test this method directly because this method requires importing `java.file.io`. This can't be mocked in any simple way. I really don't wanna waste time googling "How to mock File class java". The actual interaction with `Java.File.IO` is relayed to `DataManagerHelper` class. The two methods I use for `fetchRecordingFiles(path)` are:
- `DataManagerHelper.doesDirectoryExist_CreateIfNotExists()`
- `DataManagerHelper.listFilesInDirectory()`

They both throw custom inherited classes from IOExceptions if an issue occurs. This makes unit testing DataManager much easier. I don't bother unit-testing DataManagerHelper or any helper class since all the functionality I'll ever need from it is used in DataManager.

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

### Pros and Cons of this architecture

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

# Credits
Programming by: Abdullah Obaied
Design by [Ardavan Hp](https://dribbble.com/Ahp94)

![adjust-logo](https://www.adjust.com/assets/mediahub/adjust_standard.png)
