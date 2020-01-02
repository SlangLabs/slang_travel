/*
 * // (c) 2017 Copyright, Slang Labs Private Limited. All rights reserved.
 */

package in.slanglabs.slangtravel;

import android.content.Intent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import in.slanglabs.platform.SlangEntity;
import in.slanglabs.platform.SlangSession;
import in.slanglabs.platform.action.SlangAction;
import in.slanglabs.platform.action.SlangAction.Status;

/**
 *  Handler for the "search_train" intent
 */
public class SearchTrainIntentHandler {
    private static String mSrcCity, mDstCity;
    private static Date mStartDate;

    public static SlangAction.Status action(
        SlangSession slangSession,
        String dstCity,
        String srcCity,
        Date startDate
    ) {
        mSrcCity = srcCity;
        mDstCity = dstCity;
        mStartDate = startDate;

        // If we are already on the details page, just update it. Else launch it
        if (slangSession.getCurrentActivity() instanceof MainActivity) {
            // Launch the results page and pass the data to it
            Intent intent = new Intent(slangSession.getCurrentActivity(), DetailsActivity.class);
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            intent.putExtra("srcCity", srcCity);
            intent.putExtra("dstCity", dstCity);
            intent.putExtra("startDate", df.format(startDate));
            intent.putExtra("newSearch", true);
            intent.putExtra("locale", slangSession.getCurrentLocale().toString());

            slangSession.getCurrentActivity().startActivity(intent);
        } else {
            ((DetailsActivity) slangSession.getCurrentActivity()).updatePage(
                mSrcCity,
                mDstCity,
                mStartDate.toString(),
                false,
                slangSession.getCurrentLocale().getDisplayLanguage()
            );
        }

        return SlangAction.Status.SUCCESS;
    }

    public static SlangAction.Status onSrcCityResolved(SlangSession session, String city) {
        if (session.getCurrentActivity() instanceof MainActivity) {
            MainActivity activity = ((MainActivity) session.getCurrentActivity());

            activity.setSource(city);
        }
        return SlangAction.Status.SUCCESS;
    }

    public static Status onDstCityResolved(SlangSession session, String city) {
        if (session.getCurrentActivity() instanceof MainActivity) {
            MainActivity activity = ((MainActivity) session.getCurrentActivity());

            activity.setDestination(city);
        }

        return Status.SUCCESS;
    }

    public static Status onStartDateResolved(SlangSession session, Date date) {
        if (session.getCurrentActivity() instanceof MainActivity) {
            MainActivity activity = ((MainActivity) session.getCurrentActivity());
            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

            activity.setStartDate(df.format(date));
        }
        return Status.SUCCESS;
    }

    public static Status onSrcCityUnresolved(SlangSession session, SlangEntity city) {
        if (session.getCurrentActivity() instanceof DetailsActivity) {
            city.resolve(mSrcCity);
        }
        return Status.SUCCESS;
    }

    public static Status onDstCityUnresolved(SlangSession session, SlangEntity city) {
        if (session.getCurrentActivity() instanceof DetailsActivity) {
            city.resolve(mDstCity);
        }
        return Status.SUCCESS;
    }

    public static Status onStartDateUnresolved(SlangSession session, SlangEntity date) {
        if (session.getCurrentActivity() instanceof DetailsActivity) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            date.resolve(df.format(mStartDate));
        }
        return Status.SUCCESS;
    }
}
