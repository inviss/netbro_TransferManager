package kr.co.d2net.dao;

import java.util.List;

import kr.co.d2net.commons.dto.ContentInstTbl;
import kr.co.d2net.commons.exceptions.DaoNonRollbackException;

public interface MediaDao {
	public List<ContentInstTbl> findRPWmvCopy(String raceId) throws DaoNonRollbackException;
	public void updateContentInst(ContentInstTbl contentInstTbl) throws DaoNonRollbackException;
}
