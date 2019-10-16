package com.zhj.my.auto;

import java.io.IOException;
import java.io.InputStream;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

/**
 * 此线程用于接收蓝牙通道传输来的数据<BR>
 * 此前，需要一个已经建立通道的客户端socket,以及用于通知改变UI的主线程handler
 * @author user
 *
 */
public class AcceptThread extends Thread {
	private Handler mHandler;
	public static boolean flag = true;
	private BluetoothSocket socket = null;

	/**
	 * 
	 * @param socket 客户端socket
	 * @param handler UI handler
	 */
	public AcceptThread(BluetoothSocket socket, Handler handler) {
		this.socket = socket;
		this.mHandler = handler;

	}

	@Override
	public void run() {

		
		// flag一般为true，保持接收数据
		while (flag) {

			if (socket != null) {
				// 接收数据
				InputStream is = null;
				try {
					
					is = socket.getInputStream();

					byte[] buffer = new byte[1024];
					int num;
					while ((num = is.read(buffer)) != -1) {
						Message msg = new Message();
						msg.what=Helper.ACCEPTDATAS;
						byte[] bytes = new byte[num];
						for (int i = 0; i < num; i++) {
							bytes[i] = buffer[i];
						}
						msg.obj =bytes;
						
						mHandler.sendMessage(msg);
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
			
		}
		
		
	}

	/**
	 * 结束此次连接
	 */
	public void cancel() {
		try {
			if (socket!=null) {
				socket.close();
			}
		} catch (IOException e) {
		}
	}
}
