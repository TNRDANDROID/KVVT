package com.nic.KVVTSurvey.dataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "KVVTSurvey";
    private static final int DATABASE_VERSION = 2;


    public static final String VILLAGE_TABLE_NAME = " villageTable";
    public static final String SCHEME_TABLE_NAME = " schemeTable";
    public static final String COMMUNITY_TABLE_NAME = " communityTable";
    public static final String STREET_TABLE_NAME = " streetTable";
    public static final String HABITATION_TABLE_NAME = " habitaionTable";
    public static final String KVVT_LIST_TABLE_NAME = "KVVTList";
    public static final String SAVE_KVVT_DETAILS = "SaveKVVTDetails";
    public static final String SAVE_KVVT_IMAGES = "SaveKVVTImages";

    private Context context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }

    //creating tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + VILLAGE_TABLE_NAME + " ("
                + "dcode INTEGER," +
                "bcode INTEGER," +
                "pvcode INTEGER," +
                "pvname_ta TEXT,"+
                "pvname TEXT)");
        db.execSQL("CREATE TABLE " + SCHEME_TABLE_NAME + " ("
                + "exclusion_criteria_id TEXT," +
                 "photo_required TEXT," +
                 "auto_reject TEXT," +
                "exclusion_criteria_ta TEXT,"+
                "exclusion_criteria TEXT)");
        db.execSQL("CREATE TABLE " + COMMUNITY_TABLE_NAME + " ("
                + "community_id TEXT," +
                "community_name TEXT)");

        db.execSQL("CREATE TABLE " + HABITATION_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habitation_code TEXT," +
                "habitation_name_ta TEXT,"+
                "habitation_name TEXT)");
        db.execSQL("CREATE TABLE " + STREET_TABLE_NAME + " ("
                + "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "hab_code TEXT," +
                "street_code TEXT," +
                "street_name_e TEXT," +
                "street_name_t TEXT)");

        db.execSQL("CREATE TABLE " + KVVT_LIST_TABLE_NAME + " ("
                + "pvcode  TEXT," +
                "habcode  TEXT," +
                "benificiary_id  TEXT," +
                "beneficiary_name  TEXT," +
                "beneficiary_father_name  TEXT," +
                "eligible_for_auto_exclusion  TEXT," +
                "habitation_name TEXT," +
                "exclusion_criteria_id TEXT," +
                "exclusion_criteria_ta TEXT," +
                "photo_available TEXT," +
                "patta_available TEXT," +
                "is_awaas_plus_listed TEXT," +
                "is_document_available TEXT," +
                "is_natham_land_available TEXT," +
                "pvname_ta TEXT," +
                "habitation_name_ta TEXT," +
                "pvname TEXT)");
        db.execSQL("CREATE TABLE " + SAVE_KVVT_DETAILS + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "dcode TEXT," +
                "bcode TEXT," +
                "pvcode TEXT," +
                "habcode TEXT," +
                "pvname TEXT," +
                "habitation_name TEXT," +
                "benificiary_id TEXT," +
                "exclusion_criteria_id TEXT," +
                "exclusion_criteria_ta TEXT," +
                "beneficiary_name TEXT," +
                "person_eligible TEXT," +
                "existing_user TEXT," +
                "button_text TEXT," +
                "beneficiary_fat_hus_status TEXT," +
                "street_code TEXT," +
                "community_id TEXT," +
                "door_no TEXT," +
                "patta_available TEXT," +
                "is_awaas_plus_listed TEXT," +
                "is_document_available TEXT," +
                "is_natham_land_available TEXT," +
                "beneficiary_father_name TEXT)");


        db.execSQL("CREATE TABLE " + SAVE_KVVT_IMAGES + " ("
                + "kvvt_id INTEGER,"+
                "image BLOB," +
                "latitude TEXT," +
                "longitude TEXT," +
                "type_of_photo TEXT)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion) {
            //drop table if already exists
            db.execSQL("DROP TABLE IF EXISTS " + VILLAGE_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SCHEME_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + COMMUNITY_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + STREET_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + KVVT_LIST_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + HABITATION_TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_KVVT_DETAILS);
            db.execSQL("DROP TABLE IF EXISTS " + SAVE_KVVT_IMAGES);
            onCreate(db);
        }
    }


}
