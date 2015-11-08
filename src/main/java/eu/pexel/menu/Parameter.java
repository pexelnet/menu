/**
 * menu - ${project.description}
 * Copyright (c) ${project.inceptionYear}, Matej Kormuth <http://www.github.com/dobrakmato>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.pexel.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Parameter<T> {

    private static final Logger log = LoggerFactory.getLogger(Parameter.class);

    private Class<T> type;
    private String name;
    private Method converter;

    public Parameter(@Nonnull Class<T> type, @Nonnull String name) {
        this.type = type;
        this.name = name;

        this.init();
    }

    private void init() {
        try {
            this.converter = type.getMethod("valueOf", String.class);
            // Disable checks for speed.
            this.converter.setAccessible(true);
        } catch (NoSuchMethodException e) {
            log.error("Can't find valueOf(String) method on " + type.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public T convert(@Nonnull String str) {
        try {
            return (T) converter.invoke(null, str);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Can't convert '{}' to {} using valueOf(String) method!", str, converter.getClass().getName());
            return null;
        }
    }

    public Class<T> getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
