package kr.co.d2net.cron;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kr.co.d2net.commons.dto.ContentInstTbl;
import kr.co.d2net.services.MediaService;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class RepWmvCopy {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private ExecutorService wmvExec = Executors.newSingleThreadExecutor();
	
	@Autowired
	private MediaService mediaService;
	@Autowired
	private MessageSource messageSource;
	
	public void start() throws Exception {
		wmvExec.execute(new WMVCopyThread());
		if(logger.isInfoEnabled()) {
			logger.info("WMV Copy Thread Start!!");
		}
	}
	
	class WMVCopyThread implements Runnable {
		
		@Override
		public void run() {
			SimpleDateFormat dateForm = new SimpleDateFormat("yyyyMMdd");
			Set<String> ids = new HashSet<String>();
			while(true) {
				Calendar aDate = Calendar.getInstance();
				// 테스트를 위해 임의로 날짜를 지
				//aDate.set(2010, 7, 8);
				
				int week = aDate.get(Calendar.DAY_OF_WEEK);
				
				// 경기가 있는 토, 일요일에만 복사를 진행한다.
				if(week == 1 || week == 7) {
					// 경기가 오후 6시정도에 끝나기때문에 오후 7시에 실행한다.
					if(aDate.get(Calendar.AM_PM) == 1 && aDate.get(Calendar.HOUR) == 7) {

						// 당일의 복사가 진행안된 경기정보를 조회하여 복사한다.
						String kraPlace = messageSource.getMessage("application.kra.place", null, Locale.KOREA);
						String mainDrive = messageSource.getMessage("main.drive", null, Locale.KOREA);
						String repWmvDrive = messageSource.getMessage("rep.wmv.drive", null, Locale.KOREA);
						String repWmvIds = messageSource.getMessage("rep.wmv.ids", null, Locale.KOREA);
						
						// 복사할 채널을 properties 에서 읽어온다.
						String[] wmvIds = repWmvIds.split(",");
						ids.clear();
						for(String id : wmvIds) {
							ids.add(id);
						}
						
						try {
							List<ContentInstTbl> contentInstTbls = mediaService.findRPWmvCopy(kraPlace+"_"+dateForm.format(aDate.getTime()));
							if(logger.isDebugEnabled()) {
								logger.debug("race_id : "+kraPlace+"_"+dateForm.format(aDate.getTime())+", row count: "+contentInstTbls.size());
							}
							for(ContentInstTbl contentInstTbl : contentInstTbls) {
								if(!ids.isEmpty() && ids.contains(contentInstTbl.getFlNm().substring(contentInstTbl.getFlNm().length() - 2))) {
									if(logger.isDebugEnabled()) {
										logger.debug(mainDrive+"mp4"+contentInstTbl.getFlPath()+contentInstTbl.getFlNm()+".wmv");
										logger.debug(repWmvDrive+contentInstTbl.getFlPath()+contentInstTbl.getFlNm()+".wmv");
									}
									File nas = new File(mainDrive+"mp4"+contentInstTbl.getFlPath()+contentInstTbl.getFlNm()+".wmv");
									File usb = new File(repWmvDrive+contentInstTbl.getFlPath()+contentInstTbl.getFlNm()+".wmv");

									// 이미 존재한다면 파일의 크기를 원본과 비교하여 다르다면 삭제하고 같다면 복사를 하지 않는다.
									if(usb.exists()) {
										if(nas.length() != usb.length() && nas.length() > usb.length() ) {
											FileUtils.forceDelete(usb);
										} else {
											contentInstTbl.setCpYn("Y");
											// 복사여부를 DB에 반영한다.
											mediaService.updateContentInst(contentInstTbl);
											continue;
										}
									}
									FileUtils.copyFile(nas, usb);
									
									contentInstTbl.setCpYn("Y");
									// 복사가 왼료되면 복사여부를 DB에 반영한다.
									mediaService.updateContentInst(contentInstTbl);
								}
							}
						} catch (Exception e) {
							logger.error("대표 WMV 복사 에러", e);
						}
					}
				}
				
				try {
					Thread.sleep(60000L);
				} catch (Exception e) {}
				
			}
		}
	}
	
	public void stop() throws Exception {
		if(!wmvExec.isShutdown()) {
			wmvExec.shutdownNow();
			if(logger.isInfoEnabled()) {
				logger.info("WMV Copy Thread shutdown now!!");
			}
		}
	}

}
