package kr.co.d2net.ftp;

import java.io.File;
import java.io.FileOutputStream;

import kr.co.d2net.commons.dto.FtpConfig;
import kr.co.d2net.commons.dto.Transfer;
import kr.co.d2net.services.StatuService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.enterprisedt.net.ftp.FTPInputStream;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.FileTransferInputStream;

public class FtpStreamTransferImpl implements FtpTransferService {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private StatuService statuService;
	
	@Override
	public void uploadFile(Transfer transfer, FtpConfig ftpConfig) {
		
	}

	@Override
	public void downloadFile(Transfer transfer, FtpConfig ftpConfig) {
		FileTransferClient ftp = null;
		try {
			ftp = new FileTransferClient();
			ftp.setRemoteHost(ftpConfig.getFtpIp());
			ftp.setUserName(ftpConfig.getFtpUser());
			ftp.setPassword(ftpConfig.getFtpPwd());

			ftp.connect();
			if(logger.isDebugEnabled()) {
				logger.debug("[download] ftp conneted!! - ip:"+ftpConfig.getFtpIp()+", user: "+ftpConfig.getFtpId());
			}
			
			TransferAdapter listener = new TransferAdapter(transfer, statuService);
			ftp.setEventListener(listener);
			
			ftp.changeDirectory(ftpConfig.getRemoteDir());
			ftp.setContentType(FTPTransferType.BINARY);
			
			//ftp.getAdvancedSettings().setTransferBufferSize(10240);
			ftp.getAdvancedSettings().setTransferNotifyInterval(1000);
			//ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);

			String toDir = ftpConfig.getLocalDir() + transfer.getFlPath();
			transfer.setFlPath(toDir);
			transfer.setEqId(ftpConfig.getEqId());

			// 2_20110819_01_0_01
			//long fileSize = ftp.getSize(transfer.getCtId());
			long fileSize = ftp.getSize(transfer.getTfCd());
			transfer.setFlSize(fileSize);

			File f = new File(toDir);
			if(!f.exists()) f.mkdirs();

			if(logger.isInfoEnabled()){
				logger.info("ftp connected: ip: "+ftp.getRemoteHost()+
						", remote_dir: "+ftpConfig.getRemoteDir()+
						", local_dir: "+toDir);
			}

			FileOutputStream outputStream = new FileOutputStream(toDir + File.separator+transfer.getTfCd()+".mxf");
			FileTransferInputStream  inputStream = ftp.downloadStream(transfer.getTfCd());
			byte[] bytes = new byte[1024];
			try {
				int ch = 0;
				while ((ch = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, ch);
				}
			} catch (Exception e) {
				logger.error("stream down error", e);
			} finally {
				inputStream.close();
				outputStream.close();
			}
			//ftp.downloadFile(toDir + File.separator+transfer.getTfCd()+".mxf", transfer.getTfCd(), WriteMode.OVERWRITE);

			ftp.disconnect();
			if(logger.isInfoEnabled()){
				logger.info("Ftp Client Closed!! - FL_NM: "+transfer.getTfCd()+".mxf");
			}
			
			
			// MXF Cron 생성
			//fileCronControl.put(transfer);
			
		} catch (Exception e) {
			try {
				transfer.setStatus("E");
				//statuService.saveStatus(transfer);
			} catch (Exception e2) {
				logger.error("Download Transfer Error - "+e2.getMessage(), e2);
			}
		} finally {
			try {
				if(ftp.isConnected()) {
					ftp.disconnect();
				}
				ftp = null;
			} catch (Exception e2) {
			}
		}
	}

}
