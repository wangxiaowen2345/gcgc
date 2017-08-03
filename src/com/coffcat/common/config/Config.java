package com.coffcat.common.config;

import com.coffcat.common.model._MappingKit;
import com.coffcat.index.AdminController;
import com.coffcat.index.AdminMsgController;
import com.coffcat.index.FcController;
import com.coffcat.index.FileController;
import com.coffcat.index.IndexController;
import com.coffcat.index.MController;
import com.coffcat.util.Sys;
import com.coffcat.weixin.WeiXinOauthController;
import com.coffcat.weixin.WeixinMsgController;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.weixin.sdk.api.ApiConfig;
import com.jfinal.weixin.sdk.api.ApiConfigKit;

/**
 * API引导式配置
 */
public class Config extends JFinalConfig {

	/**
	 * 配置常量
	 */
	public void configConstant(Constants me) {
		Sys.out("Load Config");
		// 加载少量必要配置，随后可用PropKit.get(...)获取值
		PropKit.use("a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		me.setBaseUploadPath(PathKit.getWebRootPath() + "/upload");
		
		
		me.setMaxPostSize(10485760*10);
		
		

		ApiConfigKit.setDevMode(true);

		ApiConfig ac = new ApiConfig();
		// 配置微信 API 相关常量
		ac.setToken(PropKit.get("token"));
		ac.setAppId(PropKit.get("appId"));
		ac.setAppSecret(PropKit.get("appSecret"));

		/**
		 * 是否对消息进行加密，对应于微信平台的消息加解密方式： 1：true进行加密且必须配置 encodingAesKey
		 * 2：false采用明文模式，同时也支持混合模式
		 */
		ac.setEncryptMessage(PropKit.getBoolean("encryptMessage", false));
		ac.setEncodingAesKey(PropKit.get("encodingAesKey",
				"setting it in config file"));

		ApiConfigKit.setThreadLocalApiConfig(ac);

		Conts.init();
	}

	/**
	 * 配置路由
	 */
	public void configRoute(Routes me) {
		Sys.out("Load Route");
		me.add("/", IndexController.class, "/index");
		me.add("/admin", AdminController.class);
		me.add("/fc", FcController.class,"/admin");
		me.add("/file", FileController.class);

		me.add("/wx", WeixinMsgController.class);
		me.add("/wxOauth", WeiXinOauthController.class);
		
		me.add("/m",MController.class,"/ykly");
		
		me.add("/msgnum",AdminMsgController.class);
		
	}

	public static C3p0Plugin createC3p0Plugin() {
		return new C3p0Plugin(PropKit.get("jdbcUrl"), PropKit.get("user"),
				PropKit.get("password").trim());
	}

	/**
	 * 配置插件
	 */
	public void configPlugin(Plugins me) {
		Sys.out("Load Plugins");
		// 配置C3p0数据库连接池插件
		C3p0Plugin C3p0Plugin = createC3p0Plugin();
		me.add(C3p0Plugin);

		// 配置ActiveRecord插件
		ActiveRecordPlugin arp = new ActiveRecordPlugin(C3p0Plugin);
		me.add(arp);

		// 所有配置在 MappingKit 中搞定
		_MappingKit.mapping(arp);
	}

	/**
	 * 配置全局拦截器
	 */
	public void configInterceptor(Interceptors me) {

	}

	/**
	 * 配置处理器
	 */
	public void configHandler(Handlers me) {

	}

	/**
	 * 建议使用 JFinal 手册推荐的方式启动项目 运行此 main
	 * 方法可以启动项目，此main方法可以放置在任意的Class类定义中，不一定要放于此
	 */
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/", 5);

	}
}
