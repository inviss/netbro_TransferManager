package kr.co.d2net.cron;

import java.io.File;
import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import kr.co.d2net.commons.ServiceConstants;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.commons.exceptions.XmlWriteException;
import kr.co.d2net.commons.utils.Utility;
import kr.co.d2net.commons.utils.XmlFileService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class FileCronControl {
	final Logger logger = LoggerFactory.getLogger(getClass());


	private ExecutorService cronCopy = Executors.newSingleThreadExecutor();
	private BlockingQueue<Transfer> cronQueue = new PriorityBlockingQueue<Transfer>(5, getCompare());
	
	@Autowired
	private XmlFileService xmlWriter;

	public void start() throws Exception {
		cronCopy.execute(new FileCronThread());
	}
	
	public void put(Transfer transfer) {
		try {
			cronQueue.put(transfer);
			
			if(!transfer.getOrgPath().endsWith("/")) transfer.setOrgPath(transfer.getOrgPath()+"/");
			
			// TC에서 작업할 정보 생성
			StringBuffer buffer = new StringBuffer();
			buffer.append(transfer.getOrgPath().replaceAll("\\/", "\\\\")+transfer.getTfCd()+".mxf"+"|");
			buffer.append(transfer.getOrgPath().replaceAll("\\/", "\\\\")+transfer.getTfCd()+".wmv");
			
			xmlWriter.StringToFile(buffer.toString(), ServiceConstants.TM_DIR, transfer.getTfCd()+".txt");
		} catch (InterruptedException e) {
			logger.error("FileCron put Error - "+e.getMessage());
		} catch (Exception e) {
			if(e instanceof XmlWriteException) {
				logger.error("FileCronControl put Error - "+e.getMessage());
			} else {
				logger.error("FileCronControl put Error - "+e.getMessage());
			}
		}
	}

	/**
	 * <pre>
	 * </pre>
	 * @author Administrator
	 *
	 */
	class FileCronThread implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					if(logger.isDebugEnabled()) {
						logger.debug("cronQueue size : "+cronQueue.size());
					}
					Transfer transfer = cronQueue.take();
					
					String fromDir = transfer.getFlPath() + File.separator + transfer.getTfCd()+".mxf";
					File fromFile = new File(fromDir);
					
					String toDir = ServiceConstants.CRON_DIR +transfer.getOrgPath().replace("/", "\\\\");
					File toFile = new File(toDir);
					if(toFile.exists())
						toFile.mkdirs();
					toFile = new File(toDir, transfer.getTfCd()+".mxf");
					
					// Copy
					FileUtils.copyFile(fromFile, toFile, false);
				} catch (Exception e) {
					logger.error("[File Cron Copy] CronThread Error", e);
				}
				try {
					Thread.sleep(ServiceConstants.CRON_THREAD_TIME);
				} catch (Exception e) {}
			}
		}

	}

	public void stop() throws Exception {
		try {
			if(!cronCopy.isShutdown()) {
				cronCopy.shutdownNow();
				if(logger.isInfoEnabled()) {
					logger.info("CronCopy Thread shutdown now!!");
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
