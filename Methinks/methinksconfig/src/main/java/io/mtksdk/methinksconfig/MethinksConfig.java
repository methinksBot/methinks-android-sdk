package io.mtksdk.methinksconfig;

import android.content.Context;
import io.mtksdk.methinksdk.MethinkSDKManager;

/**
 * Created by kgy 2019. 9. 24.
 */

public class MethinksConfig {

    final Context context;
    final String methinksAuthKey;
    final Boolean debug;
    MethinkSDKManager methinkSDKManager;

    public MethinksConfig(Context context, String authKey, Boolean debug) {
        this.context = context;
        this.methinksAuthKey = authKey;
        this.debug = debug;
    }

    /**
     * Builder for creating MethinksConfig.
     */
    public static class Builder{
        private Context context;
        private String methinksAuthKey;
        private Boolean debug;

        /**
         * Start building a new MethinksConfig instance.
         */
        public Builder(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context must not be null");
            }
            this.context = context;
        }

        /**
         * Set the MethinksConfig to build with
         */
        public Builder methinksAuthKey(String authKey) {
            if (authKey == null) {
                throw new IllegalArgumentException("MethinksAuthKey must not be null");
            }
            this.methinksAuthKey = authKey;

            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public MethinksConfig build() {
            return new MethinksConfig(context, methinksAuthKey, false);
        }

    }

    public void initialize() {
        new MethinkSDKManager(context, this.methinksAuthKey);
    }

    public void setUser(String userId) {
        MethinkSDKManager.setUser(userId);
    }

    public void logUserAttributes(String key, Object value) {
        MethinkSDKManager.setLogUserAttributes(key, value);
    }



}
