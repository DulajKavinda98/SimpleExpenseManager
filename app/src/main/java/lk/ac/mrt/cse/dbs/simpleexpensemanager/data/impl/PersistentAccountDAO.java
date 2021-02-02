package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO{
    public SQLiteDatabase db;

    public PersistentAccountDAO(SQLiteDatabase db){this.db = db;}

    @Override
    public List<String> getAccountNumbersList() {
        Cursor result = db.rawQuery("SELECT accountNo FROM Accounts",null);
        List<String> accountNumberList = new ArrayList<>();

        if(result.moveToFirst()){
            do{
                accountNumberList.add(result.getString(result.getColumnIndex("accountNo")));
            }while(result.moveToNext());
        }
        return accountNumberList;
    }

    @Override
    public List<Account> getAccountsList() {
        Cursor result = db.rawQuery("SELECT * FROM Accounts",null);
        List<Account> accountList = new ArrayList<>();

        if(result.moveToFirst()){
            do{
                accountList.add(new Account(result.getString(result.getColumnIndex("accountNo")),
                        result.getString(result.getColumnIndex("bankName")),
                        result.getString(result.getColumnIndex("accountHolderName")),
                        result.getDouble(result.getColumnIndex("balance"))));

            }while(result.moveToNext());
        }

        return accountList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor result = db.rawQuery("SELECT * FROM Accounts WHERE accountNo = "+accountNo,null);
        Account account = null;

        if(result.moveToFirst()){
            do{
                account = new Account(result.getString(result.getColumnIndex("accountNo")),
                        result.getString(result.getColumnIndex("bankName")),
                        result.getString(result.getColumnIndex("accountHolderName")),
                        result.getDouble(result.getColumnIndex("balance")));
            }while(result.moveToNext());
        }

        return account;
    }

    @Override
    public void addAccount(Account account) {
        String sql = "INSERT INTO Accounts (accountNo,bankName,accountHolderName,balance) VALUES (?,?,?,?)";
        SQLiteStatement statement = db.compileStatement(sql);

        statement.bindString(1, account.getAccountNo());
        statement.bindString(2, account.getBankName());
        statement.bindString(3, account.getAccountHolderName());
        statement.bindDouble(4, account.getBalance());

        statement.executeInsert();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sql1 = "DELETE FROM TransactionLog WHERE accountNo =?";
        String sql2 = "DELETE FROM Accounts WHERE accountNo =?";
        SQLiteStatement statement1 = db.compileStatement(sql1);
        SQLiteStatement statement2 = db.compileStatement(sql2);

        statement1.bindString(1,accountNo);
        statement2.bindString(1,accountNo);

        statement1.executeUpdateDelete();
        statement2.executeUpdateDelete();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String sql = "UPDATE Accounts SET balance = balance + ? WHERE accountNo = ?";
        SQLiteStatement statement = db.compileStatement(sql);

        if(expenseType == ExpenseType.EXPENSE){
            statement.bindDouble(1,-amount);
        }else{
            statement.bindDouble(1,amount);
        }
        statement.bindString(2,accountNo);

        statement.executeUpdateDelete();
    }
}
