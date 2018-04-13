// ITelephony.aidl
package com.aa.calldefender.com.android.internal.telephony;

// Declare any non-default types here with import statements

interface ITelephony {

   boolean endCall();
   void answerRingingCall();
   void silenceRinger();

}
