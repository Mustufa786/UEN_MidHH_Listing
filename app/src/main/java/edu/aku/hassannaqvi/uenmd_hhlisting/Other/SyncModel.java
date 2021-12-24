package edu.aku.hassannaqvi.uenmd_hhlisting.Other;


import static edu.aku.hassannaqvi.uenmd_hhlisting.Core.MainApp._EMPTY_;

public class SyncModel {
    String tableName;
    String status;
    int statusID;
    String message;
    String filter = null;
    String select = null;

    public SyncModel(String tableName) {

        this.tableName = tableName;
        this.status = _EMPTY_;
        this.statusID = 0;
        this.message = _EMPTY_;


    }

    public SyncModel(String tableName, String select) {

        this.tableName = tableName;
        this.status = _EMPTY_;
        this.statusID = 0;
        this.message = _EMPTY_;
        this.select = select;
    }

    public SyncModel(String tableName, String select, String filter) {

        this.tableName = tableName;
        this.status = _EMPTY_;
        this.statusID = 0;
        this.message = _EMPTY_;
        this.select = select;
        this.filter = filter;

    }


    public String gettableName() {
        return tableName;
    }

    public void settableName(String tableName) {
        this.tableName = tableName;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public int getstatusID() {
        return statusID;
    }

    public void setstatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getmessage() {
        return message;
    }

    public void setmessage(String message) {
        this.message = message;
    }

    public String getFilter() {

        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }
}
