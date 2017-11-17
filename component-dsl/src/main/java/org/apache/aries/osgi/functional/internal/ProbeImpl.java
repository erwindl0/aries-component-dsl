/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.aries.osgi.functional.internal;

import org.apache.aries.osgi.functional.OSGi;
import org.apache.aries.osgi.functional.OSGiResult;
import org.osgi.framework.BundleContext;

import java.util.function.Function;

/**
 * @author Carlos Sierra Andrés
 */
public class ProbeImpl<T> extends OSGiImpl<T> {

    public ProbeImpl() {
        super(new ProbeOperationImpl<>());
    }

    public Function<T, Runnable> getOperation() {
        return ((ProbeOperationImpl<T>) _operation)._op;
    }

    public static <T, S> Function<T, Runnable> getProbePipe(
        Function<OSGi<T>, OSGi<S>> then, BundleContext bundleContext,
        Function<S, Runnable> publisher) {

        ProbeImpl<T> thenProbe = new ProbeImpl<>();

        OSGiImpl<S> thenNext = (OSGiImpl<S>) then.apply(thenProbe);

        OSGiResult thenResult = thenNext._operation.run(
            bundleContext, publisher);

        Function<T, Runnable> thenPipe = thenProbe.getOperation();

        thenResult.start();

        return thenPipe;
    }

    private static class ProbeOperationImpl<T> implements OSGiOperationImpl<T> {

        BundleContext _bundleContext;
        Function<T, Runnable> _op;

        @Override
        public OSGiResultImpl run(
            BundleContext bundleContext, Function<T, Runnable> op) {
            _bundleContext = bundleContext;
            _op = op;

            return new OSGiResultImpl(NOOP, NOOP);
        }
    }

}