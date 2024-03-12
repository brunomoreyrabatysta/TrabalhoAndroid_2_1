package com.brunobatista.trabalhoandroid_2_1;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthSignleton {

    private static FirebaseAuth firebaseAuth;

    private FirebaseAuthSignleton() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public static FirebaseAuth getInstance() {
        if (firebaseAuth == null) {
            synchronized (FirebaseAuthSignleton.class) {
                if (firebaseAuth == null) {
                    new FirebaseAuthSignleton();
                }
            }
        }
        return firebaseAuth;
    }
}
