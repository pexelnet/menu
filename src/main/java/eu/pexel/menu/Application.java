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

import eu.pexel.menu.console.Console;
import eu.pexel.menu.console.DefaultConsole;
import eu.pexel.menu.exceptions.CommandExecutionException;
import eu.pexel.menu.exceptions.CommandNotFoundException;
import eu.pexel.menu.exceptions.InvalidUsageException;
import eu.pexel.menu.output.ANSIConsoleOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    private final Map<String, Map<String, Command>> commands = new HashMap<>();
    private final CommandDispatcher dispatcher = new CommandDispatcher();
    private final Console console = new DefaultConsole();

    private boolean exiting;

    public Application(List<Command> commandList) {
        // Terminal init.
        console.writeLine(ANSI.GREEN + "Pexel.eu - Use help for command list - 1.0" + ANSI.RESET);

        // Add system commands.
        addCommand(new HelpCommand());
        addCommand(new ExitCommand());

        // Add user commands.
        commandList.forEach(this::addCommand);

        // Init reading.
        Scanner scanner = new Scanner(System.in);
        String line;
        while (!exiting) {
            line = scanner.nextLine();
            try {
                long startTime = System.currentTimeMillis();
                dispatcher.dispatch(line);
                long took = System.currentTimeMillis() - startTime;
                if (took > 5000) {
                    console.write(ANSI.GREEN + "Command " + line + " executed in "
                            + new DecimalFormat("##.##").format(took / 1000f) + " seconds." + ANSI.RESET);
                    console.writeLine("" + (char) 7);
                }

            } catch (CommandNotFoundException e) {
                console.writeLine(ANSI.RED + e.getMessage() + ANSI.RESET);
            } catch (InvalidUsageException e) {
                console.writeLine(ANSI.RED + "Invalid usage of this command!" + ANSI.RESET);
                console.writeLine(ANSI.CYAN + "Usage: (this is not implemented, blame programmer)" + ANSI.RESET);
            } catch (CommandExecutionException e) {
                console.writeLine(ANSI.RED + e.getMessage() + ANSI.RESET);
                log.error("Can't dispatch command " + line + "!", e);
            }
        }
    }

    public void addCommand(@Nonnull Command cmd) {
        if (!commands.containsKey(cmd.getCategory())) {
            commands.put(cmd.getCategory(), new HashMap<>());
        }

        if (commands.get(cmd.getCategory()).containsKey(cmd.getName())) {
            throw new IllegalArgumentException("Command with specified name '" + cmd.getName()
                    + "' is already registered in group " + cmd.getCategory());
        }

        commands.get(cmd.getCategory()).put(cmd.getName(), cmd);
        log.info("Registered command {}:{} on {}", cmd.getCategory(), cmd.getName(), cmd.getClass().getName());
        cmd.out = new ANSIConsoleOutput(this.console);
    }

    private class HelpCommand extends Command {
        public HelpCommand() {
            super("help", "Displays all commands.");
        }

        @Override
        public void run(@Nonnull CommandArgs args) {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Map<String, Command>> entry : commands.entrySet()) {
                sb.append(ANSI.GREEN);
                sb.append(" Group: ");
                sb.append(entry.getKey());
                sb.append(ANSI.RESET);
                sb.append('\n');
                for (Map.Entry<String, Command> entry2 : entry.getValue().entrySet()) {
                    sb.append(ANSI.YELLOW);
                    sb.append("  ");
                    if (!entry.getKey().equals("general")) {
                        sb.append(entry.getKey());
                        sb.append(':');
                    }
                    sb.append(entry2.getKey());
                    sb.append(ANSI.RESET);
                    if (entry2.getValue().getParamsString() != null) {
                        sb.append(' ');
                        sb.append(entry2.getValue().getParamsString());
                    }
                    sb.append(" - ");
                    sb.append(entry2.getValue().getDescription());
                    sb.append('\n');
                }
            }

            // Output to console.
            console.write("Available commands: \n" + sb.toString());
        }
    }


    private class ExitCommand extends Command {

        public ExitCommand() {
            super("exit", "Exits the menu.");
        }

        @Override
        public void run(@Nonnull CommandArgs args) {
            exiting = true;
        }
    }

    private class CommandDispatcher {

        public void dispatch(String line) {
            String category = "general";
            String name;
            String[] params = new String[0];

            // Parse.
            if (line.contains(" ")) {
                String[] array = line.split(Pattern.quote(" "));
                if (array[0].contains(":")) {
                    String[] array2 = array[0].split(":");
                    category = array2[0];
                    name = array2[1];
                } else {
                    name = array[0];
                }
                params = new String[array.length - 1];
                System.arraycopy(array, 1, params, 0, params.length);
            } else {
                if (line.contains(":")) {
                    String[] array2 = line.split(":");
                    category = array2[0];
                    name = array2[1];
                } else {
                    name = line;
                }
            }

            // Dispatch
            dispatch0(category, name, params);
        }

        private void dispatch0(String category, String name, String[] params) {
            if (!commands.containsKey(category)) {
                throw new CommandNotFoundException("Category " + category + " was not found!");
            }

            if (!commands.get(category).containsKey(name)) {
                if (category.equals("general")) {
                    throw new CommandNotFoundException("Command " + name + " was not found!");
                } else {
                    throw new CommandNotFoundException("Command " + category + ":" + name + " was not found!");
                }
            }

            try {
                log.info("Running command {}:{} {}...", category, name,
                        Arrays.stream(params).collect(Collectors.joining(", ")));
                commands.get(category).get(name).run(new CommandArgs(params));
            } catch (Exception e) {
                throw new CommandExecutionException(e);
            }
        }
    }

}
