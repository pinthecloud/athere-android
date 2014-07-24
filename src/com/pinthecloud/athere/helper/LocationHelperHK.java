package com.pinthecloud.athere.helper;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class LocationHelperHK {
	public static Location getMyLocation(Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		// 정확도
		criteria.setAccuracy(Criteria.NO_REQUIREMENT); 
		// 전원 소비량
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		// 고도, 높이 값을 얻어 올지를 결정
		criteria.setAltitudeRequired(false);
		// provider 기본 정보(방위, 방향)
		criteria.setBearingRequired(false);
		// 속도
		criteria.setSpeedRequired(false);
		// 위치 정보를 얻어 오는데 들어가는 금전적 비용
		criteria.setCostAllowed(true);
		
	    String provider = locationManager.getBestProvider(criteria, false);
	    Location location = locationManager.getLastKnownLocation(provider);
	    return location;
	}
}
