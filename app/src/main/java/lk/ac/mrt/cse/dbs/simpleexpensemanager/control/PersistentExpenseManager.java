package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

public class PersistentExpenseManager extends ExpenseManager{
    public Context context;

    public PersistentExpenseManager(Context context) throws ExpenseManagerException {
        this.context = context;
        setup();
    }

    @Override
    public void setup() throws ExpenseManagerException {

        SQLiteDatabase db = context.openOrCreateDatabase("180108M", context.MODE_PRIVATE, null);

        db.execSQL("CREATE TABLE IF NOT EXISTS Accounts (" +
                "accountNo VARCHAR PRIMARY KEY, " +
                "bankName VARCHAR, " +
                "accountHolderName VARCHAR, " +
                "balance REAL);");

        db.execSQL("CREATE TABLE IF NOT EXISTS TransactionLog (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "accountNo TEXT ," +
                "expenseType LONG , " +
                "amount DOUBLE, " +
                "date LONG, " +
                "FOREIGN KEY(accountNo) REFERENCES Accounts(accountNo));");

        TransactionDAO persistentTransactionDAO = new PersistentTransactionDAO(db);
        setTransactionsDAO(persistentTransactionDAO);

        AccountDAO persistentAccountDAO = new PersistentAccountDAO(db);
        setAccountsDAO(persistentAccountDAO);
    }
}
