package com.sbs.das.web;

import java.rmi.RemoteException;

import javax.jws.WebService;

@WebService
public interface Nevigator {
	public String schedulerForceExecute(String xml) throws RemoteException;
	
	public String archiveStatus(String xml) throws RemoteException;
	
	public String addClipInfoService(String xml) throws RemoteException;
	
	public String archiveService(String xml) throws RemoteException;
	
	public String subAddClipService(String xml) throws RemoteException;
}
