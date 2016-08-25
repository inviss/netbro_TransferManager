package kr.co.d2net.commons.dto;

import java.sql.Timestamp;

public class Transfer {
	
	private Long tfId;
	private String status;
	private Integer progress;
	private Integer recount;
	private Timestamp regDtm;
	private String regUsrid;
	private Timestamp modDtm;
	private String modUsrid;
	private String useYn;
	private Integer priors;
	private String camCd;
	private String ctId;
	private String tfGb;
	private String ctNm;
	private String flPath;
	private Long flSize;
	private String raceId;
	private String raceCd;
	private Integer raceNo;
	private String eqId;
	private String tfCd;
	private String orgPath;

	public String getOrgPath() {
		return orgPath;
	}
	public void setOrgPath(String orgPath) {
		this.orgPath = orgPath;
	}
	public String getTfCd() {
		return tfCd;
	}
	public void setTfCd(String tfCd) {
		this.tfCd = tfCd;
	}
	public String getEqId() {
		return eqId;
	}
	public void setEqId(String eqId) {
		this.eqId = eqId;
	}
	public String getTfGb() {
		return tfGb;
	}
	public void setTfGb(String tfGb) {
		this.tfGb = tfGb;
	}
	public Long getFlSize() {
		return flSize;
	}
	public void setFlSize(Long flSize) {
		this.flSize = flSize;
	}
	public String getRaceId() {
		return raceId;
	}
	public void setRaceId(String raceId) {
		this.raceId = raceId;
	}
	public String getRaceCd() {
		return raceCd;
	}
	public void setRaceCd(String raceCd) {
		this.raceCd = raceCd;
	}
	public Integer getRaceNo() {
		return raceNo;
	}
	public void setRaceNo(Integer raceNo) {
		this.raceNo = raceNo;
	}
	public String getCtNm() {
		return ctNm;
	}
	public void setCtNm(String ctNm) {
		this.ctNm = ctNm;
	}
	public String getFlPath() {
		return flPath;
	}
	public void setFlPath(String flPath) {
		this.flPath = flPath;
	}
	public String getCtId() {
		return ctId;
	}
	public void setCtId(String ctId) {
		this.ctId = ctId;
	}
	public Long getTfId() {
		return tfId;
	}
	public void setTfId(Long tfId) {
		this.tfId = tfId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getProgress() {
		return progress;
	}
	public void setProgress(Integer progress) {
		this.progress = progress;
	}
	public Integer getRecount() {
		return recount;
	}
	public void setRecount(Integer recount) {
		this.recount = recount;
	}
	public Timestamp getRegDtm() {
		return regDtm;
	}
	public void setRegDtm(Timestamp regDtm) {
		this.regDtm = regDtm;
	}
	public String getRegUsrid() {
		return regUsrid;
	}
	public void setRegUsrid(String regUsrid) {
		this.regUsrid = regUsrid;
	}
	public Timestamp getModDtm() {
		return modDtm;
	}
	public void setModDtm(Timestamp modDtm) {
		this.modDtm = modDtm;
	}
	public String getModUsrid() {
		return modUsrid;
	}
	public void setModUsrid(String modUsrid) {
		this.modUsrid = modUsrid;
	}
	public String getUseYn() {
		return useYn;
	}
	public void setUseYn(String useYn) {
		this.useYn = useYn;
	}
	public Integer getPriors() {
		return priors;
	}
	public void setPriors(Integer priors) {
		this.priors = priors;
	}
	public String getCamCd() {
		return camCd;
	}
	public void setCamCd(String camCd) {
		this.camCd = camCd;
	}
	
}
