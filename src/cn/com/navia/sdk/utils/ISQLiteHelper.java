package cn.com.navia.sdk.utils;

import android.content.ContentValues;

import java.util.List;

import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.com.navia.sdk.utils.NaviaSpecDaoHelper.RowProcessor;

public interface ISQLiteHelper {

	public abstract List<SpectrumInfo> query(String sqlWhere);

	public abstract List<SpectrumInfo> query(String sqlWhere, String[] sqlWhereArgs);

	public abstract long insert( ContentValues values);
	public abstract long insert( SpectrumInfo info);

	public abstract int delete(String sqlWhere);
	
	public abstract void close();

	/**
	 * 查询得到列表
	 * 
	 * @param sql
	 *            　完整的select语句，可包含?，但不能用;结尾
	 * @param selectionArgs
	 *            　查询参数
	 * @param rp
	 *            　　每行的处理，可使用DAOHelper.MAPROWPROCESSOR
	 * @return
	 */
	public abstract <T> List<T> query(String sql, String[] selectionArgs, RowProcessor<T> rp);

}