package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO{
    public SQLiteDatabase db;

    public PersistentTransactionDAO(SQLiteDatabase db){
        this.db=db;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String sql = "INSERT INTO TransactionLog (accountNo,expenseType,amount,date) VALUES (?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String strDate = formatter.format(date);

        statement.bindString(1,accountNo);
        statement.bindLong(2,(expenseType == ExpenseType.EXPENSE) ? 0 :1);
        statement.bindDouble(3,amount);
        statement.bindString(4,strDate);

        statement.executeInsert();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {

        Cursor result = db.rawQuery("SELECT * FROM TransactionLog",null);
        List<Transaction> logList = new ArrayList<>();

        if(result.moveToFirst()){
            do{
                Transaction transaction = new Transaction(new Date(result.getLong(result.getColumnIndex("date"))),
                        result.getString(result.getColumnIndex("accountNo")), (result.getInt(result.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        result.getDouble(result.getColumnIndex("amount")));

                logList.add(transaction);
            }while(result.moveToNext());
        }

        return logList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor result = db.rawQuery("SELECT * FROM TransactionLog LIMIT " + limit,null);
        List<Transaction> transactions = new ArrayList<>();

        if(result.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(new Date(result.getLong(result.getColumnIndex("date"))), result.getString(result.getColumnIndex("accountNo")), (result.getInt(result.getColumnIndex("expenseType")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME, result.getDouble(result.getColumnIndex("amount")));
                transactions.add(transaction);
            } while (result.moveToNext());
        }

        return transactions;
    }
}
