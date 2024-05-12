package com.rainsoul.bilibili.service.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * AESUtil 提供AES加密和解密的功能。
 * AES: Advanced Encryption Standard 高级加密标准
 * 最常见的对称加密算法，即加密和解密使用同样的密钥，加密结果可逆
 * 特点：加密速度非常快，适合经常发送数据的场合
 */
public class AESUtil {

    private static final String KEY_ALGORITHM = "AES"; // AES算法标识

    private static final char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' }; // 十六进制字符数组

    private final Cipher decryptCipher; // 解密Cipher

    private final Cipher encryptCipher; // 加密Cipher

    private final String seed; // 种子字符串，用于生成密钥

    /**
     * AESUtil 构造函数。
     *
     * @param seed 用于生成AES密钥的种子字符串。
     * @throws Exception 初始化Cipher时可能抛出的异常。
     */
    public AESUtil(String seed) throws Exception {
        this.seed = seed;
        decryptCipher = Cipher.getInstance(KEY_ALGORITHM);
        encryptCipher = Cipher.getInstance(KEY_ALGORITHM);
        decryptCipher.init(Cipher.DECRYPT_MODE, this.getSecretKey());
        encryptCipher.init(Cipher.ENCRYPT_MODE, this.getSecretKey());
    }

    /**
     * 解密指定的AES加密字符串。
     *
     * @param content 待解密的字符串。
     * @return 解密后的字符串。
     * @throws Exception 解密过程中可能抛出的异常。
     */
    public String decrypt(String content) throws Exception {
        byte[] bytes = Base64.decodeBase64(content); // Base64解码
        byte[] result = decryptCipher.doFinal(bytes); // AES解密
        return new String(result, StandardCharsets.UTF_8); // 转为字符串
    }

    /**
     * 加密指定的字符串。
     *
     * @param content 待加密的字符串。
     * @return 加密后的字符串。
     * @throws Exception 加密过程中可能抛出的异常。
     */
    public String encrypt(String content) throws Exception {
        byte[] result = encryptCipher.doFinal(content.getBytes(StandardCharsets.UTF_8)); // AES加密
        return Base64.encodeBase64String(result); // Base64编码
    }

    /**
     * 根据种子字符串生成AES密钥。
     *
     * @return 生成的AES密钥。
     * @throws Exception 生成密钥可能抛出的异常。
     */
    public SecretKey getSecretKey() throws Exception {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG"); // 获取SHA1PRNG随机数生成器
        random.setSeed(seed.getBytes()); // 设置种子
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM); // 获取AES密钥生成器
        kg.init(128, random); // 初始化密钥生成器
        return kg.generateKey(); // 生成密钥
    }
}
