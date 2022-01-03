package com.nic.KVVTSurvey.model;

import android.graphics.Bitmap;

/**
 * Created by AchanthiSundar on 01-11-2017.
 */

public class KVVTSurvey {

    private String distictCode;
    private String districtName;
    private String streetCode;
    private String streetName;
    private String streetNameTa;

    private String blockCode;
    private String exclusion_criteria_id;
    private String community_id;
    private String community_name;
    private String photo_required;
    private String photo_availavle;

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
    private String HabitationNameTa;
    private String kvvtId;

    private String buttonText;
    private String eligible_for_auto_exclusion;
    private String existing_user;
    private String fat_hus_status;
    private String door_no;
    private String eleigible_auto_rejection;

    private String IS_DOCUMENT_AVAILABLE;
    private String IS_NATHAM_LAND_AVAILABLE;

    private String exclusion_criteria_ta;



    public String getHabitationNameTa() {
        return HabitationNameTa;
    }

    public void setHabitationNameTa(String habitationNameTa) {
        HabitationNameTa = habitationNameTa;
    }

    public String getIS_DOCUMENT_AVAILABLE() {
        return IS_DOCUMENT_AVAILABLE;
    }

    public void setIS_DOCUMENT_AVAILABLE(String IS_DOCUMENT_AVAILABLE) {
        this.IS_DOCUMENT_AVAILABLE = IS_DOCUMENT_AVAILABLE;
    }

    public String getIS_NATHAM_LAND_AVAILABLE() {
        return IS_NATHAM_LAND_AVAILABLE;
    }

    public void setIS_NATHAM_LAND_AVAILABLE(String IS_NATHAM_LAND_AVAILABLE) {
        this.IS_NATHAM_LAND_AVAILABLE = IS_NATHAM_LAND_AVAILABLE;
    }

    private String patta_available_status;
    private String is_awaas_plus_list;

    public String getPatta_available_status() {
        return patta_available_status;
    }

    public void setPatta_available_status(String patta_available_status) {
        this.patta_available_status = patta_available_status;
    }

    public String getIs_awaas_plus_list() {
        return is_awaas_plus_list;
    }

    public void setIs_awaas_plus_list(String is_awaas_plus_list) {
        this.is_awaas_plus_list = is_awaas_plus_list;
    }

    public String getEleigible_auto_rejection() {
        return eleigible_auto_rejection;
    }

    public void setEleigible_auto_rejection(String eleigible_auto_rejection) {
        this.eleigible_auto_rejection = eleigible_auto_rejection;
    }

    public String getDoor_no() {
        return door_no;
    }

    public KVVTSurvey setDoor_no(String door_no) {
        this.door_no = door_no;
        return this;
    }

    public String getFat_hus_status() {
        return fat_hus_status;
    }

    public KVVTSurvey setFat_hus_status(String fat_hus_status) {
        this.fat_hus_status = fat_hus_status;
        return this;
    }

    public String getStreetNameTa() {
        return streetNameTa;
    }

    public KVVTSurvey setStreetNameTa(String streetNameTa) {
        this.streetNameTa = streetNameTa;
        return this;
    }

    public String getCommunity_id() {
        return community_id;
    }

    public KVVTSurvey setCommunity_id(String community_id) {
        this.community_id = community_id;
        return this;
    }

    public String getCommunity_name() {
        return community_name;
    }

    public KVVTSurvey setCommunity_name(String community_name) {
        this.community_name = community_name;
        return this;
    }

    public String getStreetCode() {
        return streetCode;
    }

    public KVVTSurvey setStreetCode(String streetCode) {
        this.streetCode = streetCode;
        return this;
    }

    public String getStreetName() {
        return streetName;
    }

    public KVVTSurvey setStreetName(String streetName) {
        this.streetName = streetName;
        return this;
    }

    public String getExisting_user() {
        return existing_user;
    }

    public KVVTSurvey setExisting_user(String existing_user) {
        this.existing_user = existing_user;
        return this;
    }

    public String getPhoto_availavle() {
        return photo_availavle;
    }

    public KVVTSurvey setPhoto_availavle(String photo_availavle) {
        this.photo_availavle = photo_availavle;
        return this;
    }

    public String getPhoto_required() {
        return photo_required;
    }

    public KVVTSurvey setPhoto_required(String photo_required) {
        this.photo_required = photo_required;
        return this;
    }

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
    private String PvNameTa;

    private String blockName;

    private String exclusion_criteria;

    public String getExclusion_criteria_ta() {
        return exclusion_criteria_ta;
    }

    public void setExclusion_criteria_ta(String exclusion_criteria_ta) {
        this.exclusion_criteria_ta = exclusion_criteria_ta;
    }

    public String getPvNameTa() {
        return PvNameTa;
    }

    public void setPvNameTa(String pvNameTa) {
        PvNameTa = pvNameTa;
    }

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