package com.zhj.my.auto;

import java.util.HashMap;

/**
 * 数据校验工具类
 * 
 * @author user
 * 
 */
public class CRCVerify {

	/**
	 * 
	 * @param reqOrder
	 *            命令字
	 * @param reqDatas
	 *            数据域
	 * @return 根据命令字和数据域生成的Byte数组
	 */
	public static byte[] ReqToBytes(String reqOrder, String reqDatas) {
		//需要数据域，如SN写入
		if(reqOrder.equals("0E")){

			// 长度字、命令字、校验字 各占一位（byte），以及数据域
			String reqLength = Integer.toHexString(3 + reqDatas.length());
			// 缺高位补0，保证格式正确
			if (reqLength.length() == 1) {
				reqLength = "0" + reqLength;
			}

			byte byteVerify = 0;

			byte byteLength = (byte) Integer.parseInt(reqLength, 16);
			byte byteOrder = (byte) Integer.parseInt(reqOrder, 16);

			byteVerify = (byte) (byteLength + byteOrder);

			// 有时不需要数据域
			byte[] byteDatas = null;
			if (reqDatas != null && !reqDatas.equals("")) {
				byteDatas = reqDatas.getBytes();


				for (int i = 0; i < byteDatas.length; i++) {
					byte b = byteDatas[i];

					byteVerify = (byte) (byteVerify + b);

				}

			}
			byte head = (byte) Integer.parseInt("55", 16);
			byte end = (byte) Integer.parseInt("AA", 16);

			byte[] bytes = new  byte[5+byteDatas.length];
			bytes[0] = head;
			bytes[1] = byteLength;
			bytes[2] = byteOrder;
			for (int i = 0; i < byteDatas.length; i++) {
				byte b = byteDatas[i];
				bytes[3+i] = b;
			}

			bytes[bytes.length-2] = byteVerify;
			bytes[bytes.length-1] = end;

			return bytes;

		}else{
			//不需要数据时，代码中给"00"，算一字节byte = 0
			// 长度字、命令字、校验字 各占一位（byte），以及数据域字节数（2位作为1字节）
			String reqLength = Integer.toHexString(3 + reqDatas.length() / 2);
			// 缺高位补0，保证格式正确
			if (reqLength.length() == 1) {
				reqLength = "0" + reqLength;
			}

			byte byteVerify = 0;

			byte byteLength = (byte) Integer.parseInt(reqLength, 16);
			byte byteOrder = (byte) Integer.parseInt(reqOrder, 16);

			byteVerify = (byte) (byteLength + byteOrder);

			// 有时不需要数据域
			byte byteData = 0;
			if (reqDatas != null && !reqDatas.equals("")) {
				byteData = (byte) Integer.parseInt(reqDatas, 16);
				byteVerify = (byte) (byteVerify + byteData);
			}
			byte head = (byte) Integer.parseInt("55", 16);
			byte end = (byte) Integer.parseInt("AA", 16);
			byte[] bytes = new byte[] { head, byteLength, byteOrder, byteData,
					byteVerify, end };

			return bytes;

		}
}

	/**
	 * 
	 * @param reqOrder
	 * @param result
	 * @param reqDatas
	 * @return 根据命令字、结果、数据域生成的Byte数组
	 */
	public static byte[] ReqToBytes(String reqOrder, String result,
			String reqDatas) {
		// 长度字、命令字、校验字 各占一位（byte），以及数据域字节数（2位作为1字节）
		String reqLength = Integer.toHexString(4 + reqDatas.length() / 2);
		// 缺高位补0，保证格式正确
		if (reqLength.length() == 1) {
			reqLength = "0" + reqLength;
		}

		byte byteVerify = 0;

		byte byteLength = (byte) Integer.parseInt(reqLength, 16);
		byte byteOrder = (byte) Integer.parseInt(reqOrder, 16);
		byte byteResult = (byte) Integer.parseInt(result, 16);
		byteVerify = (byte) (byteLength + byteOrder);
		byteVerify = (byte) (byteVerify + byteResult);

		// 有时不需要数据域
		byte byteData = 0;
		if (reqDatas != null && !reqDatas.equals("")) {

			byteData = (byte) Integer.parseInt(reqDatas, 16);
			byteVerify = (byte) (byteVerify + byteData);
		}
		byte head = (byte) Integer.parseInt("55", 16);
		byte end = (byte) Integer.parseInt("AA", 16);
		byte[] bytes = new byte[] { head, byteLength, byteOrder, byteResult,
				byteData, byteVerify, end };

		return bytes;

	}

/*	*//**
	 * 非应答字符串转为数据集
	 * 
	 * @param str
	 *            收到的字符串
	 * @return 拆分好的数据map集合，若头尾有误、校验字有误，都返回null
	 *//*
	public static HashMap<String, Object> stringToReq(String str) {
		String head = str.substring(0, 2);
		String end = str.substring(str.length() - 2, str.length());
		if (head.equals("55") && end.equals("AA")) {
			String length = str.substring(2, 4);
			String order = str.substring(4, 6);
			// String result = str.substring(6, 8);
			String datas = "";
			// if ((str.length() - 4) > 8) {
			if ((str.length() - 4) > 6) {

				// datas = str.substring(8, str.length() - 4);
				datas = str.substring(6, str.length() - 4);
			}
			String verify = str.substring(str.length() - 4, str.length() - 2);

			byte bVerify = 0;
			byte bLength = (byte) Integer.parseInt(length, 16);
			byte bOrder = (byte) Integer.parseInt(order, 16);
			// byte bResult = (byte) Integer.parseInt(result, 16);

			bVerify = (byte) (bLength + bOrder);
			// bVerify = (byte) (bVerify + bResult);
			if (!datas.equals("")) {
				byte[] b = datas.getBytes();
				for (int i = 0; i < b.length; i++) {
					bVerify = (byte) (bVerify + b[i]);
				}
			}
			// 转为int
			int iVerify = bVerify & 0xff;
			// 再转为16进制的字符串
			String reqVerify = Integer.toHexString(iVerify);
			// 缺高位补0，保证格式正确
			if (reqVerify.length() == 1) {
				reqVerify = "0" + reqVerify;
			}

			if (!reqVerify.equals(verify)) {
				return null;
			}

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("length", length);
			map.put("order", order);
			// map.put("result", result);
			map.put("datas", datas);
			map.put("verify", verify);

			return map;
		}
		return null;
	}*/

	/**
	 * 接收到的数据格式转为分离的数据集
	 * 
	 * @param bytes
	 * @return
	 */
	public static HashMap<String, Object> bytesToMap(byte[] bytes) {
		byte head = bytes[0];
		byte length = bytes[1];
		byte order = bytes[2];
		byte verify = bytes[bytes.length - 2];
		byte end = bytes[bytes.length - 1];

		byte v = 0;
		// [85, 13, 1, 0, 86, 49, 46, 48, 46, 48, 46, 50, 0, -86]
		// [85, 5, 5, 0, 15, 25, -86]
		for (int i = 1; i < bytes.length - 2; i++) {
			v = (byte) (v + bytes[i]);
		}
		if (v != bytes[bytes.length - 2]) {
			return null;
		}

		if (length != bytes.length - 2) {
			return null;
		}
		String strEnd = Integer.toHexString(end);
		strEnd = strEnd.substring(strEnd.length() - 2, strEnd.length())
				.toUpperCase().trim();
		if (!Integer.toHexString(head).equals("55") || !strEnd.equals("AA")) {
			return null;
		}

		if (order == 2) {
			byte[] datas = new byte[bytes.length - 5];
			for (int i = 3; i < bytes.length - 2; i++) {
				datas[i - 3] = bytes[i];
			}

			String strDatas = new String(datas);
			String strHead = Integer.toHexString(head);
			String strLength = Integer.toHexString(length);
			// 缺高位补0，保证格式正确
			if (strLength.length() == 1) {
				strLength = "0" + strLength;
			}
			String strOrder = Integer.toHexString(order);
			// 缺高位补0，保证格式正确
			if (strOrder.length() == 1) {
				strOrder = "0" + strOrder;
			}

			String temp = Integer.toHexString(verify);
			// 缺高位补0，保证格式正确
						if (temp.length() == 1) {
							temp = "0" + temp;
						}
			String strVerify = temp.substring(temp.length() - 2, temp.length())
					.toUpperCase().trim();
			// 缺高位补0，保证格式正确
			if (strVerify.length() == 1) {
				strVerify = "0" + strVerify;
			}

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("length", strLength);
			map.put("order", strOrder);
			map.put("datas", strDatas);
			map.put("verify", strVerify);

			return map;
		} else if (order == 1) {
			byte result = bytes[3];

			byte[] datas = new byte[bytes.length - 6];
			for (int i = 4; i < bytes.length - 2; i++) {
				datas[i - 4] = bytes[i];
			}

			String strDatas = new String(datas);
			String strHead = Integer.toHexString(head);
			String strLength = Integer.toHexString(length);
			// 缺高位补0，保证格式正确
			if (strLength.length() == 1) {
				strLength = "0" + strLength;
			}
			String strOrder = Integer.toHexString(order);
			// 缺高位补0，保证格式正确
			if (strOrder.length() == 1) {
				strOrder = "0" + strOrder;
			}

			String strResult = Integer.toHexString(result);

			String temp = Integer.toHexString(verify);
			
			// 缺高位补0，保证格式正确
						if (temp.length() == 1) {
							temp = "0" + temp;
						}
			
			String strVerify = temp.substring(temp.length() - 2, temp.length())
					.toUpperCase().trim();
			

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("length", strLength);
			map.put("order", strOrder);
			map.put("result", strResult);
			map.put("datas", strDatas);
			map.put("verify", strVerify);

			return map;

		} else if (order == 5) {
			byte result = bytes[3];

			byte datas = bytes[4];

			String strDatas = Integer.toHexString(datas).toUpperCase();
			// 缺高位补0，保证格式正确
			if (strDatas.length() == 1) {
				strDatas = "0" + strDatas;
			}
			strDatas = strDatas.substring(strDatas.length() - 2, strDatas.length()).toUpperCase().trim();


			String strHead = Integer.toHexString(head);
			String strLength = Integer.toHexString(length);
			// 缺高位补0，保证格式正确
			if (strLength.length() == 1) {
				strLength = "0" + strLength;
			}
			String strOrder = Integer.toHexString(order);
			// 缺高位补0，保证格式正确
			if (strOrder.length() == 1) {
				strOrder = "0" + strOrder;
			}

			String strResult = Integer.toHexString(result);

			String temp = Integer.toHexString(verify);
			// 缺高位补0，保证格式正确
			if (temp.length() == 1) {
				temp = "0" + temp;
			}
			String strVerify = temp.substring(temp.length() - 2, temp.length())
					.toUpperCase().trim();


			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("length", strLength);
			map.put("order", strOrder);
			map.put("result", strResult);
			map.put("datas", strDatas);
			map.put("verify", strVerify);

			return map;

		} else {
			return null;
		}
	}

}
