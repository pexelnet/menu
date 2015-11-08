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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;

public class Utils {

    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    private Utils() {
    }

    @Nullable
    public static Process exec(@Nonnull String cmd) {
        try {
            return Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            log.error("Error while executing command " + cmd, e);
        }
        return null;
    }

    @Nullable
    public static ProcessBuilder execBuild(@Nonnull String... cmds) {
        return new ProcessBuilder(cmds);
    }

    public static void copy(@Nonnull String from, @Nonnull String to) {
        try {
            Files.copy(Paths.get(from), Paths.get(to));
        } catch (IOException e) {
            log.error("Error while copying files! ", e);
        }
    }

    public static void mkdir(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            log.error("Can't create directory!");
        }
    }

    public static void download(String from, Path to) throws IOException {
        try {
            URL website = new URL(from);
            try (InputStream in = website.openStream()) {
                Files.copy(in, to, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            log.error("Failed to download file!", e);
            throw new IOException("File not downloaded!", e);
        }
    }

    public static String execOutput(String cmd) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(exec(cmd).getInputStream()))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    public static void sleep(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            // Nothing...
        }
    }
}
