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
import eu.pexel.menu.commands.server.Type;
import eu.pexel.menu.exceptions.CommandExecutionException;

import java.io.IOException;
import java.nio.file.Files;

public class ListCommand extends Command {

    public ListCommand() {
        super("server:list", "Lists all created servers and their status.");
    }

    @Override
    public void run(CommandArgs args) {
        out.info("Configured servers: ");

        String ls;
        try {
            ls = Utils.execOutput("screen -ls");
        } catch (IOException e) {
            throw new CommandExecutionException(e);
        }

        try {
            final String finalLs = ls;
            Files.list(ServerUtils.SERVERS_DIR)
                    .filter(path -> Files.isDirectory(path))
                    .forEach(path -> {
                        String name = path.getFileName().toString();
                        Type type = ServerUtils.getServerType(path);
                        if (finalLs.contains(ServerUtils.screenSocketName(name))) {
                            out.info(ANSI.GREEN + " [UP] " + ANSI.WHITE + name + ANSI.RESET + " (" + type + ") ");
                        } else {
                            out.info(ANSI.RED + " [DOWN] " + ANSI.WHITE + name + ANSI.RESET + " (" + type + ") ");
                        }
                    });
        } catch (IOException e) {
            throw new CommandExecutionException(e);
        }
    }
}
