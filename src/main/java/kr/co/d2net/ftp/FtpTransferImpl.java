package kr.co.d2net.ftp;

import java.io.File;

import kr.co.d2net.commons.dto.FtpConfig;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.cron.FileCronControl;
import kr.co.d2net.services.StatuService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.WriteMode;

public class FtpTransferImpl implements FtpTransferService {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private StatuService statuService;
	@Autowired
	private FileCronControl fileCronControl;

	@Override
	public void downloadFile(Transfer transfer, FtpConfig ftpConfig) throws Exception {
		
		FileTransferClient ftp = null;
		try {
			ftp = new FileTransferClient();
			ftp.setRemoteHost(FtpTransferControl.ACTIVE_IP);
			ftp.setUserName(ftpConfig.getFtpUser());
			ftp.setPassword(ftpConfig.getFtpPwd());
			
			try {
				ftp.connect();
			} catch (Exception e) {
				if(StringUtils.isNotBlank(ftpConfig.getFtpIp2())) {
					if(FtpTransferControl.ACTIVE_IP.equals(ftpConfig.getFtpIp1())) {
						logger.info("ip change : ["+ftpConfig.getFtpIp1()+"] -> ["+ftpConfig.getFtpIp2()+"]");
						FtpTransferControl.ACTIVE_IP = ftpConfig.getFtpIp2();
					} else if(FtpTransferControl.ACTIVE_IP.equals(ftpConfig.getFtpIp2())) {
						logger.info("ip change : ["+ftpConfig.getFtpIp1()+"] -> ["+ftpConfig.getFtpIp1()+"]");
						FtpTransferControl.ACTIVE_IP = ftpConfig.getFtpIp1();
					}
					ftp.setRemoteHost(FtpTransferControl.ACTIVE_IP);
					ftp.connect();
				} else throw e;
			}
			
			if(logger.isDebugEnabled()) {
				logger.debug("[download] ftp conneted!! - ip:"+FtpTransferControl.ACTIVE_IP+", user: "+ftpConfig.getFtpUser()+", remoteDir: "+ftpConfig.getRemoteDir());
			}
			
			ftp.setContentType(FTPTransferType.BINARY);
			ftp.changeDirectory(ftpConfig.getRemoteDir());
			ftp.getAdvancedSettings().setTransferBufferSize(32768);
			ftp.getAdvancedSettings().setTransferNotifyInterval(1000);
			ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
			ftp.getAdvancedSettings().setControlEncoding("euc-kr");
			
			if(logger.isDebugEnabled()) {
				logger.debug("change dir : "+ftp.getRemoteDirectory());
				logger.debug("getLocalDir : "+ftpConfig.getLocalDir());
				logger.debug("getFlPath : "+transfer.getFlPath());
				logger.debug("getTfCd : "+transfer.getTfCd());
			}
			
			transfer.setTfCd(transfer.getTfCd().trim());
			
			String toDir = ftpConfig.getLocalDir() + transfer.getFlPath();
			transfer.setOrgPath(transfer.getFlPath());
			transfer.setFlPath(toDir);
			transfer.setEqId(ftpConfig.getEqId());

			// 2_20110819_01_0_01
			long fileSize = ftp.getSize(transfer.getTfCd());
			transfer.setFlSize(fileSize);
			if(logger.isDebugEnabled()) {
				logger.debug("fileSize : "+fileSize);
			}

			TransferAdapter listener = new TransferAdapter(transfer, statuService);
			ftp.setEventListener(listener);

			File f = new File(toDir);
			if(!f.exists()) f.mkdirs();

			if(logger.isInfoEnabled()){
				logger.info("ftp connected: ip: "+ftp.getRemoteHost()+
						", remote_dir: "+ftpConfig.getRemoteDir()+
						", local_dir: "+toDir);
			}

			ftp.downloadFile(toDir + File.separator+transfer.getTfCd()+".mxf", transfer.getTfCd(), WriteMode.OVERWRITE);

			transfer.setStatus("C");
			statuService.saveStatus(transfer);
			
			ftp.disconnect();
			if(logger.isInfoEnabled()){
				logger.info("Ftp Client Closed!! - FL_NM: "+transfer.getTfCd()+".mxf");
			}
			
			// MXF Cron 생성
			fileCronControl.put(transfer);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(ftp.isConnected()) {
					ftp.disconnect();
				}
				ftp = null;
			} catch (Exception e2) {}
		}
	}

	@Override
	public void uploadFile(Transfer transfer, FtpConfig ftpConfig) throws Exception {
		FileTransferClient ftp = null;

		try {

			if(logger.isDebugEnabled()) {
				logger.debug("[upload] ftp - ip:"+ftpConfig.getFtpIp()+", user: "+ftpConfig.getFtpUser()+", pass: "+ftpConfig.getFtpPwd());
			}
			ftp = new FileTransferClient();
			ftp.setRemoteHost(FtpTransferControl.ACTIVE_IP);
			ftp.setUserName(ftpConfig.getFtpUser());
			ftp.setPassword(ftpConfig.getFtpPwd());
			
			try {
				ftp.connect();
			} catch (Exception e) {
				if(StringUtils.isNotBlank(ftpConfig.getFtpIp2())) {
					if(FtpTransferControl.ACTIVE_IP.equals(ftpConfig.getFtpIp1())) {
						FtpTransferControl.ACTIVE_IP = ftpConfig.getFtpIp2();
					} else if(FtpTransferControl.ACTIVE_IP.equals(ftpConfig.getFtpIp2())) {
						FtpTransferControl.ACTIVE_IP = ftpConfig.getFtpIp1();
					}
					ftp.setRemoteHost(FtpTransferControl.ACTIVE_IP);
					ftp.connect();
				} else throw e;
			}
			
			String tfCd = transfer.getTfCd().trim();

			if(!transfer.getFlPath().startsWith("/")) {
				transfer.setFlPath("/"+transfer.getFlPath());
			}
			if(!transfer.getFlPath().endsWith("/")) {
				transfer.setFlPath(transfer.getFlPath()+"/");
			}
			String fromDir = ftpConfig.getLocalDir() + transfer.getFlPath();
			transfer.setFlPath(ftpConfig.getRemoteDir());
			transfer.setEqId(ftpConfig.getEqId());

			//File f = new File(fromDir+File.separator+transfer.getCtId()+".mxf");
			File f = new File(fromDir+tfCd+".mxf");
			if(!f.exists()) {
				logger.error("MXF File Not Found! - "+f.getAbsolutePath());
				transfer.setStatus("E");
			} else {
				transfer.setFlSize(f.length());

				if(StringUtils.isNotBlank(ftpConfig.getRemoteDir()))
					ftp.changeDirectory(ftpConfig.getRemoteDir());
				
				ftp.setContentType(FTPTransferType.BINARY);
				ftp.getAdvancedSettings().setTransferBufferSize(32768);
				ftp.getAdvancedSettings().setTransferNotifyInterval(1000);
				ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.ACTIVE);
				ftp.getAdvancedSettings().setControlEncoding("euc-kr");

				try {
					if(logger.isDebugEnabled()) {
						logger.debug("remote exist yn:"+ftp.exists(transfer.getTfCd()));
					}
					ftp.deleteFile(transfer.getTfCd());
				} catch (Exception e) {
					logger.error("remote delete error", e.getMessage());
				}
				
				if(logger.isDebugEnabled()) {
					logger.debug("from:"+f.getAbsolutePath()+", remoteDir: "+ftpConfig.getRemoteDir()+", tfId: "+tfCd);
				}
				
				TransferAdapter listener = new TransferAdapter(transfer, statuService);
				ftp.setEventListener(listener);
				
				String msg = ftp.uploadFile(f.getAbsolutePath(), tfCd, WriteMode.OVERWRITE);
				transfer.setStatus("C");
				if(logger.isDebugEnabled()) {
					logger.debug("ftp return msg : "+msg);
				}
			}
			
			transfer.setTfGb("U");
			statuService.saveStatus(transfer);

			ftp.disconnect();
		} catch (Exception e) {
			logger.error("ftp upload error", e);
			throw e;
		} finally {
			try {
				if(ftp.isConnected()) {
					ftp.disconnect();
				}
				ftp = null;
			} catch (Exception e2) {}
		}
	}

}
