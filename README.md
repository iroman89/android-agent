# Android Instana Agent 

## Getting started

### Before beginning

Make sure that the Google, Maven Central and JCenter's Maven repositories are included in you project-level `buil.gradle` file:

```groovy
buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}
allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}
```

### 1. Add Instana Agent SDK to your app
In your module (app-level) Gradle file (usually `app/build.gradle`):
```groovy
dependencies {
    implementation 'com.instana.android:android-agent:0.9'
}
```

### 2. Add Instana Agent Plugin to your app
In your module (app-level) Gradle file (usually `app/build.gradle`), after applying the `com.android.application` plugin:
```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.instana.android.plugin'
```

In your module (app-level) Gradle file (usually `app/build.gradle`):
```groovy
buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath 'digital.wup:android-maven-publish:3.6.2'
        classpath "com.instana.android:plugin:1.0.0"
    }
}
```

### 3. Initialize Instana Agent when your app starts

In your class extending `Application`, replacing `YOUR_REPORTING_URL` and `YOUR_APP_KEY` with the configuration values you'll find in your Instana Dashboard:
```kotlin
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Instana.setup(
            this,
            InstanaConfig(
                reportingURL = "YOUR_REPORTING_URL",
                key = "YOUR_APP_KEY"
            )
        )
    }
}
```

### Additional configuration settings

The configuration described in `Step 3` is the minimum configuration you must provide to Instana Agent to function. 

Please check for additional options in the [Android API documentation](https://documentation.link).

## Contributing 

### Components

- `app`: demo app covering (most of) the usage scenarios of the Agent 
- `plugin`: gradle plugin to simplify configuration for apps
- `runtime`: weaving logic, instrumentation, beacon handling, ...

### Compile

You must use the full IntelliJ Ultimate IDE in order to compile the `plugin` and `runtime`.

If you want to work on the `runtime` or `plugin` components of the Agent, you must publish them to your local maven repository so they can be found by the `app`.

Relevant commands:
- compile and publish `runtime` to local maven: 
```shell script
gradlew :runtime::publishMavenAarPublicationToMavenLocal
```
- compile and publish `plugin` to local maven:
```shell script
gradlew :plugin::publishPluginMavenPublicationToMavenLocal
```
- assemble `app`:
```shell script
:app::assemble
```