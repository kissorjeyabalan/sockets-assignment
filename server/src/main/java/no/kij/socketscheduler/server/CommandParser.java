package no.kij.socketscheduler.server;

import java.util.Arrays;

public class CommandParser {

    public CommandDetails parse(String command) {
        String[] splitCommand = command.toLowerCase().split(" ");
        if (splitCommand.length < 1)
            return null;

        switch (splitCommand[0]) {
            case "list":
                return createListCommand(splitCommand);
                break;
            case "search":
                break;
        }
    }


    private CommandDetails createListCommand(String[] args) {
        if (args.length < 2)
            return createHelp(CommandType.LIST);

        CommandType cmdType = parseCommandType(args[1]);
        if (cmdType == null)
            return createHelp(CommandType.LIST);

        CommandDetails listCmd = new CommandDetails();
        listCmd.setAction(CommandAction.LIST_ALL);
        listCmd.setType(cmdType);

        return listCmd;
    }

    private CommandDetails createHelp(CommandType type) {
        return new CommandDetails(CommandAction.SEND_HELP, type, null);
    }

    private CommandType parseCommandType(String type) {
        CommandType commandType = null;
        switch (type) {
            case "lecturer":
                commandType = CommandType.LECTURER;
                break;
            case "subject":
                commandType = CommandType.SUBJECT;
                break;
        }
        return commandType;
    }

}
