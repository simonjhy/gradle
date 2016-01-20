/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.performance

import org.gradle.performance.categories.Experiment
import org.gradle.performance.categories.JavaPerformanceTest
import org.junit.experimental.categories.Category
import spock.lang.Unroll

import static org.gradle.performance.measure.DataAmount.mbytes
import static org.gradle.performance.measure.Duration.millis

@Category([Experiment, JavaPerformanceTest])
class JavaFullAssembleDaemonPerformanceTest extends AbstractCrossVersionPerformanceTest {
    @Unroll("full assemble Java software model build - #testProject")
    def "full assemble Java software model build"() {
        given:
        runner.testId = "clean build java project $testProject which doesn't declare any API"
        runner.testProject = testProject
        runner.tasksToRun = ['clean', 'assemble']
        runner.maxExecutionTimeRegression = maxTimeRegression
        runner.maxMemoryRegression = maxMemoryRegression
        runner.targetVersions = ['2.9', '2.10', 'last']
        runner.useDaemon = true
        runner.gradleOpts = ["-Xms2g", "-Xmx2g", "-XX:MaxPermSize=256m"]

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()

        where:
        testProject                                  | maxTimeRegression | maxMemoryRegression
        "smallJavaSwModelCompileAvoidanceWithoutApi" | millis(800)       | mbytes(5)
        "largeJavaSwModelCompileAvoidanceWithoutApi" | millis(1200)      | mbytes(50)
        "largeJavaSwModelProject"                    | millis(1200)      | mbytes(50)
    }

    @Unroll("#scenario build")
    def "build"() {
        given:
        runner.testId = "big project old java plugin $scenario build"
        runner.testProject = "bigOldJavaMoreSource"
        runner.useDaemon = true
        runner.tasksToRun = tasks
        runner.maxExecutionTimeRegression = millis(1000) // std dev for these tests ~1 s
        runner.maxMemoryRegression = mbytes(50)
        runner.targetVersions = ['2.0', '2.8', '2.11', 'last']
        runner.gradleOpts = ["-Xms1g", "-Xmx1g", "-XX:MaxPermSize=256m"]

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()

        where:
        scenario  | tasks
        "full"    | ["clean", "assemble"]
    }
}