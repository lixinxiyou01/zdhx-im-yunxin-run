/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package zhwx.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.netease.nim.demo.DemoCache;
import com.netease.nim.demo.ECApplication;

import zhwx.common.util.IMUtils;
import zhwx.common.util.LogUtil;


/**
 * 数据库访问接口
 * @author Li.Xin @ 中电和讯
 * @date 2016-11-24
 * @version 2.0
 */
public abstract class AbstractSQLManager {

    public static final String TAG = AbstractSQLManager.class.getName();

    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase sqliteDB;

    public AbstractSQLManager() {
        openDatabase(ECApplication.getInstance(), IMUtils.getVersionCode());
    }

    private void openDatabase(Context context, int databaseVersion) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context,this , databaseVersion);
        }
        if (sqliteDB == null) {
            sqliteDB = databaseHelper.getWritableDatabase();
        }
    }

    public void destroy() {
        try {
            if (databaseHelper != null) {
                databaseHelper.close();
            }
            closeDB();
        } catch (Exception e) {
            LogUtil.e(e.toString());
        }
    }

    private void open(boolean isReadonly) {
        if (sqliteDB == null) {
            if (isReadonly) {
                sqliteDB = databaseHelper.getReadableDatabase();
            } else {
                sqliteDB = databaseHelper.getWritableDatabase();/*DatabaseManager.getInstance().openDatabase()*/;
            }
        }
    }

    public final void reopen() {
        closeDB();
        open(false);
        LogUtil.w("[SQLiteManager] reopen this db.");
    }

    private void closeDB() {
        if (sqliteDB != null) {
            sqliteDB = null;
            sqliteDB.close();
            sqliteDB = null;
        }
    }

    protected final SQLiteDatabase sqliteDB() {
        open(false);
        return sqliteDB;
    }

    /**
     * 创建基础表结构
     */
    static class DatabaseHelper extends SQLiteOpenHelper {

        /**数据库名称*/
        static final String DATABASE_NAME = "ZDHX_IM.db";
        static final String DESC = "DESC";
        static final String ASC = "ASC";
        static final String TABLES_NAME_CONTACT = "contacts";
//        static final String TABLES_NAME_SYSTEM_NOTICE = "system_notice";

        private AbstractSQLManager mAbstractSQLManager;

        public DatabaseHelper(Context context, AbstractSQLManager manager ,int version) {
            this(context, manager , DemoCache.getAccount() + "_" + DATABASE_NAME, null, version);
        }

        public DatabaseHelper(Context context, AbstractSQLManager manager , String name,CursorFactory factory, int version) {
            super(context, name, factory, version);
            mAbstractSQLManager = manager ;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            createTables(db);
        }

        /**
         * @param db
         */
        private void createTables(SQLiteDatabase db) {
            // 创建联系人表
            createTableForContacts(db);
        }

        /**
         * 创建联系人表
         * @param db
         */
        void createTableForContacts(SQLiteDatabase db) {

            String sql = "CREATE TABLE IF NOT EXISTS "
                    + TABLES_NAME_CONTACT
                    + " ("
                    + ContactsColumn.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ContactsColumn.CONTACT_ID + " TEXT UNIQUE ON CONFLICT ABORT, "
                    + ContactsColumn.type + " INTEGER, "
                    + ContactsColumn.USERNAME + " TEXT, "
                    + ContactsColumn.SEX + " TEXT, "
                    + ContactsColumn.TOKEN + " TEXT, "
                    + ContactsColumn.PHOTO + " TEXT, "
                    + ContactsColumn.SIGNATURE + " TEXT, "
                    + ContactsColumn.DEPARTMENT + " TEXT, "
                    + ContactsColumn.REMARK + " TEXT, "
                    + ContactsColumn.IM_ID + " TEXT ,"
                    + ContactsColumn.V3_ID + " TEXT ,"
                    + ContactsColumn.PINYIN + " TEXT "
                    + ")";
            LogUtil.v(TAG + ":" + sql);
            db.execSQL(sql);
        }
    }

    class BaseColumn {
        public static final String ID = "ID";
        public static final String UNREAD_NUM = "unreadCount";
    }

    /**
     * 联系人表
     */
    public class ContactsColumn extends BaseColumn {
        /**账号*/
        public static final String CONTACT_ID = "contact_id";
        /**Token*/
        public static final String TOKEN = "token";
        /**昵称*/
        public static final String USERNAME = "username";
        /**性别*/
        public static final String SEX = "sex";
        /**类型*/
        public static final String type = "type";
        /**头像*/
        public static final String PHOTO = "headImaUrl";
        /**签名*/
        public static final String SIGNATURE = "signature";
        /**部门*/
        public static final String DEPARTMENT = "department";
        /**备注*/
        public static final String REMARK = "remark";
        /**IMid*/
        public static final String IM_ID = "im_id";
        /**V3Id*/
        public static final String V3_ID = "v3_id";
        /**拼音*/
        public static final String PINYIN = "pinyin";
    }



    private final MessageObservable mMsgObservable = new MessageObservable();

    /**注册数据观察者*/
    protected void registerObserver(OnMessageChange observer) {
        mMsgObservable.registerObserver(observer);
    }

    protected void unregisterObserver(OnMessageChange observer) {
        mMsgObservable.unregisterObserver(observer);
    }

    protected void notifyChanged(String session) {
        mMsgObservable.notifyChanged(session);
    }


    protected void release() {
        destroy();
        closeDB();
        databaseHelper = null;
    }
}
