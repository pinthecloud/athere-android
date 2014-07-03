package com.pinthecloud.athere;

import java.util.List;

public interface AhListCallback<E> {

	public void onCompleted(List<E> list, int count);

}
