package com.fyxridd.lib.core.lang;

import java.io.Serializable;

public class LangUser implements Serializable{
	private String name;
	private String lang;
	public LangUser(){}

	public LangUser(String name, String lang) {
		this.name = name;
		this.lang = lang;
	}

    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		LangUser user = (LangUser) obj;
		return user.name.equals(name);
	}
}
