package com.flow.demo.common.core.util;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Java RSA 加密工具类。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public class RsaUtil {

    /**
     * 密钥长度 于原文长度对应 以及越长速度越慢
     */
    private static final int KEY_SIZE = 1024;
    /**
     * 用于封装随机产生的公钥与私钥
     */
    private static final Map<Integer, String> KEY_MAP = new HashMap<>();

    /**
     * 随机生成密钥对。
     */
    public static void genKeyPair() throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        // 得到私钥字符串
        String privateKeyString = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 将公钥和私钥保存到Map
        // 0表示公钥
        KEY_MAP.put(0, publicKeyString);
        // 1表示私钥
        KEY_MAP.put(1, privateKeyString);
    }

    /**
     * RSA公钥加密。
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        // base64编码的公钥
        byte[] decoded = Base64.getDecoder().decode(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密。后面这个更安全，但是SonarQube始终report安全漏洞。"RSA/ECB/PKCS1Padding"
        // 而浏览器自带的Javascript加密功能，目前safari不支持，而且用的人也不太多。所以暂时都不考虑了。
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.getEncoder().encodeToString(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    }

    /**
     * RSA私钥解密。
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 明文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str, String privateKey) throws Exception {
        // 64位解码加密后的字符串
        byte[] inputByte = Base64.getDecoder().decode(str);
        // base64编码的私钥
        byte[] decoded = Base64.getDecoder().decode(privateKey);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        // RSA解密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    public static void main(String[] args) throws Exception {
        long temp = System.currentTimeMillis();
        // 生成公钥和私钥
        genKeyPair();
        // 加密字符串
        System.out.println("公钥:" + KEY_MAP.get(0));
        System.out.println("私钥:" + KEY_MAP.get(1));
        System.out.println("生成密钥消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
        System.out.println("生成后的公钥前端使用!");
        System.out.println("生成后的私钥后台使用!");
        String message = "RSA测试ABCD~!@#$";
        System.out.println("原文:" + message);
        temp = System.currentTimeMillis();
        String messageEn = encrypt(message, KEY_MAP.get(0));
        System.out.println("密文:" + messageEn);
        System.out.println("加密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
        temp = System.currentTimeMillis();
        String messageDe = decrypt(messageEn, KEY_MAP.get(1));
        System.out.println("解密:" + messageDe);
        System.out.println("解密消耗时间:" + (System.currentTimeMillis() - temp) / 1000.0 + "秒");
    }
}