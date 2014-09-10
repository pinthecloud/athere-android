package com.pinthecloud.athere.analysis;

import java.util.HashMap;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pinthecloud.athere.AhApplication;
import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.AhActivity;

public class GAHelper {

	/**
	 * Enum used to identify the tracker that needs to be used for tracking.
	 *
	 * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
	 * storing them all in Application object helps ensure that they are created only once per
	 * application instance.
	 */
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
	}
	private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
	
	private final String GA_PROPERTY_ID = "UA-53944359-1";
	private AhApplication app;


	public GAHelper() {
		super();
		this.app = AhApplication.getInstance();
	}


	private synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {
			GoogleAnalytics analytics = GoogleAnalytics.getInstance(app);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(GA_PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
							: analytics.newTracker(R.xml.ecommerce_tracker);
					mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}


	/*
	 * Send Event only when it is real
	 */
	public void sendEventGA(String category, String action, String label){
		if(!AhGlobalVariable.DEBUG_MODE){
			getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.EventBuilder()
			.setCategory(category)
			.setAction(action)
			.setLabel(label)
			.build());
		}
	}


	/*
	 * Send Timing only when it is real
	 */
	public void sendTimeingGA(String category, String variable, String label){
		if(!AhGlobalVariable.DEBUG_MODE){
			getTracker(TrackerName.APP_TRACKER).send(new HitBuilders.TimingBuilder()
			.setCategory(category)
			.setVariable(variable)
			.setLabel(label)
			.build());
		}
	}


	public void sendScreenGA(String name){
		if(!AhGlobalVariable.DEBUG_MODE){
			Tracker tracker = getTracker(TrackerName.APP_TRACKER);
			tracker.setScreenName(name);
			tracker.send(new HitBuilders.AppViewBuilder().build());
		}
	}


	public void reportActivityStart(AhActivity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			GoogleAnalytics.getInstance(app).reportActivityStart(activity);
		}
	}


	public void reportActivityStop(AhActivity activity){
		if(!AhGlobalVariable.DEBUG_MODE){
			GoogleAnalytics.getInstance(app).reportActivityStop(activity);
		}
	}
}
