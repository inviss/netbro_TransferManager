package kr.co.d2net.soap;

import javax.jws.WebService;

@WebService
public interface Navigator {
	/**
	 * <pre>
	 * 
	 * </pre>
	 * @param xml
	 * @return
	 */
	public String saveEqStatus(String xml);
}
