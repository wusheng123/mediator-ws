package com.mytijian.mediator.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.mytijian.mediator.api.enums.UpgradeStatus;
import com.mytijian.mediator.api.model.UpgradeMessage;
import com.mytijian.mediator.common.DAO.UpgradeMessageMapper;
import com.mytijian.mediator.util.MyClassLoader;
import com.mytijian.mediator.util.Packager;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

@Controller
public class PackController {

	@Resource(name = "upgradeMessageMapper")
	private UpgradeMessageMapper upgradeMessageMapper;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;

	@Value("${temp.folder}")
	private String tempFolder;

	@RequestMapping(value = "/index")
	public ModelAndView test() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		List<Hospital> hospitals = hospitalService.getHospitals(null, null);
		mv.addObject("hospitals", hospitals);
		mv.addObject("selectedHosp", hospitals.get(0).getId());
		return mv;
	}

	// com/mytijian/mediator/listener/GlobalSpringContextListener
	// linabc.txt : WEB-INF/classes/com/mytijian/mediator/listener/GlobalSpringContextListener.class
	// winabc.txt : WEB-INF\classes\类名
	@RequestMapping(value = "/pack", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public void pack(HttpServletRequest request, @RequestParam(value = "file", required = true) MultipartFile file,
			@RequestParam(value = "hospitalId") Integer hospitalId,
			@RequestParam(value = "currVersion") String currVersion,
			@RequestParam(value = "newVersion") String newVersion, HttpServletResponse response) throws IOException,
			ClassNotFoundException {
		response.setHeader("Content-Type", "text/html; charset=UTF-8");
		if (file == null || !file.getOriginalFilename().endsWith(".zip")) {
			response.getWriter().write("文件格式不正确");
			return;
		}

		ZipInputStream oriZipFile = new ZipInputStream(file.getInputStream());
		ZipEntry zipEntry = oriZipFile.getNextEntry();
		StringBuilder linuxBuilder = new StringBuilder();
		StringBuilder winBuilder = new StringBuilder();

		// 升级zip包
		File zip = new File(tempFolder, "upgrade.zip");
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zip));
		zipOut.setLevel(Deflater.DEFAULT_COMPRESSION);

		while (zipEntry != null) {
			String fileName = zipEntry.getName();
			byte[] byteArray = zipFile(oriZipFile, zipOut, fileName);

			if (fileName.endsWith(".class")) {
				MyClassLoader loader = new MyClassLoader();

				loader.setClassByte(byteArray);
				try {
					loader.findClass(fileName);
				} catch (Throwable e) {
					String cName = e.getMessage()
							.substring(e.getMessage().indexOf(":") + 1, e.getMessage().length() - 1).trim()
							+ ".class";
					resolve(linuxBuilder, winBuilder, cName, "WEB-INF/classes/");
				}

			} else if (fileName.endsWith(".jar")) {
				resolve(linuxBuilder, winBuilder, fileName, "WEB-INF/lib/");
			} else {
				// 配置文件
				resolve(linuxBuilder, winBuilder, fileName, "WEB-INF/classes/");
			}

			zipEntry = oriZipFile.getNextEntry();
		}

		zipConfigFile(linuxBuilder, zipOut, "linabc.txt");
		zipConfigFile(winBuilder, zipOut, "winabc.txt");

		zipOut.flush();
		zipOut.close();

		addUpgradeMessage(hospitalId, currVersion, newVersion, zip);

		response.getWriter().print("上传成功!");
	}

	protected void addUpgradeMessage(Integer hospitalId, String currVersion, String newVersion, File zip) {
		UpgradeMessage message = new UpgradeMessage();
		message.setHospitalId(hospitalId);
		message.setCurrentVersion(currVersion);
		message.setNewVersion(newVersion);
		message.setPackFile(zip.getPath());
		message.setStatus(UpgradeStatus.ReadyForUpgrade.getCode());
		upgradeMessageMapper.insert(message);
	}

	protected void zipConfigFile(StringBuilder build, ZipOutputStream zipOut, String fileName) throws IOException {
		InputStream is = new ByteArrayInputStream(build.toString().getBytes());
		Packager.zipFile(zipOut, "", fileName, is);
	}

	protected byte[] zipFile(ZipInputStream oriZipFile, ZipOutputStream zipOut, String fileName) throws IOException {
		byte[] byteArray = IOUtils.toByteArray(oriZipFile);
		InputStream inputStream = new ByteArrayInputStream(byteArray);
		Packager.zipFile(zipOut, "", fileName, inputStream);
		return byteArray;
	}

	private void resolve(StringBuilder linuxBuilder, StringBuilder winBuilder, String fileName, String prefix) {
		//配置文件每一行的格式： 文件名+空格+文件路径
		String name = fileName.substring(fileName.lastIndexOf("/") == -1 ? 0 : fileName.lastIndexOf("/") + 1);
		String p = name + " " + prefix + fileName;
		linuxBuilder.append(p).append("\n");
		winBuilder.append(p.replace("/", "\\")).append("\n");
	}

}
