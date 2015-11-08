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
package eu.pexel.menu.commands.spigot;

import eu.pexel.menu.ANSI;
import eu.pexel.menu.Command;
import eu.pexel.menu.CommandArgs;
import eu.pexel.menu.Utils;
import eu.pexel.menu.exceptions.CommandExecutionException;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class BuildCommand extends Command {

    private static final Path WORKING_DIR = Paths.get("system", "spigot");
    private static final String BUILD_TOOLS_URL = "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar";

    public static Optional<Path> getSpigot() {
        try {
            return Files.list(WORKING_DIR).filter(path -> path.getFileName().toString().contains("spigot")).findFirst();
        } catch (IOException e) {
            throw new CommandExecutionException("Can't get spigot JAR file!", e);
        }
    }

    public BuildCommand() {
        super("spigot:build", "Builds latest spigot from git.");
    }

    @Override
    public void run(@Nonnull CommandArgs args) throws CommandExecutionException {
        // Create directory.
        if (!Files.exists(WORKING_DIR)) {
            out.info("Creating working directory...");
            try {
                Files.createDirectory(WORKING_DIR);
            } catch (IOException e) {
                throw new CommandExecutionException("Can't create working directory.", e);
            }
        }


        // Download build tools.
        try {
            out.info("Downloading BuildTools.jar...");
            Utils.download(BUILD_TOOLS_URL, WORKING_DIR.resolve("BuildTools.jar"));
            out.success("Downloaded!");
        } catch (IOException e) {
            throw new CommandExecutionException("Can't download file!");
        }

        // Start build.
        out.info("Building spigot using BuildTools.jar...");
        Process process;
        try {
            process = Utils.execBuild("java", "-jar", "BuildTools.jar")
                    .directory(WORKING_DIR.toFile())
                    .start();
        } catch (IOException e) {
            throw new CommandExecutionException(e);
        }

        try {
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                out.info("> " + ANSI.WHITE + line);
            }
        } catch (IOException e) {
            throw new CommandExecutionException(e);
        }

    }
}
