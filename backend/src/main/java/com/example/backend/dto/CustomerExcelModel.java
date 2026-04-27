package com.example.backend.dto;

import com.alibaba.excel.annotation.ExcelProperty;

public class CustomerExcelModel {

    @ExcelProperty("Name")
    private String name;

    @ExcelProperty("NIC Number")
    private String nicNumber;

    @ExcelProperty("DOB")
    private String dob;

    @ExcelProperty("Mobile Number")
    private String mobileNumber;

    @ExcelProperty("City ID")
    private Long cityId;

    @ExcelProperty("Country ID")
    private Long countryId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNicNumber() { return nicNumber; }
    public void setNicNumber(String nicNumber) { this.nicNumber = nicNumber; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    public Long getCityId() { return cityId; }
    public void setCityId(Long cityId) { this.cityId = cityId; }
    public Long getCountryId() { return countryId; }
    public void setCountryId(Long countryId) { this.countryId = countryId; }
}
