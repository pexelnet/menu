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

import eu.pexel.menu.Command;
import eu.pexel.menu.CommandArgs;
import eu.pexel.menu.Report;
import eu.pexel.menu.commands.server.ServerUtils;
import eu.pexel.menu.commands.server.Type;
import eu.pexel.menu.commands.server.creators.Creators;
import eu.pexel.menu.commands.server.exceptions.UnsupportedServerType;
import eu.pexel.menu.exceptions.CommandExecutionException;
import eu.pexel.menu.exceptions.InvalidUsageException;

import javax.annotation.Nonnull;
import java.nio.file.Path;

public class CreateCommand extends Command {

    public CreateCommand() {
        super("server:create <type> <name>", "Creates a new server of specified type with specified name.");
    }

    @Override
    public void run(@Nonnull CommandArgs args) {
        if (args.length() < 2) {
            throw new InvalidUsageException();
        }

        String typeStr = args.readString();
        Type type = makeType(typeStr);
        String serverName = args.readString();

        if (ServerUtils.exists(serverName)) {
            throw new CommandExecutionException("Server " + serverName + " already exists!");
        }

        Path serverFolder = ServerUtils.getServerFolder(serverName);

        out.info("Creating server '" + serverName + "' of type " + type.toString());
        Report report = Creators
                .resolve(type)
                .create(serverFolder, serverName);

        if (!report.isEmpty()) {
            out.info(report.toString());
        }

        if (report.isSuccessful()) {
            out.success("Server created! Use 'server:start " + serverName + "' to start created server.");
        }
    }

    private Type makeType(String typeStr) {
        try {
            for (Type t : Type.values()) {
                if (t.toString().equalsIgnoreCase(typeStr) || t.is(typeStr)) {
                    return t;
                }
            }
            throw new UnsupportedServerType();
        } catch (Exception e) {
            throw new UnsupportedServerType();
        }
    }
}
