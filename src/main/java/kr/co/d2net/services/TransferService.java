package kr.co.d2net.services;

import java.util.List;
import java.util.Map;

import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.ServiceException;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.DEFAULT)
public interface TransferService {
	
	/**
	 * FTP Module을 가져온다.
	 * @return
	 * @throws ServiceException
	 */
	public List<EqTbl> findEq(Map<String, Object> params) throws ServiceException;
	
	/**
	 * <pre>
	 * FTP로 put, down이 필요한 영상을 조회한다.
	 * </pre>
	 * @param gb
	 * @return
	 * @throws ServiceException
	 */
	public List<Transfer> findTransfer(String gb, Integer queSize) throws ServiceException;
	
	/**
	 * <pre>
	 * FTP로 put, down이 필요한 한건의 영상을 조회한다.
	 * </pre>
	 * @param gb
	 * @return
	 * @throws ServiceException
	 */
	public Transfer getTransfer(String ctId) throws ServiceException;
	
	/**
	 * <pre>
	 * FTP 요청의 상태값을 변경한다.
	 * </pre>
	 * @param ctId
	 * @throws ServiceException
	 */
	@Transactional
	public void updateTransfer(String ctiId, String status) throws ServiceException;
	
	@Transactional
	public void updateTransfer(List<String> list) throws ServiceException;
	
	@Transactional
	public void deleteTransfer(List<String> list) throws ServiceException;
	
	@Transactional
	public void insertEq(EqTbl eqTbl) throws ServiceException;
	
}
