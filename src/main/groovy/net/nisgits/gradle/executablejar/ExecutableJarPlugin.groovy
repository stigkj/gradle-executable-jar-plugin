/*
 * Copyright 2011- the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.nisgits.gradle.executablejar

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact
import org.gradle.api.internal.artifacts.repositories.CommonsHttpClientResolver
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin

/**
 * A plugin for adding executable jar support to {@link JavaPlugin java projects}
 *
 * TODO should copy the "main" jar into main/main.jar (see One-JAR docs)
 * @author Stig Kleppe-Jørgensen
 */
public class ExecutableJarPlugin implements Plugin<Project> {
    public static final String EXECUTABLE_JAR_TASK_NAME = "executable-jar";

    void apply(Project project) {
        project.apply(plugin: 'java')

        project.configurations.add('executableJar') {
            visible = false
            transitive = false
            description = "The One-JAR library to be used for this project which will override the default."
        }

        project.configurations.add('executableJarDefault') {
            visible = false
            transitive = false
            description = "The default One-JAR library to be used for this project."
        }

        project.dependencies {
            executableJarDefault 'one-jar:one-jar-boot:0.97'
        }

        ExecutableJar task = project.tasks.add(EXECUTABLE_JAR_TASK_NAME, ExecutableJar);
        project.tasks.assemble.dependsOn(task)

// TODO maybe have dir for native libs here?
//      task.from {
//          file('native-libs')
//      }

        task.from {
            def configuration = project.configurations.executableJar

            if (configuration.dependencies.empty) {
                // Only add SourceForge as a repository if we must use our default version of One-JAR
                // TODO Should not be needed at all, just have to deploy One-JAR to Maven Central first
                if (project.repositories.findByName('SourceForge') == null) {
                    project.repositories.add(new CommonsHttpClientResolver(null, null)) {
                        name = 'SourceForge'
                        addArtifactPattern 'https://sourceforge.net/projects/[organization]/files/[organization]/[organization]-[revision]/[module]-[revision].[ext]/download'
                    }
                }

                configuration = project.configurations.executableJarDefault
            }

            configuration.collect { file ->
                project.zipTree(file).matching {
                    exclude 'src/**'
                }
            }
        }

        task.dependsOn {
            project.convention.plugins.java.sourceSets.main.runtimeClasspath + project.files(project.tasks.jar)
        }

        task.classpath {
            project.convention.plugins.java.sourceSets.main.runtimeClasspath + project.files(project.tasks.jar)
        }

        task.conventionMapping.mainClass = { project.mainClass }
        task.setDescription("Generates an executable jar archive with all runtime dependencies embedded.");
        task.setGroup(BasePlugin.BUILD_GROUP);

        project.extensions.defaultArtifacts.addCandidate(new ArchivePublishArtifact(task));
    }
}
