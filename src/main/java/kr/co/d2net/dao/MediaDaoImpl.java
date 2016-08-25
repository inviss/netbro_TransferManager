package kr.co.d2net.dao;

import java.util.Collections;
import java.util.List;

import kr.co.d2net.commons.dto.ContentInstTbl;
import kr.co.d2net.commons.exceptions.DaoNonRollbackException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapClient;

@Repository(value="mediaDao")
public class MediaDaoImpl extends SqlMapClientDaoSupport implements MediaDao {

	@Autowired
	public final void setSqlMapClientWorkaround(SqlMapClient sqlMapClient) { 
		super.setSqlMapClient(sqlMapClient); 
	}
	
	@SuppressWarnings("unchecked")
	public List<ContentInstTbl> findRPWmvCopy(String raceId)
			throws DaoNonRollbackException {
		List<ContentInstTbl> contentInstTbls = getSqlMapClientTemplate().queryForList("Trsf.findRPWmvCopy", raceId);
		return contentInstTbls == null ? Collections.EMPTY_LIST : contentInstTbls;
	}

	@Override
	public void updateContentInst(ContentInstTbl contentInstTbl)
			throws DaoNonRollbackException {
		getSqlMapClientTemplate().update("Trsf.updateCtInst", contentInstTbl);
	}

}
