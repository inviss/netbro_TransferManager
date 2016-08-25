package kr.co.d2net.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.ServiceException;
import kr.co.d2net.commons.utils.Utility;
import kr.co.d2net.dao.TransferDao;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="statuService")
public class StatuServiceImpl implements StatuService {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TransferDao transferDao;

	@Override
	public List<Transfer> findTransferStatus(String state) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(state))
			params.put("state", state);
		
		return transferDao.findTransferStatus(params);
	}

	@Override
	public Transfer getStatus(String ctId) throws ServiceException {
		return null;
	}

	@Override
	public void saveStatus(Transfer transfer) throws ServiceException {
		
		transfer.setModDtm(Utility.getTimestamp());
		transferDao.updateTransfer(transfer);
		
		// CMS에서 상태 변경을 하므로 무시
		/*if(StringUtils.isNotBlank(transfer.getEqId())) {
			EqTbl eqTbl = new EqTbl();
			if(transfer.getStatus().equals("C") || transfer.getStatus().equals("E")) {
				eqTbl.setStatus("");
				eqTbl.setJobId("");
			} else {
				eqTbl.setStatus(transfer.getStatus());
				eqTbl.setJobId(transfer.getCtId());
			}
			eqTbl.setEqId(transfer.getEqId());
			transferDao.updateEq(eqTbl);
		}*/
		
		if(transfer.getTfGb() != null && transfer.getTfGb().equals("U")) {
			try {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ctId", transfer.getCtId());
				params.put("vsDelDt", null);
				params.put("modDtm", Utility.getTimestamp());
				transferDao.updateCtInfo(params);
			} catch (Exception e) {
				logger.error("content update error", e);
			}
			
		}
	}

	@Override
	public List<EqTbl> findEqStatus() throws ServiceException {
		return transferDao.findEqStatus();
	}

	@Override
	public void saveStatus(String ctId, String status) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(ctId)) {
			params.put("ctId", ctId);
		}
		params.put("status", status);
		if(status.equals("Q")) {
			params.put("progress", 0);
		}
	}

	@Override
	public void updateContents(Transfer transfer) throws ServiceException {
		Map<String, Object> params = new HashMap<String, Object>();
		
		// 컨텐츠 정보 변경
		params.put("ctId", transfer.getCtId());
		params.put("ctLoc", "2");
		params.put("csState", "02");
		params.put("modDtm", Utility.getTimestamp("yyyyMMddHHmmss"));
		
		transferDao.updateCtInfo(params);
		//transferDao.updateCtInstInfo(params);
	}

}
