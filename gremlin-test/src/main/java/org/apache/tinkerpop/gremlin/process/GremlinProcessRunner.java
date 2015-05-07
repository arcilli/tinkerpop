/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tinkerpop.gremlin.process;

import org.apache.tinkerpop.gremlin.AbstractGremlinTest;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.verification.ComputerVerificationException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

/**
 * @author Daniel Kuppitz (http://gremlin.guru)
 */
public class GremlinProcessRunner extends BlockJUnit4ClassRunner {

    public GremlinProcessRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected Statement possiblyExpectingExceptions(final FrameworkMethod method, final Object test, final Statement next) {
        return new ExpectComputerVerificationException(next, (AbstractGremlinTest) test);
    }

    class ExpectComputerVerificationException extends Statement {

        private Statement next;
        private AbstractGremlinTest test;

        public ExpectComputerVerificationException(final Statement next, final AbstractGremlinTest test) {
            this.next = next;
            this.test = test;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                next.evaluate();
            } catch (ComputerVerificationException e) {
                if (!test.isComputerTest()) throw e;
                final boolean muted = Boolean.parseBoolean(System.getProperty("muteTestLogs", "false"));
                if (!muted) System.out.println(String.format(
                        "The following traversal is not valid for computer execution: %s",
                        e.getTraversal()));
            }
        }
    }
}
