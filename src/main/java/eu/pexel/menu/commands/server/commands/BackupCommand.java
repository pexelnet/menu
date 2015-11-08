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
package eu.pexel.menu.commands.server.commands;

import eu.pexel.menu.ANSI;
import eu.pexel.menu.Command;
import eu.pexel.menu.CommandArgs;
import eu.pexel.menu.Utils;
import eu.pexel.menu.commands.server.ServerUtils;
import eu.pexel.menu.exceptions.CommandExecutionException;
import eu.pexel.menu.exceptions.InvalidUsageException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BackupCommand extends Command {

    private static final Path BACKUPS_DIR = Paths.get("backups");
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    public BackupCommand() {
        super("server:backup <name>", "Backups specific server.");
    }

    @Override
    public void run(CommandArgs args) {
        if (args.length() < 1) {
            throw new InvalidUsageException();
        }

        String serverName = args.readString();
        if (!ServerUtils.exists(serverName)) {
            throw new CommandExecutionException("Server " + serverName + " does not exists!");
        }

        String ls;
        try {
            ls = Utils.execOutput("screen -ls");
        } catch (IOException e) {
            throw new CommandExecutionException(e);
        }

        if (!ls.contains(serverName)) {
            // Backup folder using tar.
            out.info("Backing up server " + serverName + "...");
            String tarName = serverName + "_" + sdf.format(new Date()) + ".tar";

            Path tarFile = BACKUPS_DIR.resolve(tarName).toAbsolutePath();
            Path serverFolder = ServerUtils.getServerFolder(serverName).toAbsolutePath();

            Process process;
            try {
                process = Utils.execBuild("tar", "-cpzf", tarFile.toString(), serverFolder.toString()).start();
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

            out.success("Server " + serverName + " backed up successfully!");

        } else {
            throw new CommandExecutionException("Server is still running! Please stop the server before backing it up!");
        }
    }
}
