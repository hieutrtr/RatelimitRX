package com.hieutrtr.examples;

import com.hieutrtr.messenger.*;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.Observer;

public class NormalUser {

  // This is SMS API extends Message for sending via Messenger
  static class SMSMessage extends Message {
    private String phoneNumber;
    public SMSMessage(String phoneNumber, int userID, String message) {
      super(userID, message);
      this.phoneNumber = phoneNumber;
    }

    @Override
    public void postMessage() throws Exception {
      if(phoneNumber.isEmpty()) {
        throw new Exception("Phone number is empty");
      }
      if( phoneNumber.length() < 12 || phoneNumber.length() > 13
      || (!phoneNumber.startsWith("+84") && !phoneNumber.startsWith("84")) ) {
        throw new Exception("Phone number is invalid");
      }
      System.out.printf("User <%d>: SMS was sent by %s - %s",userID, phoneNumber, message);
    }
  }

  public static void main(String[] args) {
    (new Thread(new Messenger())).start();
    try { Thread.sleep(1000); }
    catch(Exception e) {}
    for(int i = 0; i < 1000; i++) {
      Message mess = new SMSMessage("+84909192322", i, "I'm Spammer ! HAHA! \n");
      Messenger.sendMessage(mess);
    }
    System.out.println("End of example");
  }
}
