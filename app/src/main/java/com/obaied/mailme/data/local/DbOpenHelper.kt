package com.obaied.mailme.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ab on 06.08.17.
 */

//@Singleton
//class DbOpenHelper
//@Inject constructor(Context: Context) :
//        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
//
//    override fun onCreate(db: SQLiteDatabase) {
////        db.beginTransaction()
//        try {
////            db.execSQL(Db.DbQuotesTable.CREATE)
////            db.setTransactionSuccessful()
//        } finally {
////            db.endTransaction()
//        }
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
//
//    companion object {
//        val DATABASE_NAME = "mailmes.db"
//        val DATABASE_VERSION = 1
//    }
//}