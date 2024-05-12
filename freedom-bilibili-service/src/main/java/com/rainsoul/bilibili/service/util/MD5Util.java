package com.rainsoul.bilibili.service.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * MD5加密工具类
 * 提供基本的MD5加密、验证以及文件MD5计算方法
 */
public class MD5Util {

    /**
     * 为字符串内容生成MD5签名
     *
     * @param content 需要签名的内容
     * @param salt 盐值，用于增强签名的复杂度和唯一性
     * @param charset 字符集，用于确定内容的编码方式
     * @return 签名后的字符串
     */
	public static String sign(String content, String salt, String charset) {
		content = content + salt; // 拼接盐值
		return DigestUtils.md5Hex(getContentBytes(content, charset)); // 返回MD5签名
	}

    /**
     * 验证字符串内容的MD5签名是否有效
     *
     * @param content 需要验证的内容
     * @param sign 提供的签名字符串
     * @param salt 盐值，用于增强签名的复杂度和唯一性
     * @param charset 字符集，用于确定内容的编码方式
     * @return 验证通过返回true，否则返回false
     */
	public static boolean verify(String content, String sign, String salt, String charset) {
		content = content + salt; // 拼接盐值
		String mysign = DigestUtils.md5Hex(getContentBytes(content, charset)); // 生成当前内容的签名
		return mysign.equals(sign); // 比较签名是否一致
	}

    /**
     * 获取字符串内容的字节数组
     *
     * @param content 需要转换为字节数组的内容
     * @param charset 字符集，用于确定内容的编码方式
     * @return 内容的字节数组
     */
	private static byte[] getContentBytes(String content, String charset) {
		if (!"".equals(charset)) {
			try {
				return content.getBytes(charset); // 根据指定字符集获取字节数组
			} catch (UnsupportedEncodingException var3) {
				throw new RuntimeException("MD5签名过程中出现错误,指定的编码集错误"); // 处理字符集不支持异常
			}
		} else {
			return content.getBytes(); // 默认字符集获取字节数组
		}
	}

    /**
     * 计算上传文件的MD5值
     *
     * @param file 上传的文件
     * @return 文件的MD5字符串
     * @throws IOException 读取文件流时可能抛出的异常
     */
	public static String getFileMD5(MultipartFile file) throws IOException {
		InputStream inputStream = file.getInputStream(); // 获取文件输入流
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 创建输出流用于存储读取的文件内容
		byte[] buffer = new byte[1024]; // 缓冲区，用于存储临时读取的数据
		int byteRead;
		// 循环读取文件内容，并写入到输出流中
		while((byteRead = inputStream.read(buffer))>0){
			baos.write(buffer,0,byteRead);
		}
		inputStream.close(); // 关闭输入流
		String md5Hex = DigestUtils.md5Hex(baos.toByteArray()); // 计算输出流内容的MD5值
		return md5Hex; // 返回文件的MD5字符串
	}
}
