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

@Category([JavaPerformanceTest])
class JavaFullAssembleDaemonPerformanceTest extends AbstractCrossVersionPerformanceTest {

    @Category([Experiment])
    @Unroll("full assemble Java software model build - #testProject")
    def "full assemble Java software model build"() {
        given:
        runner.testId = "full assemble Java build $testProject (daemon)"
        runner.previousTestIds = ["clean build java project $testProject which doesn't declare any API"]
        runner.testProject = testProject
        runner.tasksToRun = ['clean', 'assemble']
        runner.maxExecutionTimeRegression = maxTimeRegression
        runner.maxMemoryRegression = maxMemoryRegression
        runner.targetVersions = targetVersions
        runner.useDaemon = true
        runner.gradleOpts = ["-Xms2g", "-Xmx2g"]

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()

        where:
        testProject                                  | maxTimeRegression | maxMemoryRegression | targetVersions
        "smallJavaSwModelCompileAvoidanceWithoutApi" | millis(800)       | mbytes(5)           | ['2.11', 'last']
        "largeJavaSwModelCompileAvoidanceWithoutApi" | millis(3000)      | mbytes(50)          | ['2.11', 'last']
        "smallJavaSwModelProject"                    | millis(800)       | mbytes(5)           | ['2.9', 'last']
        "largeJavaSwModelProject"                    | millis(5000)      | mbytes(50)          | ['2.10', 'last']
        "bigNewJava"                                 | millis(1000)      | mbytes(50)          | ['2.11', 'last']
        "mediumNewJava"                              | millis(1000)      | mbytes(50)          | ['2.11', 'last']
        "smallNewJava"                               | millis(800)       | mbytes(5)           | ['2.9', '2.10', 'last']
    }

    @Unroll("full assemble Java build - #testProject")
    def "full assemble Java build"() {
        given:
        runner.testId = "full assemble Java build $testProject (daemon)"
        if (testProject == "bigOldJavaMoreSource") {
            runner.previousTestIds = ["big project old java plugin full build"]
        }
        runner.testProject = testProject
        runner.useDaemon = true
        runner.tasksToRun = ["clean", "assemble"]
        runner.maxExecutionTimeRegression = maxExecutionTimeRegression
        runner.maxMemoryRegression = mbytes(50)
        runner.targetVersions = ['2.11', 'last']
        runner.gradleOpts = ["-Xms2g", "-Xmx2g"]

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()

        where:
        testProject            | maxExecutionTimeRegression
        "bigOldJavaMoreSource" | millis(1000)
        "bigOldJava"           | millis(1000)
        "mediumOldJava"        | millis(1000)
        "smallOldJava"         | millis(1000)
    }
}
