package com.mytijian.mediator.service.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 
 * @author huangwei
 *
 */
@Component("rasSecurityUtil")
public class RasSecurityUtil {
	
	@Value("${privateKey}")
	private String privateKey;
	
	@Value("${ivString}")
	private String ivString;
	
	/**
	 * 对数据进行加密
	 * @param sourceDate 待加密数据
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String sourceDate) throws Exception{
		return encrypt(sourceDate,privateKey.getBytes("utf-8"),ivString.getBytes("utf-8"));
	}
	
	/**
	 * 对数据进行解密
	 * @param sourceDate 待解密数据
	 * @return
	 * @throws Exception
	 */
	public String decrypt(String sourceDate) throws Exception{
		return decrypt(sourceDate,privateKey.getBytes("utf-8"),ivString.getBytes("utf-8"));
	}
	
	/**
     * 提供密钥和向量进行加密
     * 
     * @param sSrc
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    private String encrypt(String sSrc, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        
        // "算法/模式/补码方式"
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        // 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return Base64.encodeBase64String(encrypted);
    }

    /**
     * 提供密钥和向量进行解密
     * 
     * @param sSrc
     * @param key
     * @param iv
     * @return
     * @throws Exception
     */
    private String decrypt(String sSrc, byte[] key, byte[] iv) throws Exception{
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        byte[] encrypted = Base64.decodeBase64(sSrc);
        byte[] original = cipher.doFinal(encrypted);
        
        return new String(original, "utf-8");
    }
   
//    public static void main(String[] args) throws Exception {
//		String key = "$u3isu^e+h&ZOE2Jo|sdgjtEs@V_Afpt";
//		String iv = "$u3isu^e+h&ZOE2J";
//		RasSecurityUtil util = new RasSecurityUtil();
//		String s = util.decrypt("IjcuTSYTkew5Oe+CwdhS3boQ+JUcKRJpqOC5RJgwW80FoHiNBy+oVdNMsNYf5AngASyksQ4F9PJ2XeFPP+KfeSjUu9Oiwxd9zcR9VDqnTtBBOgrIa5w70j9fQwgk9o/jriSogrCnBwJhA23hlMBCcsiX3hTQGdOtTl4WpK+++bM=");
//		System.out.println(s);
//	}
	
}
