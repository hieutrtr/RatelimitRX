// package com.hieutrtr.examples;
//
// import com.hieutrtr.messenger.*;
// import io.reactivex.observers.DisposableObserver;
// import io.reactivex.disposables.Disposable;
// import io.reactivex.Observer;
//
// public class Spammer {
//
//   // This is SMS API extends Message for sending via Messenger
//   static class SMSMessage extends Message {
//     private String phoneNumber;
//     public SMSMessage(String phoneNumber, int userID, String message) {
//       super(userID, message);
//       this.phoneNumber = phoneNumber;
//     }
//
//     @Override
//     public boolean postMessage() throws Exception {
//       if(phoneNumber.isEmpty()) {
//         throw new Exception("Phone number is empty");
//       }
//       if( phoneNumber.length() < 12 || phoneNumber.length() > 13
//       || (!phoneNumber.startsWith("+84") && !phoneNumber.startsWith("84")) ) {
//         throw new Exception("Phone number is invalid");
//       }
//       System.out.printf("User <%d>: SMS was sent by %s - %s",userID, phoneNumber, message);
//     }
//   }
//
//   public static void main(String[] args) {
//     (new Thread(new Messenger())).start();
//     try { Thread.sleep(1000); }
//     catch(Exception e) {}
//     Message mess = new SMSMessage("+84909192322", 1907, "I'm Spammer ! HAHA! \n");
//     for(int i = 0; i < 10; i++) {
//       DisposableObserver<Exception> d = new DisposableObserver<Exception>() {
//          @Override public void onStart() {}
//          @Override public void onNext(Exception e) {
//            if(e.getMessage() != "") {
//              System.out.println(e);
//            }
//          }
//          @Override public void onError(Throwable t) {
//            System.err.println(t);
//          }
//          @Override public void onComplete() {
//          }
//       };
//       Messenger.sendMessage(mess).subscribe(d);
//       d.dispose();
//     }
//     System.out.println("End of example");
//   }
// }
