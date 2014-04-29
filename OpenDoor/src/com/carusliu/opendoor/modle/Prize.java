package com.carusliu.opendoor.modle;

import android.graphics.Bitmap;

public class Prize {

	private String id;
	private Bitmap bigPic;
	private Bitmap smallPic;
	private String name;
	private String info;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Bitmap getBigPic() {
		return bigPic;
	}
	public void setBigPic(Bitmap bigPic) {
		this.bigPic = bigPic;
	}
	public Bitmap getSmallPic() {
		return smallPic;
	}
	public void setSmallPic(Bitmap smallPic) {
		this.smallPic = smallPic;
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
