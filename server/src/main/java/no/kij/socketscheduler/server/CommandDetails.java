package no.kij.socketscheduler.server;

import java.util.List;

public class CommandDetails {
    private CommandAction action;
    private CommandType type;
    private List<String> args;

    public CommandDetails() {
        this.action = null;
        this.type = null;
        this.args = null;
    }

    public CommandDetails(CommandAction action, CommandType type) {
        this.action = action;
        this.type = type;
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
