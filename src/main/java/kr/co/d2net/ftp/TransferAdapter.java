package kr.co.d2net.ftp;

/*
 * (@)# TransferAdapter.java
 * ==================================================================
 * <<D2net>>., Software Licence, Version 1.0
 * 
 * Copyright (c) 1998-2009 <<디투넷>>.,
 * <<서울 영등포구 양평동 3가 16번지 우림이비즈센타 812>> * All reght reserved.
 * 
 * 이 소스의 저작권은 디투넷에 있습니다.
 * url: www.d2net.co.kr
 * ==================================================================
 */

import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.ServiceException;
import kr.co.d2net.services.StatuService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enterprisedt.net.ftp.EventListener;

public class TransferAdapter implements EventListener {

	final Logger logger = LoggerFactory.getLogger(getClass());

	private Transfer transfer;
	private StatuService statuService;
	private int percent = 0;

	public TransferAdapter(Transfer transfer, StatuService statuService) {
		this.transfer = transfer;
		this.statuService = statuService;
	}

	public void bytesTransferred(String remoteFilename, String connId, long bytes) {
		try {
			int i = (int)(((double)bytes / (double)transfer.getFlSize())*100);
			if(i != percent) {
				percent = i;
				if((percent%2) == 0) {
					transfer.setProgress(percent);
					if(percent <= 99) {
						transfer.setStatus("I");
					} else if(percent == 100) {
						transfer.setStatus("C");
					}
					statuService.saveStatus(transfer);
				}
			}

		} catch (Exception e) {}
	}

	public void commandSent(String connId, String cmd) {
	}

	public void downloadCompleted(String connId, String remoteFilename) {

		try {
			
			// 진행상태값이 100은 없으므로 99를 완료로 정하고 99보다 적은 값일경우 진행 취소로 판단됨
			if(percent < 95){
				transfer.setStatus("E");
			} else {
				transfer.setStatus("C");
				
				// 다운로드가 완료되면 WMV 생성을 위해 TC_QUE에 등록한다.
				//statuService.insertTranscode(transfer);
				
				logger.debug("completed transfer percent: "+percent);
				
				transfer.setProgress(100);
				
				// 컨텐츠 정보를 완료 상태로 변경한다.
				statuService.saveStatus(transfer);
				statuService.updateContents(transfer);
			}
		} catch (Exception e) {
			logger.error("Download Error - "+e.getCause());
		}

	}

	public void downloadStarted(String connId, String remoteFilename) {
		if (logger.isInfoEnabled()) {
			logger.info(remoteFilename+" Download Started!");
		}
		try {
			transfer.setStatus("I");
			statuService.saveStatus(transfer);
		} catch (ServiceException e) {
			logger.error("Download Status Update Error", e);
		}
	}

	public void replyReceived(String connId, String remoteFilename) {

	}

	public void uploadCompleted(String connId, String remoteFilename) {
		try {
			// 진행상태값이 100은 없으므로 99를 완료로 정하고 99보다 적은 값일경우 진행 취소로 판단됨
			 if(percent < 99){
				transfer.setStatus("E");
				if (logger.isInfoEnabled()) {
					logger.info(remoteFilename+" Upload Error!");
				}
			} else {
				transfer.setStatus("C");
				if (logger.isInfoEnabled()) {
					logger.info(remoteFilename+" Upload Completed!");
				}
			}

			statuService.saveStatus(transfer);
		} catch (Exception e) {
			logger.error("Transfer Request Error"+e.getCause());
		}
	}

	public void uploadStarted(String connId, String remoteFilename) {
		if (logger.isInfoEnabled()) {
			logger.info(remoteFilename+" Upload Started!");
		}
		
		try {
			transfer.setStatus("I");
			statuService.saveStatus(transfer);
		} catch (ServiceException e) {
			logger.error("Upload Status Update Error"+e.getCause());
		}
	}

}
