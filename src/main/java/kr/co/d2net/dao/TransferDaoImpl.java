package kr.co.d2net.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.DaoNonRollbackException;
import kr.co.d2net.commons.exceptions.DaoRollbackException;
import kr.co.d2net.commons.utils.Utility;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.stereotype.Repository;

import com.ibatis.sqlmap.client.SqlMapClient;

@Repository(value="transferDao")
public class TransferDaoImpl extends SqlMapClientDaoSupport implements TransferDao {
	
	@Autowired
	public final void setSqlMapClientWorkaround(SqlMapClient sqlMapClient) { 
		super.setSqlMapClient(sqlMapClient); 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EqTbl> findEqStatus() throws DaoNonRollbackException {
		return getSqlMapClientTemplate().queryForList("Eq.getOperatingStatus");
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Transfer> findTransferStatus(Map<String, Object> params)
			throws DaoNonRollbackException {
		
		return getSqlMapClientTemplate().queryForList("Trsf.findTransferStatus", params);
	}

	@Override
	public Transfer getTransfer(Map<String, Object> params) throws DaoNonRollbackException {
		return (Transfer)getSqlMapClientTemplate().queryForObject("", params);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<EqTbl> findEq(Map<String, Object> params) throws DaoNonRollbackException {
		return getSqlMapClientTemplate().queryForList("Eq.findEq", params);
	}

	@Override
	public void updateEq(EqTbl eqTbl)
			throws DaoRollbackException {
		getSqlMapClientTemplate().update("Eq.updateEq", eqTbl);
	}

	@Override
	public void updateTransfer(Transfer transfer)
			throws DaoRollbackException {
		if(StringUtils.isNotBlank(transfer.getTfGb())) {
			if(transfer.getTfGb().equals("U"))
				getSqlMapClientTemplate().update("Trsf.updateVsTransfer", transfer);
			else
				getSqlMapClientTemplate().update("Trsf.updateCsTransfer", transfer);
		} else {
			throw new DaoRollbackException("tfGb 값이 없습니다.");
		}
	}

	@Override
	public void insertEq(EqTbl eqTbl) throws DaoRollbackException {
		getSqlMapClientTemplate().insert("Eq.insertEq", eqTbl);
	}

	@Override
	public void updateCtInfo(Map<String, Object> params)
			throws DaoRollbackException {
		getSqlMapClientTemplate().update("Trsf.updateContent", params);
	}

	@Override
	public void updateCtInstInfo(Map<String, Object> params)
			throws DaoRollbackException {
		getSqlMapClientTemplate().update("Transfer.updateContentInst", params);
	}

	@SuppressWarnings("unchecked")
	public List<Transfer> findTransfer(Map<String, Object> params)
			throws DaoNonRollbackException {
		return getSqlMapClientTemplate().queryForList("Trsf.findTransfer", params);
	}

	@Override
	public void deleteTransfer(List<String> list) throws DaoRollbackException {
		try {
			getSqlMapClientTemplate().getSqlMapClient().startTransaction();
			getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().setAutoCommit(false);
			getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
			
			for(String value : list) {
				String[] values = value.split(",");
				String ctId = values[0];
				if(values.length == 3 && StringUtils.isNotBlank(values[2]) && values[2].equals("실패")) {
					if(StringUtils.isNotBlank(ctId)) {
						if(values[1].equals("업로드"))
							getSqlMapClientTemplate().delete("Trsf.deleteVsTransfer", ctId);
						else
							getSqlMapClientTemplate().delete("Trsf.deleteCsTransfer", ctId);
					}
				}
			}
			getSqlMapClientTemplate().getSqlMapClient().executeBatch();
			getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().commit();
		} catch (SQLException e) {
			throw new DaoRollbackException("deleteTransfer error", e);
		} finally {
			try {
				getSqlMapClientTemplate().getSqlMapClient().endTransaction();
				getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().setAutoCommit(true);
			} catch (Exception e2) {}
		}
	}
	
	@Override
	public void updateTransfer(List<String> list) throws DaoRollbackException {
		try {
			getSqlMapClientTemplate().getSqlMapClient().startTransaction();
			getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().setAutoCommit(false);
			getSqlMapClientTemplate().getSqlMapClient().commitTransaction();
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("status", "W");
			params.put("recount", 0);
			params.put("progress", 0);
			params.put("modDtm", Utility.getTimestamp());
			for(String value : list) {
				String[] values = value.split(",");
				String ctId = values[0];
				if(values.length == 3 && StringUtils.isNotBlank(values[2]) && values[2].equals("실패")) {
					if(StringUtils.isNotBlank(ctId)) {
						params.put("ctId", ctId);
						if(values[1].equals("업로드"))
							getSqlMapClientTemplate().delete("Trsf.updateVsTransferMap", params);
						else
							getSqlMapClientTemplate().delete("Trsf.updateCsTransferMap", params);
					}
				}
			}
			getSqlMapClientTemplate().getSqlMapClient().executeBatch();
			getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().commit();
		} catch (SQLException e) {
			throw new DaoRollbackException("deleteTransfer error", e);
		} finally {
			try {
				getSqlMapClientTemplate().getSqlMapClient().endTransaction();
				getSqlMapClientTemplate().getSqlMapClient().getCurrentConnection().setAutoCommit(true);
			} catch (Exception e2) {}
		}
	}

}
