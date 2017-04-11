// package com.hieutrtr.messenger;
//
// import static org.junit.Assert.*;
// import static org.mockito.Mockito.*;
// import org.mockito.runners.MockitoJUnitRunner;
// import org.junit.Test;
// import org.junit.Before;
// import org.junit.runner.RunWith;
// import io.reactivex.functions.*;
// import io.reactivex.subscribers.*;
// import io.reactivex.observables.*;
// import io.reactivex.Observable;
// import io.reactivex.Observer;
// import io.reactivex.disposables.Disposable;
// import io.reactivex.observers.TestObserver;
// import java.util.concurrent.TimeoutException;
// import java.lang.RuntimeException;
//
// @RunWith(MockitoJUnitRunner.class)
// public class TestMessenger {
//
//   class MockMessage extends Message {
//     public MockMessage(int userID, String message) {
//       super(userID,message);
//     }
//     @Override
//     public boolean postMessage() throws Exception {
//       System.out.println("Do PostSMS: " + userID + " " + message);
//     }
//   }
//
//   @Before
//    public void setUp() throws Exception {
//      (new Thread(new Messenger())).start();
//      Thread.sleep(2000);
//    }
//
//   @Test
//   public void TestUserMessageStream() {
//     TestObserver<GroupedObservable<Integer,Message>> testObserver = new TestObserver<>();
//     testObserver = Messenger.userMessageStream().test();
//     Messenger.sendMessage(new MockMessage(1,"Hello Hieu"));
//     Messenger.sendMessage(new MockMessage(2,"Hello Hieu"));
//     Messenger.sendMessage(new MockMessage(3,"Hello Hieu"));
//     Messenger.sendMessage(new MockMessage(4,"Hello Hieu"));
//     Messenger.sendMessage(new MockMessage(5,"Hello Hieu"));
//     testObserver.assertNoErrors();
//     testObserver.assertValueCount(5);
//   }
//
//   @Test
//   public void TestSpamErrorFromCallBack() throws Exception {
//     TestObserver<Exception> TestOb1 = new TestObserver<Exception>();
//     TestObserver<Exception> TestOb2 = new TestObserver<Exception>();
//     Messenger.sendMessage(new MockMessage(1,"Hello Hieu")).subscribe(TestOb1);
//     TestOb1.assertNoErrors();
//     Messenger.sendMessage(new MockMessage(1,"Hello Hieu")).subscribe(TestOb2);
//     TestOb2.assertError(TimeoutException.class);
//   }
//
//   @Test
//   public void TestSuccessfulSendMessage() throws Exception {
//     TestObserver<Exception> TestOb1 = new TestObserver<Exception>();
//     TestObserver<Exception> TestOb2 = new TestObserver<Exception>();
//     Messenger.sendMessage(new MockMessage(1,"Hello Hieu")).subscribe(TestOb1);
//     TestOb1.assertNoErrors();
//     Thread.sleep(100);
//     Messenger.sendMessage(new MockMessage(1,"Hello Hieu")).subscribe(TestOb2);
//     TestOb1.assertNoErrors();
//   }
//
//   @Test
//   public void TestSuccessfulSendMessageOn2Users() throws Exception {
//     TestObserver<Exception> TestOb1 = new TestObserver<Exception>();
//     TestObserver<Exception> TestOb2 = new TestObserver<Exception>();
//     Messenger.sendMessage(new MockMessage(1,"Hello Hieu")).subscribe(TestOb1);
//     Messenger.sendMessage(new MockMessage(2,"Hello Hieu")).subscribe(TestOb2);
//     TestOb1.assertNoErrors();
//     TestOb2.assertNoErrors();
//   }
//
//   @Test
//   public void TestHadlePostMessageError() throws Exception {
//     Message errMess = new Message(1,"Hello Hieu") {
//       @Override
//       public boolean postMessage() throws Exception {
//         throw new RuntimeException("Cannot sent message");
//       }
//     };
//     TestObserver<Exception> testOb = new TestObserver<Exception>();
//     Messenger.sendMessage(errMess).subscribe(testOb);
//     testOb.assertNoErrors();
//     testOb.assertValueCount(1);
//   }
// }
