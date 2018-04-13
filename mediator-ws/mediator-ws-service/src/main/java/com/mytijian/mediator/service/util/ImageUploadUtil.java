package com.mytijian.mediator.service.util;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aliyun.oss.OSSClient;

@Component("imageUploadUtil")
public class ImageUploadUtil {
	@Value("${aliyun.oss.endpoint}")
	private String endpoint;
	
	@Value("${aliyun.oss.bucket}")
	private String bucket;
	
	@Value("${aliyun.oss.accessKeyId}")
	private String accessKeyId;
	
	@Value("${aliyun.oss.accessKeySecret}")
	private String accessKeySecret;
	
	
	public  void upLoadImg(InputStream  inputStream , String fileName) {
		//fileName的格式为hospital/${hospitalId}/examReportImage/${report_id}/${文件名}
		OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
		// 写入文件内容
		ossClient.putObject(bucket, fileName, inputStream );
		ossClient.shutdown();

	}
}
