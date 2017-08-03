package com.coffcat.util;

import java.util.Map;

import javax.management.RuntimeErrorException;

import org.junit.Test;

import com.coffcat.common.config.Conts;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

/**
 * 工具类，搜索工具类
 * 
 * @author hero
 * @Email:mahaojie299@163.com
 */
public class SearchUtil {

	/**
	 * 模糊搜索
	 * 
	 * @author hero
	 * @Email:mahaojie299@163.com
	 * @param c
	 *            extends Jfinal_Contraller，控制器，在方法中使用时，只需要填写this
	 * @param table
	 *            数据库表名
	 * @param field
	 *            String[]类型，参与模糊搜索的字段名
	 * @return Page<Record> 搜索结果
	 */
	public static Page<Record> FuzzySearch(Controller c, String table,
			String[] field) {
		String s = "1=1";
		String search = "";

		Map<String, String[]> m = c.getParaMap();

		if (m.size() > 0 && c.getPara("search") != null) {
			search = c.getPara("search");

			for (int i = 0; i < field.length; i++) {
				if (i == 0) {
					s = " 1=0 ";
				}
				s += ("or " + field[i] + " like '%" + search + "' or "
						+ field[i] + " like '" + search + "%' or " + field[i]
						+ " like '%" + search + "%' or " + field[i]
						+ " like '%" + search + "%'");
			}
		}
		c.setAttr("search", search);
		Page<Record> p = Db.paginate(c.getParaToInt(0, 1), Conts.PageSize,
				"select *", "from " + table + " where " + s
						+ " order by id asc");
		return p;
	}

	public static String getSql(String Sql, String... v) {

		if (getnum(Sql) != v.length) {
			throw new RuntimeException("长度不符合。");
		}

		int num = 0;
		for (int i = 0; i < Sql.length(); i++) {
			if ('?' == Sql.charAt(i)) {
				Sql = repchartoString(Sql, '?', v[num].replace("'", ""));
				num++;
			}
		}

		return Sql;
	}

	private static int getnum(String s) {
		int num = 0;
		char[] chars = s.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if ('?' == chars[i]) {
				num++;
			}
		}

		return num;
	}

	private static String repchartoString(String v, char c, String r) {

		int tag = 0;
		r = "'" + r + "'";
		for (int i = 0; i < v.length(); i++) {
			if ('?' == v.charAt(i)) {
				tag = i;
				break;
			}
		}

		int twobegin = v.length();
		if (tag + 1 <= v.length())
			twobegin = tag + 1;
		return v.substring(0, tag) + r + v.substring(twobegin);
	}

}
