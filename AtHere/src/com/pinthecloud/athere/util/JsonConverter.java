package com.pinthecloud.athere.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.pinthecloud.athere.model.AhUser;
import com.pinthecloud.athere.model.Square;

public class JsonConverter {
	private enum TYPE {
		STRING,
		DOUBLE,
		INT,
		BOOLEAN
	}


	private static JsonElement getDefaultValue(TYPE type) {
		String jsonString = "";
		switch(type) {
		case STRING : break;
		case DOUBLE : jsonString = "0.0"; break;
		case INT : jsonString = "0"; break;
		case BOOLEAN : jsonString = "false"; break;
		default : break;
		}
		return new JsonParser().parse(jsonString);
	}


	private static JsonElement getValue(JsonObject jo, String key, TYPE type) {
		JsonElement je = jo.get(key);
		if (je == null) return getDefaultValue(type);
		return je;
	}


	public static List<Square> convertToSquareList(JsonArray jsonArray) {
		List<Square> list = new ArrayList<Square>();
		for (int i = 0 ; i < jsonArray.size() ; i++) {
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject jo = jsonElement.getAsJsonObject();
			String id = getValue(jo, ("id"), TYPE.STRING).getAsString();
			String name = getValue(jo, ("name"), TYPE.STRING).getAsString();
			double latitude = getValue(jo, ("latitude"), TYPE.DOUBLE).getAsDouble();
			double longitude = getValue(jo, ("longitude"), TYPE.DOUBLE).getAsDouble();
			String whoMade = getValue(jo, ("whoMade"), TYPE.STRING).getAsString();
			int maleNum = getValue(jo, "maleNum", TYPE.INT).getAsInt();
			int femaleNum = getValue(jo, "femaleNum", TYPE.INT).getAsInt();
			boolean isAdmin = getValue(jo, ("isAdmin"), TYPE.BOOLEAN).getAsBoolean();
			String code = getValue(jo, ("code"), TYPE.STRING).getAsString();
			int resetTime = getValue(jo, ("resetTime"), TYPE.INT).getAsInt();

			Square square = new Square();
			square.setId(id);
			square.setWhoMade(whoMade);
			square.setName(name);
			square.setLatitude(latitude);
			square.setLongitude(longitude);
			square.setMaleNum(maleNum);
			square.setFemaleNum(femaleNum);
			square.setAdmin(isAdmin);
			square.setCode(code);
			square.setResetTime(resetTime);

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
			AhUser user = JsonConverter.convertToUser(jo);
			list.add(user);
		}
		return list;
	}


	public static AhUser convertToUser(JsonObject jo) {
		AhUser user = new AhUser();
		user.setId(getValue(jo, ("id"), TYPE.STRING).getAsString());
		user.setAhId(getValue(jo, ("ahId"), TYPE.STRING).getAsString());
		user.setMobileId(getValue(jo, ("mobileId"), TYPE.STRING).getAsString());
		user.setMobileType(getValue(jo, ("mobileType"), TYPE.STRING).getAsString());
		user.setRegistrationId(getValue(jo, ("registrationId"), TYPE.STRING).getAsString());
		user.setMale(getValue(jo, ("isMale"), TYPE.BOOLEAN).getAsBoolean());
		user.setBirthYear(getValue(jo, ("birthYear"), TYPE.INT).getAsInt());
		user.setProfilePic(getValue(jo, ("profilePic"), TYPE.STRING).getAsString());
		user.setNickName(getValue(jo, ("nickName"), TYPE.STRING).getAsString());
		user.setChupaEnable(getValue(jo, ("isChupaEnable"), TYPE.BOOLEAN).getAsBoolean());
		return user;
	}


	public static String convertToUserId(JsonElement json) {
		JsonObject userListJson = json.getAsJsonObject();
		JsonElement userList = userListJson.get("userId");
		return userList.getAsString();
	}
}
