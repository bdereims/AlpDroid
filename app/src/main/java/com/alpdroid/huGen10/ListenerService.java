package com.alpdroid.huGen10;


import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.net.ConnectivityManager;
import android.service.notification.NotificationListenerService;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;


public class ListenerService extends NotificationListenerService
        implements MediaSessionManager.OnActiveSessionsChangedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = ListenerService.class.getName();

    private List<MediaController> mediaControllers = new ArrayList<>();
    private final Map<MediaController, MediaController.Callback> controllerCallbacks = new WeakHashMap<>();
    private PlaybackTracker playbackTracker;
    private SharedPreferences sharedPreferences;
    private AlpdroidEr alpdroidEr;
    private NetworkStateReceiver networkStateReceiver;


    @Override
    public void onCreate() {
        AlpdroidApplication application = (AlpdroidApplication) getApplication();

        sharedPreferences = application.getSharedPreferences();
        ConnectivityManager connectivityManager =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);


        alpdroidEr =
                new AlpdroidEr(connectivityManager);

        playbackTracker = new PlaybackTracker( alpdroidEr );


        MediaSessionManager mediaSessionManager =
                (MediaSessionManager)
                        getApplicationContext().getSystemService(Context.MEDIA_SESSION_SERVICE);

        ComponentName componentName = new ComponentName(this, this.getClass());
        mediaSessionManager.addOnActiveSessionsChangedListener(this, componentName);

        try{
            if(networkStateReceiver!=null)
                unregisterReceiver(networkStateReceiver);

        }catch(Exception e){ //nothing
             }


        networkStateReceiver = new NetworkStateReceiver(alpdroidEr);
        IntentFilter filter = new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(networkStateReceiver, filter);

        // Trigger change event with existing set of sessions.
        List<MediaController> initialSessions = mediaSessionManager.getActiveSessions(componentName);
        onActiveSessionsChanged(initialSessions);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public static boolean isNotificationAccessEnabled(Context context) {
        return NotificationManagerCompat.getEnabledListenerPackages(context)
                .contains(context.getPackageName());
    }

    @Override
    public void onListenerDisconnected()
    {
        try{
            if(networkStateReceiver!=null) {
                unregisterReceiver(networkStateReceiver);
            }
        }catch(Exception e){}

        super.onListenerDisconnected();
    }

    @Override
    public void onDestroy()
    {
        try{
            if(networkStateReceiver!=null) {
                unregisterReceiver(networkStateReceiver);
            }
        }catch(Exception e){}
        alpdroidEr=null;
        playbackTracker=null;
        mediaControllers.clear();
        controllerCallbacks.clear();
        stopSelf();
        super.onDestroy();
    }

    @Override
    public void onActiveSessionsChanged(List<MediaController> activeMediaControllers) {

        Set<MediaController> existingControllers;
        existingControllers = ImmutableSet.copyOf(Iterables.filter(mediaControllers, controllerCallbacks::containsKey));
        Set<MediaController> newControllers = new HashSet<>(activeMediaControllers);

        Set<MediaController> toRemove = Sets.difference(existingControllers, newControllers);
        Set<MediaController> toAdd = Sets.difference(newControllers, existingControllers);

        for (MediaController controller : toRemove) {
            controller.unregisterCallback(controllerCallbacks.get(controller));
            playbackTracker.handleSessionTermination(controller.getPackageName());
            controllerCallbacks.remove(controller);
        }

        for (final MediaController controller : toAdd) {
            String packageName = controller.getPackageName();
            String prefKey = "player." + packageName;

            if (!sharedPreferences.contains(prefKey)) {
                boolean defaultVal = sharedPreferences.getBoolean("list_new_players", true);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(prefKey, defaultVal);
                editor.apply();
            }

            if (!sharedPreferences.getBoolean(prefKey, true)) {
                continue;
            }

            MediaController.Callback callback =
                    new MediaController.Callback() {
                        @Override
                        public void onPlaybackStateChanged(@NonNull PlaybackState state) {
                            controllerPlaybackStateChanged(controller, state);
                        }

                        @Override
                        public void onMetadataChanged(MediaMetadata metadata) {
                            controllerMetadataChanged(controller, metadata);
                        }
                    };

            controllerCallbacks.put(controller, callback);
            controller.registerCallback(callback);

            // Media may already be playing - update with current state.
            controllerPlaybackStateChanged(controller, controller.getPlaybackState());
            controllerMetadataChanged(controller, controller.getMetadata());
        }

        mediaControllers = activeMediaControllers;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.startsWith("player.")) {
            final String packageName = key.substring(7);

            if (sharedPreferences.getBoolean(key, true)) {
                onActiveSessionsChanged(mediaControllers);
            } else {
               final Optional<MediaController> optionalController =
                        Iterables.tryFind(
                                mediaControllers, input -> input.getPackageName().equals(packageName));

                if (optionalController.isPresent()
                        && controllerCallbacks.containsKey(optionalController.get())) {
                    MediaController controller = optionalController.get();
                    controller.unregisterCallback(controllerCallbacks.get(controller));
                    playbackTracker.handleSessionTermination(controller.getPackageName());
                    controllerCallbacks.remove(controller);
                }
            }
        }
    }

    private void controllerPlaybackStateChanged(MediaController controller, PlaybackState state) {
        playbackTracker.handlePlaybackStateChange(controller.getPackageName(), state);
    }

    private void controllerMetadataChanged(MediaController controller, MediaMetadata metadata) {
        playbackTracker.handleMetadataChange(controller.getPackageName(), metadata);
    }
}