/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.testkit.runner.fixtures

import org.gradle.integtests.fixtures.AbstractMultiTestRunner
import org.gradle.integtests.fixtures.executer.IntegrationTestBuildContext
import org.gradle.testkit.runner.fixtures.annotations.Debug
import org.gradle.testkit.runner.fixtures.annotations.NoDebug
import org.gradle.wrapper.GradleUserHomeLookup

class GradleRunnerIntegTestRunner extends AbstractMultiTestRunner {

    /**
     * Read by tests to configure themselves for debug or not.
     */
    public static boolean debug

    protected static final IntegrationTestBuildContext BUILD_CONTEXT = new IntegrationTestBuildContext()

    GradleRunnerIntegTestRunner(Class<?> target) {
        super(target)
    }

    @Override
    protected void createExecutions() {
        [true, false].each { add(new GradleRunnerExecution(it)) }
    }

    protected static class GradleRunnerExecution extends AbstractMultiTestRunner.Execution {

        protected final boolean debug
        private String gradleUserHomeSetting

        GradleRunnerExecution(boolean debug) {
            this.debug = debug
        }

        @Override
        protected String getDisplayName() {
            "debug = $debug"
        }

        @Override
        protected void before() {
            GradleRunnerIntegTestRunner.debug = debug
            gradleUserHomeSetting = System.setProperty(GradleUserHomeLookup.GRADLE_USER_HOME_PROPERTY_KEY, BUILD_CONTEXT.gradleUserHomeDir.absolutePath)
        }

        @Override
        protected void after() {
            if (gradleUserHomeSetting) {
                System.setProperty(GradleUserHomeLookup.GRADLE_USER_HOME_PROPERTY_KEY, gradleUserHomeSetting)
            }
        }

        @Override
        protected boolean isTestEnabled(AbstractMultiTestRunner.TestDetails testDetails) {
            (!debug || !testDetails.getAnnotation(NoDebug)) && (debug || !testDetails.getAnnotation(Debug))
        }
    }

}
