package no.kij.socketscheduler.server.cmd;

import java.util.Arrays;
import java.util.List;

public class CommandParser {

    public CommandDetails parse(String command) {
        String[] splitCommand = command.toLowerCase().split(" ");
        if (splitCommand.length < 1)
            return null;

        switch (splitCommand[0]) {
            case "list":
                return createListCommand(splitCommand);
            case "search":
                return createSearchCommand(splitCommand);
            case "help":
                return createHelp(splitCommand);
        }
        return null;
    }


    private CommandDetails createListCommand(String[] args) {
        if (args.length < 2)
            return createUsage(CommandType.LIST);

        CommandType cmdType = parseCommandType(args[1]);
        if (cmdType == null)
            return createUsage(CommandType.LIST);

        CommandDetails listCmd = new CommandDetails();
        listCmd.setAction(CommandAction.LIST);
        listCmd.setType(cmdType);
        return listCmd;
    }

    private CommandDetails createSearchCommand(String[] args) {
        if (!(args.length > 2))
            return createUsage(CommandType.SEARCH);

        CommandType cmdType = parseCommandType(args[1]);
        if (cmdType == null) {
            return createUsage(CommandType.SEARCH);
        }

        CommandDetails search = new CommandDetails();
        search.setAction(CommandAction.SEARCH);
        search.setType(cmdType);
        List<String> argsList = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
        search.setArgs(argsList);

        return search;
    }

    private CommandDetails createUsage(CommandType type) {
        return new CommandDetails(CommandAction.SEND_USAGE, type);
    }

    private CommandDetails createHelp(String[] args) {
        CommandDetails help = new CommandDetails(CommandAction.SEND_HELP, CommandType.NONE);
        if (args.length > 1) {
            help.setType(parseCommandType(args[1]));
            help.addArg(args[1]);
        }
        return help;
    }

    private CommandType parseCommandType(String type) {
        CommandType commandType;
        switch (type) {
            case "lecturer":
                commandType = CommandType.LECTURER;
                break;
            case "subject":
                commandType = CommandType.SUBJECT;
                break;
            case "search":
                commandType = CommandType.SEARCH;
                break;
            case "list":
                commandType = CommandType.LIST;
                break;
            default:
                commandType = CommandType.NONE;
                break;
        }
        return commandType;
    }

}
