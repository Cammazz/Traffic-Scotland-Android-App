package com.example.corke_cameron_s2327100;

public class Situation {

    // DECLERATIONS
    private String overallStartTime; //<validityTimeSpecification>
    private String overallEndTime;
    private String numberOfOperationalLanes; // impact
    private String tpegDirection;
    private double fromLatitude; // <pointCoordinates>
    private double fromLongitude; // <pointCoordinates>
    private double toLatitude; // <pointCoordinates>
    private double toLongitude; // <pointCoordinates>
    private String descriptor;
    private String generalPublicComment;
    private String situationRecord;
    private String delaysType;



    public Situation() { }

    // GETTERS
    public String getOverallStartTime() { return overallStartTime; }
    public String getOverallEndTime() { return overallEndTime; }
    public String getNumberOfOperationalLanes() { return numberOfOperationalLanes; }
    public String getTpegDirection() { return tpegDirection; }
    public double getToLatitude() { return toLatitude; }
    public double getToLongitude() { return toLongitude; }
    public double getFromLatitude() { return fromLatitude; }
    public double getFromLongitude() { return fromLongitude; }
    public String getSituationRecord() { return situationRecord; }
    public String getGeneralPublicComment() { return generalPublicComment; }
    public String getDescriptor() { return descriptor; }
    public String getDelaysType() { return delaysType; }

    // SETTERS
    public void setOverallStartTime(String overallStartTime) {this.overallStartTime = overallStartTime;}
    public void setNumberOfOperationalLanes(String numberOfOperationalLanes) {this.numberOfOperationalLanes = numberOfOperationalLanes;}
    public void setTpegDirection(String tpegDirection) {this.tpegDirection = tpegDirection;}
    public void setFromLatitude(double fromLatitude) {this.fromLatitude = fromLatitude;}
    public void setFromLongitude(double fromLongitude) {this.fromLongitude = fromLongitude;}
    public void setToLatitude(double toLatitude) {this.toLatitude = toLatitude;}
    public void setToLongitude(double toLongitude) {this.toLongitude = toLongitude;}
    public void setSituationRecord(String situationRecord) {this.situationRecord = situationRecord;}
    public void setGeneralPublicComment(String generalPublicComment) {this.generalPublicComment = generalPublicComment;}
    public void setDescriptor(String descriptor) {this.descriptor = descriptor;}
    public void setOverallEndTime(String overallEndTime) {this.overallEndTime = overallEndTime;}
    public void setDelaysType(String delaysType) {this.delaysType = delaysType;}


}
