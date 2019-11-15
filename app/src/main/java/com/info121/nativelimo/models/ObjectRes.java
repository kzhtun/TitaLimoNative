package com.info121.nativelimo.models;

public class ObjectRes{
	private Object jobcount;
	private Object jobs;
	private String responsemessage;
	private String status;
	private String token;

	public void setJobcount(Object jobcount){
		this.jobcount = jobcount;
	}

	public Object getJobcount(){
		return jobcount;
	}

	public void setJobs(Object jobs){
		this.jobs = jobs;
	}

	public Object getJobs(){
		return jobs;
	}

	public void setResponsemessage(String responsemessage){
		this.responsemessage = responsemessage;
	}

	public String getResponsemessage(){
		return responsemessage;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setToken(String token){
		this.token = token;
	}

	public String getToken(){
		return token;
	}

	@Override
 	public String toString(){
		return 
			"ObjectRes{" + 
			"jobcount = '" + jobcount + '\'' + 
			",jobs = '" + jobs + '\'' + 
			",responsemessage = '" + responsemessage + '\'' + 
			",status = '" + status + '\'' + 
			",token = '" + token + '\'' + 
			"}";
		}
}
