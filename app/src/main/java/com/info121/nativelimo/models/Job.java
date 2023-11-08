package com.info121.nativelimo.models;

import com.google.gson.annotations.SerializedName;

public class Job {
	@SerializedName("Customer_Tel")
	private String customerTel;

	@SerializedName("UsageDate")
	private String usageDate;

	@SerializedName("Destination")
	private String destination;

	@SerializedName("Customer")
	private String customer;

	@SerializedName("PickUp")
	private String pickUp;

	@SerializedName("Flight")
	private String flight;

	@SerializedName("VehicleType")
	private String vehicleType;

	@SerializedName("JobCat")
	private String jobCat;

	@SerializedName("ShowRemarks")
	private String showRemarks;

	@SerializedName("JobStatus")
	private String jobStatus;

	@SerializedName("ETA")
	private String eTA;

	@SerializedName("JobType")
	private String jobType;

	@SerializedName("ShowPhoto")
	private String showPhoto;

	@SerializedName("StatusOrder")
	private String statusOrder;

	@SerializedName("Remarks")
	private String remarks;

	@SerializedName("JobNo")
	private String jobNo;

	@SerializedName("PickUpTime")
	private String pickUpTime;

	@SerializedName("NoShowPhoto")
	private String noShowPhoto;

	@SerializedName("NoShowRemarks")
	private String noShowRemarks;

	@SerializedName("NoofAdult")
	private String noOfAdult;

	@SerializedName("NoofChild")
	private String noOfChild;

	@SerializedName("NoofInfant")
	private String noOfInfant;

	@SerializedName("BookNo")
	private String bookNo;

	@SerializedName("File1")
	private String file1;

	@SerializedName("FileNo")
	private String fileNo;

	@SerializedName("Location")
	private String location;

	@SerializedName("Remark")
	private String remark;

	@SerializedName("Updates")
	private String updates;

	@SerializedName("UAEType")
	private String uaeType ;

	@SerializedName("Staff")
	private String staff ;

	@SerializedName("CustomerCode")
	private String CustomerCode ;

	public void setCustomerTel(String customerTel){
		this.customerTel = customerTel;
	}

	public String getCustomerTel(){
		return customerTel;
	}

	public void setUsageDate(String usageDate){
		this.usageDate = usageDate;
	}

	public String getUsageDate(){
		return usageDate;
	}

	public void setDestination(String destination){
		this.destination = destination;
	}

	public String getDestination(){
		return destination;
	}

	public void setCustomer(String customer){
		this.customer = customer;
	}

	public String getCustomer(){
		return customer;
	}

	public void setPickUp(String pickUp){
		this.pickUp = pickUp;
	}

	public String getPickUp(){
		return pickUp;
	}

	public void setFlight(String flight){
		this.flight = flight;
	}

	public String getFlight(){
		return flight;
	}

	public void setVehicleType(String vehicleType){
		this.vehicleType = vehicleType;
	}

	public String getVehicleType(){
		return vehicleType;
	}

	public void setJobCat(String jobCat){
		this.jobCat = jobCat;
	}

	public String getJobCat(){
		return jobCat;
	}

	public void setShowRemarks(String showRemarks){
		this.showRemarks = showRemarks;
	}

	public String getShowRemarks(){
		return showRemarks;
	}

	public void setJobStatus(String jobStatus){
		this.jobStatus = jobStatus;
	}

	public String getJobStatus(){
		return jobStatus;
	}

	public void setETA(String eTA){
		this.eTA = eTA;
	}

	public String getETA(){
		return eTA;
	}

	public void setJobType(String jobType){
		this.jobType = jobType;
	}

	public String getJobType(){
		return jobType;
	}

	public void setShowPhoto(String showPhoto){
		this.showPhoto = showPhoto;
	}

	public String getShowPhoto(){
		return showPhoto;
	}

	public void setStatusOrder(String statusOrder){
		this.statusOrder = statusOrder;
	}

	public String getStatusOrder(){
		return statusOrder;
	}

	public void setRemarks(String remarks){
		this.remarks = remarks;
	}

	public String getRemarks(){
		return remarks;
	}

	public void setJobNo(String jobNo){
		this.jobNo = jobNo;
	}

	public String getJobNo(){
		return jobNo;
	}

	public void setPickUpTime(String pickUpTime){
		this.pickUpTime = pickUpTime;
	}

	public String getPickUpTime(){
		return pickUpTime;
	}

	public void setNoShowPhoto(String noShowPhoto){
		this.noShowPhoto = noShowPhoto;
	}

	public String getNoShowPhoto(){
		return noShowPhoto;
	}

	public void setNoShowRemarks(String noShowRemarks){
		this.noShowRemarks = noShowRemarks;
	}

	public String getNoShowRemarks(){
		return noShowRemarks;
	}

	public void setBookNo(String bookNo){
		this.bookNo = bookNo;
	}

	public String getBookNo(){
		return bookNo;
	}

	public void setFile1(String file1){
		this.file1 = file1;
	}

	public String getFile1(){
		return file1;
	}

	public void setLocation(String location){
		this.location = location;
	}

	public String getLocation(){
		return location;
	}

	public String geteTA() {
		return eTA;
	}

	public void seteTA(String eTA) {
		this.eTA = eTA;
	}

	public String getFileNo() {
		return fileNo;
	}

	public void setFileNo(String fileNo) {
		this.fileNo = fileNo;
	}

	public String getNoOfAdult() {
		return noOfAdult;
	}

	public void setNoOfAdult(String noOfAdult) {
		this.noOfAdult = noOfAdult;
	}

	public String getNoOfChild() {
		return noOfChild;
	}

	public void setNoOfChild(String noOfChild) {
		this.noOfChild = noOfChild;
	}

	public String getNoOfInfant() {
		return noOfInfant;
	}

	public void setNoOfInfant(String noOfInfant) {
		this.noOfInfant = noOfInfant;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUpdates() {
		return updates;
	}

	public void setUpdates(String updates) {
		this.updates = updates;
	}

	public String getUaeType() {
		return uaeType;
	}

	public void setUaeType(String uaeType) {
		this.uaeType = uaeType;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getCustomerCode() {
		return CustomerCode;
	}

	public void setCustomerCode(String customerCode) {
		CustomerCode = customerCode;
	}

	@Override
	public String toString() {
		return "Job{" +
				"customerTel='" + customerTel + '\'' +
				", usageDate='" + usageDate + '\'' +
				", destination='" + destination + '\'' +
				", customer='" + customer + '\'' +
				", pickUp='" + pickUp + '\'' +
				", flight='" + flight + '\'' +
				", vehicleType='" + vehicleType + '\'' +
				", jobCat='" + jobCat + '\'' +
				", showRemarks='" + showRemarks + '\'' +
				", jobStatus='" + jobStatus + '\'' +
				", eTA='" + eTA + '\'' +
				", jobType='" + jobType + '\'' +
				", showPhoto='" + showPhoto + '\'' +
				", statusOrder='" + statusOrder + '\'' +
				", remarks='" + remarks + '\'' +
				", jobNo='" + jobNo + '\'' +
				", pickUpTime='" + pickUpTime + '\'' +
				", noShowPhoto='" + noShowPhoto + '\'' +
				", noShowRemarks='" + noShowRemarks + '\'' +
				", noOfAdult='" + noOfAdult + '\'' +
				", noOfChild='" + noOfChild + '\'' +
				", noOfInfant='" + noOfInfant + '\'' +
				", bookNo='" + bookNo + '\'' +
				", file1='" + file1 + '\'' +
				", fileNo='" + fileNo + '\'' +
				", location='" + location + '\'' +
				'}';
	}
}
