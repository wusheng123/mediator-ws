package com.mytijian.mediator.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.annotation.Resource;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.ws.soap.MTOM;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mytijian.mediator.api.model.UpgradeMessage;
import com.mytijian.mediator.api.service.UpgradeService;
import com.mytijian.mediator.common.DAO.UpgradeMessageMapper;

@Service("upgradeService")
@MTOM
public class UpgradeServiceImpl implements UpgradeService {
	private Logger logger = LoggerFactory.getLogger(UpgradeServiceImpl.class);

	@Resource(name = "upgradeMessageMapper")
	private UpgradeMessageMapper upgradeMessageMapper;
	
	@Value("${temp.folder}")
	private String tempFolder;

	@Override
	public UpgradeMessage getUpgradeInfo(Integer deployId) {
		return upgradeMessageMapper.selectById(deployId);
	}

	@Override
	public void updateUpgradeInfo(Integer deployId, Integer status) {
		upgradeMessageMapper.updateStatus(deployId, status);

	}

	@Override
	@XmlMimeType("application/octet-stream")
	public DataHandler packFile(Integer deployId) {
		UpgradeMessage log = upgradeMessageMapper.selectById(deployId);

		if (log != null) {
			return new DataHandler(new FileDataSource(new File(log.getPackFile())));
		}

		return null;
	}

	@Override
	@XmlMimeType("application/octet-stream")
	public DataHandler downloadFile(String url) {
		if (StringUtils.isNotBlank(url)) {
			try {
				URL fileUrl = new URL(url);
				File file = new File(tempFolder + getFilename(url));
				logger.info("server dir : {}", tempFolder + getFilename(url));
				FileUtils.copyURLToFile(fileUrl, file);
				return new DataHandler(new FileDataSource(file));
			} catch (IOException e) {
				logger.error("download error", e);
				return null;
			}
		} else {
			return null;
		}
	}
	
	private static String getFilename(String url) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String name = url.substring(url.lastIndexOf("/") + 1);
		return format.format(Calendar.getInstance().getTime()) + "_" + name;
	}
	
}
