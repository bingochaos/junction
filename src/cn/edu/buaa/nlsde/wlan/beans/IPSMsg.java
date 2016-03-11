/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.beans;

import java.util.List;

/**
 * { head:{version: 1, major_v:1, minor_v:0} Body:[ data:{ mac: ””, z: Map Id,
 * //尽可能统一 p: {x,y, time} , r: m //2~5m c: 0 //坐标系表示0:m, 1:84坐标 st: //发送时间 },
 * ...] }
 *
 * @author gaojie
 */
public class IPSMsg {

	private Head head;
	private Body body;

	public IPSMsg(int version, Origin origin) {
		this.head = new Head(version, origin);
	}

	public class Body {

		private int dataType;
		private List<IPSData> data;

		public Body(int dataType) {
			this.dataType = dataType;
		}

		public int getDataType() {
			return dataType;
		}

		public void setDataType(int dataType) {
			this.dataType = dataType;
		}

		public Object getData() {
			return data;
		}

		public void setData(List<IPSData> data) {
			this.data = data;
		}

	}

	public enum Origin {
		PHONE, AP
	}

	public class Head {

		private int version;
		private Origin origin = Origin.AP;

		public Origin getOrigin() {
			return origin;
		}

		public void setOrigin(Origin origin) {
			this.origin = origin;
		}

		private Head(int version, Origin origin) {
			this.version = version;
			this.origin = origin;
		}

		public int getVersion() {
			return version;
		}

		public void setVersion(int version) {
			this.version = version;
		}
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

}
