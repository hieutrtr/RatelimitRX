package com.hieutrtr.examples;

import com.hieutrtr.messenger.*;
import io.reactivex.functions.*;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.Observer;

public class NormalUser {

  public static void main(String[] args) {
    (new Thread(new Messenger())).start();

    try { Thread.sleep(1000); }
    catch(Exception e) {}

    // Normal users
    for(int i = 0; i < 10; i++) {
      Messenger.sendMessage(new SMSMessage("+84909192322",i,"I'm normal user"), new Consumer<Throwable>() {
        @Override
        public void accept(Throwable t) {
          System.out.println("Handle this error: " + t);
        }
      });
    }
  }
}
