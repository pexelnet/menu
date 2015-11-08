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
package eu.pexel.menu.commands.server.creators;

import eu.pexel.menu.Report;
import eu.pexel.menu.Utils;
import eu.pexel.menu.commands.spigot.BuildCommand;
import eu.pexel.menu.exceptions.CommandExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class SpigotServerCreator implements Creator {

    private static final Logger log = LoggerFactory.getLogger(SpigotServerCreator.class);

    @Override
    public Report create(Path folder, String name) {
        Report report = new Report();

        // Create server directory.
        try {
            Files.createDirectory(folder);
        } catch (IOException e) {
            log.error("Can't create directory!", e);
            report.error("Server directory couldn't be created!");
        }

        // Copy spigot.
        Optional<Path> spigotJar = BuildCommand.getSpigot();
        if (!spigotJar.isPresent()) {
            throw new CommandExecutionException("Spigot JAR is not found! Try running spigot:build first!");
        }

        Utils.copy(spigotJar.get().toAbsolutePath().toString(),
                folder.resolve("spigot.jar").toAbsolutePath().toString());

        // Generate plugins folder.
        try {
            Files.createDirectory(folder.resolve("plugins"));
        } catch (IOException e) {
            log.error("Can't create plugins directory!", e);
            report.warn("Can't generate plugins directory!");
        }

        // Generate server.properties.
        try {
            Files.write(folder.resolve("server.properties"), "".getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("Can't create server.properties!", e);
            report.warn("server.properties was not generated!");
        }

        // Generate eula.txt.
        try {
            Files.write(folder.resolve("eula.txt"), "eula=true".getBytes("UTF-8"));
        } catch (IOException e) {
            log.error("Can't create eula.txt!", e);
            report.warn("eula.txt was not generated!");
        }
        return report;
    }
}
