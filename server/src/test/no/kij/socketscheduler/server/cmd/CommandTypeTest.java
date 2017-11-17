package no.kij.socketscheduler.server.cmd;

import org.junit.Test;

import static org.junit.Assert.*;

public class CommandTypeTest {

    @Test
    public void testLecturerEnumNotNull() {
        assertNotNull(CommandType.LECTURER);
    }

    @Test
    public void testSubjectEnumNotNull() {
        assertNotNull(CommandType.SUBJECT);
    }

    @Test
    public void testListEnumNotNull() {
        assertNotNull(CommandType.LIST);
    }

    @Test
    public void testSearchEnumNotNull() {
        assertNotNull(CommandType.SEARCH);
    }

    @Test
    public void testNoneEnumNotNull() {
        assertNotNull(CommandType.NONE);
    }
}