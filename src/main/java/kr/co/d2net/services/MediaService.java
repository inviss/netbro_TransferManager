package kr.co.d2net.services;

import java.util.List;

import kr.co.d2net.commons.dto.ContentInstTbl;
import kr.co.d2net.commons.exceptions.ServiceException;

import org.springframework.transaction.annotation.Transactional;

public interface MediaService {
	public List<ContentInstTbl> findRPWmvCopy(String raceId) throws ServiceException;
	@Transactional
	public void updateContentInst(ContentInstTbl contentInstTbl) throws ServiceException;
}
