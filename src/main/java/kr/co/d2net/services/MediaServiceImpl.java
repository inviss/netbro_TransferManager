package kr.co.d2net.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.d2net.commons.dto.ContentInstTbl;
import kr.co.d2net.commons.exceptions.ServiceException;
import kr.co.d2net.dao.MediaDao;

@Service(value="mediaService")
public class MediaServiceImpl implements MediaService {

	@Autowired
	private MediaDao mediaDao;
	
	@Override
	public List<ContentInstTbl> findRPWmvCopy(String raceId)
			throws ServiceException {
		return mediaDao.findRPWmvCopy(raceId);
	}

	@Override
	public void updateContentInst(ContentInstTbl contentInstTbl)
			throws ServiceException {

		mediaDao.updateContentInst(contentInstTbl);
	}

}
