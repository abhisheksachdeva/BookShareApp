package com.sdsmdg.bookshareapp.BSA;

import android.app.Application;

import com.sdsmdg.bookshareapp.BSA.api.NetworkingFactory;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;

/**
 * Created by ajayrahul on 26/9/17.
 */

public class BSApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetworkingFactory.init(this);
        if (!BuildConfig.DEBUG) {
            Sentry.init(BuildConfig.SENTRY_DSN, new AndroidSentryClientFactory(this));
        }
        Realm.init(this);

        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }
}
