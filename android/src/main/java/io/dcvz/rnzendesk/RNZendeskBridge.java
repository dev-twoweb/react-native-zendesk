package io.dcvz.rnzendesk;

import android.content.Intent;

import zendesk.commonui.UiConfig;
import zendesk.core.AnonymousIdentity;
import zendesk.core.Zendesk;
import zendesk.core.Identity;
import zendesk.core.AnonymousIdentity;
import zendesk.core.JwtIdentity;
import zendesk.support.Support;
import zendesk.support.guide.HelpCenterActivity;
import zendesk.support.request.RequestActivity;

import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.ArrayList;

public class RNZendeskBridge extends ReactContextBaseJavaModule {

    public RNZendeskBridge(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNZendesk";
    }

    @ReactMethod
    public void initialize(ReadableMap config) {
        String appId = config.getString("appId");
        String zendeskUrl = config.getString("zendeskUrl");
        String clientId = config.getString("clientId");

        Zendesk.INSTANCE.init(getReactApplicationContext(), zendeskUrl, appId, clientId);
        Identity identity = new AnonymousIdentity();
        Zendesk.INSTANCE.setIdentity(identity);
        Support.INSTANCE.init(Zendesk.INSTANCE);
    }
    
    @ReactMethod
    public void identifyAnon() {
        Identity identity = new AnonymousIdentity();
        Zendesk.INSTANCE.setIdentity(identity);
    }

    @ReactMethod
    public void identifyAnon(String name, String email) {
        Identity identity = new AnonymousIdentity.Builder()
            .withNameIdentifier(name)
            .withEmailIdentifier(email)
            .build();
        Zendesk.INSTANCE.setIdentity(identity);
    }

    @ReactMethod
    public void identifyJWT(String token) {
        JwtIdentity identity = new JwtIdentity(token);
        Zendesk.INSTANCE.setIdentity(identity);
    }

    @ReactMethod
    public void showHelpCenter(ReadableMap options) {
        UiConfig hcConfig = HelpCenterActivity.builder()
                .withContactUsButtonVisible(!(options.hasKey("hideContactSupport") && options.getBoolean("hideContactSupport")))
                .config();
        Intent intent = HelpCenterActivity.builder()
                .withContactUsButtonVisible(true)
                .intent(getReactApplicationContext(), hcConfig);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
    }

    @ReactMethod
    public void showNewTicket(ReadableMap options) {
        ArrayList tags = options.getArray("tags").toArrayList();

        Intent intent = RequestActivity.builder()
                .withTags(tags)
                .intent(getReactApplicationContext());
        
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getReactApplicationContext().startActivity(intent);
        
    }
}
