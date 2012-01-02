/*
 * Copyright 2011- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, softexecutable jare
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT executable jarRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nisgits.gradle.executablejar

import org.gradle.api.file.CopySpec
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.bundling.Jar
import org.gradle.util.ConfigureUtil
import org.gradle.api.java.archives.Manifest

/**
 * Generates an executable jar with all the dependencies embedded.
 * <p>
 * This means it can be started from the command line like this:
 * <p>
 * <code>java -jar name-of-jar.jar</code>
 *
 * FIXME include license from One-JAR
 * TODO include other file types too, like native files (.dll, .so, etc); check One-JAR docs
 * @author Stig Kleppe-Jørgensen
 */
class ExecutableJar extends Jar {
    public static final String EXEC_JAR_APPENDIX = 'execjar'

    FileCollection classpath
    CopySpec lib

    ExecutableJar() {
        appendix = EXEC_JAR_APPENDIX

        // Add as separate specs, so they are not affected by the changes to the main spec
        lib = copyAction.rootSpec.addChild()
        lib.into('lib') {
            from {
                def classpath = getClasspath()
                classpath ? classpath.filter {File file -> file.isFile()} : []
            }
        }
    }

    @Override
    Manifest getManifest() {
        Manifest manifest = super.getManifest()
        manifest.attributes.putAll([
                'Created-By': 'Gradle Executable Jar task',
                'Main-Class': 'com.simontuffs.onejar.Boot',
                'One-Jar-Main-Class': getMainClass()
        ])

        return manifest
    }

    /**
     * The class that contains the main method to invoke when starting the executable jar
     * <p>
     * This is implemented dynamically from the task's convention mapping setup in <code>ExecutableJarPlugin</code>
     *
     * @see ExecutableJarPlugin
     */
    @Input
    String getMainClass() {
        null
    }

    CopySpec getLib() {
        return lib.addChild()
    }

    /**
     * Adds some content to the {@code lib} directory for this executable jar archive.
     *
     * <p>The given closure is executed to configure a {@link CopySpec}. The {@code CopySpec} is passed to the closure
     * as its delegate.
     *
     * @param configureClosure The closure to execute
     * @return The newly created {@code CopySpec}.
     */
    CopySpec lib(Closure configureClosure) {
        return ConfigureUtil.configure(configureClosure, getLib())
    }

    /**
     * Returns the classpath to include in the executable jar archive. Any JAR or ZIP?? files in this classpath are
     * included in the {@code lib} directory.
     *
     * @return The classpath. Returns an empty collection when there is no classpath to include in the executable jar.
     */
    @InputFiles @Optional
    FileCollection getClasspath() {
        return classpath
    }

    /**
     * Sets the classpath to include in the executable jar archive.
     *
     * @param classpath The classpath. Must not be null.
     */
    void setClasspath(Object classpath) {
        this.classpath = project.files(classpath)
    }

    /**
     * Adds files to the classpath to include in the executable jar archive.
     *
     * @param classpath The files to add. These are evaluated as for {@link org.gradle.api.Project#files(Object [])}
     */
    void classpath(Object... classpath) {
        FileCollection oldClasspath = getClasspath()
        this.classpath = project.files(oldClasspath ?: [], classpath)
    }
}