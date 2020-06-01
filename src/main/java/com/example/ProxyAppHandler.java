/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

class ProxyAppHandler implements InvocationHandler {

    private final MethodHandles.Lookup lookup;

    ProxyAppHandler(MethodHandles.Lookup lookup) {
        this.lookup = lookup;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if ("readYaml".equals(methodName) && args.length == 1) {
            InputStream arg = (InputStream) args[0];
            return lookup
                    .in(App.class)
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(proxy)
                    .invoke(arg);
        } else if ("toString".equals(methodName)) {
            return "App(proxy)";
        } else if ("toJson".equals(methodName) && args.length == 1) {
            Object arg = args[0];
            return arg.toString();
        }
        throw new UnsupportedOperationException(
                String.format("%s with argument[%s] is not supported.", methodName, Arrays.toString(args)));
    }
}
