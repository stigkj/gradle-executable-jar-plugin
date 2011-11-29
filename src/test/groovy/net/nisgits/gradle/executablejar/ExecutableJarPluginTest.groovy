/*
 * Copyright 2011 the original author or authors.
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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class ExecutableJarPluginTest extends Specification {
    private final Project project = ProjectBuilder.builder().build()
    private final ExecutableJarPlugin plugin = new ExecutableJarPlugin()

    def "should add configuration named executablejar"() {
        when:
        plugin.apply(project)
        project.mainClass = "test"
        
        then:
        project.configurations.executableJar
    }
}
