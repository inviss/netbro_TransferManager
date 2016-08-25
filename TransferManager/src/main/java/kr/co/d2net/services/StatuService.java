package kr.co.d2net.services;

import java.util.List;

import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.ServiceException;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.REQUIRED, isolation=Isolation.DEFAULT)
public interface StatuService {
	
	/**
	 * <pre>
	 * 업로드 및 다운로드 요청된 모든 컨텐츠에대한 정보 및 진행현황을 조회한다.
	 * </pre>
	 * @return
	 * @throws ServiceException
	 */
	public List<Transfer> findTransferStatus(String state) throws ServiceException;
	
	/**
	 * <pre>
	 * 요청받은 컨텐츠에 대한 전송 정보를 조회한다.
	 * </pre>
	 * @throws ServiceException
	 */
	public Transfer getStatus(String ctId) throws ServiceException;
	
	/**
	 * <pre>
	 * 전송중인 컨텐츠에대한 메타정보를 저장한다.
	 * </pre>
	 * @param transfer
	 * @throws ServiceException
	 */
	@Transactional
	public void saveStatus(Transfer transfer) throws ServiceException;
	
	/**
	 * <pre>
	 * 전달받은 한건의 컨텐츠에 대하여 입력받은 상태값으로 변경한다.
	 * 예) 완료 : [status:C, progress:100], 요청 : [status:Q, progress:0]
	 * </pre>
	 * @param ctId
	 * @throws ServiceException
	 */
	@Transactional
	public void saveStatus(String ctId, String status) throws ServiceException;
	
	/**
	 * <pre>
	 * 전송을 담당하는 트랜스퍼 인스턴스에 대한 정보를 조회한다.
	 * 업로드 및 다운로드의 인스턴스명 및 부가정보를 가져온다.
	 * </pre>
	 * @return
	 * @throws ServiceException
	 */
	public List<EqTbl> findEqStatus() throws ServiceException;
	
	public void updateContents(Transfer transfer) throws ServiceException;
	
}
