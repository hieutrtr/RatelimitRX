package com.hieutrtr.messenger;

import org.junit.Test;
import org.junit.Ignore;
import static org.junit.Assert.*;

public class TestMessage {
  @Test
  public void TestMessage() {
    Message mess = new Message(1907,"I'm fine") {
      @Override
      public void postMessage() throws Exception {
        System.out.println("Do PostSMS: " + userID + " " + message);
      }
    };
    assertEquals(1907,mess.getUser());
    assertEquals("I'm fine",mess.getMessage());
  }
}
