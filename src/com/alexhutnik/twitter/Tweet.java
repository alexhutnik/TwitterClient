package com.alexhutnik.twitter;

import java.io.Serializable;

public class Tweet implements Serializable {
	private String name;
	private String text;
	
	public Tweet (String name, String text){
		this.name = name;
		this.text = text;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
