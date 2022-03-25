package com.loan.recovery.util;

/**
 * Created by mjonnakuti on 15/03/2020.
 */

public class AppConstants {
    public static String BASE_URL1 = "https://surecollect.ai:3000/";
//    public static String BASE_URL1 = "http://192.168.0.116:3000/";

    public static String BASE_URL2 = "http://52.66.187.186:7085/";
//    public static String BASE_URL2 = "http://192.168.0.116:7085/";

    public static String BASE_URL3 = "http://103.50.163.103:7085/";
    public static String BASE_URL4 = "https://app.retrafinance.com/aiq/";
    public static String BASE_URL5 = "https://surecollect.ai:9003/";

    public static String PREFERENCES_FILE_NAME = "surecollect";
    public static int REALESTATE_TYPE = 1024;
    public static int NONRETRA_TYPE = 1023;
    public static String API_CALL_TYPE = "application/x-www-form-urlencoded";
    //https://surecollect.ai:9003/getLuCaseEduData

    // Call Recording Service Constants

    public static final String APP_THEME = "theme";
    public static final String STORAGE = "storage";
    public static final String STORAGE_PATH = "public_storage_path";
    public static final String SPEAKER_USE = "put_on_speaker";
    public static final String FORMAT = "format";
    public static final String MODE = "mode";
    public static final String ENABLED = "enabled";
    public static final String SOURCE = "source";
    public static final String RECORDING_EXTRA = "recording_extra";
    public static final String FILE_PROVIDER = "net.synapticweb.callrecorder.eval.fileprovider";
    public static final String DATABASE_NAME = "callrecorder.db";
    public static final String PHONE_NUMBER_AIRTEL = "08045810891";
    public static final int PICK_FILE_RESULT_CODE = 1234;

    // Logger Constants
    private static final String TAG = "CallRecorder";
    private final static int MAX_FILE_SIZE = 1000000;
    private final static int MAX_FILE_COUNT = 5;
    private final static String LOG_FILE_NAME = "log";

    public static final String DEBUG = " DEBUG ";
    public static final String WARN = " WARN ";
    public static final String ERROR = " ERROR ";
}
