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

package org.apache.aries.component.dsl.internal;

import org.apache.aries.component.dsl.OSGiResult;

/**
 * @author Carlos Sierra Andrés
 */
public class EffectsOSGi extends OSGiImpl<Void> {

    public EffectsOSGi(
        Runnable onAddingBefore, Runnable onAddingAfter,
        Runnable onRemovingBefore, Runnable onRemovingAfter) {

        super((bundleContext, op) -> {
            onAddingBefore.run();

            try {
                Runnable terminator = op.publish(null);

                OSGiResult result = () -> {
                    try {
                        onRemovingBefore.run();
                    }
                    catch (Exception e) {
                        //TODO: logging
                    }

                    try {
                        terminator.run();
                    }
                    catch (Exception e) {
                        //TODO: logging
                    }

                    try {
                        onRemovingAfter.run();
                    }
                    catch (Exception e) {
                        //TODO: logging
                    }
                };

                try {
                    onAddingAfter.run();
                }
                catch (Exception e) {
                    result.run();

                    throw e;
                }

                return new OSGiResultImpl(result);
            }
            catch (Exception e) {
                try {
                    onRemovingAfter.run();
                }
                catch (Exception e1) {
                    //TODO: logging
                }

                throw e;
            }
        });
    }
}
