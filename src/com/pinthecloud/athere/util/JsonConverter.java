package com.pinthecloud.athere.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pinthecloud.athere.interfaces.AhException;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.User;

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
		} catch (Exception e){
			throw new AhException(e,"convertToSquareList");
		}
		return list;
	}

	public static List<User> convertToUserList(JsonElement json){
		List<User> list = new ArrayList<User>();

		JsonObject userListJson = json.getAsJsonObject();
		JsonElement userList = userListJson.get("userList");
		JsonArray jsonArray = userList.getAsJsonArray();
		for(int i = 0 ; i < jsonArray.size() ; i++){
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject jo = jsonElement.getAsJsonObject();
			User user = new User();
			user.setId(jo.get("id").getAsString());
			user.setNickName(jo.get("nickname").getAsString());
			user.setProfilePic(jo.get("profilepic").getAsString());
			user.setMobileId(jo.get("mobileid").getAsString());
			user.setRegistrationId(jo.get("registrationid").getAsString());
			user.setMale(jo.get("ismale").getAsBoolean());
			user.setCompanyNum(jo.get("companynum").getAsInt());
			user.setAge(jo.get("age").getAsInt());
			user.setSquareId(jo.get("squareid").getAsString());

			list.add(user);
		}
		return list;
	}
}
