# Executable Jar Plugin for Gradle

The Executable Jar plugin creates an executable jar from your project with all its dependencies embedded.

## Usage
To use the executable jar plugin, include in your build script:

```groovy
apply plugin: exeuctable-jar

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'net.nisgits.gradle:gradle-executable-jar-plugin:1.0-SNAPSHOT'
    }
}
```