package com.pinthecloud.athere.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pinthecloud.athere.model.Square;
import com.pinthecloud.athere.model.AhUser;

public class JsonConverter {
	private static JsonElement getJsonElement(JsonObject jo, String key) {
		JsonElement je = jo.get(key);
		if (je == null) je = jo.get(key.toLowerCase());
		return je;
	}
	public static List<Square> convertToSquareList(JsonArray jsonArray) {
		List<Square> list = new ArrayList<Square>();

		for (int i = 0 ; i < jsonArray.size() ; i++) {
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject jo = jsonElement.getAsJsonObject();
			String id = jo.get("id").getAsString();
			String name = jo.get("name").getAsString();
			double latitude = jo.get("latitude").getAsDouble();
			double longitude = jo.get("longitude").getAsDouble();
			String whoMade = jo.get("whoMade").getAsString();
//			int maleNum = jo.get("maleNum").getAsInt();
//			int femaleNum = jo.get("femaleNum").getAsInt();
			int maleNum = getJsonElement(jo, "maleNum").getAsInt();
			int femaleNum = getJsonElement(jo, "femaleNum").getAsInt();
			int distance = jo.get("distance").getAsInt();
			boolean isAdmin = jo.get("isAdmin").getAsBoolean();
			String code = "";
			if(isAdmin){
				code = jo.get("code").getAsString();
			}

			Square square = new Square();
			square.setId(id);
			square.setWhoMade(whoMade);
			square.setName(name);
			square.setLatitude(latitude);
			square.setLongitude(longitude);
			square.setMaleNum(maleNum);
			square.setFemaleNum(femaleNum);
			square.setDistance(distance);
			square.setAdmin(isAdmin);
			square.setCode(code);

			list.add(square);
		}
		return list;
	}

	public static List<AhUser> convertToUserList(JsonElement json){
		List<AhUser> list = new ArrayList<AhUser>();

		JsonObject userListJson = json.getAsJsonObject();
		JsonElement userList = userListJson.get("list");
		JsonArray jsonArray = userList.getAsJsonArray();
		for(int i = 0 ; i < jsonArray.size() ; i++){
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject jo = jsonElement.getAsJsonObject();
			AhUser user = new AhUser();
			user.setId(jo.get("id").getAsString());
			user.setNickName(jo.get("nickName").getAsString());
			user.setProfilePic(jo.get("profilePic").getAsString());
			user.setMobileId(jo.get("mobileId").getAsString());
			user.setRegistrationId(jo.get("registrationId").getAsString());
			user.setMale(jo.get("isMale").getAsBoolean());
			user.setCompanyNum(jo.get("companyNum").getAsInt());
			user.setAge(jo.get("age").getAsInt());
			user.setSquareId(jo.get("squareId").getAsString());
			user.setChupaEnable(jo.get("isChupaEnable").getAsBoolean());

			list.add(user);
		}
		return list;
	}
	
	public static String convertToUserId(JsonElement json) {
		JsonObject userListJson = json.getAsJsonObject();
		JsonElement userList = userListJson.get("userId");
		return userList.getAsString();
	}
}
