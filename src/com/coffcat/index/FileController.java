package com.coffcat.index;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.coffcat.common.config.Conts;
import com.coffcat.interceptor.AdminInterceptor;
import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class FileController extends BaseController {

	@Before(AdminInterceptor.class)
	public void AdminUploadFile() {
		List<UploadFile> list = this.getFiles();

		int i = 0;

		List<Map<String, Object>> l = new ArrayList<Map<String, Object>>();

		for (UploadFile f : list) {
			i++;

			String filename = System.currentTimeMillis() + "."
					+ Filegetprefix(f.getFile());
			String URL = PropKit.get("FileBaseURL") + Conts.BaseUploadDir + "/"
					+ filename;
			f.getFile().renameTo(
					new File(PathKit.getWebRootPath() + "\\"
							+ Conts.BaseUploadDir + "\\" + filename));

			Map<String, Object> m = new HashMap<String, Object>();
			m.put("ParameterName", f.getParameterName());
			m.put("URL", "?key=filename&value=" + filename);
			m.put("staticurl", PropKit.get("StaticFileBaseURL") + filename);
			m.put("path", f.getUploadPath());

			String ip = this.getRequest().getRemoteAddr();

			System.out.println("远程ip为：" + ip);

			com.coffcat.common.model.File fr = new com.coffcat.common.model.File();

			fr.set("user",
					((Record) this.getSessionAttr("admin_user")).get("id"))
					.set("path", f.getUploadPath()).set("url", URL)
					.set("filename", filename).set("ip", ip)
					.set("usertype", "admin");

			fr.save();

			l.add(m);
		}

		Map<String, Object> map = new HashMap<String, Object>();

		map.put("code", "200");
		map.put("count", i);
		map.put("url", l);

		this.renderJson(map);
	}

	@Before(AdminInterceptor.class)
	public void testupload() {
		this.render("testupload.html");
	}

	/**
	 * 获取文件拓展名
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 * @param f
	 * @return
	 */
	private String Filegetprefix(File f) {
		String fileName = f.getName();
		String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
		return prefix;
	}

	public void index() {

		if (!(this.getPara("key").equals("id") || this.getPara("key").equals(
				"filename"))) {
			this.renderText("指令错误！");
			return;
		}

		Record r = Db.findFirst(
				"select * from file where " + this.getPara("key") + "=?",
				this.getPara("value"));
		if (r == null) {
			this.renderText("文件不存在！");
			return;
		}

		System.out.println(r.getStr("path"));

		this.renderFile(new File(r.getStr("path") + "/" + r.getStr("filename")));
	}

}
