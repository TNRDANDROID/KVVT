package com.nic.KVVTSurvey.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.nic.KVVTSurvey.constant.AppConstant;
import com.nic.KVVTSurvey.model.KVVTSurvey;

import java.util.ArrayList;


public class dbData {
    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;
    private Context context;

    public dbData(Context context){
        this.dbHelper = new DBHelper(context);
        this.context = context;
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        if(dbHelper != null) {
            dbHelper.close();
        }
    }

    /****** DISTRICT TABLE *****/


    /****** VILLAGE TABLE *****/
    public KVVTSurvey insertVillage(KVVTSurvey kvvtSurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, kvvtSurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, kvvtSurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, kvvtSurvey.getPvCode());
        values.put(AppConstant.PV_NAME, kvvtSurvey.getPvName());

        long id = db.insert(DBHelper.VILLAGE_TABLE_NAME,null,values);
        Log.d("Inserted_id_village", String.valueOf(id));

        return kvvtSurvey;
    }
    public KVVTSurvey insertscheme(KVVTSurvey kvvtSurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.EXCLUSION_CRITERIA_ID, kvvtSurvey.getExclusion_criteria_id());
        values.put(AppConstant.EXCLUSION_CRITERIA, kvvtSurvey.getExclusion_criteria());
        values.put(AppConstant.PHOTO_REQUIRED, kvvtSurvey.getPhoto_required());
        values.put(AppConstant.AUTO_REJECT, kvvtSurvey.getEleigible_auto_rejection());
        long id = db.insert(DBHelper.SCHEME_TABLE_NAME,null,values);
        Log.d("Inserted_id_criteria", String.valueOf(id));

        return kvvtSurvey;
    }
    public KVVTSurvey insertCommunity(KVVTSurvey kvvtSurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.COMMUNITY_ID, kvvtSurvey.getCommunity_id());
        values.put(AppConstant.COMMUNITY_NAME, kvvtSurvey.getCommunity_name());
        long id = db.insert(DBHelper.COMMUNITY_TABLE_NAME,null,values);
        Log.d("Inserted_id_Community", String.valueOf(id));

        return kvvtSurvey;
    }
    public ArrayList<KVVTSurvey> getAll_Village(String dcode, String bcode) {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.VILLAGE_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by pvname asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    KVVTSurvey card = new KVVTSurvey();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<KVVTSurvey> getAll_scheme() {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.SCHEME_TABLE_NAME,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    KVVTSurvey card = new KVVTSurvey();
                    card.setExclusion_criteria_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA_ID)));
                    card.setExclusion_criteria(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA)));
                    card.setPhoto_required(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PHOTO_REQUIRED)));
                    card.setEleigible_auto_rejection(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.AUTO_REJECT)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<KVVTSurvey> getAll_community() {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.COMMUNITY_TABLE_NAME,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    KVVTSurvey card = new KVVTSurvey();
                    card.setCommunity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.COMMUNITY_ID)));
                    card.setCommunity_name(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.COMMUNITY_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public KVVTSurvey insertHabitation(KVVTSurvey kvvtSurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, kvvtSurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, kvvtSurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, kvvtSurvey.getPvCode());
        values.put(AppConstant.HABB_CODE, kvvtSurvey.getHabCode());
        values.put(AppConstant.HABITATION_NAME, kvvtSurvey.getHabitationName());

        long id = db.insert(DBHelper.HABITATION_TABLE_NAME,null,values);
        Log.d("Inserted_id_habitation", String.valueOf(id));

        return kvvtSurvey;
    }
    public KVVTSurvey insertStreet(KVVTSurvey kvvtSurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.DISTRICT_CODE, kvvtSurvey.getDistictCode());
        values.put(AppConstant.BLOCK_CODE, kvvtSurvey.getBlockCode());
        values.put(AppConstant.PV_CODE, kvvtSurvey.getPvCode());
        values.put(AppConstant.HABIT_CODE, kvvtSurvey.getHabCode());
        values.put(AppConstant.STREET_CODE, kvvtSurvey.getStreetCode());
        values.put(AppConstant.STREET_NAME_EN, kvvtSurvey.getStreetName());
        values.put(AppConstant.STREET_NAME_TA, kvvtSurvey.getStreetNameTa());

        long id = db.insert(DBHelper.STREET_TABLE_NAME,null,values);
        Log.d("Inserted_id_street", String.valueOf(id));

        return kvvtSurvey;
    }
    public ArrayList<KVVTSurvey> getAll_Habitation(String dcode, String bcode) {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.HABITATION_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" order by habitation_name asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    KVVTSurvey card = new KVVTSurvey();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABB_CODE)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }
    public ArrayList<KVVTSurvey> getAll_Street(String dcode, String bcode, String pvcode, String hab_code) {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery("select * from "+DBHelper.STREET_TABLE_NAME+" where dcode = "+dcode+" and bcode = "+bcode+" and pvcode = "+pvcode+" and hab_code = "+hab_code+" order by street_name_e asc",null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    KVVTSurvey card = new KVVTSurvey();
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABIT_CODE)));
                    card.setStreetCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.STREET_CODE)));
                    card.setStreetName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.STREET_NAME_EN)));
                    card.setStreetNameTa(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.STREET_NAME_TA)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public KVVTSurvey insertKVVT(KVVTSurvey kvvtSurvey) {

        ContentValues values = new ContentValues();
        values.put(AppConstant.PV_CODE, kvvtSurvey.getPvCode());
        values.put(AppConstant.HAB_CODE, kvvtSurvey.getHabCode());
        values.put(AppConstant.BENEFICIARY_ID, kvvtSurvey.getBeneficiaryId());
        values.put(AppConstant.BENEFICIARY_NAME, kvvtSurvey.getBeneficiaryName());
        values.put(AppConstant.BENEFICIARY_FATHER_NAME, kvvtSurvey.getBeneficiaryFatherName());
        values.put(AppConstant.ELIGIBLE_FOR_AUTO_EXCLUSION, kvvtSurvey.getEligible_for_auto_exclusion());
        values.put(AppConstant.HABITATION_NAME, kvvtSurvey.getHabitationName());
        values.put(AppConstant.EXCLUSION_CRITERIA_ID, kvvtSurvey.getExclusion_criteria_id());
        values.put(AppConstant.PV_NAME, kvvtSurvey.getPvName());
        values.put(AppConstant.PHOTO_AVAILABLE, kvvtSurvey.getPhoto_availavle());
        values.put(AppConstant.PATTA_AVAILABLE, kvvtSurvey.getPatta_available_status());
        values.put(AppConstant.IS_AWAAS_PLUS_LISTED, kvvtSurvey.getIs_awaas_plus_list());

        values.put(AppConstant.IS_DOCUMENT_AVAILABLE, kvvtSurvey.getIS_DOCUMENT_AVAILABLE());
        values.put(AppConstant.IS_NATHAM_LAND_AVAILABLE, kvvtSurvey.getIS_NATHAM_LAND_AVAILABLE());
        long id = db.insert(DBHelper.KVVT_LIST_TABLE_NAME,null,values);
        Log.d("Inserted_id_KVVT_LIST", String.valueOf(id));

        return kvvtSurvey;
    }

    public ArrayList<KVVTSurvey> getAll_KVVTList(String pvcode, String habcode) {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;

        String condition = "";

        if (habcode != "") {
            condition = " where pvcode = '" + pvcode+"' and habcode = '" + habcode+"'" ;
        }else {
            condition = "";
        }

        try {
            cursor = db.rawQuery("select * from "+DBHelper.KVVT_LIST_TABLE_NAME + condition,null);
            // cursor = db.query(CardsDBHelper.TABLE_CARDS,
            //       COLUMNS, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    KVVTSurvey card = new KVVTSurvey();
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setBeneficiaryId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setBeneficiaryFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));
                    card.setEligible_for_auto_exclusion(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.ELIGIBLE_FOR_AUTO_EXCLUSION)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setExclusion_criteria_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA_ID)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setPhoto_availavle(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PHOTO_AVAILABLE)));
                    card.setIs_awaas_plus_list(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_AWAAS_PLUS_LISTED)));
                    card.setPatta_available_status(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PATTA_AVAILABLE)));

                    card.setIS_DOCUMENT_AVAILABLE(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_DOCUMENT_AVAILABLE)));
                    card.setIS_NATHAM_LAND_AVAILABLE(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_NATHAM_LAND_AVAILABLE)));

                    cards.add(card);
                }
            }
        } catch (Exception e){
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally{
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }

    public ArrayList<KVVTSurvey> getSavedKVVTDetails() {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;


        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_DETAILS,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_KVVT_DETAILS +" where id in (select kvvt_id from "+DBHelper.SAVE_KVVT_IMAGES +")",null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    KVVTSurvey card = new KVVTSurvey();


                    card.setKvvtId(cursor.getString(cursor
                            .getColumnIndexOrThrow("id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setBeneficiaryId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_ID)));
                    card.setExclusion_criteria_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setBeneficiaryFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));
                    card.setIsEligible(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ELIGIBLE)));
                    card.setButtonText(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BUTTON_TEXT)));
                    card.setExisting_user(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EXISTING_USER)));
                    card.setFat_hus_status(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FAT_HUS_STATUS)));
                    card.setStreetCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.STREET_CODE)));
                    card.setDoor_no(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DOOR_NO)));
                    card.setCommunity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.COMMUNITY_ID)));
                    card.setIs_awaas_plus_list(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_AWAAS_PLUS_LISTED)));
                    card.setPatta_available_status(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PATTA_AVAILABLE)));

                    card.setIS_DOCUMENT_AVAILABLE(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_DOCUMENT_AVAILABLE)));
                    card.setIS_NATHAM_LAND_AVAILABLE(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_NATHAM_LAND_AVAILABLE)));




                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        try {
//            cursor = db.query(DBHelper.SAVE_PMAY_DETAILS,
//                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            cursor = db.rawQuery("select * from "+DBHelper.SAVE_KVVT_DETAILS +" where button_text = 'Save details'",null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    KVVTSurvey card = new KVVTSurvey();

                    card.setKvvtId(cursor.getString(cursor
                            .getColumnIndexOrThrow("id")));
                    card.setDistictCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DISTRICT_CODE)));
                    card.setBlockCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BLOCK_CODE)));
                    card.setPvCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_CODE)));
                    card.setHabCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HAB_CODE)));
                    card.setPvName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PV_NAME)));
                    card.setHabitationName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.HABITATION_NAME)));
                    card.setBeneficiaryId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_ID)));
                    card.setExclusion_criteria_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EXCLUSION_CRITERIA_ID)));
                    card.setBeneficiaryName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_NAME)));
                    card.setBeneficiaryFatherName(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FATHER_NAME)));
                    card.setIsEligible(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PERSON_ELIGIBLE)));
                    card.setButtonText(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BUTTON_TEXT)));
                    card.setExisting_user(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.EXISTING_USER)));
                    card.setFat_hus_status(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.BENEFICIARY_FAT_HUS_STATUS)));
                    card.setStreetCode(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.STREET_CODE)));
                    card.setDoor_no(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.DOOR_NO)));
                    card.setCommunity_id(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.COMMUNITY_ID)));
                    card.setIs_awaas_plus_list(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_AWAAS_PLUS_LISTED)));
                    card.setPatta_available_status(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.PATTA_AVAILABLE)));

                    card.setIS_DOCUMENT_AVAILABLE(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_DOCUMENT_AVAILABLE)));
                    card.setIS_NATHAM_LAND_AVAILABLE(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.IS_NATHAM_LAND_AVAILABLE)));



                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public ArrayList<KVVTSurvey> getSavedKVVTImages(String kvvt_id, String type_of_photo) {

        ArrayList<KVVTSurvey> cards = new ArrayList<>();
        Cursor cursor = null;
        String selection = null;
        String[] selectionArgs = null;

        if(!type_of_photo.isEmpty()){
            selection = "kvvt_id = ? and type_of_photo = ? ";
            selectionArgs = new String[]{kvvt_id,type_of_photo};
        }
        else if(type_of_photo.isEmpty()) {
            selection = "kvvt_id = ? ";
            selectionArgs = new String[]{kvvt_id};
        }


        try {
            cursor = db.query(DBHelper.SAVE_KVVT_IMAGES,
                    new String[]{"*"}, selection, selectionArgs, null, null, null);
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    byte[] photo = cursor.getBlob(cursor.getColumnIndexOrThrow(AppConstant.KEY_IMAGE));
                    byte[] decodedString = Base64.decode(photo, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    KVVTSurvey card = new KVVTSurvey();


                    card.setKvvtId(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KVVT_ID)));
                    card.setTypeOfPhoto(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.TYPE_OF_PHOTO)));
                    card.setLatitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LATITUDE)));
                    card.setLongitude(cursor.getString(cursor
                            .getColumnIndexOrThrow(AppConstant.KEY_LONGITUDE)));

                    card.setImage(decodedByte);

                    cards.add(card);
                }
            }
        } catch (Exception e) {
            //   Log.d(DEBUG_TAG, "Exception raised with a value of " + e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return cards;
    }


    public void deleteVillageTable() {
        db.execSQL("delete from " + DBHelper.VILLAGE_TABLE_NAME);
    }
    public void deleteSchemeTable() { db.execSQL("delete from " + DBHelper.SCHEME_TABLE_NAME); }
    public void deleteCommunityTable() { db.execSQL("delete from " + DBHelper.COMMUNITY_TABLE_NAME); }
    public void deleteStreetTable() { db.execSQL("delete from " + DBHelper.STREET_TABLE_NAME); }
    public void deleteHabTable() { db.execSQL("delete from " + DBHelper.HABITATION_TABLE_NAME); }

    public void deleteKVVTTable() {
        db.execSQL("delete from " + DBHelper.KVVT_LIST_TABLE_NAME);
    }

    public void deleteKVVTDetails() { db.execSQL("delete from " + DBHelper.SAVE_KVVT_DETAILS); }

    public void deleteKVVTImages() { db.execSQL("delete from " + DBHelper.SAVE_KVVT_IMAGES);}




    public void deleteAll() {

        deleteVillageTable();
        deleteSchemeTable();
        deleteCommunityTable();
        deleteStreetTable();
        deleteHabTable();
        deleteKVVTTable();
        deleteKVVTDetails();
        deleteKVVTImages();
    }



}
