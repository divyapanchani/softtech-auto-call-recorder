package com.softtech.apps.callrecorder;

public class Config {
	int _id;
	int _value = 0;
	int _keyword;
	public Config(int _id, int _value) {
		super();
		this._id = _id;
		this._value = _value;
	}
	public Config(int _id, int _value, int _keyword) {
		super();
		this._id = _id;
		this._value = _value;
		this._keyword = _keyword;
	}
	public int get_keyword() {
		return _keyword;
	}
	public void set_keyword(int _keyword) {
		this._keyword = _keyword;
	}
	public Config() {
		super();
		// TODO Auto-generated constructor stub
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public int get_value() {
		return _value;
	}
	public void set_value(int _value) {
		this._value = _value;
	}
	
}
