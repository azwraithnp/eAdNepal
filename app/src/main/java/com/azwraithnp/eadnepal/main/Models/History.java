package com.azwraithnp.eadnepal.main.Models;

public class History {

    String sn, adName, viewDate;

    public History()
    {

    }

    public History(String sn, String adName, String viewDate) {
        this.sn = sn;
        this.adName = adName;
        this.viewDate = viewDate;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getViewDate() {
        return viewDate;
    }

    public void setViewDate(String viewDate) {
        this.viewDate = viewDate;
    }
}
