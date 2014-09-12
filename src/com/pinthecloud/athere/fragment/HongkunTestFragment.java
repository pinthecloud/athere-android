package com.pinthecloud.athere.fragment;

import java.io.File;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.exception.AhException;
import com.pinthecloud.athere.helper.BlobStorageHelper;
import com.pinthecloud.athere.interfaces.AhEntityCallback;
import com.pinthecloud.athere.model.AhMessage;
import com.pinthecloud.athere.model.Square;


/**
 * 
 * @author hongkunyoo
 * Test Fragment for hongkunyoo's own experiments
 */
public class HongkunTestFragment extends AhFragment {

	Button[] btnArr;
	TextView messageText;
	int count = 0;
	int[] countArr;
	StringBuilder squareId = new StringBuilder();
	String __id = "";
	ImageView img;
	Button myBtn;
	MobileServiceClient mClient;
	ListView listView; 
	BlobStorageHelper blobStorageHelper;
	static final String SENDER_ID = "838051405989";


	ArrayAdapter<String> adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mClient = app.getmClient();
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_hongkun_test, container, false);

		btnArr = new Button[6];
		//btnArr[0] = (Button)view.findViewById(R.id.drawer_user_chupa_btn);
		btnArr[1] = (Button)view.findViewById(R.id.button2);
		btnArr[2] = (Button)view.findViewById(R.id.button3);
		btnArr[3] = (Button)view.findViewById(R.id.button4);
		btnArr[4] = (Button)view.findViewById(R.id.button5);
		btnArr[5] = (Button)view.findViewById(R.id.button6);
		messageText = (TextView)view.findViewById(R.id.message_text);
		img = (ImageView)view.findViewById(R.id.hongkun_id_image_view);

		countArr = new int[6];
		btnArr = new Button[6];
		btnArr[0] = (Button)view.findViewById(R.id.drawer_user_chupa_btn);
		btnArr[1] = (Button)view.findViewById(R.id.button2);
		btnArr[2] = (Button)view.findViewById(R.id.button3);
		btnArr[3] = (Button)view.findViewById(R.id.button4);
		btnArr[4] = (Button)view.findViewById(R.id.button5);
		btnArr[5] = (Button)view.findViewById(R.id.button6);
		messageText = (TextView)view.findViewById(R.id.message_text);
		listView = (ListView)view.findViewById(R.id.hongkun_list_view);
		img = (ImageView)view.findViewById(R.id.hongkun_id_image_view);
		blobStorageHelper = new BlobStorageHelper();

		adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		for(int i = 0 ; i < 6 ; i++){
			btnArr[i].setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Button b = (Button)v;
					if (b.getId() == btnArr[0].getId()) {
						Square square = new Square();
						square.setName("건대 왕대박");
						square.setWhoMade("wangdaibak");
						square.setDistance(0);
						square.setCode("0307");
						square.setAdmin(true);
						square.setMaleNum(0);
						square.setFemaleNum(0);
						square.setLatitude(0);
						square.setLongitude(0);
						squareHelper.createSquareAsync(thisFragment, square, new AhEntityCallback<Square>(){

							@Override
							public void onCompleted(Square entity) {
								// TODO Auto-generated method stub
								Log(thisFragment, "OK");
							}

						});

					} else if (b.getId() == btnArr[1].getId()) {
						AhMessage message = messageDBHelper.getLastMessage(AhMessage.TYPE.TALK);
						Log(thisFragment, message);
					} else if (b.getId() == btnArr[2].getId()) {
						String filename = "gogo.png";
						File filePath = context.getFileStreamPath(filename);
						Log(thisFragment, context.getFilesDir()+"/"+filename, filePath, context.getFilesDir()+"/"+filename.equals(filePath));

					} else if (b.getId() == btnArr[3].getId()) {
						b.setText("deleteAll");
						messageDBHelper.deleteAllMessages();
					} else if (b.getId() == btnArr[4].getId()) {

					} else if (b.getId() == btnArr[5].getId()) {

					}
					messageText.setText(b.getText());
				}
			});
		}

		return view;
	}


	@Override
	public void handleException(AhException ex) {
		Log(thisFragment, "in handle Hongkunyoo" + ex);
	}
}
