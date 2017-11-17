package no.kij.socketscheduler.server.cmd;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains all the details about a command.
 *
 * @author Kissor Jeyabalan
 * @since 1.0
 */
public class CommandDetails {
    private CommandAction action;
    private CommandType type;
    private List<String> args;

    public CommandDetails() {
        this.action = null;
        this.type = null;
        this.args = new ArrayList<>();
    }

    /**
     * Create a new command with details.
     *
     * @param action CommandAction - This is the action to be executed
     * @param type CommandType - This is the type for the command
     */
    public CommandDetails(CommandAction action, CommandType type) {
        this.action = action;
        this.type = type;
        this.args = new ArrayList<>();
    }

    public CommandAction getAction() {
        return action;
    }

    public void setAction(CommandAction action) {
        this.action = action;
    }

    public CommandType getType() {
        return type;
    }

    public void setType(CommandType type) {
        this.type = type;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public void addArg(String arg) {
        this.args.add(arg);
    }
}
