package no.kij.socketscheduler.server;

public class CommandDetails {
    private CommandAction action;
    private CommandType type;
    private String[] args;

    public CommandDetails() {
        this.action = null;
        this.type = null;
        this.args = null;
    }

    public CommandDetails(CommandAction action, CommandType type, String[] args) {
        this.action = action;
        this.type = type;
        this.args = args;
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

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
