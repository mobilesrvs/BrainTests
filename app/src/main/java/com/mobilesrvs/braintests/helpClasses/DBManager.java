package com.mobilesrvs.braintests.helpClasses;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mobilesrvs.braintests.objects.QuestionObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Vikesh PC on 08-04-2016.
 */
public class DBManager extends SQLiteOpenHelper {

    private static final String Database_path = "/data/data/com.mobilesrvs.braintests/databases/";
    private static final String Database_name = "questions.db";//NAME of database stored in Assets folder
    private static final String Table_name = "question";//name of table
    private static final String uid = "_id";//name of column1

    private static final int version = 1;//version of database signifies if there is any upgradation or not
    public SQLiteDatabase sqlite;//object of type SQLiteDatabase
    private Context context;//Context object to get context from Question Activity

    public DBManager(Context context) {//constructor
        super(context, Database_name, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //No code because we have already created the database
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //No code because we have already created the database
    }

    public void createDatabase() {
        createDB();
    }

    private void createDB() {

        boolean dbexist = DBexists();//calling the function to check db exists or not
        if (!dbexist)//if database doesnot exist
        {

            this.getReadableDatabase();//Create an empty file
            copyDBfromResource();//copy the database file information of assets folder to newly create file
        }
    }

    private void copyDBfromResource() {

        InputStream is;
        OutputStream os;
        String filePath = Database_path + Database_name;
        try {
            is = context.getAssets().open(Database_name);//reading purpose
            os = new FileOutputStream(filePath);//writing purpose
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);//writing to file
            }
            os.flush();//flush the outputstream
            is.close();//close the inputstream
            os.close();//close the outputstream

        } catch (IOException e) {
            throw new Error("Problem copying database file:");
        }
    }

    public void openDatabase() throws SQLException//called by onCreate method of Questions Activity
    {

        String myPath = Database_path + Database_name;
        sqlite = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    private boolean DBexists()//Check whether the db file exists or not
    {
        SQLiteDatabase db = null;
        try {
            String databasePath = Database_path + Database_name;
            db = SQLiteDatabase.openDatabase(databasePath, null, SQLiteDatabase.OPEN_READWRITE);
            db.setLocale(Locale.getDefault());
            db.setVersion(1);
            db.setLockingEnabled(true);
        } catch (SQLException e) {
            Log.e("Sqlite", "Database not found");
        }
        if (db != null)
            db.close();///close the opened file
        return db != null ? true : false;

    }

    public ArrayList<QuestionObject> getAllQuestionByType(String type) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<QuestionObject> subs = new ArrayList<>();
        QuestionObject question;
        try {
            Cursor cursor = db.query(Table_name, null, "Type=?", new String[]{type}, null, null, null);


            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();
                    question = new QuestionObject();

                    question.setUid(cursor.getString(0));
                    question.setQuestion(cursor.getString(1));
                    question.setOptionA(cursor.getString(2));
                    question.setOptionB(cursor.getString(3));
                    question.setOptionC(cursor.getString(4));
                    question.setOptionD(cursor.getString(5));
                    question.setAnswer(cursor.getString(6));
                    question.setType(cursor.getString(7));

                    subs.add(question);
                }
            }
            cursor.close();
            db.close();
        } catch (SQLException e) {
            Log.e("getAllQuestionByType", "Database not found" +type);
        }
        return subs;
    }
}
