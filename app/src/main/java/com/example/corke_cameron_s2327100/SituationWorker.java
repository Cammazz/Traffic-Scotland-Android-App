package com.example.corke_cameron_s2327100;

import android.util.Log;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SituationWorker extends Worker {
    public SituationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("SituationWorker","Background Refresh Started.");
        boolean unplanned = Feed.downloadFeed(getApplicationContext(),
                "https://datex2.trafficscotland.org/rest/2.3/publications/UnplannedEvents/Content.xml",
                BuildConfig.DATEX_CLIENT_ID,
                BuildConfig.DATEX_CLIENT_KEY,
        "unplannedEvents");
        boolean roadworks = Feed.downloadFeed(getApplicationContext(),
                "https://datex2.trafficscotland.org/rest/2.3/publications/CurrentRoadworks/Content.xml",
                BuildConfig.DATEX_CLIENT_ID,
                BuildConfig.DATEX_CLIENT_KEY,
                "currentRoadworks");
        Log.d("SituationWorker", "Background Refresh Finished - Unplanned: " + unplanned + ", Roadworked: " + roadworks);
        if (unplanned && roadworks) { return Result.success(); }
        else { return Result.retry(); }
}}
