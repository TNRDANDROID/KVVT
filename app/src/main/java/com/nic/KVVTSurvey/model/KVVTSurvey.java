package com.nic.KVVTSurvey.model;

import android.graphics.Bitmap;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class KVVTSurvey {

    private String distictCode;
    private String districtName;

    private String blockCode;
    private String exclusion_criteria_id;

    public String getHabCode() {
        return HabCode;
    }

    public void setHabCode(String habCode) {
        HabCode = habCode;
    }

    private String HabCode;

    private String Description;
    private String Latitude;
    private String BeneficiaryName;
    private String BeneficiaryFatherName;
    private String BeneficiaryId;
    private String HabitationName;
    private String kvvtId;
    private String fatherName;
    private String personAlive;
    private String buttonText;
    private String eligible_for_auto_exclusion;

    public String getEligible_for_auto_exclusion() {
        return eligible_for_auto_exclusion;
    }

    public KVVTSurvey setEligible_for_auto_exclusion(String eligible_for_auto_exclusion) {
        this.eligible_for_auto_exclusion = eligible_for_auto_exclusion;
        return this;
    }

    public String getBeneficiaryFatherName() {
        return BeneficiaryFatherName;
    }

    public KVVTSurvey setBeneficiaryFatherName(String beneficiaryFatherName) {
        BeneficiaryFatherName = beneficiaryFatherName;
        return this;
    }

    public String getBeneficiaryId() {
        return BeneficiaryId;
    }

    public KVVTSurvey setBeneficiaryId(String beneficiaryId) {
        BeneficiaryId = beneficiaryId;
        return this;
    }

    public String getExclusion_criteria_id() {
        return exclusion_criteria_id;
    }

    public KVVTSurvey setExclusion_criteria_id(String exclusion_criteria_id) {
        this.exclusion_criteria_id = exclusion_criteria_id;
        return this;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public String getPersonAlive() {
        return personAlive;
    }

    public void setPersonAlive(String personAlive) {
        this.personAlive = personAlive;
    }

    public String getIsLegel() {
        return isLegel;
    }

    public void setIsLegel(String isLegel) {
        this.isLegel = isLegel;
    }

    public String getIsEligible() {
        return isEligible;
    }

    public void setIsEligible(String isEligible) {
        this.isEligible = isEligible;
    }

    private String isLegel;
    private String isEligible;

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getKvvtId() {
        return kvvtId;
    }

    public void setKvvtId(String kvvtId) {
        this.kvvtId = kvvtId;
    }


    public String getBeneficiaryName() {
        return BeneficiaryName;
    }

    public void setBeneficiaryName(String beneficiaryName) {
        BeneficiaryName = beneficiaryName;
    }

    public String getHabitationName() {
        return HabitationName;
    }

    public void setHabitationName(String habitationName) {
        HabitationName = habitationName;
    }



    private String PvCode;
    private String PvName;

    private String blockName;

    private String exclusion_criteria;

    public String getExclusion_criteria() {
        return exclusion_criteria;
    }

    public KVVTSurvey setExclusion_criteria(String exclusion_criteria) {
        this.exclusion_criteria = exclusion_criteria;
        return this;
    }

    public String getTypeOfPhoto() {
        return typeOfPhoto;
    }

    public void setTypeOfPhoto(String typeOfPhoto) {
        this.typeOfPhoto = typeOfPhoto;
    }

    private String typeOfPhoto;
    private String imageRemark;
    private String dateTime;
    private String imageAvailable;



    public String getImageAvailable() {
        return imageAvailable;
    }

    public void setImageAvailable(String imageAvailable) {
        this.imageAvailable = imageAvailable;
    }



    public String getImageRemark() {
        return imageRemark;
    }

    public void setImageRemark(String imageRemark) {
        this.imageRemark = imageRemark;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }









    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }


    public String getPvName() {
        return PvName;
    }

    public void setPvName(String name) {
        PvName = name;
    }


    public String getDistictCode() {
        return distictCode;
    }

    public void setDistictCode(String distictCode) {
        this.distictCode = distictCode;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }





    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    private String Longitude;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    private Bitmap Image;



    public String getPvCode() {
        return PvCode;
    }

    public void setPvCode(String pvCode) {
        this.PvCode = pvCode;
    }



}