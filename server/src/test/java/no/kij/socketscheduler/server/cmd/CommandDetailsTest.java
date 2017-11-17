package no.kij.socketscheduler.server.cmd;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandDetailsTest {

    @Test
    public void testAddArg() {
        CommandDetails commandDetails = new CommandDetails();
        commandDetails.addArg("arg1");
        commandDetails.addArg("arg2");
        commandDetails.addArg("arg3");

        assertEquals(commandDetails.getArgs().get(0), "arg1");
        assertEquals(commandDetails.getArgs().get(1), "arg2");
        assertEquals(commandDetails.getArgs().get(2), "arg3");
    }

    @Test
    public void testEmptyConstructor() {
        CommandDetails commandDetails = new CommandDetails();

        assertNull(commandDetails.getAction());
        assertNull(commandDetails.getType());
        assertEquals(0, commandDetails.getArgs().size());
    }

    @Test
    public void testParameterConstructor() {
        CommandDetails commandDetails = new CommandDetails(CommandAction.LIST, CommandType.LECTURER);
        assertEquals(commandDetails.getAction(), CommandAction.LIST);
        assertEquals(commandDetails.getType(), CommandType.LECTURER);
        assertEquals(0, commandDetails.getArgs().size());
    }

}