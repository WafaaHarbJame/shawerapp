package com.shawerapp.android.backend.base;


import com.shawerapp.android.autovalue.IndividualUser;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by john.ernest on 05/10/2017.
 */

public interface AuthFramework {

    boolean isLoggedIn();

    Observable<String> getAuthenticationStatus();

    Completable updateEmail(String newEmail);

    Completable updatePassword(String newPassword);

    Maybe<String> createUserWithEmailAndPassword(String email, String password);

    Maybe<String> signInWithEmailAndPassword(String email, String password);

    Maybe<String> signInWithCustomToken(String token);

    Completable sendPasswordResetEmail(String email);

    Completable resendVerificationEmail();

    Completable reAuthenticate(String email, String password);

    Maybe<Boolean> isEmailUsed(String email);

    Completable logout();

//    Maybe<String> checkInvoiceStatus(String transactionID, String secureHash);


}
