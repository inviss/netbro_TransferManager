package test;

import java.io.File;
import java.io.IOException;

import kr.co.d2net.ftp.FtpTransferControl;
import kr.co.d2net.ftp.TransferAdapter;

import org.apache.commons.lang.StringUtils;

import com.enterprisedt.net.ftp.FTPConnectMode;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.enterprisedt.net.ftp.FileTransferClient;
import com.enterprisedt.net.ftp.WriteMode;

public class FtpClientTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileTransferClient ftp = null;

		try {

			ftp = new FileTransferClient();
			ftp.setRemoteHost("10.1.0.5");
			ftp.setUserName("administrator");
			ftp.setPassword("adminGV!");

			try {
				ftp.connect();
			} catch (Exception e) {
				e.printStackTrace();
			}

			ftp.changeDirectory("/mxf/default");

			try {
				ftp.deleteFile("1_20150412_01_0_05");
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
			
			ftp.setContentType(FTPTransferType.BINARY);
			ftp.getAdvancedSettings().setTransferBufferSize(32768);
			ftp.getAdvancedSettings().setTransferNotifyInterval(1000);
			ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);
			ftp.getAdvancedSettings().setControlEncoding("euc-kr");


			String msg = ftp.uploadFile("X:/mp2/2015/04/12/1_20150412_01_0_05.mxf", "1_20150412_01_0_05", WriteMode.OVERWRITE);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(ftp != null) {
				try {
					ftp.disconnect();
				} catch (Exception e) {}
			}
		}

	}
}
