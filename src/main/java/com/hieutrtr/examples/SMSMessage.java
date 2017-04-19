package com.hieutrtr.examples;

import com.hieutrtr.messenger.*;

public class SMSMessage extends Message {
  private String phoneNumber;
  public SMSMessage(String phoneNumber, int userID, String message) {
    super(userID, message);
    this.phoneNumber = phoneNumber;
  }

  @Override
  public boolean postMessage() throws Exception {
    if(phoneNumber.isEmpty()) {
      throw new Exception("Phone number is empty");
    }
    if( phoneNumber.length() < 12 || phoneNumber.length() > 13
    || (!phoneNumber.startsWith("+84") && !phoneNumber.startsWith("84")) ) {
      throw new Exception("Phone number is invalid");
    }
    System.out.printf("User <%d>: SMS was sent by %s - %s\n",userID, phoneNumber, message);
    return true;
  }
}
