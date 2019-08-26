package com.northmeter.northmetersmart.order;

import java.security.Key;
import java.security.MessageDigest;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Create_direct_opt_by_user {

	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);
		String str = s.next();
		String str2 = s.next();

		Create_direct_opt_by_user create = new Create_direct_opt_by_user();
		System.out.println("sign:" + create.get_sign(str, str2));
		System.out.println("eqpt:" + create.get_eqpt_pwd(str, str2));

	}

	// public static byte[] hexStr2Bytes(String src) {
	// int m=0,n=0;
	// int l=src.length()/2;
	// System.out.println(l);
	// byte[] ret = new byte[l];
	// for (int i = 0; i < l; i++) {
	// m=i*2+1;
	// n=m+1;
	// ret[i] = uniteBytes(src.substring(i*2, m),src.substring(m,n));
	// }
	// return ret;
	// }
	// private static byte uniteBytes(String src0, String src1) {
	// byte b0 = Byte.decode("0x" + src0).byteValue();
	// b0 = (byte) (b0 << 4);
	// byte b1 = Byte.decode("0x" + src1).byteValue();
	// byte ret = (byte) (b0 | b1);
	// return ret;
	// }
	// appǩ��
	// <param name="str">����</param>
	// <param name="key">��Կ</param>
	public String get_sign(String str, String str2) {
		try {
			String temp0 = str;
			String temp = MD55(temp0);
			str2 = MD55(str2);

			byte[] arrtemp = new byte[temp.length() / 2];
			for (int i = 0; i < arrtemp.length; i++) {
				// arrtemp[i] = Convert.ToByte(temp.Substring(i * 2, 2), 16);
				char[] t = temp.substring(i * 2, 2).toCharArray();
				String s = String.valueOf(t);
				if (s.equals("")) {
					break;
				}
				arrtemp[i] = (byte) Integer.parseInt(s, 16); // parseUsignedInt

			}

			byte[] keytemp = new byte[str2.length() / 2];
			for (int i = 0; i < str2.length() / 2; i++) {
				// keytemp[i] = Convert.ToByte(str2.Substring(i * 2, 2), 16);
				char[] k = str2.substring(i * 2, 2).toCharArray();
				String s = String.valueOf(k);
				if (s.equals("")) {
					break;
				}
				keytemp[i] = (byte) Integer.parseInt(s, 16);
			}
			String[] ivstr = ("0x11,0x22,0x33,0x44,0x55,0x66,0x77,0x88,0x99")
					.split(",");
			byte[] ivtemp = new byte[ivstr.length];
			for (int i = 0; i < ivstr.length; i++) {
				// ivtemp[i] = Convert.ToByte(ivstr[i], 16);
				int iValue = Integer.parseInt(
						ivstr[i].substring(2, ivstr[i].length()), 16);
				// System.out.println("==="+ivstr[i].substring(2,ivstr[i].length()));
				ivtemp[i] = (byte) iValue;

			}
			byte[] tempresult = _3DES2Encrypt(keytemp, ivtemp, arrtemp);
			String xx = "";
			for (int i = 0; i < tempresult.length; i++) {
				// String temp1 = new String(tempresult[i], 16);
				String temp1 = Integer.toString(tempresult[i], 16);
				if (temp1.length() == 1) {
					temp1 = "0" + temp1;
				}
				xx += temp1;
			}
			return xx;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return str2;
	}

	// int ת��Ϊbyte
	public byte[] intToByte(int i) {
		byte[] bt = new byte[4];
		bt[0] = (byte) (0xff & i);
		bt[1] = (byte) ((0xff00 & i) >> 8);
		bt[2] = (byte) ((0xff0000 & i) >> 16);
		bt[3] = (byte) ((0xff000000 & i) >> 24);
		return bt;
	}

	// �豸��Կ
	// <param name="str">����</param>
	// <param name="str2">��Կ</param>
	public String get_eqpt_pwd(String str, String str2) {
		try {
			String temp = str;
			str2 = MD55(str2);
			int ys = str2.length() % 8;
			String t0 = "";
			if (ys > 0) {

				for (int i = 0; i < str2.length(); i++) {
					t0 += "0";
				}
			}
			str2 = t0 + str2;
			byte[] arrtemp = new byte[temp.length() / 2];
			for (int i = 0; i < arrtemp.length; i++) {
				// arrtemp[i] = convert.ToByte(temp.substring(i * 2, 2), 16);
				char[] t = temp.substring(i * 2, 2).toCharArray();
				String s = String.valueOf(t);
				if (s.equals("")) {
					break;
				}
				arrtemp[i] = (byte) Integer.parseInt(s, 16);
			}

			byte[] keytemp = new byte[str2.length() / 2];
			for (int i = 0; i < str2.length() / 2; i++) {
				// keytemp[i] = Convert.ToByte(str2.substring(i * 2, 2), 16);
				char[] k = str2.substring(i * 2, 2).toCharArray();
				String s = String.valueOf(k);
				if (s.equals("")) {
					break;
				}
				keytemp[i] = (byte) Integer.parseInt(s, 16);
			}
			String[] ivstr = ("0x11,0x22,0x33,0x44,0x55,0x66,0x77,0x88,0x99")
					.split(",");
			byte[] ivtemp = new byte[ivstr.length];
			for (int i = 0; i < ivstr.length; i++) {
				// ivtemp[i] = Convert.ToByte(ivstr[i], 16);
				int iValue = Integer.parseInt(
						ivstr[i].substring(2, ivstr[i].length()), 16);
				// System.out.println("==="+ivstr[i].substring(2,ivstr[i].length()));
				ivtemp[i] = (byte) iValue;

			}
			byte[] tempresult = _3DES2Encrypt(keytemp, ivtemp, arrtemp);
			String xx = "";
			for (int i = 0; i < tempresult.length; i++) {
				// String temp1 = ToString(tempresult[i], 16);
				String temp1 = Integer.toString(tempresult[i], 16);
				if (temp1.length() == 1) {
					temp1 = "0" + temp1;
				}
				xx += temp1;
			}
			return xx;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str2;
	}

	// MD5����
	public static String MD55(String str) throws Exception {
		String encoding = "UTF-8";
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(str.getBytes(encoding));
		byte[] result = md.digest();
		StringBuffer sb = new StringBuffer(32);
		for (int i = 0; i < result.length; i++) {
			int val = result[i] & 0xff;
			if (val < 0xf) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(val));
		}
		return sb.toString().toUpperCase();
	}

	// 3des����������
	public static byte[] _3DES2Encrypt(byte[] key, byte[] iv, byte[] data)
			throws Exception {
		byte[] key1 = new byte[8];
		System.arraycopy(key, 0, key1, 0, 8);
		byte[] key2 = new byte[8];
		System.arraycopy(key, 8, key2, 0, 8);
		byte[] data1;
		data1 = EncryptECB(data, key1, null);
		data1 = DecryptECB(data1, key2, null);
		data1 = EncryptECB(data1, key1, null);
		return data1;
	}

	// 3des����������
	public static byte[] _3DES2Descrypt(byte[] key, byte[] iv, byte[] data)
			throws Exception {

		byte[] key1 = new byte[8];
		System.arraycopy(key, 0, key1, 0, 8);
		byte[] key2 = new byte[8];
		System.arraycopy(key, 8, key2, 0, 8);

		byte[] data1;
		data1 = DecryptECB(data, key1, null);
		data1 = EncryptECB(data1, key2, null);
		data1 = DecryptECB(data1, key1, null);
		return data1;
	}

	// 3DES���� ������
	public static byte[] EncryptECB(byte[] data, byte[] key, byte[] iv)
			throws Exception {

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(iv);
		cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
		return cipher.doFinal(data, 0, data.length);

	}

	// 3DES ���� ������
	public static byte[] DecryptECB(byte[] data, byte[] key, byte[] iv)
			throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
		deskey = keyFactory.generateSecret(spec);

		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
		return cipher.doFinal(data, 0, data.length);
	}

	// escape
	public static String escape(String src) {
		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (Character.isDigit(j) || Character.isLowerCase(j)
					|| Character.isUpperCase(j))
				tmp.append(j);
			else if (j < 256) {
				tmp.append("%");
				if (j < 16)
					tmp.append("0");
				tmp.append(Integer.toString(j, 16));
			} else {
				tmp.append("%u");
				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	// unescape
	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(
							src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(
							src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}

}
