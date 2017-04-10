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
  private static ConcurrentHashMap<String,Exception> downErrorStream = new ConcurrentHashMap<String,Exception>();
  // Instead of PublishSubject cause missing event when starting
  private static ReplaySubject<Message> mainStream = ReplaySubject.create();
  private final static int minThread = 50;
  private final static int maxThread = 500;
  private final static int maxQueue = 500; // Max
  private static ThreadPoolExecutor threadPool;

  /*
  sendMessage
  userID: ID of sender
  message: message to sent
  Description : The message will be emmitted by mainStream of Message class.
  The method return a Observable that waiting for response of postMessage with specificed timeout.
  */
  public static Observable<Exception> sendMessage(Message message) {
    long timeout = 1000; // timeout of waiting for the response of exception
    mainStream.onNext(message);
    Future<Exception> futureJob = getError(message.getUMessID());
    return Observable.fromFuture(futureJob,timeout,TimeUnit.MILLISECONDS).doOnDispose(() -> futureJob.cancel(true));
  }

  public static Observable<GroupedObservable<Integer,Message>> userMessageStream() {
    return mainStream.groupBy(item -> item.getUser());
  }

  private static Future<Exception> getError(String uMessID) {
      Callable<Exception> task = () -> {
          try {
              while(true) {
                if(downErrorStream.containsKey(uMessID)) {
                  return downErrorStream.remove(uMessID);
                }
                Thread.sleep(10);
              }
          }
          catch (InterruptedException e) {
              throw new IllegalStateException("task interrupted", e);
          }
      };
      return threadPool.submit(task);
  }

  private static void initWorkerPool() {
      BlockingQueue queue = new ArrayBlockingQueue<Runnable>(maxQueue);
      threadPool = new ThreadPoolExecutor(minThread, maxThread, 0L, TimeUnit.MILLISECONDS, queue);
      // by default (unfortunately) the ThreadPoolExecutor will throw an exception
      // when you submit the (maxThread + 1)st job, to have it block do:
      threadPool.setRejectedExecutionHandler(new RejectedExecutionHandler() {
         public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
           try {
            // this will block if the queue is full
            executor.getQueue().put(r);
          } catch (Exception e) {
            e.printStackTrace();
          }
         }
      });
  }

  public void run() {
    // Initialize thread pool for error responses worker
    initWorkerPool();

    // Start the main stream of messages
    Messenger.userMessageStream()
    .subscribe(observable -> {
      observable
      .throttleFirst(10,TimeUnit.MILLISECONDS) // Limit message rate 1/10 ms
      .subscribe(new Observer<Message>() {
          @Override
          public void onNext(Message item) {
            try {
              item.postMessage();
              // A Little hack - Put an empty message Exception to regconize there's no error.
              downErrorStream.putIfAbsent(item.getUMessID(), new Exception(""));
            } catch(Exception e) {
              downErrorStream.putIfAbsent(item.getUMessID(), e);
            }
          }
          @Override
          public void onError(Throwable error) {}
          @Override public void onComplete() {}
          @Override public void onSubscribe(Disposable d) {}
      });
    });
  }
}
