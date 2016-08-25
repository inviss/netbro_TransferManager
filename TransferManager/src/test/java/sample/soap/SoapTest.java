package sample.soap;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sbs.das.web.Nevigator;

public class SoapTest extends BaseConfig{
	
	@Autowired
	private JaxWsProxyFactoryBean jaxWsProxyFactoryBean;

	@Test
	public void soapConnTest() {
		try {
			
			//String xml = FileUtils.readFileToString(new File("D:/tmp/archive2.xml"), "utf-8");
			Nevigator navigator = (Nevigator)jaxWsProxyFactoryBean.create();
			System.out.println(navigator.schedulerForceExecute("<das><info><req_method>scrap</req_method><limit_day>20121126</limit_day><co_cd>S</co_cd></info></das>"));
			
			//System.out.println(navigator.addClipInfoService(xml));
			
			//System.out.println(navigator.archiveStatus(xml));
			
			/*File f = new File("D:/tmp/archive");
			File[] xmlFiles = f.listFiles(new UserFileFilter());
			
			for(File fx : xmlFiles) {
				String xml = FileUtils.readFileToString(fx, "utf-8");
				navigator.archiveService(xml);
				fx.renameTo(new File(fx.getAbsolutePath()+".bak"));
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class UserFileFilter implements FileFilter {

		private final String[] useFileExtensions = new String[] {"xml"};

		public boolean accept(File file) {
			for (String extension : useFileExtensions) {
				if (file.getName().toLowerCase().endsWith(extension)) {
					return true;
				}
			}
			return false;
		}

	}
}
