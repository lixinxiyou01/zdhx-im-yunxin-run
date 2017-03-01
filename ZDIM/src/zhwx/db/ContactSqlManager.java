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

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.netease.nim.demo.DemoCache;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import zhwx.common.model.ECContacts;

/**
 * 联系人数据库管理
 * @author Li.Xin @ 中电和讯
 * @date 2016-11-24 10:00:13
 */
public class ContactSqlManager extends AbstractSQLManager {

    private static ContactSqlManager sInstance;
    private static ContactSqlManager getInstance() {
        if(sInstance == null) {
            sInstance = new ContactSqlManager();
        }
        return sInstance;
    }

    public static boolean hasContact(String contactId) {
        String sql = "select contact_id from contacts where contact_id = '" + contactId + "'";
        Cursor cursor = getInstance().sqliteDB().rawQuery(sql , null);
        if(cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 插入联系人到数据库
     * @param contacts
     * @return
     */
    public static ArrayList<Long> insertContacts(List<ECContacts> contacts) {

        ArrayList<Long> rows = new ArrayList<Long>();
        try {
            getInstance().sqliteDB().beginTransaction();
            for(ECContacts c : contacts) {
                long rowId = insertContact(c);
                if(rowId != -1L) {
                    rows.add(rowId);
                }
            }
            // 初始化系统联系人
//            insertSystemNoticeContact();
            getInstance().sqliteDB().setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getInstance().sqliteDB().endTransaction();
        }
        return rows;
    }

//    /**
//     * 初始化联系人数据库
//     * @return
//     * @return
//     */
//    private static void insertSystemNoticeContact() {
//
//        ECContacts contacts = new ECContacts(GroupNoticeSqlManager.CONTACT_ID);
//        contacts.setNickname("群组通知");
//        contacts.setRemark("touxiang_notice.png");
//        insertContact(contacts);
//
//        ECContacts contact = new ECContacts(GroupNoticeSqlManager.CONTACT_ID1);
//        contact.setNickname("新的朋友");
//        contact.setRemark("touxiang_notice.png");
//        insertContact(contact);
//
//        ECContacts contact1 = new ECContacts(GroupNoticeSqlManager.CONTACT_ID2);
//        contact1.setNickname("消息提醒");
//        contact1.setRemark("touxiang_notice.png");
//        insertContact(contact1);
//    }

    public static long updateContactPhoto(ECContacts contact) {
        return insertContact(contact , 1 , true);
    }

    public static void deleteContactByid(String contactId){
    	long l = getInstance().sqliteDB().delete(DatabaseHelper.TABLES_NAME_CONTACT, "contact_id LIKE '" + contactId + "'", null);
    }
    
    
    /**
     * 根据性别更新联系人信息（区分联系人头像）
     * @param contact
     * @param sex
     * @return
     */
    public static long insertContact(ECContacts contact , int sex) {
        return insertContact(contact , sex , true);
    }
    
    public static long insertContact(ECContacts contact , int sex , boolean hasPhoto) {
        if(contact == null || TextUtils.isEmpty(contact.getContactid())) {
            return -1;
        }
        try {
            ContentValues values = contact.buildContentValues();
//            if(!hasPhoto ) {
//                int index = getIntRandom(3, 0);
//                if(sex == 2) {
//                    index = 4;
//                }
//                String remark = ContactLogic.CONVER_PHONTO[index];
//                contact.setRemark(remark);
//            }
            values.put(AbstractSQLManager.ContactsColumn.REMARK, contact.getRemark());
            if(!hasContact(contact.getContactid())) {
                return getInstance().sqliteDB().insert(DatabaseHelper.TABLES_NAME_CONTACT, null, values);
            }
            getInstance().sqliteDB().update(DatabaseHelper.TABLES_NAME_CONTACT , values , "contact_id = '" + contact.getContactid() + "'" , null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 插入联系人到数据库
     * @param contact
     * @return
     */
    public static long insertContact(ECContacts contact) {
        return insertContact(contact , 1);
    }
    
    /**
     * 查询联系人名称
     * @param contactId
     * @return
     */
    public static ArrayList<String> getContactName(String[] contactId) {
        ArrayList<String> contacts = null;
        try {
            String sql = "select username ,contact_id from contacts where contact_id in ";
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < contactId.length; i++) {
                sb.append("'").append(contactId[i]).append("'");
                if (i != contactId.length - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            Cursor cursor = getInstance().sqliteDB().rawQuery(
                    sql + sb.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                contacts = new ArrayList<String>();
                // 过滤自己的联系人账号
                while (cursor.moveToNext()) {
                    if (DemoCache.getAccount().equals(
                            cursor.getString(0))) {
                        continue;
                    }
                    String displayName = cursor.getString(0);
                    String contact_id = cursor.getString(1);
                    if(TextUtils.isEmpty(displayName) || TextUtils.isEmpty(contact_id) || displayName.equals(contact_id)) {
                        continue;
                    }
                    contacts.add(displayName);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * 查询联系人名称
     * @param contactId
     * @return
     */
    public static ArrayList<String> getContactRemark(String[] contactId) {
        ArrayList<String> contacts = null;
        try {
            String sql = "select remark from contacts where contact_id in ";
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < contactId.length; i++) {
                sb.append("'").append(contactId[i]).append("'");
                if (i != contactId.length - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            Cursor cursor = getInstance().sqliteDB().rawQuery(
                    sql + sb.toString(), null);
            if (cursor != null && cursor.getCount() > 0) {
                contacts = new ArrayList<String>();
                while (cursor.moveToNext()) {
                    contacts.add(cursor.getString(0));
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * 根据联系人账号查询
     * @param contactId
     * @return
     */
    public static ECContacts getContact(String contactId) {
        if(TextUtils.isEmpty(contactId)) {
            return null;
        }
        ECContacts c = new ECContacts(contactId);
        c.setNickname(contactId);
        try {
            Cursor cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_CONTACT, new String[]{ContactsColumn.ID,ContactsColumn.USERNAME ,ContactsColumn.CONTACT_ID ,ContactsColumn.REMARK},
                    "contact_id=?", new String[]{contactId},null , null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    c = new ECContacts(cursor.getString(2));
                    c.setNickname(cursor.getString(1));
                    c.setRemark(cursor.getString(3));
                    c.setId(cursor.getInt(0));
                }
                cursor.close();
            }
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * 根据昵称查询信息
     * @param nikeName
     * @return
     */
    public static ECContacts getContactLikeUsername(String nikeName) {
        if(TextUtils.isEmpty(nikeName)) {
            return null;
        }
        try {
            Cursor cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_CONTACT, new String[]{ContactsColumn.ID,ContactsColumn.USERNAME ,ContactsColumn.CONTACT_ID ,ContactsColumn.REMARK},
                    "username LIKE '" + nikeName + "'" , null,null , null, null, null);
            ECContacts c = null;
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    c = new ECContacts(cursor.getString(2));
                    c.setNickname(cursor.getString(1));
                    c.setRemark(cursor.getString(3));
                    c.setId(cursor.getInt(0));
                }
                cursor.close();
            }
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 根据昵称模糊查询信息
     * @param nikeName
     * @return
     */
    public static List<ECContacts> getContactByUsername(String nikeName) {
    	if(TextUtils.isEmpty(nikeName)) {
    		return null;
    	}
    	try {
    		Cursor cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_CONTACT, new String[]{ContactsColumn.ID,ContactsColumn.USERNAME ,ContactsColumn.CONTACT_ID ,ContactsColumn.REMARK,ContactsColumn.IM_ID,ContactsColumn.V3_ID},
    				"pinyin LIKE '%" + nikeName + "%'" , null,null , null, null, null);
    		List<ECContacts> list = new ArrayList<ECContacts>();
    		if (cursor != null && cursor.getCount() > 0) {
    			while (cursor.moveToNext()) {
                    ECContacts c = new ECContacts(cursor.getString(2));
                    c.setId(cursor.getInt(0));
                    c.setNickname(cursor.getString(1));
                    c.setRemark(cursor.getString(3));
                    c.setImId(cursor.getString(4));
                    c.setV3Id(cursor.getString(5));
    				list.add(c);
    			}
    			cursor.close();
    		}
    		return list;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

    public static ECContacts getContactLikeUserId(String nikeName) {
        if(TextUtils.isEmpty(nikeName)) {
            return null;
        }
        try {
            Cursor cursor = getInstance().sqliteDB().query(DatabaseHelper.TABLES_NAME_CONTACT, new String[]{ContactsColumn.ID,ContactsColumn.USERNAME ,ContactsColumn.CONTACT_ID ,ContactsColumn.REMARK},
                    "username LIKE '" + nikeName + "'" , null,null , null, null, null);
            ECContacts c = null;
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    c = new ECContacts(cursor.getString(2));
                    c.setNickname(cursor.getString(1));
                    c.setRemark(cursor.getString(3));
                    c.setId(cursor.getInt(0));
                }
                cursor.close();
            }
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void reset() {
        getInstance().release();
        sInstance = null;
    }

    public static int getIntRandom(int max, int min) {
        Assert.assertTrue(max > min);
        return (new Random(System.currentTimeMillis()).nextInt(max - min + 1) + min);
    }
}
