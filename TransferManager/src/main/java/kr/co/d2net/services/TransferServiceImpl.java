package kr.co.d2net.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.d2net.commons.ServiceConstants;
import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.ServiceException;
import kr.co.d2net.commons.utils.Utility;
import kr.co.d2net.dao.TransferDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="transferService")
public class TransferServiceImpl implements TransferService {
	
	@Autowired
	private TransferDao	 transferDao;

	@Override
	public List<Transfer> findTransfer(String gb, Integer queSize) throws ServiceException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("tfGb", gb);
		params.put("recount", 4);
		params.put("rownum", (ServiceConstants.FTP_POOL_SIZE - queSize));
		
		return transferDao.findTransfer(params);
	}

	@Override
	public Transfer getTransfer(String ctId) throws ServiceException {
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("ctId", ctId);
		
		return transferDao.getTransfer(params);
	}

	@Override
	public void updateTransfer(String ctiId, String status)
			throws ServiceException {
		Transfer transfer = new Transfer();
		transfer.setCtId(ctiId);
		transfer.setStatus(status);
		transfer.setModDtm(Utility.getTimestamp());
		
		transferDao.updateTransfer(transfer);
	}

	@Override
	public void insertEq(EqTbl eqTbl) throws ServiceException {
		transferDao.insertEq(eqTbl);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EqTbl> findEq(Map<String, Object> params) throws ServiceException {
		List<EqTbl> eqTbls = transferDao.findEq(params);
		return eqTbls == null ? Collections.EMPTY_LIST : eqTbls;
	}

	@Override
	public void deleteTransfer(List<String> list) throws ServiceException {
		transferDao.deleteTransfer(list);
	}

	@Override
	public void updateTransfer(List<String> list) throws ServiceException {
		transferDao.updateTransfer(list);
	}

}
