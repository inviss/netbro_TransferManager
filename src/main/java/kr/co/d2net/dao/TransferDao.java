package kr.co.d2net.dao;

import java.util.List;
import java.util.Map;

import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.DaoNonRollbackException;
import kr.co.d2net.commons.exceptions.DaoRollbackException;

public interface TransferDao {
	
	/**
	 * FTP Transfer Module 리스트를 조회한다.
	 * @return Transfer EqList
	 * @throws DaoNonRollbackException
	 */
	public List<EqTbl> findEq(Map<String, Object> params) throws DaoNonRollbackException;
	
	/**
	 * FTP Transfer Module의 상태를 조회한다.
	 * @return
	 * @throws DaoNonRollbackException
	 */
	public List<EqTbl> findEqStatus() throws DaoNonRollbackException;
	
	/**
	 * 전송요청된 리스트를 조회한다.
	 * @param params
	 * @return
	 * @throws DaoNonRollbackException
	 */
	public List<Transfer> findTransferStatus(Map<String, Object> params) throws DaoNonRollbackException;
	
	/**
	 * 전송 요청된 컨텐츠 단일건을 조회한다.
	 * @param params
	 * @return
	 * @throws DaoNonRollbackException
	 */
	public Transfer getTransfer(Map<String, Object> params) throws DaoNonRollbackException;
	
	/**
	 * 전송 요청된 트랜스퍼 정보를 변경한다.
	 * @param params
	 * @throws DaoRollbackException
	 */
	public void updateTransfer(Transfer transfer) throws DaoRollbackException;
	
	public void updateTransfer(List<String> list) throws DaoRollbackException;
	
	/**
	 * 작동중인 FTP Module의 상태를 변경한다.
	 * @param params
	 * @throws DaoRollbackException
	 */
	public void updateEq(EqTbl eqTbl) throws DaoRollbackException;
	
	/**
	 * Transfer Module을 등록한다.
	 * @param tfConfig
	 * @throws DaoRollbackException
	 */
	public void insertEq(EqTbl eqTbl) throws DaoRollbackException;
	
	/**
	 * 컨텐츠 정보를 변경한다.
	 * @param params
	 * @throws DaoRollbackException
	 */
	public void updateCtInfo(Map<String, Object> params) throws DaoRollbackException;
	
	/**
	 * 컨텐츠 영상정보를 변경한다.
	 * @param params
	 * @throws DaoRollbackException
	 */
	public void updateCtInstInfo(Map<String, Object> params) throws DaoRollbackException;
	
	public List<Transfer> findTransfer(Map<String, Object> params) throws DaoNonRollbackException;
	
	public void deleteTransfer(List<String> list) throws DaoRollbackException;
	
}
