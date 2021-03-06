package com.subang.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class BaseDao<T> {

	protected static final Logger LOG = Logger.getLogger ( BaseDao.class.getName());
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;

	protected Class<T> entityClass;

	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class) params[0];
	}

	protected List<T> findByPage(final String sql, final Object[] args, final int offset,
			final int pageSize) {
		final List<T> pageItems = new ArrayList<T>();
		final BeanPropertyRowMapper<T> rowMapper = new BeanPropertyRowMapper<T>(entityClass);
		jdbcTemplate.query(sql, args, new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				int currentRow = 0;
				rs.beforeFirst();
				while (rs.next() && currentRow < offset + pageSize) {
					if (currentRow >= offset) {
						pageItems.add((T) rowMapper.mapRow(rs, currentRow));
					}
					currentRow++;
				}
			}
		});
		return pageItems;

	}
}
