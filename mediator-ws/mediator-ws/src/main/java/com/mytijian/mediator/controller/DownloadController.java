package com.mytijian.mediator.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author linzhihao
 */

@Controller
public class DownloadController {
	
	@Value("${upload}")
	private String upload;

	@RequestMapping(value = "/fetch/{dataId}", method = RequestMethod.GET)
	public void load(@PathVariable("dataId") String dataId, HttpServletResponse response) 
			throws IOException {
		OutputStream out = response.getOutputStream();
		String filePath = String.format("%s/%s", this.upload, dataId);
		System.out.println(filePath);
		File dataFile = new File(filePath);
		if (dataFile.exists() && dataFile.isFile()) {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(dataFile));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int len = -1;
			while ((len=in.read(buff))!=-1) {
				baos.write(buff, 0, len);
			}
			in.close();
			out.write(baos.toByteArray());
			out.flush();
			out.close();
		} else {
			response.sendError(404, "DATA FILE NOT EXISTS: "+dataId);
		}
	}

}
