package com.example.shash.shashanksummer;

public class MyData {
    String ecat,ename,edesc,estart,eend,eurl,eco;
    Double elat,elong;

    public String getEcat() {
        return ecat;
    }

    public void setEcat(String ecat) {
        this.ecat = ecat;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getEdesc() {
        return edesc;
    }

    public void setEdesc(String edesc) {
        this.edesc = edesc;
    }

    public String getEstart() {
        return estart;
    }

    public void setEstart(String estart) {
        this.estart = estart;
    }

    public String getEend() {
        return eend;
    }

    public void setEend(String eend) {
        this.eend = eend;
    }

    public String getEurl() {
        return eurl;
    }

    public void setEurl(String eurl) {
        this.eurl = eurl;
    }

    public String getEco() {
        return eco;
    }

    public void setEco(String eco) {
        this.eco = eco;
    }

    public Double getElat() {
        return elat;
    }

    public void setElat(Double elat) {
        this.elat = elat;
    }

    public Double getElong() {
        return elong;
    }

    public void setElong(Double elong) {
        this.elong = elong;
    }

    public MyData(String ecat, String ename, String edesc, String estart, String eend, String eurl, String eco, Double elat, Double elong) {
        this.ecat = ecat;
        this.ename = ename;
        this.edesc = edesc;
        this.estart = estart;
        this.eend = eend;

        this.eurl = eurl;
        this.eco = eco;
        this.elat = elat;
        this.elong = elong;
    }
}
