package com.pinthecloud.athere.fragment;


/**
 * 
 * @author hongkunyoo
 * Test Fragment for hongkunyoo's own experiments
 */
public class HongkunTestFragment extends AhFragment {

//	private Button[] btnArr;
//	private TextView messageText;
//	private int count = 0;
//	private int[] countArr;
//	private StringBuilder squareId = new StringBuilder();
//	private String __id = "";
//	private ImageView img;
//	private Button myBtn;
//	private MobileServiceClient mClient;
//	ListView listView; 
//	private BlobStorageHelper blobStorageHelper;
//	public static final String SENDER_ID = "838051405989";
//
//	
//	ArrayAdapter<String> adapter;
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		mClient = app.getmClient();
//	}
//
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.fragment_hongkun_test, container, false);
//
//		btnArr = new Button[6];
//		//btnArr[0] = (Button)view.findViewById(R.id.drawer_user_chupa_btn);
//		btnArr[1] = (Button)view.findViewById(R.id.button2);
//		btnArr[2] = (Button)view.findViewById(R.id.button3);
//		btnArr[3] = (Button)view.findViewById(R.id.button4);
//		btnArr[4] = (Button)view.findViewById(R.id.button5);
//		btnArr[5] = (Button)view.findViewById(R.id.button6);
//		messageText = (TextView)view.findViewById(R.id.message_text);
//		img = (ImageView)view.findViewById(R.id.hongkun_id_image_view);
//
//		countArr = new int[6];
//		btnArr = new Button[6];
//		btnArr[0] = (Button)view.findViewById(R.id.drawer_user_chupa_btn);
//		btnArr[1] = (Button)view.findViewById(R.id.button2);
//		btnArr[2] = (Button)view.findViewById(R.id.button3);
//		btnArr[3] = (Button)view.findViewById(R.id.button4);
//		btnArr[4] = (Button)view.findViewById(R.id.button5);
//		btnArr[5] = (Button)view.findViewById(R.id.button6);
//		messageText = (TextView)view.findViewById(R.id.message_text);
//		listView = (ListView)view.findViewById(R.id.hongkun_list_view);
//		img = (ImageView)view.findViewById(R.id.hongkun_id_image_view);
//		blobStorageHelper = new BlobStorageHelper();
//		
//		adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
//		listView.setAdapter(adapter);
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				
//			}
//		});
//		for(int i = 0 ; i < 6 ; i++){
//			btnArr[i].setOnClickListener(new View.OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					Button b = (Button)v;
//					if (b.getId() == btnArr[0].getId()) {
//						
//						AhMessage message = AhMessage.buildMessage(AhMessage.TYPE.TALK);
//						Log(_thisFragment, message);
//						messageDBHelper.addMessage(message);
//						message = AhMessage.buildMessage(AhMessage.TYPE.TALK);
//						Log(_thisFragment, message);
//						messageDBHelper.addMessage(message);
//					} else if (b.getId() == btnArr[1].getId()) {
//						AhMessage message = messageDBHelper.getLastMessage(AhMessage.TYPE.TALK);
//						Log(_thisFragment, message);
//					} else if (b.getId() == btnArr[2].getId()) {
//						String filename = "gogo.png";
//						File filePath = context.getFileStreamPath(filename);
//						Log(_thisFragment, context.getFilesDir()+"/"+filename, filePath, context.getFilesDir()+"/"+filename.equals(filePath));
//
//					} else if (b.getId() == btnArr[3].getId()) {
//						b.setText("deleteAll");
//						messageDBHelper.deleteAllMessages();
//					} else if (b.getId() == btnArr[4].getId()) {
//
//					} else if (b.getId() == btnArr[5].getId()) {
//
//					}
//					messageText.setText(b.getText());
//				}
//			});
//		}
//		
//		return view;
//	}
//
//
//	@Override
//	public void handleException(AhException ex) {
//		Log(_thisFragment, "in handle Hongkunyoo" + ex);
//	}
}
