package com.zhj.my.auto;

import java.util.UUID;

public class Helper {
	
	/**
	 * SPP协议的UUID
	 */
	public static final UUID uuid = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	/**
	 * SPP协议
	 */
	public static final int SPP = 0;
	/**
	 * HID协议
	 */
	public static final int HID = 1;
	/**
	 * BLE协议
	 */
	public static final int LE = 2;
	
	/**
	 * 收到蓝牙数据
	 */
	public static final int  ACCEPTDATAS = 0X01;
	
	/**
	 * 连接失败
	 */
	public static final int  CONNECTFAIL = 0X66;
	/**
	 * 连接成功
	 */
	public static final int  CONNECTSUCCESS = 0x77;
}
