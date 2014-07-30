package com.softtech.apps.callrecorder;

public class Contact {

	 //private variables
    String _id;
    String _name;
    String _phone_number;
    String _contact_id;
	public String get_contact_id() {
		return _contact_id;
	}

	public void set_contact_id(String _contact_id) {
		this._contact_id = _contact_id;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_phone_number() {
		return _phone_number;
	}

	public void set_phone_number(String _phone_number) {
		this._phone_number = _phone_number;
	}

	public Contact() {
		// TODO Auto-generated constructor stub
	}

	// constructor
    public Contact(String id, String name, String _phone_number, String _contact_id){
        this._id = id;
        this._name = name;
        this._phone_number = _phone_number;
        this._contact_id = _contact_id;
    }
     
    // constructor
    public Contact(String name, String _phone_number){
        this._name = name;
        this._phone_number = _phone_number;
    }
	
 // constructor
    public Contact(String name, String _phone_number, String _contact_id){
        this._name = name;
        this._phone_number = _phone_number;
        this._contact_id = _contact_id;
    }
	
}
