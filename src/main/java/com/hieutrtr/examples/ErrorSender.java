package com.hieutrtr.examples;

import com.hieutrtr.messenger.*;
import io.reactivex.functions.*;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.Observer;

public class ErrorSender {

  public static void main(String[] args) {
    (new Thread(new Messenger())).start();
    try { Thread.sleep(1000); }
    catch(Exception e) {}

    Message mess = new SMSMessage("+8490919232", 1907, "I'm Spammer ! HAHA! \n");
    for(int i = 0; i < 100; i++) {
      Messenger.sendMessage(mess, new Consumer<Throwable>() {
        @Override
        public void accept(Throwable t) {
          System.out.println("Handle this error: " + t);
        }
      });
      try{
        Thread.sleep(2);
      } catch(Exception e) {}
    }
  }
}
