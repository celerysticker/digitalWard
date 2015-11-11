/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utility;

/**
 *
 * @author Qi Zhou
 */
public class Patient {
    private int wardNum;
    private int bedNum;
    private String fName;
    private String lName;
    private String gender;
    private String dob;
    private int respRate;
    private int oxySat;
    private int sysBlood;
    private float temp;
    private int heartRate;
    private int psews;
    private String datafile;
    
    public Patient(int wardNum, int bedNum, String fName, String lName, String g, String dob) {
        this.wardNum = wardNum;
        this.bedNum = bedNum;
        this.fName = fName;
        this.lName = lName;
        this.gender = g;
        this.dob = dob;
    }

    public int getWardNumber() {
        return wardNum;
    }
    
    public int getBedNumber() {
        return bedNum;
    }
    
    public String getFirstName() {
        return fName;
    }

    public String getLastName() {
        return lName;
    }
    
    public String getGender() {
        return gender;
    }

    public String getDOB() {
        return dob;
    }
    
    public int getPSEWS() {
        return this.psews;
    }
    
    public void setPSEWS(int input) {
        this.psews = input;
    }
    
    public String getDataFile() {
        return this.datafile;
    }
    
    public void setDataFile(String datafile) {
        this.datafile = datafile;
    }
    
    public void setAll(int rr, int oxy, int sys, float temp, int hr) {
        this.respRate = rr;
        this.oxySat = oxy;
        this.sysBlood = sys;
        this.temp = temp;
        this.heartRate = hr;
        
        // update pSEWS
        int psews = 0;
        if (this.respRate <= 8 || this.respRate >= 36) {
            psews += 3;
        }
        else if (this.respRate >= 31 && this.respRate <= 35) {
            psews += 2;
        }
        else if (this.respRate >= 21 && this.respRate <= 30) {
            psews += 1;
        }
        else {
            psews += 0;
        }
        
        // oxySat
        if (this.oxySat < 85) {
            psews += 3;
        }
        else if (this.oxySat >= 85 && this.oxySat <= 89) {
            psews += 2;
        }
        else if (this.oxySat >= 90 && this.oxySat <= 92) {
            psews += 1;
        }
        else {
            psews += 0;
        }
        
        if (this.sysBlood <= 69) {
            psews += 3;
        }
        else if ((this.sysBlood >= 70 && this.sysBlood <= 79) || this.sysBlood >= 200) {
            psews += 2;
        }
        else if (this.sysBlood >= 80 && this.sysBlood <= 99) {
            psews += 1;
        }
        else {
            psews += 0;
        }
        
        if (this.temp < 34) {
            psews += 3;
        }
        else if ((this.temp >= 34 && this.temp <= 34.9) || this.temp >= 38.5) {
            psews += 2;
        }
        else if ((this.temp >= 35 && this.temp <= 35.9) || (this.temp >= 38 && this.temp <= 38.4)) {
            psews += 1;
        }
        else {
            psews += 0;
        }
        
        if (this.heartRate < 29 || this.heartRate >= 130) {
            psews += 3;
        }
        else if ((this.heartRate >= 30 && this.heartRate <= 39) || (this.heartRate >= 110 && this.heartRate <= 129)) {
            psews += 2;
        }
        else if ((this.heartRate >= 40 && this.heartRate <= 49) || (this.heartRate >= 100 && this.heartRate <= 109)) {
            psews += 1;
        }
        else {
            psews += 0;
        }
        
        this.setPSEWS(psews); 
    }
}
