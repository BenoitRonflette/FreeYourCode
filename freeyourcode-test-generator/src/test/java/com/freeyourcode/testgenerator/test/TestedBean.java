package com.freeyourcode.testgenerator.test;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class TestedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String libelle;
	
	private int value;
	
	private Integer valueObject;
	
	private TestedSubBean subBean;
	
	private List<Object> list;
	
	private Set<Object> set;
	
	public TestedBean() {
	}
	
	public TestedBean(String libelle, int value, Integer valueObject) {
		this(libelle, value, valueObject, null);
	}
	
	public TestedBean(String libelle, int value, Integer valueObject,TestedSubBean subBean) {
		this(libelle, value, valueObject, subBean, null);
	}
	
	public TestedBean(String libelle, int value, Integer valueObject,TestedSubBean subBean, Set<Object> set) {
		super();
		this.libelle = libelle;
		this.value = value;
		this.valueObject = valueObject;
		this.subBean = subBean;
		this.set = set;
	}
	
	

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((libelle == null) ? 0 : libelle.hashCode());
		result = prime * result + ((list == null) ? 0 : list.hashCode());
		result = prime * result + ((subBean == null) ? 0 : subBean.hashCode());
		result = prime * result + value;
		result = prime * result
				+ ((valueObject == null) ? 0 : valueObject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestedBean other = (TestedBean) obj;
		if (libelle == null) {
			if (other.libelle != null)
				return false;
		} else if (!libelle.equals(other.libelle))
			return false;
		if (list == null) {
			if (other.list != null)
				return false;
		} else if (!list.equals(other.list))
			return false;
		if (subBean == null) {
			if (other.subBean != null)
				return false;
		} else if (!subBean.equals(other.subBean))
			return false;
		if (value != other.value)
			return false;
		if (valueObject == null) {
			if (other.valueObject != null)
				return false;
		} else if (!valueObject.equals(other.valueObject))
			return false;
		return true;
	}
	
	

}
