package com.shawerapp.android.backend.base;

import io.reactivex.Maybe;

/**
 * Created by john.ernest on 1/5/18.
 */

public interface RestFramework {

    Maybe<Boolean> checkUsernameAvailability(String username);

    Maybe<Boolean> checkEmailAvailability(String email);

    Maybe<String> getUserEmail(String username);

}


