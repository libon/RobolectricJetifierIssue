This is a sample project to demonstrate an issue with Robolectric and jetifier.

Prerequisites for the problem:
* Robolectric shadows 4.1
* Android gradle plugin 3.3.0 (this project uses 3.2.1, so you have to modify it to reproduce the issue)
* A unit test which uses a shadow on a `SwipeRefreshLayout`

Behavior:
* Building the app is ok: `./gradlew clean assembleDebug` produces an apk with no errors.
* Running tests fails:

```
./gradlew testDebugUnitTest
Starting a Gradle Daemon, 1 incompatible and 3 stopped Daemons could not be reused, use --status for details

FAILURE: Build completed with 2 failures.

1: Task failed with an exception.
-----------
* What went wrong:
Transformation hasn't been executed yet

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.
==============================================================================

2: Task failed with an exception.
-----------
* What went wrong:
Transformation hasn't been executed yet

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.
==============================================================================

* Get more help at https://help.gradle.org
```

Update to gradle 5.1 to see more details about the error:

Update gradle:

```
./gradlew wrapper --gradle-distribution-url https://services.gradle.org/distributions/gradle-5.1-all.zip
./gradlew wrapper --gradle-distribution-url https://services.gradle.org/distributions/gradle-5.1-all.zip # yes, run it twice
```

Run tests:
```
./gradlew testDebugUnitTest
```

Now, I saw two different errors. First, what appears to be a jetifier error:

```
> Transform artifact wagon-http-lightweight.jar (org.apache.maven.wagon:wagon-http-lightweight:1.0-beta-6) with JetifyTransform
ERROR: [TAG] Failed to resolve variable '${pom.groupId}'
ERROR: [TAG] Failed to resolve variable '${pom.version}'

> Transform artifact maven-ant-tasks.jar (org.apache.maven:maven-ant-tasks:2.1.3) with JetifyTransform
ERROR: [TAG] Failed to resolve variable '${pom.groupId}'
ERROR: [TAG] Failed to resolve variable '${pom.version}'

> Transform artifact guava.jar (com.google.guava:guava:20.0) with JetifyTransform
ERROR: [TAG] Failed to resolve variable '${animal.sniffer.version}'

> Task :app:javaPreCompileDebugUnitTest FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:javaPreCompileDebugUnitTest'.
> Could not resolve all files for configuration ':app:debugUnitTestCompileClasspath'.
   > Failed to transform artifact 'shadows-supportv4.jar (org.robolectric:shadows-supportv4:4.1)' to match attributes {artifactType=android-classes, org.gradle.usage=java-runtime-jars}
      > Execution failed for JetifyTransform: /Users/calvarez/.gradle/caches/modules-2/files-2.1/org.robolectric/shadows-supportv4/4.1/33c79041197f506a2d186a07f145d1e775146b47/shadows-supportv4-4.1.jar.
         > Failed to transform '/Users/calvarez/.gradle/caches/modules-2/files-2.1/org.robolectric/shadows-supportv4/4.1/33c79041197f506a2d186a07f145d1e775146b47/shadows-supportv4-4.1.jar' using Jetifier. Reason: The given artifact contains a string literal with a package reference 'android.support.v4.content' that cannot be safely rewritten. Libraries using reflection such as annotation processors need to be updated manually to add support for androidx.. (Run with --stacktrace for more details.)

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.

* Get more help at https://help.gradle.org

Deprecated Gradle features were used in this build, making it incompatible with Gradle 6.0.
Use '--warning-mode all' to show the individual deprecation warnings.
See https://docs.gradle.org/5.1/userguide/command_line_interface.html#sec:command_line_warnings
```

To get around this one, try this, inspired by https://issuetracker.google.com/issues/118658603 :
Add the following to the root `gradle.properties`:

```
android.jetifier.blacklist = shadows-supportv4-4.1.jar
```

Then you get a compilation error:
```
./gradlew testDebugUnitTest

> Configure project :app
WARNING: The option setting 'android.jetifier.blacklist=shadows-supportv4-4.1.jar' is experimental and unsupported.


> Task :app:compileDebugUnitTestJavaWithJavac FAILED
/Users/calvarez/dev/projects/RobolectricJetifierIssue/app/src/test/java/com/example/robolectric/jetifierissue/ExampleUnitTest.java:21: error: cannot access SwipeRefreshLayout
        Shadows.shadowOf(swipeRefreshLayout);
               ^
  class file for android.support.v4.widget.SwipeRefreshLayout not found
1 error
```

Note that after trying to clean up this project, I no longer got the error about "The given artifact contains a string literal with a package reference 'android.support.v4.content' that cannot be safely rewritten", even after removing the `android.jetifier.blacklist = shadows-supportv4-4.1.jar` config. I don't understand what made it go away.

But then, I still end up with the compilation error "cannot access SwipeRefreshLayout".
