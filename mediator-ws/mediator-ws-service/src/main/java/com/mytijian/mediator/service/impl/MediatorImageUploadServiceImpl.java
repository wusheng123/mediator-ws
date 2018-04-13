package com.mytijian.mediator.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.api.service.EnvironmentKeeper;
import com.mytijian.mediator.api.service.MediatorImageUploadService;
import com.mytijian.mediator.service.util.ImageUploadUtil;

@Service("mediatorImageUploadService")
public class MediatorImageUploadServiceImpl implements MediatorImageUploadService{
	private static Logger logger = LoggerFactory.getLogger(MediatorImageUploadServiceImpl.class);
	@Resource(name = "soapHeaderInterceptor")
	private EnvironmentKeeper envKeeper;
	
	@Resource(name = "imageUploadUtil")
	private ImageUploadUtil imageUploadUtil;
	
	@Override
	public void sendImage(String basicUrl,String imageData){
		if(StringUtils.isEmpty(basicUrl)||StringUtils.isEmpty(imageData)) {
			logger.error("同步体检报告没有内容 hospitalId={},basicUrl={}",envKeeper.getHospital(),basicUrl);
			return;
		}
		try {
			Integer hospitalId = envKeeper.getHospital();
			logger.info("hospitalId:"+hospitalId+"basicUrl:"+basicUrl);
			
			//basicUrl:${report_id}/${文件名}
			String fileName = "hospital/" + hospitalId + "/examReportImage/" + basicUrl ;
			InputStream is = stringToInputStream(imageData);
			imageUploadUtil.upLoadImg(is,fileName);
			is.close();
		}catch(Exception e) {
			logger.error("同步体检报告图片  hospitalId={},content={}",envKeeper.getHospital(),e);
		}
		
	}
	
	private InputStream stringToInputStream(String imageDate) {
		  // 图像数据为空
		 byte[] b=java.util.Base64.getDecoder().decode(imageDate);
		 InputStream in = new ByteArrayInputStream(b);
		 return in;
	}
	
	
}
