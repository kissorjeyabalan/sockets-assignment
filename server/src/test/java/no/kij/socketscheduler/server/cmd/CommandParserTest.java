package no.kij.socketscheduler.server.cmd;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandParserTest {
    private CommandParser parser;

    @Before
    public void setUp() {
        parser = new CommandParser();
    }

    @Test
    public void testParseLecturerListReturnsValidCommandDetails() {
        String cmd = "list lecturer";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.LIST, details.getAction());
        assertEquals(CommandType.LECTURER, details.getType());
        assertEquals(0, details.getArgs().size());
    }

    @Test
    public void testParseSubjectListReturnsValidCommandDetails() {
        String cmd = "list subject";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.LIST, details.getAction());
        assertEquals(CommandType.SUBJECT, details.getType());
        assertEquals(0, details.getArgs().size());
    }

    @Test
    public void testParseListReturnsUsageCommandDetailsWhenWrongArgumentLength() {
        String cmd = "list";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEND_USAGE, details.getAction());
        assertEquals(CommandType.LIST, details.getType());
        assertEquals(0, details.getArgs().size());
    }

    @Test
    public void testParseListReturnsUsageCommandDetailsWhenNonExistingType() {
        String cmd = "list lec";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEND_USAGE, details.getAction());
        assertEquals(CommandType.LIST, details.getType());
        assertEquals(0, details.getArgs().size());
    }

    @Test
    public void testParseSearchLecturerReturnsValidCommandDetails() {
        String cmd = "search lecturer vilde";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEARCH, details.getAction());
        assertEquals(CommandType.LECTURER, details.getType());
        assertEquals(1, details.getArgs().size());
        assertEquals("vilde", details.getArgs().get(0));
    }

    @Test
    public void testParseSearchSubjectReturnsValidCommandDetails() {
        String cmd = "search subject avansert java";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEARCH, details.getAction());
        assertEquals(CommandType.SUBJECT, details.getType());
        assertEquals(2, details.getArgs().size());
        assertEquals("avansert", details.getArgs().get(0));
        assertEquals("java", details.getArgs().get(1));
    }

    @Test
    public void testParseSearchReturnsUsageOnInvalidArgumentLength() {
        String cmd = "search subject";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEND_USAGE, details.getAction());
        assertEquals(CommandType.SEARCH, details.getType());
        assertEquals(0, details.getArgs().size());
    }

    @Test
    public void testParseSearchReturnsUsageOnInvalidCommandType() {
        String cmd = "search suabfsjdf avans";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEND_USAGE, details.getAction());
        assertEquals(CommandType.SEARCH, details.getType());
        assertEquals(0, details.getArgs().size());
    }

    @Test
    public void testParseEmptyHelpReturnsValidCommandDetails() {
        String cmd = "help";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEND_HELP, details.getAction());
        assertEquals(CommandType.NONE, details.getType());
        assertEquals(0, details.getArgs().size());
    }

    @Test
    public void testParseHelpWithParameterReturnsValidCommandDetails() {
        CommandDetails details = parser.parse("help list");
        assertEquals(CommandAction.SEND_HELP, details.getAction());
        assertEquals(CommandType.LIST, details.getType());
        assertEquals("list", details.getArgs().get(0));

        details = parser.parse("help search");
        assertEquals(CommandAction.SEND_HELP, details.getAction());
        assertEquals(CommandType.SEARCH, details.getType());
        assertEquals("search", details.getArgs().get(0));
    }

    @Test
    public void testParseHelpWithWrongParameterReturnsValidCommandDetails() {
        String cmd = "help asdfasdfs";
        CommandDetails details = parser.parse(cmd);

        assertEquals(CommandAction.SEND_HELP, details.getAction());
        assertEquals(CommandType.NONE, details.getType());
        assertEquals("asdfasdfs", details.getArgs().get(0));
    }

    @Test
    public void testParseReturnsNullWhenNoCommand() {
        String cmd = " ";
        CommandDetails details = parser.parse(cmd);

        assertNull(details);
    }

    @Test
    public void testParseReturnsNullWhenAllWrongInput() {
        String cmd = "asdf kajhsdf khjasdfkhja";
        CommandDetails details = parser.parse(cmd);

        assertNull(details);
    }

}