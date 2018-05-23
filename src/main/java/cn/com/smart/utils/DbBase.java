package cn.com.smart.utils;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DbBase {
	
	/**
	 * @author 陶光胜
	 * @Description: 查询并返回list 适用于sql没有参数
	 * @date 2016年10月17日 下午3:22:15
	 */
	public List<Map<String, Object>> queryForList(String sql){
		Connection con = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			con = DbConfig.getConnection();
			PreparedStatement sm = con.prepareStatement(sql);
			ResultSet set = sm.executeQuery();
			
			ResultSetMetaData resultMetaData = null;
			resultMetaData = set.getMetaData();
			int count = resultMetaData.getColumnCount();
			
			while(set.next()){
				Map<String, Object> row = new HashMap<String,Object>();
				for(int i=0; i< count;i++){
					row.put(resultMetaData.getColumnLabel(i+1),set.getObject(i+1));
				}
				list.add(row);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return list;
	}
	
	
	
	/**
	 * @author 陶光胜
	 * @Description: 查询并返回list 适用于sql没有参数
	 * @date 2016年10月17日 下午3:22:15
	 */
	public List<Map<String, Object>> queryForList(Connection con, String sql){
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			PreparedStatement sm = con.prepareStatement(sql);
			ResultSet set = sm.executeQuery();
			
			ResultSetMetaData resultMetaData = null;
			resultMetaData = set.getMetaData();
			int count = resultMetaData.getColumnCount();
			
			while(set.next()){
				Map<String, Object> row = new HashMap<String,Object>();
				for(int i=0; i< count;i++){
					row.put(resultMetaData.getColumnLabel(i+1),set.getObject(i+1));
				}
				list.add(row);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 查询返回list  适用于需要携带参数
	 * @date 2016年10月17日 下午3:22:53
	 */
	public List<Map<String, Object>> queryForList(String sql, Object args[]){
		Connection con = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			con = DbConfig.getConnection();
			PreparedStatement psm = con.prepareStatement(sql);
			if(args.length>0){
				for(int i=0; i<args.length; i++){
					psm.setObject(i+1, args[i]);
				}
			}
			ResultSet set = psm.executeQuery();
			
			ResultSetMetaData resultMetaData = null;
			resultMetaData = set.getMetaData();
			int count = resultMetaData.getColumnCount();
			
			while(set.next()){
				Map<String, Object> row = new HashMap<String,Object>();
				for(int i=0; i< count;i++){
					row.put(resultMetaData.getColumnLabel(i+1),set.getObject(i+1));
				}
				list.add(row);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbConfig.close(con);
		}
		return list;
	}
	
	
	
	public int getTotalCount(String tableName){
		int count = 0;
		Connection con = null;
		String sql = "select count(1) from "+tableName +" where (isdel=0 or isdel=2)";
		try {
			con = DbConfig.getConnection();
			PreparedStatement psm = con.prepareStatement(sql);
			ResultSet set = psm.executeQuery();
			if(set.next()){
				count = set.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbConfig.close(con);
		}
		return count;
	}
	public int getTotalCounts(String tableName,String tableName1,int isdel){
		int count = 0;
		Connection con = null;
		String sql = "select count(*) from "+tableName+" k ,"+tableName1+" k1 where k.common_id=k1.parent_id and k.isdel="+isdel;
		try {
			con = DbConfig.getConnection();
			PreparedStatement psm = con.prepareStatement(sql);
			ResultSet set = psm.executeQuery();
			if(set.next()){
				count = set.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbConfig.close(con);
		}
		return count;
	}
	
	
	/**
	 * @author 陶光胜
	 * @Description: 增删改操作，需要适用事务时传入连接，并自行关闭连接
	 * @date 2016年10月17日 下午3:23:26
	 */
	public boolean saveOrUpdate(Connection con, String sql){
		boolean result = true;
		try {
			PreparedStatement psm = con.prepareStatement(sql);
			psm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 增删改操作 不需要事务
	 * @date 2016年10月17日 下午3:23:26
	 */
	public boolean saveOrUpdate(String sql){
		Connection con = DbConfig.getConnection();
		boolean result = true;
		try {
			PreparedStatement psm = con.prepareStatement(sql);
			psm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}finally{
			DbConfig.close(con);
		}
		return result;
	}
	/**
	 * @author 陶光胜
	 * @Description: 增删改操作，需要适用事务时传入连接，并自行关闭连接
	 * @date 2016年10月17日 下午3:23:26
	 */
	public boolean saveOrUpdate(Connection con, String sql, Object args[]){
		boolean result = true;
		try {
			PreparedStatement psm = con.prepareStatement(sql);
			if(args.length > 0){
				for(int i =0; i<args.length; i++){
					psm.setObject(i+1, args[i]);
				}
			}
			psm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 增删改操作 不需要事务
	 * @date 2016年10月17日 下午3:23:26
	 */
	public boolean saveOrUpdate(String sql, Object args[]){
		Connection con = DbConfig.getConnection();
		boolean result = true;
		try {
			PreparedStatement psm = con.prepareStatement(sql);
			if(args.length > 0){
				for(int i =0; i<args.length; i++){
					psm.setObject(i+1, args[i]);
				}
			}
			psm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}finally{
			DbConfig.close(con);
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 返回更新的记录数
	 * @date 2016年10月17日 上午9:55:59
	 */
	public int doUpdata(Connection con, String sql, Object args[]){
		int result = 0;
		try {
			PreparedStatement psm = con.prepareStatement(sql);
			if(args.length > 0){
				for(int i =0; i<args.length; i++){
					psm.setObject(i+1, args[i]);
				}
			}
			result = psm.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 新增记录 并返回新增记录的ID  适用需要开启事务
	 * @date 2016年10月17日 上午10:00:06
	 */
	public Long save(Connection con, String sql){
		Long result = 0l;
		try {
			PreparedStatement psm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			psm.executeUpdate();
			ResultSet rs = psm.getGeneratedKeys();
			if(rs.next()){
				result = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 新增记录 并返回新增记录的ID 无需开启事务
	 * @date 2016年10月17日 上午10:00:06
	 */
	public Long save(String sql){
		Connection con = DbConfig.getConnection();
		Long result = 0l;
		try {
			PreparedStatement psm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			psm.executeUpdate();
			ResultSet rs = psm.getGeneratedKeys();
			if(rs.next()){
				result = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbConfig.close(con);
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 新增记录 并返回新增记录的ID  适用于需要开启事务
	 * @date 2016年10月17日 上午10:00:06
	 */
	public Long save(Connection con, String sql, Object args[]){
		Long result = 0l;
		try {
			PreparedStatement psm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if(args.length > 0){
				for(int i =0; i<args.length; i++){
					psm.setObject(i+1, args[i]);
				}
			}
			psm.execute();
			ResultSet rs = psm.getGeneratedKeys();
			if(rs.next()){
				result = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 新增记录 并返回新增记录的ID 
	 * @date 2016年10月17日 上午10:00:06
	 */
	public Long save(String sql, Object args[]){
		Connection con = DbConfig.getConnection();
		Long result = 0l;
		try {
			PreparedStatement psm = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			if(args != null){
				for(int i =0; i<args.length; i++){
					psm.setObject(i+1, args[i]);
				}
			}
			psm.execute();
			ResultSet rs = psm.getGeneratedKeys();
			if(rs.next()){
				result = rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			DbConfig.close(con);
		}
		return result;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 获取某一条记录  ，map中的key与数据库一致
	 * @date 2016年11月3日 上午8:59:41
	 */
	public Map<String, Object> getRowData(String tableName, Long commonId){
		String sql = "select * from "+tableName+" where common_id=?";
		List<Map<String, Object>> list = queryForList(sql, new Object[]{commonId});
		Map<String, Object> map = list.size()==0 ? null : list.get(0);
		return map;
	}
	
	
	/**
	 * 查询某张表的某个字段的最大值
	 * @param tableName 表名
	 * @param fieldName 字段名
	 * @return
	 */
	public int getMaxVal(String tableName,String fieldName){
		int count = 0;
		Connection con = null;
		String sql = "select max("+fieldName+") from "+tableName;
		try {
			con = DbConfig.getConnection();
			PreparedStatement psm = con.prepareStatement(sql);
			ResultSet set = psm.executeQuery();
			if(set.next()){
				count = set.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			DbConfig.close(con);
		}
		return count;
	}
	
	/**
	 * @author 陶光胜
	 * @Description: 查询并返回list 适用于sql没有参数
	 * @date 2016年10月17日 下午3:22:15
	 */
	public int queryForCount(String sql){
		Connection con = null;
		int count=0;
		try {
			con = DbConfig.getConnection();
			PreparedStatement psm = con.prepareStatement(sql);
			ResultSet set = psm.executeQuery();
			if(set.next()){
				count = set.getInt(1);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try {
				if(con != null){
					con.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return count;
	}
	
	public static void main(String args[]){
		System.out.println("11111111111111111111"+new DbBase().saveOrUpdate( "update cmdb_cloud_hosts set cpu=5 where id=?", new Object[]{1}));
	}
}
