package com.pinthecloud.athere.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.fragment.AhFragment;

/*
 * [ Usage ]
	AsyncChainer.asyncChain(_thisFragment, new Chainable() {

		@Override
		public void doNext(final AhFragment frag) {
			messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {

				@Override
				public void onCompleted(AhMessage entity) {
					Log(_thisFragment, "on Complete in First" + __id);
					__id = __id + " after 1";
				}
			});
		}
	}, new Chainable() {

		@Override
		public void doNext(final AhFragment frag) {
			messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {

				@Override
				public void onCompleted(AhMessage entity) {
					Log(_thisFragment, "on Complete in Second : " + __id);
					__id = __id + " after 2";
					try{
						Thread.sleep(100);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}, new Chainable() {

		@Override
		public void doNext(final AhFragment frag) {
			messageHelper.sendMessageAsync(frag, message, new AhEntityCallback<AhMessage>() {

				@Override
				public void onCompleted(AhMessage entity) {
					Log(_thisFragment, "on Complete in Third : " + __id);
				}
			});
		}
	});
 */


public class AsyncChainer {
	private static final int NUM_OF_QUEUE = 10;
	private static Map<String, Queue<Chainable>> mapQueue;
	static {
		mapQueue = new HashMap<String, Queue<Chainable>>();
	}

	
	public static void asyncChain(AhFragment frag, Chainable...chains) {
		Class<?> clazz = null;
		if (frag == null) {
			clazz = AhFragment.class;
		} else {
			clazz = frag.getClass();
		}

		Queue<Chainable> queue = mapQueue.get(clazz.getName());
		if (queue == null) {
			mapQueue.put(clazz.getName(), new ArrayBlockingQueue<Chainable>(NUM_OF_QUEUE));
			queue = mapQueue.get(clazz.getName());
		}
		for(Chainable c : chains) {
			queue.add(c);
		}
		AsyncChainer.notifyNext(frag);
	}

	
	public static void notifyNext(AhFragment frag) {
		Class<?> clazz = null;
		if (frag == null) {
			clazz = AhFragment.class;
		} else {
			clazz = frag.getClass();
		}
		Queue<Chainable> queue = mapQueue.get(clazz.getName());
		if (queue == null) {
			//			Log.e("ERROR", "No such Chainable : " + clazz.getName());
			return;
		}
		if (!queue.isEmpty()) {
			Chainable c = queue.poll();
			if (c == null) throw new AhException("chain == null");
			c.doNext(frag);
		}
	}

	
	public static interface Chainable {
		public void doNext(AhFragment frag);
	}
}
