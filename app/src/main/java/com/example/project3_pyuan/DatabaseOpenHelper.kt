package com.example.project3_pyuan

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseOpenHelper(context: Context) : SQLiteOpenHelper(context, "HydrateDB", null, 2) {
    private val hydrateTable: String = "HYDRATE_TABLE"
    private val date: String = "DATE"
    private val waterConsumed: String = "WATER_CONSUMED" // stored as milliliters
    private val hydrateColumns: Array<String> = arrayOf(date, waterConsumed)

    override fun onCreate(db: SQLiteDatabase) {
        val createHydrateTable: String = "CREATE TABLE $hydrateTable ($date INTEGER, $waterConsumed INTEGER);"
        db.execSQL(createHydrateTable)
//        Log.w("INFO", "Worked")

//        val values: ContentValues = ContentValues()
//        values.put(date, System.currentTimeMillis() / 1000)
//        values.put(waterConsumed, 0)
//        db.insert(hydrateTable, null, values);
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    fun readTodayVal(): Int {
        val db: SQLiteDatabase  = this.readableDatabase
        val cursor = db.query(hydrateTable, hydrateColumns, null, null, null, null, null)
        cursor.moveToFirst()
        val x = cursor.getInt(0)
        cursor.close()
        return x
    }
}