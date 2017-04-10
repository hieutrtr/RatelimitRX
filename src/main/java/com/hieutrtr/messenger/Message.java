package com.hieutrtr.messenger;
import java.sql.Timestamp;
// import io.reactivex.*;

public abstract class Message {

  protected int userID;
  protected String message;
  protected String umi;

  public Message(int uid, String mess) {
    userID = uid;
    message = mess;
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    umi = String.valueOf(userID) + timestamp.getTime();
  }

  public String getUMessID() {
    return umi;
  }

  public String getMessage() {
    return message;
  }

  public int getUser() {
    return userID;
  }

  public abstract void postMessage() throws Exception;
}
