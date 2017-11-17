package no.kij.socketscheduler.server.cmd;

import java.util.Arrays;
import java.util.List;

/**
 * The purpose of this class is to parse a given command into something that makes sense.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
public class CommandParser {

    /**
     * Parses a given string into a command.
     *
     * @param command A string containing a command
     * @return CommandDetail about the command
     */
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
            case "exit":
                return new CommandDetails(CommandAction.EXIT, CommandType.NONE);
        }
        return null;
    }

    /**
     * Creates a list command.
     *
     * @param args The arguments to determine the type from
     * @return CommandDetail containing the list or usage command
     */
    private CommandDetails createListCommand(String[] args) {
        if (args.length < 2)
            return createUsage(CommandType.LIST);

        CommandType cmdType = parseCommandType(args[1]);
        if (cmdType == CommandType.NONE)
            return createUsage(CommandType.LIST);

        CommandDetails listCmd = new CommandDetails();
        listCmd.setAction(CommandAction.LIST);
        listCmd.setType(cmdType);
        return listCmd;
    }

    /**
     * Creates a search command.
     *
     * @param args The arguments to determine the type and content to search for
     * @return CommandDetail containing the list or usage command
     */
    private CommandDetails createSearchCommand(String[] args) {
        if (!(args.length > 2))
            return createUsage(CommandType.SEARCH);

        CommandType cmdType = parseCommandType(args[1]);
        if (cmdType == CommandType.NONE) {
            return createUsage(CommandType.SEARCH);
        }

        CommandDetails search = new CommandDetails();
        search.setAction(CommandAction.SEARCH);
        search.setType(cmdType);
        List<String> argsList = Arrays.asList(Arrays.copyOfRange(args, 2, args.length));
        search.setArgs(argsList);

        return search;
    }

    /**
     * Create a usage command for the given type
     * @param type Type to create usage command for
     * @return CommandDetail
     */
    private CommandDetails createUsage(CommandType type) {
        return new CommandDetails(CommandAction.SEND_USAGE, type);
    }

    /**
     * Creates a help command
     * @param args The type to create help for
     * @return CommandDetail containing help for given type
     */
    private CommandDetails createHelp(String[] args) {
        CommandDetails help = new CommandDetails(CommandAction.SEND_HELP, CommandType.NONE);
        if (args.length > 1) {
            help.setType(parseCommandType(args[1]));
            help.addArg(args[1]);
        }
        return help;
    }

    /**
     * Parses the type for the command
     * @param type Type to try and find a match for
     * @return CommandType
     */
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
