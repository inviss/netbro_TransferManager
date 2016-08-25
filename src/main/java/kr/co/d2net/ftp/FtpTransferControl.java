package kr.co.d2net.ftp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import kr.co.d2net.commons.ServiceConstants;
import kr.co.d2net.commons.dto.EqTbl;
import kr.co.d2net.commons.dto.FtpConfig;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.utils.StringUtil;
import kr.co.d2net.commons.utils.Utility;
import kr.co.d2net.services.StatuService;
import kr.co.d2net.services.TransferService;
import kr.co.d2net.soap.Navigator;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class FtpTransferControl {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private MessageSource messageSource;
	@Autowired
	private FtpTransferService ftpTransferService;
	@Autowired
	private TransferService transferService;
	@Autowired
	private StatuService statuService;
	@Autowired
	private JaxWsProxyFactoryBean jaxWsProxyFactoryBean;

	private ExecutorService jobReq = Executors.newSingleThreadExecutor();
	private ExecutorService hutReq = Executors.newSingleThreadExecutor();
	private final static BlockingQueue<Transfer> putQueue = new PriorityBlockingQueue<Transfer>(10, getCompare());
	private final static BlockingQueue<Transfer> downQueue = new PriorityBlockingQueue<Transfer>(10, getCompare());
	private ExecutorService putPool = Executors.newFixedThreadPool(ServiceConstants.FTP_POOL_SIZE);
	private ExecutorService downPool = Executors.newFixedThreadPool(ServiceConstants.FTP_POOL_SIZE);
	private static volatile Map<String, String> eqIds = new HashMap<String, String>();
	
	public static volatile String ACTIVE_IP = "";
	
	private Navigator navigator;

	public void start() throws Exception {

		FtpConfig ftpConfig = new FtpConfig();
		ftpConfig.setRemoteDir(messageSource.getMessage("ftp.remoteDir", null, Locale.KOREA));
		ftpConfig.setLocalDir(messageSource.getMessage("ftp.localDir", null, Locale.KOREA));
		
		String tmp = messageSource.getMessage("ftp.ip", null, Locale.KOREA);
		if(tmp.indexOf(",") > -1) {
			String[] tmp2 = tmp.split("\\,");
			ftpConfig.setFtpIp(tmp2[0]);
			ftpConfig.setFtpIp1(tmp2[0]);
			ftpConfig.setFtpIp2(tmp2[1]);
		} else {
			ftpConfig.setFtpIp(messageSource.getMessage("ftp.ip", null, Locale.KOREA));
			ftpConfig.setFtpIp1(messageSource.getMessage("ftp.ip", null, Locale.KOREA));
		}
		ACTIVE_IP = ftpConfig.getFtpIp1();
		
		ftpConfig.setFtpUser(messageSource.getMessage("ftp.usr", null, Locale.KOREA));
		ftpConfig.setFtpPwd(messageSource.getMessage("ftp.pwd", null, Locale.KOREA));

		FtpConfig copyConfig = null;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("eqId", "FTP%");
		params.put("useYn", "Y");

		List<EqTbl> eqTbls = transferService.findEq(params);

		int ftpCount = 1;
		if(eqTbls.isEmpty()) {

			params.put("eqGb", "D");
			String ftpDownNo = messageSource.getMessage("ftp.down.no", null, Locale.KOREA);

			EqTbl eqTbl = null;
			for(int i=ftpCount; i<=Integer.valueOf(ftpDownNo); i++) {
				eqTbl = new EqTbl();
				eqTbl.setEqId("FTP"+StringUtil.padLeft(String.valueOf(i), "0", 2));
				eqTbl.setEqGb("D");
				eqTbl.setEqIp(ftpConfig.getFtpIp());
				eqTbl.setEqNm("Download "+eqTbl.getEqId());
				eqTbl.setUseYn("Y");
				transferService.insertEq(eqTbl);

				ftpCount ++;

				ftpConfig.setEqId(eqTbl.getEqId());
				ftpConfig.setEqGb(eqTbl.getEqGb());

				copyConfig = new FtpConfig();
				Utility.cloneObj(copyConfig, ftpConfig);

				downPool.execute(new TransferDown(copyConfig));
			}
		} else {
			for(EqTbl eqTbl : eqTbls) {
				ftpConfig.setEqId(eqTbl.getEqId());
				ftpConfig.setEqGb(eqTbl.getEqGb());

				copyConfig = new FtpConfig();
				Utility.cloneObj(copyConfig, ftpConfig);

				downPool.execute(new TransferDown(copyConfig));
			}
		}

		eqTbls = null;
		params.put("eqId", "FTP%");
		params.put("eqGb", "U");
		eqTbls = transferService.findEq(params);
		if(eqTbls.isEmpty()) {
			String ftpUpNo = messageSource.getMessage("ftp.up.no", null, Locale.KOREA);

			EqTbl eqTbl = null;
			for(int i=ftpCount; i<(ftpCount+Integer.valueOf(ftpUpNo)); i++) {
				eqTbl = new EqTbl();
				eqTbl.setEqId("FTP"+StringUtil.padLeft(String.valueOf(i), "0", 2));
				eqTbl.setEqGb("U");
				eqTbl.setEqIp(ftpConfig.getFtpIp());
				eqTbl.setEqNm("Upload "+eqTbl.getEqId());
				eqTbl.setUseYn("Y");
				transferService.insertEq(eqTbl);

				ftpConfig.setEqId(eqTbl.getEqId());
				ftpConfig.setEqGb(eqTbl.getEqGb());

				copyConfig = new FtpConfig();
				Utility.cloneObj(copyConfig, ftpConfig);

				putPool.execute(new TransferUpload(copyConfig));
			}
		} else {
			for(EqTbl eqTbl : eqTbls) {
				ftpConfig.setEqId(eqTbl.getEqId());
				ftpConfig.setEqGb(eqTbl.getEqGb());

				copyConfig = new FtpConfig();
				Utility.cloneObj(copyConfig, ftpConfig);

				putPool.execute(new TransferUpload(copyConfig));
			}
		}

		// 현재 Queue size를 계산하여 job을 분배한다.
		jobReq.execute(new NotiThread());
		
		// CMS에 상태를 전달할 인터페이스를 연결한다.
		navigator = (Navigator)jaxWsProxyFactoryBean.create();
		
		// CMS에 상태를 전달할 쓰레드 실행
		hutReq.execute(new HutbitThread());
	}
	
	class HutbitThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					Iterator<String> it = eqIds.keySet().iterator();
					StringBuffer xml = new StringBuffer();
					xml.append("<status>");
					while(it.hasNext()) {
						String eqId = it.next();
						String state = eqIds.get(eqId);
						String ctId = "";
						if(state.indexOf(",") > -1) {
							ctId = state.split(",")[0];
							state = state.split(",")[1];
						}
						
						xml.append("<eq><eq_id>"+eqId+"</eq_id><job_id>"+ctId+"</job_id><state>"+state+"</state></eq>");
					}
					xml.append("</status>");
					navigator.saveEqStatus(xml.toString());
				}  catch (Exception e) {
					logger.error("[FTP Control] NotiThread Error - "+e.getMessage());
				}
				try {
					Thread.sleep(ServiceConstants.FTP_THREAD_TIME);
				} catch (Exception e) {}
			}
		}
		
	}

	/**
	 * <pre>
	 * jobQueue의 등록된 건수가 2건 이하일 경우에 DB에서 전송이 필요한 데이타를 조회한다.
	 * </pre>
	 * @author Administrator
	 *
	 */
	class NotiThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					//Upload queue 조회. queue는 최대 2개까지만 쌓아놓도록 한다.
					if(logger.isDebugEnabled()) {
						logger.debug("empty: "+putQueue.isEmpty()+", size: "+putQueue.size());
					}
					if(putQueue.isEmpty() || putQueue.size() < (ServiceConstants.FTP_POOL_SIZE)) {
						List<Transfer> putList = transferService.findTransfer("U", putQueue.size());
						if(logger.isDebugEnabled()) {
							logger.debug("putList['U'] size : "+putList.size());
						}
						for(Transfer transfer : putList) {
							putQueue.put(transfer);
							transfer.setStatus("Q");
							statuService.saveStatus(transfer);
							//Thread.sleep(500);
							if(logger.isDebugEnabled()) {
								logger.debug(transfer.getTfCd()+", change status - 'Q'");
							}
						}
					}
					if(logger.isDebugEnabled()) {
						logger.debug("putQueue size : "+putQueue.size());
					}

					
					//Download queue 조회. queue는 최대 2개까지만 쌓아놓도록 한다.
					if(downQueue.isEmpty() || downQueue.size() < (ServiceConstants.FTP_POOL_SIZE)) {
						List<Transfer> downList = transferService.findTransfer("D", downQueue.size());
						for(Transfer transfer : downList) {
							downQueue.put(transfer);
							transfer.setStatus("Q");
							statuService.saveStatus(transfer);
							Thread.sleep(500);
						}
					}
					if(logger.isDebugEnabled()) {
						logger.debug("downQueue size : "+downQueue.size());
					}
				} catch (Exception e) {
					logger.error("[FTP Control] NotiThread Error - "+e.getMessage());
				}
				try {
					Thread.sleep(ServiceConstants.FTP_THREAD_TIME);
				} catch (Exception e) {}
				
			}
		}
	}

	class TransferUpload implements Runnable {
		private FtpConfig ftpConfig;
		public TransferUpload(FtpConfig ftpConfig) {
			this.ftpConfig = ftpConfig;
		}
		@Override
		public void run() {

			while(true) {
				Transfer transfer = null;
				try {
					eqIds.put(ftpConfig.getEqId(), "I");
					transfer = putQueue.take();
					if(logger.isDebugEnabled()) {
						logger.debug("################ ftp put data ################");
						logger.debug("eq_id : "+ftpConfig.getEqId());
						logger.debug("ct_id : "+transfer.getCtId());
						logger.debug("tf_id : "+transfer.getTfId());
						logger.debug("tf_cd : "+transfer.getTfCd());
						logger.debug("fl_path: "+transfer.getFlPath());
					}
					
					eqIds.put(ftpConfig.getEqId(), transfer.getCtId()+",W");
					
					ftpTransferService.uploadFile(transfer, ftpConfig);
				} catch (Exception e) {
					logger.error("ftp upload Error!!", e);
					try {
						transfer.setStatus("E");
						statuService.saveStatus(transfer);
					} catch (Exception e2) {
						logger.error("Transfer Statues Update Error - "+e2.getMessage(), e2);
					}
				}

				try {
					Thread.sleep(100L);
				} catch (Exception e) {}
			}

		}
	}

	class TransferDown implements Runnable {

		private FtpConfig ftpConfig;
		public TransferDown(FtpConfig ftpConfig) {
			this.ftpConfig = ftpConfig;
		}

		@Override
		public void run() {
			while(true) {
				Transfer transfer = null;
				try {
					eqIds.put(ftpConfig.getEqId(), "I");
					transfer = downQueue.take();
					if(logger.isDebugEnabled()) {
						logger.debug("################ ftp down data ################");
						logger.debug("eq_id : "+ftpConfig.getEqId());
						logger.debug("ct_id : "+transfer.getCtId());
						logger.debug("tf_id : "+transfer.getTfId());
						logger.debug("tf_cd : "+transfer.getTfCd());
						logger.debug("fl_path: "+transfer.getFlPath());
					}
					
					eqIds.put(ftpConfig.getEqId(), transfer.getCtId()+",W");
					
					//ftpStreamTransferService.downloadFile(transfer, ftpConfig);
					ftpTransferService.downloadFile(transfer, ftpConfig);
				} catch (Exception e) {
					logger.error("ftp download Error!!", e);
					try {
						transfer.setStatus("E");
						statuService.saveStatus(transfer);
					} catch (Exception e2) {
						logger.error("Transfer Statues Update Error - "+e2.getMessage(), e2);
					}
				}

				try {
					Thread.sleep(100L);
				} catch (Exception e) {}
			}
		}
	}

	public void stop() throws Exception {
		try {
			if(!putPool.isShutdown()) {
				putPool.shutdownNow();
				if(logger.isInfoEnabled()) {
					logger.info("PutPool Thread shutdown now!!");
				}
			}
			if(!downPool.isShutdown()) {
				downPool.shutdownNow();
				if(logger.isInfoEnabled()) {
					logger.info("DownPool Thread shutdown now!!");
				}
			}
			if(!jobReq.isShutdown()) {
				jobReq.shutdownNow();
				if(logger.isInfoEnabled()) {
					logger.info("JobReq Thread shutdown now!!");
				}
			}
			if(!hutReq.isShutdown()) {
				hutReq.shutdownNow();
				if(logger.isInfoEnabled()) {
					logger.info("HutReq Thread shutdown now!!");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <pre>
	 * Queue 등록시 요청시간을 비교해서 순위를 정한다.
	 * 요청시간은 사용자별 삭제 요청시간이다.
	 * </pre>
	 * @return
	 */
	private static Comparator<? super Transfer> getCompare() {
		return new Comparator<Transfer>() {
			public int compare(Transfer d1, Transfer d2) {
				if(d1.getPriors() == d2.getPriors()) {
					if(d1.getRegDtm().getTime() < d2.getRegDtm().getTime()) return -1;
					else return 1;
				} else {
					if(d1.getPriors() < d2.getPriors()) return -1;
					else return 1;
				}
			}
		};
	}
}
