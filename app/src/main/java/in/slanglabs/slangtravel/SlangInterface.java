package in.slanglabs.slangtravel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.slanglabs.platform.SlangBuddy;
import in.slanglabs.platform.SlangBuddyOptions;
import in.slanglabs.platform.SlangEntity;
import in.slanglabs.platform.SlangIntent;
import in.slanglabs.platform.SlangLocale;
import in.slanglabs.platform.SlangSession;
import in.slanglabs.platform.action.SlangAction.Status;
import in.slanglabs.platform.action.SlangMultiStepIntentAction;

/**
 * A utility class for initializing and managing integration with Slang,
 * where all Slang specific code resides.
 *
 * For more examples and details about how to integrate Slang with your app,
 * please refer to the documentation at:
 *     https://docs.slanglabs.in/slang/developer-guide/sdk-integration/android
 *
 * To find more details about the APIs referenced in the following code,
 * please see the API reference at:
 *     https://slanglabs.in/docs/slang/api/android/
 *
 * To initialize Slang in your application, call:
 *     SlangInterface.init(context)
 * from your onCreate method of your Application class.
 *
 * In the activity from where you want Slang to show up (the mic icon), call
 *     SlangBuddy.getBuiltinUI().show(this)
 * from the onResume method of that activity
 */
public class SlangInterface {
    private static String sBuddyId = "fill_with_your_buddy_id";
    private static String sApiKey = "fill_with_your_account_key";
    private static Application sApplication;

    /****************************************************************
     * App specific logic goes here
     ***************************************************************/

    /*
     * Action callback functions for intents
     */

    /**
     * Action for unsupported_features intent 
     */
    private static Status doUnsupportedFeatures(SlangSession slangSession, String UnsupportedActions, String searchItem, String searchPrefix) {
        Toast.makeText(sApplication, "Recognized unsupported_features intent", Toast.LENGTH_LONG).show();  // TODO: FIXME: implement it
        return Status.SUCCESS;
    }

    /**
     * Action for sort_train intent 
     */
    private static Status doSortTrain(SlangSession slangSession, String sortItem, String sortType) {
        Toast.makeText(sApplication, "Recognized sort_train intent", Toast.LENGTH_LONG).show();  // TODO: FIXME: implement it
        return Status.SUCCESS;
    }

    /****************************************************************
     * Standard boiler plate goes here.
     ***************************************************************/

    /*
     * Intents and Entities defined in Slang Console
     */

    public static final String UNSUPPORTED_FEATURES = "unsupported_features";
    public static final String UNSUPPORTED_FEATURES_UNSUPPORTEDACTIONS = "UnsupportedActions";
    public static final String UNSUPPORTED_FEATURES_SEARCH_ITEM = "search_item";
    public static final String UNSUPPORTED_FEATURES_SEARCH_PREFIX = "search_prefix";

    public static final String SORT_TRAIN = "sort_train";
    public static final String SORT_TRAIN_SORT_ITEM = "sort_item";
    public static final String SORT_TRAIN_SORT_TYPE = "sort_type";

    public static final String SEARCH_TRAIN = "search_train";
    public static final String SEARCH_TRAIN_DEST_CITY = "dest_city";
    public static final String SEARCH_TRAIN_SRC_CITY = "src_city";
    public static final String SEARCH_TRAIN_START_DATE = "start_date";

    public static void init(Application application) {
        sApplication = application;

        try {
            SlangBuddyOptions options = new SlangBuddyOptions.Builder()
                .setApplication(sApplication)
                .setBuddyId(sBuddyId)
                .setAPIKey(sApiKey)
                .setListener(new BuddyListener())
                .setIntentAction(new IntentActionHandler())
                .setRequestedLocales(SlangLocale.getSupportedLocales())
                .setDefaultLocale(SlangLocale.LOCALE_ENGLISH_IN)
                // change env to production when the buddy is published to production
                .setEnvironment(SlangBuddy.Environment.STAGING)
                .build();
            SlangBuddy.initialize(options);
        } catch (SlangBuddyOptions.InvalidOptionException e) {
            e.printStackTrace();
        } catch (SlangBuddy.InsufficientPrivilegeException e) {
            e.printStackTrace();
        }
    }

    private static class BuddyListener implements SlangBuddy.Listener {
        @Override
        public void onInitialized() {
            Log.d("BuddyListener", "Slang Initialised Successfully");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(sApplication, "Slang Initialised", Toast.LENGTH_LONG).show();
                }
            }, 10);
        }

        @Override
        public void onInitializationFailed(final SlangBuddy.InitializationError e) {
            Log.d("BuddyListener", "Slang failed:" + e.getMessage());

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(sApplication, "Failed to initialise Slang:" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }, 10);
        }

        @Override
        public void onLocaleChanged(final Locale newLocale) {
            Log.d("BuddyListener", "Locale Changed:" + newLocale.getDisplayName());

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(sApplication, "Locale Changed:" + newLocale.getDisplayName(), Toast.LENGTH_LONG).show();
                }
            }, 10);
        }

        @Override
        public void onLocaleChangeFailed(final Locale newLocale, final SlangBuddy.LocaleChangeError e) {
            Log.d("BuddyListener",
                "Locale(" + newLocale.getDisplayName() + ") Change Failed:" + e.getMessage());

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(sApplication,
                        "Locale(" + newLocale.getDisplayName() + ") Change Failed:" + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                }
            }, 10);
        }
    }

    private static class IntentActionHandler implements SlangMultiStepIntentAction {
        @Override
        public Status action(SlangIntent slangIntent, SlangSession slangSession) {
            // Insert the handler for the intents here
            switch (slangIntent.getName()) {
                case UNSUPPORTED_FEATURES:
                    String unsupportedFeaturesUnsupportedactions =
                        slangIntent.getEntity(UNSUPPORTED_FEATURES_UNSUPPORTEDACTIONS) != null &&
                        slangIntent.getEntity(UNSUPPORTED_FEATURES_UNSUPPORTEDACTIONS).isResolved() ?
                            slangIntent.getEntity(UNSUPPORTED_FEATURES_UNSUPPORTEDACTIONS).getValue()
                            : null;

                    String unsupportedFeaturesSearchItem =
                        slangIntent.getEntity(UNSUPPORTED_FEATURES_SEARCH_ITEM) != null &&
                        slangIntent.getEntity(UNSUPPORTED_FEATURES_SEARCH_ITEM).isResolved() ?
                            slangIntent.getEntity(UNSUPPORTED_FEATURES_SEARCH_ITEM).getValue()
                            : null;

                    String unsupportedFeaturesSearchPrefix =
                        slangIntent.getEntity(UNSUPPORTED_FEATURES_SEARCH_PREFIX) != null &&
                        slangIntent.getEntity(UNSUPPORTED_FEATURES_SEARCH_PREFIX).isResolved() ?
                            slangIntent.getEntity(UNSUPPORTED_FEATURES_SEARCH_PREFIX).getValue()
                            : null;

                    return doUnsupportedFeatures(slangSession, unsupportedFeaturesUnsupportedactions, unsupportedFeaturesSearchItem, unsupportedFeaturesSearchPrefix);

                case SORT_TRAIN:
                    String sortTrainSortItem =
                        slangIntent.getEntity(SORT_TRAIN_SORT_ITEM) != null &&
                        slangIntent.getEntity(SORT_TRAIN_SORT_ITEM).isResolved() ?
                            slangIntent.getEntity(SORT_TRAIN_SORT_ITEM).getValue()
                            : null;

                    String sortTrainSortType =
                        slangIntent.getEntity(SORT_TRAIN_SORT_TYPE) != null &&
                        slangIntent.getEntity(SORT_TRAIN_SORT_TYPE).isResolved() ?
                            slangIntent.getEntity(SORT_TRAIN_SORT_TYPE).getValue()
                            : null;

                    return doSortTrain(slangSession, sortTrainSortItem, sortTrainSortType);

                case SEARCH_TRAIN:
                    String searchTrainDestCity =
                        slangIntent.getEntity(SEARCH_TRAIN_DEST_CITY) != null &&
                        slangIntent.getEntity(SEARCH_TRAIN_DEST_CITY).isResolved() ?
                            slangIntent.getEntity(SEARCH_TRAIN_DEST_CITY).getValue()
                            : null;

                    String searchTrainSrcCity =
                        slangIntent.getEntity(SEARCH_TRAIN_SRC_CITY) != null &&
                        slangIntent.getEntity(SEARCH_TRAIN_SRC_CITY).isResolved() ?
                            slangIntent.getEntity(SEARCH_TRAIN_SRC_CITY).getValue()
                            : null;

                    Date searchTrainStartDate =
                        null;
                    try {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                        searchTrainStartDate = slangIntent.getEntity(SEARCH_TRAIN_START_DATE) != null &&
                        slangIntent.getEntity(SEARCH_TRAIN_START_DATE).isResolved() ?
                            df.parse(slangIntent.getEntity(SEARCH_TRAIN_START_DATE).getValue())
                            : null;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    return SearchTrainIntentHandler.action(slangSession, searchTrainDestCity, searchTrainSrcCity, searchTrainStartDate);

                default:
                    Log.w("IntentActionHandler", "Unexpected intent: " + slangIntent.getName());
                    return Status.SUCCESS;
            }
        }

        @Override
        public void onIntentResolutionBegin(SlangIntent intent, SlangSession session) {

        }

        @Override
        public Status onEntityUnresolved(SlangEntity entity, SlangSession session) {
            String entityName = entity.getName();
            String intentName = entity.getIntent().getName();

            switch (intentName) {
                case SEARCH_TRAIN:
                    switch (entityName) {
                        case SEARCH_TRAIN_SRC_CITY:
                            return SearchTrainIntentHandler.onSrcCityUnresolved(session, entity);

                        case SEARCH_TRAIN_DEST_CITY:
                            return SearchTrainIntentHandler.onDstCityUnresolved(session, entity);

                        case SEARCH_TRAIN_START_DATE:
                            return SearchTrainIntentHandler.onStartDateUnresolved(session, entity);
                    }
                    break;
            }

            return Status.SUCCESS;
        }

        @Override
        public Status onEntityResolved(SlangEntity entity, SlangSession session) {
            String entityName = entity.getName();
            String intentName = entity.getIntent().getName();

            switch (intentName) {
                case SEARCH_TRAIN:
                    switch (entityName) {
                        case SEARCH_TRAIN_SRC_CITY:
                            return SearchTrainIntentHandler.onSrcCityResolved(session, entity.getValue());

                        case SEARCH_TRAIN_DEST_CITY:
                            return SearchTrainIntentHandler.onDstCityResolved(session, entity.getValue());

                        case SEARCH_TRAIN_START_DATE:
                            try {
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                                return SearchTrainIntentHandler.onStartDateResolved(
                                    session,
                                    df.parse(entity.getValue())
                                );
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                    }
                    break;
            }

            return Status.SUCCESS;
        }

        @Override
        public void onIntentResolutionEnd(SlangIntent intent, SlangSession session) {

        }
    }
}
