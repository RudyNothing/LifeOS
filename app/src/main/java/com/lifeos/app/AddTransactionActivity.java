
package com.lifeos.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.lifeos.app.data.entity.FinanceTransaction;
import com.lifeos.app.viewmodel.FinanceTransactionViewModel;

import java.util.Calendar;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private EditText editTitle;
    private EditText editAmount;
    private EditText editDescription;

    private Spinner spinnerCategory;
    private Spinner spinnerPaymentMethod;

    private RadioGroup radioTransactionType;

    private Button buttonDate;
    private Button buttonSave;

    private FinanceTransactionViewModel financeViewModel;

    private String transactionType = "Income";
    private long selectedDate;

    private FinanceTransaction editingTransaction;
    private boolean isEditMode = false;

    private final String[] incomeCategories = {
            "Salary", "Business", "Freelance", "Investments", "Other"
    };

    private final String[] expenseCategories = {
            "Food", "Travel", "Education", "Shopping",
            "Bills", "Entertainment", "Healthcare", "Other"
    };

    private final String[] paymentMethods = {
            "Cash", "UPI", "Debit Card",
            "Credit Card", "Bank Transfer", "Other"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_add_transaction);

        View root = findViewById(R.id.addTransactionRoot);

        final int initialLeft = root.getPaddingLeft();
        final int initialTop = root.getPaddingTop();
        final int initialRight = root.getPaddingRight();
        final int initialBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (view, windowInsets) -> {
            Insets insets = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            view.setPadding(
                    initialLeft + insets.left,
                    initialTop + insets.top,
                    initialRight + insets.right,
                    initialBottom + insets.bottom
            );

            return windowInsets;
        });

        ViewCompat.requestApplyInsets(root);

        editTitle = findViewById(R.id.editTransactionTitle);
        editAmount = findViewById(R.id.editTransactionAmount);
        editDescription = findViewById(R.id.editTransactionDescription);

        spinnerCategory = findViewById(R.id.spinnerTransactionCategory);
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);

        radioTransactionType = findViewById(R.id.radioTransactionType);

        buttonDate = findViewById(R.id.buttonTransactionDate);
        buttonSave = findViewById(R.id.buttonSaveTransaction);

        Button buttonCancel = findViewById(R.id.buttonCancelTransaction);

        financeViewModel = new ViewModelProvider(this)
                .get(FinanceTransactionViewModel.class);

        selectedDate = Calendar.getInstance().getTimeInMillis();

        setupPaymentMethodSpinner();
        updateCategorySpinner();
        updateDateButton();

        radioTransactionType.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.radioIncome) {
                transactionType = "Income";
            } else {
                transactionType = "Expense";
            }

            updateCategorySpinner();
        });

        buttonDate.setOnClickListener(v -> showDatePicker());

        buttonSave.setOnClickListener(v -> saveTransaction());

        buttonCancel.setOnClickListener(v -> finish());

        int transactionId = getIntent().getIntExtra("transaction_id", -1);

        if (transactionId != -1) {
            isEditMode = true;
            buttonSave.setText("Update Transaction");
            loadTransaction(transactionId);
        }
    }

    private void setupPaymentMethodSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                paymentMethods
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerPaymentMethod.setAdapter(adapter);
    }

    private void updateCategorySpinner() {

        String[] categories = transactionType.equals("Income")
                ? incomeCategories
                : expenseCategories;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerCategory.setAdapter(adapter);
    }

    private void loadTransaction(int transactionId) {

        financeViewModel.getTransactionById(transactionId)
                .observe(this, transaction -> {

                    if (transaction == null) {
                        return;
                    }

                    editingTransaction = transaction;

                    editTitle.setText(transaction.getTitle());
                    editAmount.setText(
                            String.valueOf(transaction.getAmount())
                    );
                    editDescription.setText(transaction.getDescription());

                    transactionType = transaction.getType();

                    if ("Expense".equalsIgnoreCase(transactionType)) {
                        radioTransactionType.check(R.id.radioExpense);
                    } else {
                        transactionType = "Income";
                        radioTransactionType.check(R.id.radioIncome);
                    }

                    updateCategorySpinner();

                    setSpinnerSelection(
                            spinnerCategory,
                            transaction.getCategory()
                    );

                    setSpinnerSelection(
                            spinnerPaymentMethod,
                            transaction.getPaymentMethod()
                    );

                    selectedDate = transaction.getDate();
                    updateDateButton();
                });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {

        if (value == null || spinner.getAdapter() == null) {
            return;
        }

        android.widget.SpinnerAdapter adapter = spinner.getAdapter();

        for (int i = 0; i < adapter.getCount(); i++) {

            Object item = adapter.getItem(i);

            if (value.equals(String.valueOf(item))) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);

        DatePickerDialog datePicker = new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    Calendar selected = Calendar.getInstance();

                    selected.set(
                            year,
                            month,
                            day,
                            12,
                            0,
                            0
                    );

                    selected.set(Calendar.MILLISECOND, 0);

                    selectedDate = selected.getTimeInMillis();

                    updateDateButton();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.getDatePicker().setMaxDate(
                System.currentTimeMillis()
        );

        datePicker.show();
    }

    private void updateDateButton() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);

        String date = String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );

        buttonDate.setText(date);
    }

    private void saveTransaction() {

        String title = editTitle.getText().toString().trim();
        String amountText = editAmount.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (title.isEmpty()) {
            editTitle.setError("Enter transaction title");
            editTitle.requestFocus();
            return;
        }

        if (amountText.isEmpty()) {
            editAmount.setError("Enter transaction amount");
            editAmount.requestFocus();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            editAmount.setError("Enter a valid amount");
            editAmount.requestFocus();
            return;
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
            editAmount.setError("Amount must be greater than zero");
            editAmount.requestFocus();
            return;
        }

        String category = spinnerCategory.getSelectedItem().toString();

        String paymentMethod =
                spinnerPaymentMethod.getSelectedItem().toString();

        long currentTime = System.currentTimeMillis();

        if (isEditMode && editingTransaction != null) {

            editingTransaction.setType(transactionType);
            editingTransaction.setAmount(amount);
            editingTransaction.setCategory(category);
            editingTransaction.setTitle(title);
            editingTransaction.setDescription(description);
            editingTransaction.setDate(selectedDate);
            editingTransaction.setPaymentMethod(paymentMethod);
            editingTransaction.setUpdatedAt(currentTime);

            financeViewModel.update(editingTransaction);

            Toast.makeText(
                    this,
                    "Transaction updated",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            FinanceTransaction transaction = new FinanceTransaction(
                    transactionType,
                    amount,
                    category,
                    title,
                    description,
                    selectedDate,
                    paymentMethod,
                    currentTime,
                    currentTime
            );

            financeViewModel.insert(transaction);

            Toast.makeText(
                    this,
                    "Transaction saved",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }
}
