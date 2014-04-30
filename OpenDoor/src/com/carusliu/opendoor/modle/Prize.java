package com.carusliu.opendoor.modle;

import java.io.Serializable;

public class Prize implements Serializable{

	private String id;
	private String bigPicUrl;
	private String smallPicUrl;
	private String name;
	private String info;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBigPic() {
		return bigPicUrl;
	}
	public void setBigPic(String bigPic) {
		this.bigPicUrl = bigPic;
	}
	public String getSmallPic() {
		return smallPicUrl;
	}
	public void setSmallPic(String smallPic) {
		this.smallPicUrl = smallPic;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	
	
}
