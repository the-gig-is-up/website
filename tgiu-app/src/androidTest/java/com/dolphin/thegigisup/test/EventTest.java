package com.dolphin.thegigisup.test;

import com.dolphin.thegigisup.models.Event;
import org.junit.runners.JUnit4;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Tests Event
 */
@RunWith(JUnit4.class)
public class EventTest {


    private Event event;

    @Before
    public void initialize() {
        event = new Event();
        event.setId(1);
    }

    @Test
    public void getEventID() {

        assertEquals((long) 1, (long) event.getId());
    }
}
