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

import eu.pexel.menu.output.Output;

public class Report implements Output {

    public static final Report NO_REPORTS = new Report();

    private final StringBuilder stringBuilder = new StringBuilder();
    private boolean errors;
    private boolean empty = true;

    @Override
    public void info(String str) {
        empty = false;
        stringBuilder.append(str);
        stringBuilder.append('\n');
    }

    @Override
    public void error(String str) {
        empty = false;
        errors = true;
        stringBuilder.append(ANSI.RED);
        stringBuilder.append(str);
        stringBuilder.append(ANSI.RESET);
        stringBuilder.append('\n');
    }

    @Override
    public void warn(String str) {
        empty = false;
        stringBuilder.append(ANSI.YELLOW);
        stringBuilder.append(str);
        stringBuilder.append(ANSI.RESET);
        stringBuilder.append('\n');
    }

    @Override
    public void success(String str) {
        empty = false;
        stringBuilder.append(ANSI.GREEN);
        stringBuilder.append(str);
        stringBuilder.append(ANSI.RESET);
        stringBuilder.append('\n');
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

    public boolean isSuccessful() {
        return !errors;
    }

    public boolean isEmpty() {
        return empty;
    }
}
