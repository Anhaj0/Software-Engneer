package com.example.softwareengineer.upload;

import com.alibaba.excel.annotation.ExcelProperty;

public class CustomerUploadRow {
    @ExcelProperty("NIC")
    private String nic;
    @ExcelProperty("First Name")
    private String firstName;
    @ExcelProperty("Last Name")
    private String lastName;
    @ExcelProperty("Email")
    private String email;

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
