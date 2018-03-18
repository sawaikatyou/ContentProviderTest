package com.sasakik.contentprovidertest.provider;

import android.net.Uri;

import static com.sasakik.contentprovidertest.provider.DBConstants.EAT_TYPE.BREAKFAST;
import static com.sasakik.contentprovidertest.provider.DBConstants.EAT_TYPE.DINNER;
import static com.sasakik.contentprovidertest.provider.DBConstants.EAT_TYPE.LUNCH;

/**
 * Created by nine_eyes on 2018/03/18.
 */

public class DBConstants {
    public static int DATABASE_VERSION = 1;
    public static String DATABASE_NAME = "contentprovidertest.db";
    public static final String AUTHORITY = "contentprovidertest";
    public static final String PARAMETER_NOTIFY = "notify";
//

    interface EATACTION {
        int getValue();
    }

    public enum EAT_TYPE implements EATACTION{
        BREAKFAST {
            @Override
            public int getValue() {
                return 1;
            }
        },
        LUNCH {
            @Override
            public int getValue() {
                return 2;
            }
        },
        DINNER {
            @Override
            public int getValue() {
                return 3;
            }
        }
    }

    public static EAT_TYPE getEAT_TYPE(int value ) {
        EAT_TYPE result;
        switch(value) {
            case 1:
                result = BREAKFAST;
                break;
            case 2:
                result = LUNCH;
                break;
            case 3:
            default:
                result = DINNER;
                break;
        }
        return result;
    }

    interface TABLEACTION {
        Uri getUri();

        String createTableSQLStatement();
    }

    // Uri
    public static final Uri URI_IMAGES = Uri.parse("content://" + AUTHORITY + "/IMAGES");
    public static final Uri URI_IMAGES_AND_TAGS = Uri.parse("content://" + AUTHORITY + "/IMAGES_AND_TAGS");
    public static final Uri URI_AREAS = Uri.parse("content://" + AUTHORITY + "/AREAS");
    public static final Uri URI_DISHES = Uri.parse("content://" + AUTHORITY + "/DISHES");
    public static final Uri URI_IMAGES_TO_TAGS = Uri.parse("content://" + AUTHORITY + "/IMAGES_TO_TAGS");
    public static final Uri URI_TAGS = Uri.parse("content://" + AUTHORITY + "/TAGS");
    public static final Uri URI_INITDATAMAKE = Uri.parse("content://" + AUTHORITY + "/_INITDATAMAKE");

    public static class Columns {
        public static final String _ID = "_id";
        public static final String _SET_CALORIES = "set_calories";
        public static final String _TIMESTAMP = "timestamp";
        public static final String _CONTENTS = "contents";

        // areas
        // public static final String _ID = "_id";
        public static final String _IMAGES_ID = "images_id";
        public static final String _DISHES_ID = "dishes_id";
        public static final String _TYPE = "type";
        public static final String _AREAINFO = "areainfo";
        public static final String _UPLOAD = "upload";
        // public static final String _TIMESTAMP = "timestamp";
        // public static final String _CONTENTS = "CONTENTS";

        //dishes
        // public static final String _ID = "_id";
        public static final String _NAME = "name";
        public static final String _SERVER_ID = "server_id";
        public static final String _CALOLIE = "calorie";
        // public static final String _TIMESTAMP = "timestamp";
        // public static final String _CONTENTS = "contents";

        // images_to_tags
        // public static final String _IMAGES_ID = "images_id";
        public static final String _TAGS_ID = "tags_id";

        // tags
        // public static final String _ID = "_id";
        public static final String _LABEL = "label";
        public static final String _DESCRIPTION = "description";

    }

    public enum TABLE implements TABLEACTION {
        IMAGES {
            @Override
            public String toString() {
                return "IMAGES";
            }
            @Override
            public Uri getUri() {
                return Uri.parse("content://" + AUTHORITY + "/IMAGES");
            }
            @Override
            public String createTableSQLStatement() {
                return "CREATE TABLE IF NOT EXISTS IMAGES (" +
                        "_id INTEGER PRIMARY KEY," +
                        "set_calories INTEGER," +
                        "timestamp INTEGER," +
                        "contents BLOB);";
            }
        },
        AREAS {
            @Override
            public String toString() {
                return "AREAS";
            }
            @Override
            public Uri getUri() {
                return Uri.parse("content://" + AUTHORITY + "/AREAS");
            }
            @Override
            public String createTableSQLStatement() {
                return "CREATE TABLE IF NOT EXISTS AREAS (" +
                        "_id INTEGER PRIMARY KEY," +
                        "images_id INTEGER," +
                        "dishes_id INTEGER," +
                        "type INTEGER," +
                        "areainfo TEXT," +
                        "upload INTEGER," +
                        "timestamp INTEGER," +
                        "contents BLOB);";
            }
        },
        DISHES {
            @Override
            public String toString() {
                return "DISHES";
            }

            @Override
            public Uri getUri() {
                return Uri.parse("content://" + AUTHORITY + "/DISHES");
            }

            @Override
            public String createTableSQLStatement() {
                return "CREATE TABLE IF NOT EXISTS DISHES (" +
                        "_id INTEGER PRIMARY KEY," +
                        "name TEXT," +
                        "server_id INTEGER," +
                        "calorie INTEGER," +
                        "timestamp INTEGER," +
                        "contents BLOB);";
            }
        },
        IMAGES_TO_TAGS {
            @Override
            public String toString() {
                return "IMAGES_TO_TAGS";
            }

            @Override
            public Uri getUri() {
                return Uri.parse("content://" + AUTHORITY + "/IMAGES_TO_TAGS");
            }

            @Override
            public String createTableSQLStatement() {
                return "CREATE TABLE IF NOT EXISTS IMAGES_TO_TAGS (" +
                        "images_id INTEGER," +
                        "tags_id INTEGER);";
            }
        },
        TAGS {
            @Override
            public String toString() {
                return "TAGS";
            }

            @Override
            public Uri getUri() {
                return Uri.parse("content://" + AUTHORITY + "/TAGS");
            }

            @Override
            public String createTableSQLStatement() {
                return "CREATE TABLE IF NOT EXISTS TAGS (" +
                        "_id INTEGER PRIMARY KEY," +
                        "label TEXT," +
                        "description TEXT);";
            }
        },

        _INITDATAMAKE {
            @Override
            public String toString() {
                return "_INITDATAMAKE";
            }

            @Override
            public Uri getUri() {
                return Uri.parse("content://" + AUTHORITY + "/_INITDATAMAKE");
            }

            @Override
            public String createTableSQLStatement() {
                return null;
            }
        }
    }
//
}
