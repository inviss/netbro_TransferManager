package kr.co.d2net.commons.dto;

import java.sql.Timestamp;
import java.util.Date;

public class ContentInstTbl {
	
	private String ctId;
	private String ctNm;
	private String ctTyp;
	private String ctCla;
	private String rem;
	private Date vsDelDt;
	private Date csDelDt;
	private String ctiId;
	private String ctiFmt;
	private String vdQlty;
	private String useYn;
	private Integer bitRt;
	private Integer vdHresol;
	private Integer vdVresol;
	private String audioSampFrq;
	private String audioBdwt;
	private Long flSz;
	private String flNm;
	private String flPath;
	private String raceId;
	private String raceNm;
	private String raceCd;
	private Integer raceOrd;
	private String camCd;
	private Timestamp regDtm;
	private Integer pgmId;
	private String pgmNm;
	private String wmvYn;
	private String ctLeng;
	private String cpYn;
	private String drpFrmYn;
	private String som;
	private String eom;
	private String markIn;
	private String markOut;
	
	
	public String getDrpFrmYn() {
		return drpFrmYn;
	}
	public void setDrpFrmYn(String drpFrmYn) {
		this.drpFrmYn = drpFrmYn;
	}
	public String getSom() {
		return som;
	}
	public void setSom(String som) {
		this.som = som;
	}
	public String getEom() {
		return eom;
	}
	public void setEom(String eom) {
		this.eom = eom;
	}
	public String getMarkIn() {
		return markIn;
	}
	public void setMarkIn(String markIn) {
		this.markIn = markIn;
	}
	public String getMarkOut() {
		return markOut;
	}
	public void setMarkOut(String markOut) {
		this.markOut = markOut;
	}
	public String getCpYn() {
		return cpYn;
	}
	public void setCpYn(String cpYn) {
		this.cpYn = cpYn;
	}
	public String getCtLeng() {
		return ctLeng;
	}
	public void setCtLeng(String ctLeng) {
		this.ctLeng = ctLeng;
	}
	public String getWmvYn() {
		return wmvYn;
	}
	public void setWmvYn(String wmvYn) {
		this.wmvYn = wmvYn;
	}
	public String getRaceCd() {
		return raceCd;
	}
	public void setRaceCd(String raceCd) {
		this.raceCd = raceCd;
	}
	public Integer getRaceOrd() {
		return raceOrd;
	}
	public void setRaceOrd(Integer raceOrd) {
		this.raceOrd = raceOrd;
	}
	public String getRaceNm() {
		return raceNm;
	}
	public void setRaceNm(String raceNm) {
		this.raceNm = raceNm;
	}
	public Integer getPgmId() {
		return pgmId;
	}
	public void setPgmId(Integer pgmId) {
		this.pgmId = pgmId;
	}
	public String getPgmNm() {
		return pgmNm;
	}
	public void setPgmNm(String pgmNm) {
		this.pgmNm = pgmNm;
	}
	public Date getCsDelDt() {
		return csDelDt;
	}
	public void setCsDelDt(Date csDelDt) {
		this.csDelDt = csDelDt;
	}
	public String getRaceId() {
		return raceId;
	}
	public void setRaceId(String raceId) {
		this.raceId = raceId;
	}
	public String getCamCd() {
		return camCd;
	}
	public void setCamCd(String camCd) {
		this.camCd = camCd;
	}
	public Timestamp getRegDtm() {
		return regDtm;
	}
	public void setRegDtm(Timestamp regDtm) {
		this.regDtm = regDtm;
	}
	public String getCtNm() {
		return ctNm;
	}
	public void setCtNm(String ctNm) {
		this.ctNm = ctNm;
	}
	public String getCtTyp() {
		return ctTyp;
	}
	public void setCtTyp(String ctTyp) {
		this.ctTyp = ctTyp;
	}
	public String getCtCla() {
		return ctCla;
	}
	public void setCtCla(String ctCla) {
		this.ctCla = ctCla;
	}
	public String getRem() {
		return rem;
	}
	public void setRem(String rem) {
		this.rem = rem;
	}
	public Date getVsDelDt() {
		return vsDelDt;
	}
	public void setVsDelDt(Date vsDelDt) {
		this.vsDelDt = vsDelDt;
	}
	public String getCtId() {
		return ctId;
	}
	public void setCtId(String ctId) {
		this.ctId = ctId;
	}
	public String getCtiId() {
		return ctiId;
	}
	public void setCtiId(String ctiId) {
		this.ctiId = ctiId;
	}
	public String getCtiFmt() {
		return ctiFmt;
	}
	public void setCtiFmt(String ctiFmt) {
		this.ctiFmt = ctiFmt;
	}
	public String getVdQlty() {
		return vdQlty;
	}
	public void setVdQlty(String vdQlty) {
		this.vdQlty = vdQlty;
	}
	public String getUseYn() {
		return useYn;
	}
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}
	public Integer getBitRt() {
		return bitRt;
	}
	public void setBitRt(Integer bitRt) {
		this.bitRt = bitRt;
	}
	public Integer getVdHresol() {
		return vdHresol;
	}
	public void setVdHresol(Integer vdHresol) {
		this.vdHresol = vdHresol;
	}
	public Integer getVdVresol() {
		return vdVresol;
	}
	public void setVdVresol(Integer vdVresol) {
		this.vdVresol = vdVresol;
	}
	public String getAudioSampFrq() {
		return audioSampFrq;
	}
	public void setAudioSampFrq(String audioSampFrq) {
		this.audioSampFrq = audioSampFrq;
	}
	public String getAudioBdwt() {
		return audioBdwt;
	}
	public void setAudioBdwt(String audioBdwt) {
		this.audioBdwt = audioBdwt;
	}
	public Long getFlSz() {
		return flSz;
	}
	public void setFlSz(Long flSz) {
		this.flSz = flSz;
	}
	public String getFlNm() {
		return flNm;
	}
	public void setFlNm(String flNm) {
		this.flNm = flNm;
	}
	public String getFlPath() {
		return flPath;
	}
	public void setFlPath(String flPath) {
		this.flPath = flPath;
	}
	
}