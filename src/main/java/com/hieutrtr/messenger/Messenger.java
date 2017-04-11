package com.hieutrtr.messenger;

import java.util.ArrayList;
import io.reactivex.*;
import io.reactivex.observables.*;
import io.reactivex.functions.*;
import io.reactivex.disposables.Disposable;
import java.lang.Thread;
import java.util.concurrent.TimeUnit;
import org.reactivestreams.Subscriber;
import io.reactivex.subjects.ReplaySubject;
import java.util.concurrent.*;


public class Messenger implements Runnable {
  // Instead of PublishSubject cause missing event when starting
  private static ReplaySubject<ReplaySubject<Message>> mainStream = ReplaySubject.create();
  private static long retryDelay = 1000;
  /*
  sendMessage
  userID: ID of sender
  message: message to sent
  Description : The message will be emmitted by mainStream of Message class.
  The method return a Observable that waiting for response of postMessage with specificed timeout.
  */
  public static void sendMessage(Message message, Consumer<Throwable> callback) {
    long timeout = 1000; // timeout of waiting for the response of exception
    ReplaySubject<Message> messSubject = ReplaySubject.create();
    messSubject.onNext(message);
    mainStream.onNext(messSubject);

    messSubject.subscribe(
    item -> {},
    error -> {callback.accept(error);}
    );
  }

  public static Observable<GroupedObservable<Integer,ReplaySubject<Message>>> userMessageStream() {
    return mainStream.groupBy(observable -> observable.blockingFirst().getUser());
  }

  public void run() {
    // Start the main stream of messages
    Messenger.userMessageStream()
    .subscribe(observable -> {
      observable
      .throttleFirst(10,TimeUnit.MILLISECONDS) // Limit message rate 1/10 ms
      .subscribe(messSubject -> {
        messSubject.map(mess -> {
          if(!mess.postMessage()) {
            throw new Exception("Cannot sent message");
          }
          return mess;
        })
        .retryWhen(new RetryWithDelay(1, retryDelay))
        .subscribe(item -> {}, error -> {
          messSubject.onError(error);
        });
      });
    });
  }

  public class RetryWithDelay implements Function<Observable<? extends Throwable>, Observable<?>> {
    private final int maxRetries;
    private final long retryDelayMillis;
    private int retryCount;

    public RetryWithDelay(final int maxRetries, final long retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
        this.retryCount = 0;
    }

    @Override
    public Observable<?> apply(final Observable<? extends Throwable> attempts) {
      return attempts
        .flatMap(new Function<Throwable, Observable<?>>() {
          @Override
          public Observable<?> apply(final Throwable throwable) {
              if (++retryCount < maxRetries) {
                  // When this Observable calls onNext, the original
                  // Observable will be retried (i.e. re-subscribed).
                  return Observable.timer(retryDelayMillis,
                          TimeUnit.MILLISECONDS);
              }

              // Max retries hit. Just pass the error along.
              return Observable.error(throwable);
          }
      });
    }
  }

/*
------------------------------------------TEST-------------------------------------------------------
*/
  public static void main(String[] args) {
    (new Thread(new Messenger())).start();
    // Normal users
    for(int i = 0; i < 10; i++) {
      Messenger.sendMessage(new SMSMessage("+84909192322",i,"I'm normal user"), new Consumer<Throwable>() {
        @Override
        public void accept(Throwable t) {
          System.out.println("Handle this error: " + t);
        }
      });
    }

    // Spammer
    try{
      Thread.sleep(2000);
    } catch(Exception e) {}
    System.out.println("1 message / 2 ms of 100 messages");
    for(int i = 0; i < 100; i++) {
      Messenger.sendMessage(new SMSMessage("+84909192322",1907,"I'm normal spammer"), new Consumer<Throwable>() {
        @Override
        public void accept(Throwable t) {
          System.out.println("Handle this error: " + t);
        }
      });
      try{
        Thread.sleep(2);
      } catch(Exception e) {}
    }

    // Message Error
    System.out.println("Messages got error");
    for(int i = 0; i < 10; i++) {
      Messenger.sendMessage(new SMSMessage("+8490919232",i + 100,"I'm normal spammer"), new Consumer<Throwable>() {
        @Override
        public void accept(Throwable t) {
          System.out.println("Handle this error: " + t);
        }
      });
    }
  }

  static class SMSMessage extends Message {
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
}
