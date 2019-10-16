package com.zhj.my.auto;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zhj.my.R;

/**
 * 实现蓝牙SPP,HID,BLE协议
 * 
 * @author  user
 * 
 */
public class MainActivity extends Activity implements OnClickListener {

	private Button mBtnOpenOrClose;
	private Button mBtnSearch;
	private Button mBtnCloseConn;
	private Button mBtninput;
	private Button mBtnCheckOut;
	private TextView mTvCurrentState;
	private TextView mTvAcceptDatas;
	private ListView mLvSearchedBlueTooths;
	private TextView mTvDataCount;

	private Dialog mDialogSetCheckOut;
	private Dialog mDialogSendData;
	private RelativeLayout relativeLayout;
	private LinearLayout linearLayout;

	private Spinner mSpinner;
	private Switch mSwCheckOpen;
	private RadioGroup mRgtype;
	private RadioButton mCbtype1;
	private RadioButton mCbtype2;
	private RadioButton mCbtype3;
	private RadioButton mCbtype4;
	private EditText mEditLength;
	
	private EditText mRing_length;
	
	private Button mBtnSave;

	private ScrollView scrollView;
	
	
	private long count;
	
	private long hidCount = 0;

	private boolean mScanning;

	private EditText mEtInput;
	private Button mBtnSend;
	private Button mBtnClear;
	private List<String> mAcceptList;
//	private final String[] errodata = new String[] { "01", "02", "03", "04" };
	private final String[] errodata = new String[] { "3", "c"};
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private boolean SwIsChecked = false;
	private int checktype = -1;
	private int checklength = 0;
	private int erro = 0;
	private int ringlength = 1;
	/**
	 * 协议选择下拉框
	 */
	private Spinner mSpProtocol;

	/**
	 * 协议类型
	 */
	private int protocol = Helper.SPP;

	private String address;

	private BluetoothSocket clientSocket;

	private List<BluetoothDevice> mDataList;

	private MyListAdapter mListAdapter;

	private BluetoothAdapter mBluetoothAdapter;

	private AcceptThread acceptThread;

	private ConnectThread connectThread;

	private Set<BluetoothDevice> devices;

	private BluetoothGatt mBluetoothGatt;

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, int rssi,
				byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mDataList.contains(device)) {
						return;
					}
					mDataList.add(device);
					mListAdapter.notifyDataSetChanged();
					Toast.makeText(MainActivity.this,
							getResources().getString(R.string.device_search) + device.getName(), Toast.LENGTH_SHORT)
							.show();
				}
			});
		}

	};

	private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {

		@Override
		public void onConnectionStateChange(final BluetoothGatt gatt,
				int status, int newState) {

			if (newState == BluetoothProfile.STATE_CONNECTED) {

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO 自动生成的方法存根
						mTvCurrentState.setText(MainActivity.this.getResources().getString(R.string.connect)
								+ gatt.getDevice().getAddress());
						Toast.makeText(MainActivity.this,
								MainActivity.this.getResources().getString(R.string.connect) + gatt.getServices().size(),
								Toast.LENGTH_SHORT).show();

					}
				});


			} else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
				// mTvCurrentState.setText("BLE已断开");
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO 自动生成的方法存根
						mTvCurrentState.setText(MainActivity.this.getResources().getString(R.string.disconnect));

					}
				});
			}

		}

		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO 自动生成的方法存根
			// characteristic.getV
		}

		@Override
		public void onCharacteristicChanged(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {

		}

		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, int status) {
			// TODO

		}

		@Override
		public void onServicesDiscovered(BluetoothGatt gatt, int status) {
			// TODO

			if (status == BluetoothGatt.GATT_SUCCESS) {
				for (BluetoothGattService service : gatt.getServices()) {
					UUID uuid = service.getUuid();
					Log.d("UUID1", uuid.toString());
				}

			}

		}

	};

	/**
	 * 主线程handler
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Helper.ACCEPTDATAS:
				byte[] news = (byte[]) msg.obj;
				HashMap<String, Object> map = CRCVerify.bytesToMap(news);
				String newstr = null;
				String order = null;
				// Toast.makeText(MainActivity.this, "收到数据："+new String(news),
				// Toast.LENGTH_SHORT).show();
				if (map != null) {
					newstr = (String) map.get("datas");
					order = (String) map.get("order");
					// Toast.makeText(MainActivity.this,
					// "收到数据集："+map.toString(), Toast.LENGTH_SHORT).show();


					//收到数据
					SpeechUtils.getInstance(MainActivity.this).speakText(newstr);



				} else {
					Toast.makeText(MainActivity.this, getResources().getString(R.string.wrong_len_check),
							Toast.LENGTH_SHORT).show();
					// Toast.makeText(MainActivity.this, "收到错误的数据："+new
					// String(news), Toast.LENGTH_SHORT).show();
					break;
				}
				/**
				 * 接到条码数据
				 */
				if (order.equals("02")) {
					if (SwIsChecked) {
						if (checktype == 0) {
							if (mAcceptList.contains(newstr)) {
								Toast.makeText(MainActivity.this, getResources().getString(R.string.repeat),
										Toast.LENGTH_SHORT).show();
								connectThread.writeData(CRCVerify.ReqToBytes(
										"03", "01", errodata[erro - 1] + ringlength));
							} else /*
									 * if (!newstr.equals("R1") &&
									 * !newstr.equals("R2") &&
									 * !newstr.equals("R3") &&
									 * !newstr.equals("R4"))
									 */{
								mAcceptList.add(newstr.trim());
							}
						} else if (checktype == 1) {
							if (newstr.length() != checklength
							/*
							 * && !newstr.equals("R1") && !newstr.equals("R2")
							 * && !newstr.equals("R3") && !newstr.equals("R4")
							 */) {
								Toast.makeText(MainActivity.this, getResources().getString(R.string.faul_code),
										Toast.LENGTH_SHORT).show();
								connectThread.writeData(CRCVerify.ReqToBytes(
										"03", "01", errodata[erro - 1] + ringlength));
							}
						}

					}

					String oldstr = mTvAcceptDatas.getText().toString();
					String d = new SimpleDateFormat("HH:mm:ss").format(Calendar
							.getInstance().getTime());
					SpannableString spannableString = new SpannableString(
							newstr);
					spannableString.setSpan(
							new ForegroundColorSpan(Color.BLUE), 0,
							spannableString.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					if (protocol == Helper.SPP) {
						mTvAcceptDatas.requestFocus();
						mTvAcceptDatas.setText(oldstr.toString());
						mTvAcceptDatas.append(d);
						mTvAcceptDatas.append("\t\t\t\t\t\t");
						mTvAcceptDatas.append(spannableString);
						mTvAcceptDatas.append("\n");
						int offset = mTvAcceptDatas.getLineCount()
								* mTvAcceptDatas.getLineHeight();
						if (offset > mTvAcceptDatas.getHeight()) {
							mTvAcceptDatas.scrollTo(0,
									offset - mTvAcceptDatas.getHeight());
						}
						mTvAcceptDatas.invalidate();
						count++;
						mTvDataCount.setText("行数:"
								+ count);
						if (count >1000 && count <1003) {
							Toast.makeText(MainActivity.this,
									getResources().getString(R.string.num_top_one),
									Toast.LENGTH_SHORT).show();
//							mTvAcceptDatas.setText("");
						}
						if (count >2000 && count <2003) {
							Toast.makeText(MainActivity.this,
									getResources().getString(R.string.num_top_two),
									Toast.LENGTH_SHORT).show();
//							mTvAcceptDatas.setText("");
						}
						if (count >3000 && count <3003) {
							Toast.makeText(MainActivity.this,
									getResources().getString(R.string.num_top_three),
									Toast.LENGTH_SHORT).show();
//							mTvAcceptDatas.setText("");
						}

					} else {
						mTvAcceptDatas.setText(newstr);
					}

					// Toast.makeText(MainActivity.this, "收到数据：" + newstr,
					// Toast.LENGTH_SHORT).show();

				} else if (order.equals("01")) {
					Toast.makeText(MainActivity.this, getResources().getString(R.string.ring_ver) + newstr,
							Toast.LENGTH_SHORT).show();
				} else if (order.equals("05")) {
					if (newstr.equals("F0")) {

						Toast.makeText(MainActivity.this, getResources().getString(R.string.battle_charge),
								Toast.LENGTH_SHORT).show();
					} else if (newstr.equals("0F")) {
						Toast.makeText(MainActivity.this, getResources().getString(R.string.battle_ok),
								Toast.LENGTH_SHORT).show();
					}
				}

				break;

			case Helper.CONNECTFAIL:
				// Toast.makeText(MainActivity.this, "连接失败！",
				// Toast.LENGTH_SHORT)
				// .show();
				mTvCurrentState.setText(getResources().getString(R.string.con_fail));
				break;
			case Helper.CONNECTSUCCESS:
				// Toast.makeText(MainActivity.this, "连接成功！",
				// Toast.LENGTH_SHORT)
				// .show();
				mTvCurrentState.setText(getResources().getString(R.string.con_suc));

				acceptThread = new AcceptThread(clientSocket, mHandler);
				acceptThread.start();

				break;
			default:
				break;
			}
		};
	};

	/**
	 * 接收广播
	 */
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// 发现设备
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// 从Intent中获取设备对象
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// 将设备名称和地址放入list，以便在ListView中显示
				if (!mDataList.contains(device)) {
					mDataList.add(device);
					mListAdapter.notifyDataSetChanged();
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				// 搜索完毕
				mTvCurrentState.setText(getResources().getString(R.string.searched));

			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				mTvCurrentState.setText(getResources().getString(R.string.searching));
			} else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
				mTvCurrentState.setText(getResources().getString(R.string.status_change));
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				switch (device.getBondState()) {
				case BluetoothDevice.BOND_BONDING:
					mTvCurrentState.setText(getResources().getString(R.string.pairing));
					break;
				case BluetoothDevice.BOND_BONDED:
					mTvCurrentState.setText(getResources().getString(R.string.pairing_com));
					initBonedSevice();
					mListAdapter.notifyDataSetChanged();
					if (null != clientSocket && clientSocket.isConnected()) {
						connectThread.cancel();

					}
					connectThread = new ConnectThread(device);// 连接设备
					connectThread.start();
					break;
				case BluetoothDevice.BOND_NONE:
					mTvCurrentState.setText(getResources().getString(R.string.pairing_cancle));
					initBonedSevice();
					
					mListAdapter.notifyDataSetChanged();
					break;

				default:
					break;
				}
			} else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED
					.equals(action)) {
				int state = (Integer) intent.getExtras().get(
						BluetoothAdapter.EXTRA_CONNECTION_STATE);
				mTvCurrentState.setText(getResources().getString(R.string.link_status_change));
				switch (state) {
				case BluetoothAdapter.STATE_CONNECTED:
					mTvCurrentState.setText(getResources().getString(R.string.connected));
					mListAdapter.notifyDataSetChanged();

					mSpProtocol.setSelection(1);

					break;
				case BluetoothAdapter.STATE_CONNECTING:
					mTvCurrentState.setText(getResources().getString(R.string.connecting));
					// Toast.makeText(MainActivity.this, "connecting",
					// Toast.LENGTH_SHORT).show();
					break;
				case BluetoothAdapter.STATE_DISCONNECTED:
					mTvCurrentState.setText(getResources().getString(R.string.disConnected));
					address = "";
					mListAdapter.notifyDataSetChanged();
					mSpProtocol.setSelection(0);
					break;
				case BluetoothAdapter.STATE_DISCONNECTING:
					mTvCurrentState.setText(getResources().getString(R.string.disConnecting));
					break;

				default:
					break;
				}
			} else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				mTvCurrentState.setText(getResources().getString(R.string.bt_status_ch));
				int state = (Integer) intent.getExtras().get(
						BluetoothAdapter.EXTRA_STATE);
				switch (state) {
				case BluetoothAdapter.STATE_ON:
					mTvCurrentState.setText(getResources().getString(R.string.bt_open));

					mBtnOpenOrClose.setText(getResources().getString(R.string.close));
					mBtnSearch.setEnabled(true);
					break;
				case BluetoothAdapter.STATE_TURNING_ON:
					mTvCurrentState.setText(getResources().getString(R.string.bt_openning));
					break;
				case BluetoothAdapter.STATE_OFF:
					mTvCurrentState.setText(getResources().getString(R.string.bt_close));
					mBtnOpenOrClose.setText(getResources().getString(R.string.open));
					mBtnSearch.setEnabled(false);
					mDataList.clear();
					mListAdapter.notifyDataSetChanged();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
					mTvCurrentState.setText(getResources().getString(R.string.bt_closiing));
					break;

				default:
					break;
				}
			}
		}
	};
	private TextView mTxtVersion;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main2);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// final BluetoothManager bluetoothManager =
		// (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		// mBluetoothAdapter = bluetoothManager.getAdapter();

		if (mBluetoothAdapter == null) {
			
			new AlertDialog.Builder(MainActivity.this)
			.setTitle(getResources().getString(R.string.bt_wrong))
			.setMessage(getResources().getString(R.string.android_no_bt))
			.setCancelable(false)
			.setPositiveButton(
					getResources().getString(R.string.confirm),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(
								DialogInterface dialog,
								int which) {
							System.exit(0);

						}
					}).show();
//			mTvCurrentState.setText("本设备不支持蓝牙");
			return;
		}

		mAcceptList = new ArrayList<String>();
		sp = getSharedPreferences("D400settings", Context.MODE_PRIVATE);
		SwIsChecked = sp.getBoolean("SwIsChecked", false);
		erro = sp.getInt("erro", 0);
		checklength = sp.getInt("checklength", 0);
		checktype = sp.getInt("checktype", 0);
		ringlength = sp.getInt("ringlengh", 1);

		initBonedSevice();

		initView();
		initDialog();
		initData();
		initListData();

		// 注册BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
		filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
		registerReceiver(mReceiver, filter); // 不要忘了之后解除绑定

	}

	/**
	 * 初始化界面以其相关事件
	 */
	private void initView() {
		mBtnOpenOrClose = (Button) findViewById(R.id.btn_open_close);
		mBtnSearch = (Button) findViewById(R.id.btn_search);
		mBtnCloseConn = (Button) findViewById(R.id.btn_close_conn);
		mBtninput = (Button) findViewById(R.id.btn_input);
		
		mTxtVersion = (TextView) findViewById(R.id.txt_version);
        mTxtVersion.setText("ver:" + getVersionName());

		mBtninput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initSendDataDialog();
			}
		});

		mBtnCheckOut = (Button) findViewById(R.id.btn_verify);
		mBtnCheckOut.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initCheckOutDialog();
			}
		});

		mSpProtocol = (Spinner) findViewById(R.id.spinner_proto);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.spinner_list, R.layout.spinner_layout);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpProtocol.setAdapter(adapter);
		final String[] spinnerList = getResources().getStringArray(
				R.array.spinner_list);
		mSpProtocol.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String str = spinnerList[position];
				Toast.makeText(MainActivity.this, getResources().getString(R.string.selected) + str,
						Toast.LENGTH_SHORT).show();
				
				
				if (position == 0) {
					mTvDataCount.setVisibility(View.VISIBLE);
					protocol = Helper.SPP;
					mTvAcceptDatas.setVisibility(View.INVISIBLE);
					mTvAcceptDatas = (TextView) findViewById(R.id.accept_datas);
					mTvAcceptDatas.setVisibility(View.VISIBLE);
					mTvAcceptDatas.setMovementMethod(ScrollingMovementMethod.getInstance());
/*					mTvAcceptDatas.setOnTouchListener(new OnTouchListener() {

						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_MOVE) {
								scrollView.requestDisallowInterceptTouchEvent(true);
							}
							return false;
						}
					});*/
					mTvAcceptDatas
							.setOnLongClickListener(new OnLongClickListener() {

								@Override
								public boolean onLongClick(View v) {
									final AlertDialog dialog = new AlertDialog.Builder(
											MainActivity.this)
											.setNegativeButton(getResources().getString(R.string.cancle), null)
											.setPositiveButton(
													getResources().getString(R.string.confirm),
													new DialogInterface.OnClickListener() {

														@Override
														public void onClick(
																DialogInterface dialog,
																int which) {
															mTvAcceptDatas.scrollTo(0,
																	0);
															mTvAcceptDatas
																	.setText("");
															/*mTvDataCount
																	.setText("行数:0");
															count=0;*/
														}
													}).setMessage(getResources().getString(R.string.data_clean))
											.create();
									dialog.show();
									return true;
								}
							});
					mBtnCheckOut.setEnabled(true);
				} else if (position == 1) {
					mTvDataCount.setVisibility(View.INVISIBLE);
					protocol = Helper.HID;
					mTvAcceptDatas.setVisibility(View.INVISIBLE);
					mTvAcceptDatas = (TextView) findViewById(R.id.accept_dataset);
					mTvAcceptDatas.setVisibility(View.VISIBLE);
					
					((EditText)mTvAcceptDatas).addTextChangedListener(new TextWatcher() {
						
						@Override
						public void onTextChanged(CharSequence s, int start, int before, int count) {
//							Toast.makeText(MainActivity.this, s.toString()+" "+start+" "+count, Toast.LENGTH_SHORT).show();
//							if(count==1){
//								hidCount += count;
//								mTvDataCount.setText("HID数目："+(hidCount/12));
//								if(mTvAcceptDatas.length()>(5*12)){
//									mTvAcceptDatas.setText("");
//								}
//							}
						}
						
						@Override
						public void beforeTextChanged(CharSequence s, int start, int count,
								int after) {
							// TODO 自动生成的方法存根
							
						}
						
						@Override
						public void afterTextChanged(Editable s) {
							// TODO 自动生成的方法存根
							
						}
					});
					mTvAcceptDatas.requestFocus();
					mBtnCheckOut.setEnabled(false);
				} else {

					if (!getPackageManager().hasSystemFeature(
							PackageManager.FEATURE_BLUETOOTH_LE)) {
						Toast.makeText(MainActivity.this, getResources().getString(R.string.BLE_TO_SPP),
								Toast.LENGTH_SHORT).show();
						mSpProtocol.setSelection(0);
						protocol = Helper.SPP;
					} else {
						// TODO
						protocol = Helper.LE;
						mTvAcceptDatas.setVisibility(View.INVISIBLE);
						mTvAcceptDatas = (TextView) findViewById(R.id.accept_datas);
						mTvAcceptDatas.setVisibility(View.VISIBLE);
						mBtnCheckOut.setEnabled(true);
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		mTvCurrentState = (TextView) findViewById(R.id.current_state);
		mTvDataCount = (TextView) findViewById(R.id.txt_datacount);
		mTvAcceptDatas = (TextView) findViewById(R.id.accept_datas);
		mTvAcceptDatas.setMovementMethod(ScrollingMovementMethod.getInstance());

		scrollView = (ScrollView) findViewById(R.id.scrollview);

		mLvSearchedBlueTooths = (ListView) findViewById(R.id.lv_searched_bluetooths);
		// 未打开蓝牙时不可见
		mBtnSearch.setEnabled(false);

		// 开关按钮就点击事件
		mBtnOpenOrClose.setOnClickListener(this);
		// 搜索按钮点击事件
		mBtnSearch.setOnClickListener(this);
		// 断开按钮点击事件
		mBtnCloseConn.setOnClickListener(this);
		
		mLvSearchedBlueTooths.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					scrollView.requestDisallowInterceptTouchEvent(true);
				}
				return false;
			}
		});

		// 列表项点击事件
		mLvSearchedBlueTooths.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				if (clientSocket != null && clientSocket.isConnected()) {
					Toast.makeText(MainActivity.this, getResources().getString(R.string.close_con),
							Toast.LENGTH_SHORT).show();
					return;
				}
				Log.d("Message", "click");
				// Toast.makeText(
				// MainActivity.this,
				// ""
				// + (mDataList.get(position).getBondState() ==
				// BluetoothDevice.BOND_BONDED),
				// Toast.LENGTH_SHORT).show();

				if (protocol == Helper.SPP) {
					if(mBluetoothAdapter.isDiscovering()){
						mBluetoothAdapter.cancelDiscovery();
					}
					
					BluetoothDevice btDev = mBluetoothAdapter
							.getRemoteDevice(mDataList.get(position)
									.getAddress());
					try {
						if (btDev.getBondState() == BluetoothDevice.BOND_NONE) {
							
							Method createBondMethod = BluetoothDevice.class
									.getMethod("createBond");
							Boolean returnValue = (Boolean) createBondMethod
									.invoke(btDev);

						} else if (btDev.getBondState() == BluetoothDevice.BOND_BONDED) {
							mTvCurrentState.setText(getResources().getString(R.string.con_pre));

							connectThread = new ConnectThread(btDev);// 连接
							connectThread.start();
							address = mDataList.get(position).getAddress();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (protocol == Helper.LE) {

					// TODO
					Log.d("UUID", "BLE..");
					mBluetoothGatt = mDataList.get(position).connectGatt(
							MainActivity.this, false, mBluetoothGattCallback);
					// mBluetoothGatt.connect();
					mTvCurrentState.setText(getResources().getString(R.string.con_BLE));

				}

			}
		});
	}

	/**
	 * 初始化列表数据
	 */
	private void initListData() {
		mDataList = new ArrayList<BluetoothDevice>();

		mListAdapter = new MyListAdapter(this);
		mListAdapter.setDataList(mDataList);

		mLvSearchedBlueTooths.setAdapter(mListAdapter);
	}
	
    public String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

	private void initData() {
		if (!mBluetoothAdapter.isEnabled()) {
			mBtnOpenOrClose.setText(getResources().getString(R.string.open));
			mTvCurrentState.setText(getResources().getString(R.string.dev_closed));
		} else {
			mBtnOpenOrClose.setText(getResources().getString(R.string.close));
			mTvCurrentState.setText(getResources().getString(R.string.dev_opened));
			mBtnSearch.setEnabled(true);
			if (protocol == Helper.SPP) {

				mBluetoothAdapter.startDiscovery();
			} else if (protocol == Helper.LE) {
				// TODO
			}
		}
	}

	private void initBonedSevice() {
		devices = mBluetoothAdapter.getBondedDevices();
	}

	private void initDialog() {
		relativeLayout = (RelativeLayout) LayoutInflater.from(this).inflate(
				R.layout.input_dialog_layout, null);
		mDialogSendData = new Dialog(this);
		mDialogSendData.setTitle(getResources().getString(R.string.send_data));
		mEtInput = (EditText) relativeLayout.findViewById(R.id.et_input);
		mBtnSend = (Button) relativeLayout.findViewById(R.id.btn_send);
		mBtnClear = (Button) relativeLayout.findViewById(R.id.btn_clear);
		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//
				String str = mEtInput.getText().toString();
				if (str != null && !str.equals("") && connectThread != null) {

					connectThread.writeData(CRCVerify.ReqToBytes(str, "00"));
					mEtInput.setText("");
				} else {
					Toast.makeText(MainActivity.this, getResources().getString(R.string.con_ring),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mBtnClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mEtInput.setText("");
			}
		});

		linearLayout = (LinearLayout) LayoutInflater.from(this).inflate(
				R.layout.checkout_dialog_layout, null);
		mEditLength = (EditText) linearLayout.findViewById(R.id.et_length);
		mRgtype = (RadioGroup) linearLayout.findViewById(R.id.rg);

		mCbtype1 = (RadioButton) linearLayout.findViewById(R.id.cb_type1);
		mCbtype2 = (RadioButton) linearLayout.findViewById(R.id.cb_type2);
		mRing_length = (EditText) linearLayout.findViewById(R.id.ring_length);
//		mCbtype3 = (RadioButton) linearLayout.findViewById(R.id.cb_type3);
//		mCbtype4 = (RadioButton) linearLayout.findViewById(R.id.cb_type4);
		
		
		mBtnSave = (Button) linearLayout.findViewById(R.id.btn_save);
		/**
		 * 保存按钮点击
		 */
		mBtnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				editor = sp.edit();
				editor.putBoolean("SwIsChecked", mSwCheckOpen.isChecked());
				int id = mRgtype.getCheckedRadioButtonId();
				switch (id) {
				case R.id.cb_type1:
					editor.putInt("erro", 1);
					erro = 1;
					break;
				case R.id.cb_type2:
					editor.putInt("erro", 2);
					erro = 2;
					break;
//				case R.id.cb_type3:
//					editor.putInt("erro", 3);
//					erro = 3;
//					break;
//				case R.id.cb_type4:
//					editor.putInt("erro", 4);
//					erro = 4;
//					break;

				default:
					break;
				}
				editor.putInt("checktype", mSpinner.getSelectedItemPosition());
				if (mSpinner.getSelectedItemPosition() == 1) {
					String length = mEditLength.getText().toString();
					if (mEditLength.getText() == null
							|| mEditLength.getText().equals("")
							|| mEditLength.getText().length() == 0) {
						length = "0";
					}
					editor.putInt("checklength", Integer.valueOf(length));

					checklength = Integer.valueOf(length);
				}
				if(mRing_length.getText().toString() != null && !"".equals(mRing_length.getText().toString())) {
					ringlength = Integer.parseInt(mRing_length.getText().toString());
					if(ringlength < 1) {
						ringlength = 1;
					} else if(ringlength > 15) {
						ringlength = 15;
					} 
				}
				editor.putInt("ringlengh", ringlength);
				editor.commit();

				SwIsChecked = mSwCheckOpen.isChecked();
				checktype = mSpinner.getSelectedItemPosition();

				mDialogSetCheckOut.cancel();

			}
		});

		mDialogSetCheckOut = new Dialog(this);
		mDialogSetCheckOut.setTitle(getResources().getString(R.string.data_check));
		mSwCheckOpen = (Switch) linearLayout.findViewById(R.id.sw_opencheck);

		mSpinner = (Spinner) linearLayout.findViewById(R.id.sp_check_type);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.spinner_check_list, R.layout.spinner_layout);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSpinner.setAdapter(adapter);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 1) {
					mEditLength.setVisibility(View.VISIBLE);
				} else {
					mEditLength.setVisibility(View.INVISIBLE);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});

	}
	/**
	 * 初始化发送对话框
	 */
	private void initSendDataDialog() {

		mDialogSendData.show();
		mDialogSendData.getWindow().setContentView(relativeLayout);

	}

	/**
	 * 初始化校验对话框
	 */
	private void initCheckOutDialog() {

		mSwCheckOpen.setChecked(sp.getBoolean("SwIsChecked", false));

		mSpinner.setSelection(sp.getInt("checktype", 0));
		if (mSpinner.getSelectedItemPosition() == 1) {
			mEditLength.setText(String.valueOf(sp.getInt("checklength", 0)));
		}

		int type = sp.getInt("erro", 1);
		switch (type) {
		case 1:
			mCbtype1.setChecked(true);
			break;
		case 2:
			mCbtype2.setChecked(true);
			break;
		case 3:
			mCbtype3.setChecked(true);
			break;
		case 4:
			mCbtype4.setChecked(true);
			break;

		default:
			break;
		}

		mDialogSetCheckOut.show();
		mDialogSetCheckOut.getWindow().setContentView(linearLayout);

	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_open_close:
			// 打开时点击关闭，关闭时点击打开
			if (mBluetoothAdapter.isEnabled()) {
				// 关闭操作
				/*
				 * 如果线程已经存在且正在阻塞，取消线程
				 */
				if (clientSocket != null && clientSocket.isConnected()) {

					try {
						clientSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// TODO

				mBluetoothAdapter.disable();

			} else if (!mBluetoothAdapter.isEnabled()) {

				// 打开操作
				mBluetoothAdapter.enable();

			}
			break;
		case R.id.btn_search:

			if (protocol == Helper.SPP) {
				// 正在搜索则取消，以便重新开始搜索
				if (mBluetoothAdapter.isDiscovering()) {
					mBluetoothAdapter.cancelDiscovery();
				}
				
				mDataList.clear();
				mListAdapter.notifyDataSetChanged();
				
				mBluetoothAdapter.startDiscovery();
			} else if (protocol == Helper.LE) {

				if (mBluetoothAdapter.isDiscovering()) {
					mBluetoothAdapter.cancelDiscovery();
				}

				// TODO
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mScanning = false;
						mBluetoothAdapter.stopLeScan(mLeScanCallback);
						// invalidateOptionsMenu();
					}
				}, 10000);

				mScanning = true;

				mBluetoothAdapter.startLeScan(mLeScanCallback);

			}

			break;

		case R.id.btn_close_conn:
			if (protocol == Helper.SPP) {

				if (clientSocket != null && clientSocket.isConnected()) {
					try {
						acceptThread.cancel();
						clientSocket.close();
						mTvCurrentState.setText(getResources().getString(R.string.close_con));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} else if (protocol == Helper.LE) {
				// TODO
				mBluetoothGatt.disconnect();

			}
			address = "";
			break;
		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		// if (null != clientSocket && clientSocket.isConnected()) {
		//
		// connectThread.cancel();
		// }
		if (null != acceptThread && acceptThread.isAlive()) {
			acceptThread.cancel();
		}

		// TODO
	}

	/**
	 * 建立连接线程类
	 * 
	 * @author user
	 * 
	 */
	class ConnectThread extends Thread {
		BluetoothDevice device;
		OutputStream os = null;

		public ConnectThread(BluetoothDevice device) {
			this.device = device;

			try {
				// 获取 BluetoothSocket
				clientSocket = device
						.createRfcommSocketToServiceRecord(Helper.uuid);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		@Override
		public void run() {
			// 先取消继续搜索，以便建立链接
			if (mBluetoothAdapter.isDiscovering()) {
				mBluetoothAdapter.cancelDiscovery();
			}

			try {
				clientSocket.connect();
				mHandler.sendEmptyMessage(Helper.CONNECTSUCCESS);

			} catch (IOException e) {

				mHandler.sendEmptyMessage(Helper.CONNECTFAIL);
				try {
					clientSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}

			/*
			 * 连接上关闭设备通道，保持单链接
			 * 
			 * if (clientSocket != null) { try { clientSocket.close(); } catch
			 * (IOException e) { e.printStackTrace(); } }
			 */

		}

		public void writeData(byte[] bytes) {
			try {
				os = clientSocket.getOutputStream();
				os.write(bytes);
				os.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		/**
		 * 关闭连接
		 */
		public void cancel() {
			try {

				clientSocket.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * 已链接设备列表的Adapter
	 * 
	 * @author user
	 * 
	 */
	class MyListAdapter extends BaseAdapter {
		private List<BluetoothDevice> dataList;
		private Context context;

		public void setDataList(List<BluetoothDevice> dataList) {
			this.dataList = dataList;
		}

		public MyListAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return null != dataList ? dataList.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			final int p = position;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_layout, null);
				holder = new Holder();
				holder.mTextViewName = (TextView) convertView
						.findViewById(R.id.tv_name);
				holder.mTextViewAddress = (TextView) convertView
						.findViewById(R.id.tv_address);

				holder.mImageView = (ImageView) convertView
						.findViewById(R.id.img_doit);
				holder.mImageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (dataList.get(p).getBondState() == BluetoothDevice.BOND_BONDED) {
							try {
								// 解绑
								Method removeBondMethod = BluetoothDevice.class
										.getMethod("removeBond");
								removeBondMethod.invoke(dataList.get(p));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (NoSuchMethodException e) {
								e.printStackTrace();
							}
						}
					}
				});

				convertView.setTag(holder);
			} else {

				holder = (Holder) convertView.getTag();
			}
			if (dataList.get(position).getAddress().equals(address)) {
				holder.mImageView.setImageResource(R.drawable.connecting);
			} else if (dataList.get(position).getBondState() == BluetoothDevice.BOND_BONDED) {
				holder.mImageView.setImageResource(R.drawable.boned);
			} else {
				holder.mImageView.setImageResource(R.drawable.waiting);
			}
			holder.mTextViewName.setText(dataList.get(position).getName());
			holder.mTextViewAddress
					.setText(dataList.get(position).getAddress());

			return convertView;
		}

	}

	/**
	 * MyListAdapter的Holder
	 * 
	 * @author user
	 * 
	 */
	class Holder {
		TextView mTextViewName;
		TextView mTextViewAddress;
		ImageView mImageView;
	}

}
