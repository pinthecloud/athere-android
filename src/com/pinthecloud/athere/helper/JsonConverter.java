package com.pinthecloud.athere.helper;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pinthecloud.athere.model.Square;

public class JsonConverter {
	
	public static List<Square> convertToSquareList(JsonArray jsonArray) {
		List<Square> list = new ArrayList<Square>();
		
		try{
			for (int i = 0 ; i < jsonArray.size() ; i++) {
				JsonElement jsonElement = jsonArray.get(i);
				JsonObject jo = jsonElement.getAsJsonObject();
				String id = jo.get("id").getAsString();
				String whoMade = jo.get("whoMade").getAsString();
				String name = jo.get("name").getAsString();
				double latitude = jo.get("latitude").getAsDouble();
				double longitude = jo.get("longitude").getAsDouble();
				int participants = jo.get("participants").getAsInt();
				int distance = jo.get("distance").getAsInt();
			
				Square square = new Square();
				square.setId(id);
				square.setWhoMade(whoMade);
				square.setName(name);
				square.setLatitude(latitude);
				square.setLongitude(longitude);
				square.setParticipants(participants);
				square.setDistance(distance);
				list.add(square);
			}
		} catch (Exception ex){
			return null;
		}
		
		return list;
	}
}
