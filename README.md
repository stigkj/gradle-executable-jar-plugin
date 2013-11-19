# Executable Jar plugin for Gradle

The Executable Jar plugin creates an executable jar from your project with all its runtime dependencies embedded.

This means, when using this plugin in a build, you will get a *artifact_-execjar.jar* artifact than can be run like this:

```
java -jar artifact_name-execjar.jar
```

## Usage
To use the executable jar plugin, include the following in your build script:

```groovy
apply plugin: 'executable-jar'

// This is the class that starts your application
mainClass = 'the.class.that.has.Main'

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'net.nisgits.gradle:gradle-executable-jar-plugin:1.7.0'
    }
}
```
