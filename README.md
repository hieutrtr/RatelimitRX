# Messenger - Spammer Rate Limiting

## Explanation

```java
ReplaySubject<Message> mainStream = ReplaySubject.create();
Observable<GroupedObservable<Integer,Message>> groupedUserStream = mainStream.groupBy(item -> item.getUser());
```
* Using `ReplaySubject` to build a infinite stream (observable)
* Grouping mainStream to `sub observables` by `userID`. `mainStream.groupBy(item -> item.getUser());`

```java
Messenger.userMessageStream()
.subscribe(observable -> {
  observable
  .throttleFirst(10,TimeUnit.MILLISECONDS) // Limit message rate 1/10 ms
  .subscribe(new Observer<Message>() {
      ...
  });
});
```
* Limiting message rate on `grouped observable` using `throttleFirst(10,TimeUnit.MILLISECONDS)` (1 message/10 ms)

```java
public static Observable<Exception> sendMessage(Message message) {
  long timeout = 1000; // timeout of waiting for the response of exception
  mainStream.onNext(message);
  Future<Exception> futureJob = getError(message.getUMessID());
  return Observable.fromFuture(futureJob,timeout,TimeUnit.MILLISECONDS).doOnDispose(() -> futureJob.cancel(true));
}
```
* Implementation of `sendMessage` return an observable of `Exception` for callback.
* The callback observable is created from `Future` job.

```java
private static Future<Exception> getError(String uMessID) {
  Callable<Exception> task = () -> {
      ...
          while(true) {
            if(downErrorStream.containsKey(uMessID)) {
              return downErrorStream.remove(uMessID);
            }
            Thread.sleep(10);
          }
      ...
  };
  return threadPool.submit(task);
}

public void onNext(Message item) {
  try {
    item.postMessage();
    // A Little hack - Put an empty message Exception to regconize there's no error.
    downErrorStream.putIfAbsent(item.getUMessID(), new Exception(""));
  } catch(Exception e) {
    downErrorStream.putIfAbsent(item.getUMessID(), e);
  }
}
```
* `downErrorStream` is a `ConcurrentHashMap<String,Exception>` contains Exception by User Message Unique ID.
* `getError` return a future task for getting `Exception` from `downErrorStream` asynchronously.

## Example

##### Build
```
gradle fatJar
```

##### Normal Users:
Simulating sending messages by many different users.
Every user send message successfully without any delay.
```
java -cp build/libs/usersms-all-1.0.jar com.hieutrtr.examples.NormalUser
```
##### Spammer:
Simulating sending many messages by the same user continuously.
There are some message got Timeout error from callback and delay for 1s due to futureJob timeout.
With this implementation not only limit message rate but also can delay the spammer.
```
java -cp build/libs/usersms-all-1.0.jar com.hieutrtr.examples.Spammer
```
##### Non callback Spammer:
Simulating sending many messages by the same user continuously.
If there's no callback handling, the messages which over rate limiting will be drop silently.
```
java -cp build/libs/usersms-all-1.0.jar com.hieutrtr.examples.NonCallBackSpammer
```
