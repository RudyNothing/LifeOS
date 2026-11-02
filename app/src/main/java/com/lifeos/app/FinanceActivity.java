
package com.lifeos.app;

import android.app.Dialog;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.content.res.ColorStateList;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.lifeos.app.data.entity.FinanceTransaction;
import com.lifeos.app.viewmodel.FinanceTransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FinanceActivity extends AppCompatActivity {

    private FinanceTransactionViewModel financeViewModel;

    private TextView textFinanceBalance;
    private TextView textTotalIncome;
    private TextView textTotalExpenses;
    private TextView textFinanceEmpty;
    private LinearLayout layoutFinanceTransactions;

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

    private Dialog transactionDialog;

    private EditText editTitle;
    private EditText editAmount;
    private EditText editDescription;

    private RadioGroup radioTransactionType;

    private TextView textCategory;
    private TextView textPaymentMethod;
    private TextView textTransactionDate;

    private String transactionType = "Income";
    private String selectedCategory = "Salary";
    private String selectedPaymentMethod = "Cash";

    private long selectedDate;

    private FinanceTransaction editingTransaction;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_finance);

        View root = findViewById(R.id.financeRoot);

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

        textFinanceBalance = findViewById(R.id.textFinanceBalance);
        textTotalIncome = findViewById(R.id.textTotalIncome);
        textTotalExpenses = findViewById(R.id.textTotalExpenses);
        textFinanceEmpty = findViewById(R.id.textFinanceEmpty);

        layoutFinanceTransactions =
                findViewById(R.id.layoutFinanceTransactions);

        Button buttonBack = findViewById(R.id.buttonFinanceBack);
        Button buttonAddTransaction = findViewById(R.id.buttonAddTransaction);
        buttonAddTransaction.setBackgroundResource(R.drawable.bg_finance_primary);
        buttonAddTransaction.setTextColor(getColor(android.R.color.white));
        buttonAddTransaction.setAllCaps(false);

        buttonBack.setOnClickListener(v -> finish());

        buttonAddTransaction.setOnClickListener(v ->
                showTransactionDialog(null)
        );

        financeViewModel = new ViewModelProvider(this)
                .get(FinanceTransactionViewModel.class);

        financeViewModel.getAllTransactions().observe(this, transactions -> {

            double totalIncome = 0;
            double totalExpenses = 0;

            layoutFinanceTransactions.removeAllViews();

            if (transactions == null || transactions.isEmpty()) {
                textFinanceEmpty.setVisibility(View.VISIBLE);
            } else {
                textFinanceEmpty.setVisibility(View.GONE);

                for (int i = transactions.size() - 1; i >= 0; i--) {

                    FinanceTransaction transaction = transactions.get(i);

                    if (transaction == null) {
                        continue;
                    }

                    if ("Income".equalsIgnoreCase(transaction.getType())) {
                        totalIncome += transaction.getAmount();
                    } else if ("Expense".equalsIgnoreCase(transaction.getType())) {
                        totalExpenses += transaction.getAmount();
                    }

                    addTransactionRow(transaction);
                }
            }

            textTotalIncome.setText(formatCurrency(totalIncome));
            textTotalExpenses.setText(formatCurrency(totalExpenses));

            textFinanceBalance.setText(
                    formatCurrency(totalIncome - totalExpenses)
            );
        });
    }

    // ---------------------------------------------------------
    // TRANSACTION EDITOR
    // ---------------------------------------------------------

    private void showTransactionDialog(FinanceTransaction transaction) {

        isEditMode = transaction != null;
        editingTransaction = transaction;

        transactionType = "Income";
        selectedCategory = "Salary";
        selectedPaymentMethod = "Cash";
        selectedDate = Calendar.getInstance().getTimeInMillis();

        transactionDialog = new Dialog(this);
        transactionDialog.setContentView(
                R.layout.dialog_transaction_editor
        );
        transactionDialog.setCancelable(true);

        Window window = transactionDialog.getWindow();

        if (window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT)
            );

            window.setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            );
        }

        editTitle = transactionDialog.findViewById(
                R.id.editDialogTransactionTitle
        );

        editAmount = transactionDialog.findViewById(
                R.id.editDialogTransactionAmount
        );

        editDescription = transactionDialog.findViewById(
                R.id.editDialogTransactionDescription
        );

        radioTransactionType = transactionDialog.findViewById(
                R.id.radioDialogTransactionType
        );

        textCategory = transactionDialog.findViewById(
                R.id.textDialogTransactionCategory
        );

        textPaymentMethod = transactionDialog.findViewById(
                R.id.textDialogPaymentMethod
        );

        textTransactionDate = transactionDialog.findViewById(
                R.id.textDialogTransactionDate
        );

        TextView dialogTitle = transactionDialog.findViewById(
                R.id.textTransactionDialogTitle
        );

        Button saveButton = transactionDialog.findViewById(
                R.id.buttonDialogSaveTransaction
        );

        Button cancelButton = transactionDialog.findViewById(
                R.id.buttonDialogCancelTransaction
        );
        saveButton.setBackgroundResource(R.drawable.bg_finance_primary);
        saveButton.setTextColor(getColor(android.R.color.white));
        saveButton.setAllCaps(false);

        cancelButton.setBackgroundResource(R.drawable.bg_finance_secondary);
        cancelButton.setTextColor(getColor(R.color.lifeos_text));
        cancelButton.setAllCaps(false);

        RadioButton incomeRadio = transactionDialog.findViewById(
                R.id.radioDialogIncome
        );

        RadioButton expenseRadio = transactionDialog.findViewById(
                R.id.radioDialogExpense
        );

        incomeRadio.setButtonTintList(
                ColorStateList.valueOf(getColor(R.color.finance_primary))
        );

        expenseRadio.setButtonTintList(
                ColorStateList.valueOf(getColor(R.color.finance_primary))
        );

        if (isEditMode) {

            dialogTitle.setText("Edit Transaction");
            saveButton.setText("Update Transaction");

            editTitle.setText(transaction.getTitle());
            editAmount.setText(String.valueOf(transaction.getAmount()));
            editDescription.setText(transaction.getDescription());

            transactionType = "Expense".equalsIgnoreCase(transaction.getType())
                    ? "Expense" : "Income";

            if ("Expense".equals(transactionType)) {
                radioTransactionType.check(R.id.radioDialogExpense);
            } else {
                radioTransactionType.check(R.id.radioDialogIncome);
            }

            selectedCategory = transaction.getCategory() == null
                    ? "Other" : transaction.getCategory();

            selectedPaymentMethod = transaction.getPaymentMethod() == null
                    ? "Cash" : transaction.getPaymentMethod();

            selectedDate = transaction.getDate();

        } else {

            dialogTitle.setText("Add Transaction");
            saveButton.setText("Save Transaction");

            radioTransactionType.check(R.id.radioDialogIncome);
        }

        updateCategoryLabel();
        updatePaymentMethodLabel();
        updateDateLabel();

        radioTransactionType.setOnCheckedChangeListener((group, checkedId) -> {

            String newType = checkedId == R.id.radioDialogExpense
                    ? "Expense" : "Income";

            if (!transactionType.equals(newType)) {
                transactionType = newType;

                String[] categories = getCurrentCategories();

                selectedCategory = categories[0];

                updateCategoryLabel();
            }
        });

        textCategory.setOnClickListener(v ->
                showSelectionDialog(
                        "Choose Category",
                        getCurrentCategories(),
                        selectedCategory,
                        value -> {
                            selectedCategory = value;
                            updateCategoryLabel();
                        }
                )
        );

        textPaymentMethod.setOnClickListener(v ->
                showSelectionDialog(
                        "Payment Method",
                        paymentMethods,
                        selectedPaymentMethod,
                        value -> {
                            selectedPaymentMethod = value;
                            updatePaymentMethodLabel();
                        }
                )
        );

        textTransactionDate.setOnClickListener(v ->
                showCustomDatePicker()
        );

        saveButton.setOnClickListener(v ->
                saveTransaction()
        );

        cancelButton.setOnClickListener(v ->
                transactionDialog.dismiss()
        );

        transactionDialog.show();

        if (window != null) {
            window.setLayout(
                    getResources().getDisplayMetrics().widthPixels - dp(32),
                    WindowManager.LayoutParams.WRAP_CONTENT
            );

            window.setGravity(Gravity.CENTER);
        }
    }

    private String[] getCurrentCategories() {
        return "Income".equals(transactionType)
                ? incomeCategories
                : expenseCategories;
    }

    private void updateCategoryLabel() {
        textCategory.setText(selectedCategory);
    }

    private void updatePaymentMethodLabel() {
        textPaymentMethod.setText(selectedPaymentMethod);
    }

    private void updateDateLabel() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate);

        String date = String.format(
                Locale.getDefault(),
                "%02d/%02d/%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );

        textTransactionDate.setText(date);
    }

    // ---------------------------------------------------------
    // CUSTOM CATEGORY AND PAYMENT METHOD SELECTOR
    // ---------------------------------------------------------

    private interface SelectionCallback {
        void onSelected(String value);
    }

    private void showSelectionDialog(
            String title,
            String[] options,
            String currentValue,
            SelectionCallback callback
    ) {

        Dialog dialog = new Dialog(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(20), dp(20), dp(20), dp(20));
        container.setBackgroundResource(R.drawable.bg_lifeos_card);

        TextView heading = new TextView(this);
        heading.setText(title);
        heading.setTextSize(20);
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTypeface(null, Typeface.BOLD);

        container.addView(heading);

        View divider = new View(this);
        divider.setBackgroundColor(getColor(R.color.lifeos_muted));

        LinearLayout.LayoutParams dividerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        dp(1)
                );

        dividerParams.topMargin = dp(12);
        dividerParams.bottomMargin = dp(10);

        container.addView(divider, dividerParams);

        for (String option : options) {

            TextView item = new TextView(this);

            item.setText(
                    (option.equals(currentValue) ? "✓   " : "     ") + option
            );

            item.setTextSize(15);

            item.setTextColor(getColor(
                    option.equals(currentValue)
                            ? R.color.finance_primary
                            : R.color.lifeos_text
            ));

            item.setGravity(Gravity.CENTER_VERTICAL);
            item.setPadding(dp(12), dp(13), dp(12), dp(13));
            item.setBackgroundResource(R.drawable.bg_lifeos_input);

            LinearLayout.LayoutParams itemParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            itemParams.bottomMargin = dp(7);

            container.addView(item, itemParams);

            item.setOnClickListener(v -> {
                callback.onSelected(option);
                dialog.dismiss();
            });
        }

        Button cancel = new Button(this);
        cancel.setText("Cancel");
        cancel.setAllCaps(false);

        container.addView(cancel);

        cancel.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(container);
        dialog.setCancelable(true);

        showDialogWindow(dialog, false);
    }

    // ---------------------------------------------------------
    // CUSTOM DATE PICKER
    // ---------------------------------------------------------

    private void showCustomDatePicker() {

        Dialog dialog = new Dialog(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(16), dp(16), dp(16), dp(12));
        container.setBackgroundResource(R.drawable.bg_lifeos_card);

        TextView heading = new TextView(this);
        heading.setText("Choose Transaction Date");
        heading.setTextSize(19);
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTypeface(null, Typeface.BOLD);

        container.addView(heading);

        DatePicker datePicker = new DatePicker(this);

        Calendar selectedCalendar = Calendar.getInstance();
        selectedCalendar.setTimeInMillis(selectedDate);

        datePicker.updateDate(
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );

        datePicker.setMaxDate(System.currentTimeMillis());

        LinearLayout.LayoutParams pickerParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        pickerParams.topMargin = dp(8);

        container.addView(datePicker, pickerParams);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.END);

        Button cancel = new Button(this);
        cancel.setText("Cancel");
        cancel.setAllCaps(false);

        Button confirm = new Button(this);
        confirm.setText("Confirm");
        confirm.setAllCaps(false);

        buttons.addView(cancel);
        buttons.addView(confirm);

        LinearLayout.LayoutParams buttonsParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonsParams.topMargin = dp(8);

        container.addView(buttons, buttonsParams);

        cancel.setOnClickListener(v -> dialog.dismiss());

        confirm.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            calendar.set(
                    datePicker.getYear(),
                    datePicker.getMonth(),
                    datePicker.getDayOfMonth(),
                    12,
                    0,
                    0
            );

            calendar.set(Calendar.MILLISECOND, 0);

            selectedDate = calendar.getTimeInMillis();

            updateDateLabel();

            dialog.dismiss();
        });

        dialog.setContentView(container);
        dialog.setCancelable(true);

        showDialogWindow(dialog, false);
    }

    // ---------------------------------------------------------
    // SAVE TRANSACTION
    // ---------------------------------------------------------

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

        long currentTime = System.currentTimeMillis();

        if (isEditMode && editingTransaction != null) {

            editingTransaction.setType(transactionType);
            editingTransaction.setAmount(amount);
            editingTransaction.setCategory(selectedCategory);
            editingTransaction.setTitle(title);
            editingTransaction.setDescription(description);
            editingTransaction.setDate(selectedDate);
            editingTransaction.setPaymentMethod(selectedPaymentMethod);
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
                    selectedCategory,
                    title,
                    description,
                    selectedDate,
                    selectedPaymentMethod,
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

        transactionDialog.dismiss();
    }

    // ---------------------------------------------------------
    // TRANSACTION HISTORY
    // ---------------------------------------------------------

    private void addTransactionRow(FinanceTransaction transaction) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(20));
        card.setBackgroundResource(R.drawable.bg_lifeos_card);
        card.setClipChildren(false);
        card.setClipToPadding(false);
        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(10);
        card.setLayoutParams(cardParams);

        TextView title = new TextView(this);

        String transactionTitle = transaction.getTitle();

        if (transactionTitle == null || transactionTitle.trim().isEmpty()) {
            transactionTitle = "Untitled transaction";
        }

        title.setText(transactionTitle);
        title.setTextColor(getColor(R.color.lifeos_text));
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);

        TextView details = new TextView(this);

        String category = transaction.getCategory() == null
                ? "Uncategorised" : transaction.getCategory();

        String type = transaction.getType() == null
                ? "Transaction" : transaction.getType();

        String date = new SimpleDateFormat(
                "dd MMM yyyy", Locale.getDefault()
        ).format(new Date(transaction.getDate()));

        details.setText(category + " • " + type + " • " + date);
        details.setTextColor(getColor(R.color.lifeos_muted));
        details.setTextSize(13);
        details.setPadding(0, dp(5), 0, 0);

        TextView amount = new TextView(this);

        String sign = "Income".equalsIgnoreCase(transaction.getType())
                ? "+"
                : "Expense".equalsIgnoreCase(transaction.getType())
                ? "−" : "";

        amount.setText(sign + formatCurrency(transaction.getAmount()));
        amount.setTextColor(getColor(R.color.lifeos_text));
        amount.setTextSize(16);
        amount.setTypeface(null, Typeface.BOLD);
        amount.setPadding(0, dp(8), 0, 0);


        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        actions.setClipChildren(false);
        actions.setClipToPadding(false);
        actions.setClipChildren(false);
        actions.setClipToPadding(false);
        Button editButton = actionButton("Edit", false);
        Button deleteButton = actionButton("Delete", true);

        editButton.setOnClickListener(v ->
                showTransactionDialog(transaction)
        );

        deleteButton.setOnClickListener(v ->
                confirmDeleteTransaction(transaction)
        );

        LinearLayout.LayoutParams actionParams =
                new LinearLayout.LayoutParams(
                        dp(104),
                        dp(52)
                );

        actionParams.setMargins(0, dp(10), dp(10), dp(2));

        actions.addView(editButton, actionParams);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        dp(104),
                        dp(52)
                );

        deleteParams.setMargins(0, dp(10), 0, dp(2));

        actions.addView(deleteButton, deleteParams);


        card.addView(title);
        card.addView(details);
        card.addView(amount);
        card.addView(actions);

        layoutFinanceTransactions.addView(card);
    }

    // ---------------------------------------------------------
    // CUSTOM DELETE CONFIRMATION
    // ---------------------------------------------------------

    private void confirmDeleteTransaction(FinanceTransaction transaction) {

        Dialog dialog = new Dialog(this);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(dp(22), dp(22), dp(22), dp(20));
        container.setBackgroundResource(R.drawable.bg_lifeos_card);

        TextView heading = new TextView(this);
        heading.setText("Delete Transaction?");
        heading.setTextSize(21);
        heading.setTextColor(getColor(R.color.lifeos_text));
        heading.setTypeface(null, Typeface.BOLD);

        TextView message = new TextView(this);

        String title = transaction.getTitle() == null
                ? "this transaction"
                : "\"" + transaction.getTitle() + "\"";

        message.setText(
                title + " will be permanently removed. Are you sure?"
        );

        message.setTextSize(14);
        message.setTextColor(getColor(R.color.lifeos_muted));
        message.setPadding(0, dp(12), 0, dp(20));

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER_VERTICAL);
        buttons.setClipChildren(false);
        buttons.setClipToPadding(false);

        Button cancel = actionButton("Cancel", false);
        Button delete = actionButton("Delete", true);

        LinearLayout.LayoutParams cancelParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(50),
                        1
                );

        cancelParams.setMargins(0, 0, dp(6), 0);

        LinearLayout.LayoutParams deleteParams =
                new LinearLayout.LayoutParams(
                        0,
                        dp(50),
                        1
                );

        deleteParams.setMargins(dp(6), 0, 0, 0);

        buttons.addView(cancel, cancelParams);
        buttons.addView(delete, deleteParams);

        container.addView(heading);
        container.addView(message);
        container.addView(buttons);

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {

            financeViewModel.delete(transaction);

            Toast.makeText(
                    this,
                    "Transaction deleted",
                    Toast.LENGTH_SHORT
            ).show();

            dialog.dismiss();
        });

        dialog.setContentView(container);
        dialog.setCancelable(true);

        showDialogWindow(dialog, false);
    }

    // ---------------------------------------------------------
    // DIALOG WINDOW HELPERS
    // ---------------------------------------------------------

    private void showDialogWindow(Dialog dialog, boolean fullHeight) {

        dialog.show();

        Window window = dialog.getWindow();

        if (window != null) {

            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT)
            );

            window.setLayout(
                    getResources().getDisplayMetrics().widthPixels - dp(32),
                    fullHeight
                            ? WindowManager.LayoutParams.MATCH_PARENT
                            : WindowManager.LayoutParams.WRAP_CONTENT
            );

            window.setGravity(Gravity.CENTER);
        }
    }

    private String formatCurrency(double amount) {
        return "₹" + String.format(
                Locale.getDefault(),
                "%,.2f",
                amount
        );
    }


    private Button actionButton(String text, boolean danger) {

        Button button = new Button(this);

        button.setText(text);
        button.setAllCaps(false);
        button.setTextSize(13);
        button.setTypeface(null, Typeface.BOLD);

        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(0);
        button.setMinimumHeight(0);

        button.setPadding(dp(12), dp(8), dp(12), dp(8));
        button.setGravity(Gravity.CENTER);
        button.setIncludeFontPadding(true);

        GradientDrawable background = new GradientDrawable();

        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(dp(14));

        if (danger) {
            background.setColor(getColor(R.color.finance_danger_soft));
            background.setStroke(dp(1), getColor(R.color.finance_danger));
            button.setTextColor(getColor(R.color.finance_danger));
        } else {
            background.setColor(getColor(R.color.finance_soft));
            background.setStroke(dp(1), getColor(R.color.finance_primary));
            button.setTextColor(getColor(R.color.finance_primary));
        }

        button.setBackground(background);
        button.setBackgroundTintList(null);
        button.setElevation(0);
        button.setStateListAnimator(null);

        return button;
    }

    private int dp(int value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }
}
