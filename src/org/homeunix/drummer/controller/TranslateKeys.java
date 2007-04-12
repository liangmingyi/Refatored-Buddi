/*
 * Created on Jun 2, 2006 by wyatt
 */
package org.homeunix.drummer.controller;

/**
 * I use this list of keys for the Translate class.  Using this instead 
 * of string constants gives me more flexibility and less ugly code
 * that I have to wade through. 
 * @author wyatt
 *
 */
public enum TranslateKeys {
    ABOUT_COPYRIGHT,
    ABOUT_EMAIL,
    ABOUT_GPL,
    ABOUT_TEXT,
    ACCOUNT,
    ACCOUNT_AND_CATEGORY_SUMMARY,
    ACCOUNT_TYPE,
    ACTUAL,
    ACTUAL_VS_BUDGETED_EXPENSES_TITLE,
    ADD,
    ADVANCED,
    ALL,
    AMOUNT,
    AND_REPEATING_EVERY,
    AND_REPEATING_ON_THE,
    AUTO,
    AUTO_COMPLETE_TRANSACTION_INFORMATION,
    AVAILABLE_CREDIT,
    AVAILABLE_OVERDRAFT,
    BACKUP_DATA_FILE, 
    BONUS,
    BUDDI,
    BUDDI_BACKUP_FILE_DESC,
    BUDDI_FILE_DESC,
    BUDGETED,
    BUDGETED_AMOUNT,
    BUDGET_INTERVAL,
    BUDGET_NET_INCOME,
    CANCEL,
    CANCELLED_FILE_LOAD,
    CANCELLED_FILE_LOAD_MESSAGE,
    CANCELLED_FILE_RESTORE,
    CANCEL_FILE_RESTORE_MESSAGE,
    CANNOT_READ_DATA_FILE,
    CANNOT_READ_FILE,
    CANNOT_READ_PREFS_FILE,
    CANNOT_SAVE_OVER_DIR,
    CANNOT_WRITE_DATA_FILE,
    CANNOT_WRITE_OVER_FILE,
    CANNOT_WRITE_PREFS_FILE,
    CASH,
    CATEGORY,
    CHANGE_ENCRYPTION,
    CHECK_CONSOLE,
    CHEQUING,
    CHOOSE_BACKUP_FILE,
    CHOOSE_DATASTORE_LOCATION,
    CHOOSE_DATE_INTERVAL,
    CHOOSE_EXPORT_FILE,
    CHOOSE_PLUGIN_JAR,
    CLEAR,
    CLEAR_TRANSACTION,
    CLEAR_TRANSACTION_LOSE_CHANGES,
    CLOSE_DATA_FILE,
    CONFIRM_LOAD_BACKUP_FILE,
    CONFIRM_PASSWORD,
    CONFIRM_RESTORE_BACKUP_FILE,
    CREATE_NEW_DATA_FILE,
    CREDIT,
    CREDIT_CARD,
    CREDIT_LIMIT,
    CURRENCY,
    DATE,
    DATE_FORMAT,
    DECRYPT_DATA_FILE_WARNING,
    DEFAULT_SEARCH,
    DELETE,
    DELETE_CATEGORY,
    DELETE_TRANSACTION,
    DELETE_TRANSACTION_LOSE_CHANGES,
    DESCRIPTION,
    DETAILS,
    DIFFERENCE,
    DOCUMENTS,
    DONATE,
    DONATE_HEADER,
    DONATE_MESSAGE,
    DONE,
    DOWNLOAD,
    EDIT_ACCOUNT_TYPES,
    EDIT_CATEGORY,
    EDIT_LANGUAGES,
    EMPTY_PASSWORD,
    EMPTY_PASSWORD_TITLE,
    ENABLE_UPDATE_NOTIFICATIONS,
    ENCRYPT_DATA_FILE_TITLE,
    ENCRYPT_DATA_FILE_WARNING,
    ENCRYPT_DATA_FILE_YES_NO,
    ENTERTAINMENT, 
    ENTER_ACCOUNT_NAME_AND_TYPE,
    ENTER_ACCOUNT_TYPE_NAME,
    ENTER_CATEGORY_NAME,
    ENTER_CURRENCY_SYMBOL,
    ENTER_CURRENCY_SYMBOL_TITLE,
    ENTER_PASSWORD,
    ENTER_PASSWORD_TITLE,
    ERROR,
    EXITING_PROGRAM,
    EXPENSES,
    EXPENSE_ACTUAL_BUDGET,
    EXPENSE_ACTUAL_BUDGET_BAR_GRAPH,
    EXPENSE_PIE_GRAPH,
    EXPENSE_PIE_GRAPH_TITLE,
    EXPORT_TO_CSV,
    EXPORT_TO_HTML,
    FILE_SAVED,
    FORTNIGHT,
    GRAPHS,
    GROCERIES,
    HINT_DESCRIPTION,
    HINT_MEMO,
    HINT_MESSAGE,
    HINT_NUMBER,
    HOUSEHOLD,
    INCOME,
    INCOME_AND_EXPENSES_BY_CATEGORY_TITLE,
    INCOME_AND_EXPENSES_BY_DESCRIPTION_TITLE,
    INCOME_PIE_GRAPH,
    INCOME_PIE_GRAPH_TITLE,
    INCORRECT_PASSWORD,
    INCORRECT_PASSWORD_TITLE,
    INTEREST_RATE,
    INVESTMENT,
    INVESTMENT_EXPENSES, 
    INVESTMENT_INCOME,
    JAR_FILES,
    LANGUAGE,
    LANGUAGE_EDITOR_BLANK_VALUE,
    LANGUAGE_EDITOR_HELP,
    LANGUAGE_EDITOR_LOCALE,
    LANGUAGE_EDITOR_NAME,
    LAST_MONTH,
    LAST_QUARTER,
    LAST_WEEK,
    LAST_YEAR,
    LIABILITY,
    LICENCES,
    LINE_OF_CREDIT,
    LOAN,
    LOCALE,
    MENU_EDIT,
    MENU_EDIT_CLEAR_NEW,
    MENU_EDIT_PREFERENCES,
    MENU_EDIT_RECORD_UPDATE,
    MENU_EDIT_SCHEDULED_ACTIONS,
    MENU_EDIT_TOGGLE_CLEARED,
    MENU_EDIT_TOGGLE_RECONCILED,
    MENU_FILE,
    MENU_FILE_BACKUP,
    MENU_FILE_CLOSE_WINDOW,
    MENU_FILE_DECRYPT,
    MENU_FILE_ENCRYPT,
    MENU_FILE_EXPORT,
    MENU_FILE_IMPORT,
    MENU_FILE_NEW,
    MENU_FILE_OPEN,
    MENU_FILE_PRINT,
    MENU_FILE_QUIT,
    MENU_FILE_RESTORE,
    MENU_FILE_SAVE_AS,
    MENU_HELP,
    MENU_HELP_ABOUT,
    MENU_HELP_HELP,
    MENU_WINDOW,
    MENU_WINDOW_MAIN_WINDOW,
    MENU_WINDOW_MINIMIZE,
    MISC_EXPENSES,
    MISSING_DATA_FILE,
    MONTH,
    MORE_INFO_NEEDED,
    MUST_SELECT_BUDDI_FILE,
    MY_ACCOUNTS,
    MY_BUDGET,
    NAME,
    NAME_MUST_BE_UNIQUE,
    NETWORTH_LINE_GRAPH,
    NETWORTH_PIE_GRAPH,
    NET_WORTH,
    NET_WORTH_BREAKDOWN_TITLE,
    NET_WORTH_OVER_TIME_GRAPH_TITLE,
    NEW,
    NEW_CATEGORY,
    NEW_DATA_FILE, 
    NEW_DATA_FILE_OPTION,
    NEW_DATA_FILE_SAVED,
    NEW_OR_EXISTING_DATA_FILE,
    NEW_TRANSACTION,
    NEW_VERSION,
    NEW_VERSION_MESSAGE,
    NEW_VERSION_MESSAGE_2,
    NOTHING_TO_PRINT,
    NOT_CLEARED,
    NOT_NOW,
    NOT_RECONCILED,
    NO_PARENT,
    NO_PASSWORD_ENTERED,
    NO_PLUGINS_IN_JAR,
    NO_PLUGINS_IN_JAR_TITLE,
    NO_TRANSACTIONS_USING_ACCOUNT,
    NO_TRANSACTIONS_USING_CATEGORY,
    NUMBER_OF_BACKUPS,
    OF_EACH_MONTH,
    OK,
    ONE_MONTH,
    ON_EACH_OF_THE_FOLLOWING_MONTHS,
    ON_EACH_OF_THE_FOLLOWING_WEEKS,
    OPEN,
    OPENED_FILE,
    OPEN_DATA_FILE,
    OPEN_DATA_FILE_OPTION,
    OPEN_DATA_FILE_TITLE,
    OPTIONAL_TAG,
    OTHER,
    OVERDRAFT_LIMIT,
    OVERWRITE_EXISTING_FILE,
    OVERWRITE_EXISTING_FILE_MESSAGE,
    PARENT_CATEGORY,
    PASSWORD,
    PASSWORDS_DONT_MATCH,
    PAST_FORTNIGHT,
    PAST_WEEK,
    PERMANENT_DELETE_ACCOUNT,
    PERMANENT_DELETE_CATEGORY,
    PLUGINS,
    PREFERENCES,
    PREPAID_ACCOUNT,
    PRINT_ERROR,
    PROBLEM_READING_DATA_FILE_CORRUPTED,
    PROBLEM_READING_DATA_FILE_DIR_NOT_EXIST,
    PROBLEM_READING_DATA_FILE_INTRO,
    PROBLEM_SAVING_FILE,
    PROMPT_FOR_DATA_FILE_AT_STARTUP,
    QUARTER,
    RECORD,
    RECORD_BUTTON_ERROR,
    REMOVE,
    REPEAT_THIS_ACTION,
    REPORTS,
    REPORT_AS_OF_DATE,
    REPORT_BETWEEN,
    REPORT_BY_CATEGORY_HEADER,
    REPORT_BY_DESCRIPTION_HEADER,
    REPORT_DATE_ERROR,
    REPORT_INCOME_EXPENSES_BY_CATEGORY,
    REPORT_INCOME_EXPENSES_BY_DESCRIPTION,
    RESTART_NEEDED,
    RESTART_NEEDED_TITLE,
    RESTORED_FILE,
    RESTORE_DATA_FILE,
    SALARY,
    SAVE_DATA_FILE,
    SAVINGS, 
    SCHEDULED_ACTION,
    SCHEDULED_ACTIONS,
    SCHEDULED_ACTION_NAME,
    SCHEDULED_MESSAGE,
    SCHEDULED_NOT_ENOUGH_INFO,
    SCHEDULED_NOT_ENOUGH_INFO_TITLE,
    SCHEDULE_DATE_EIGHTEENTH,
    SCHEDULE_DATE_EIGHTH,
    SCHEDULE_DATE_ELEVENTH,
    SCHEDULE_DATE_FIFTEENTH,
    SCHEDULE_DATE_FIFTH,
    SCHEDULE_DATE_FIRST,
    SCHEDULE_DATE_FOURTEENTH,
    SCHEDULE_DATE_FOURTH,
    SCHEDULE_DATE_NINETEENTH,
    SCHEDULE_DATE_NINETH,
    SCHEDULE_DATE_SECOND,
    SCHEDULE_DATE_SEVENTEENTH,
    SCHEDULE_DATE_SEVENTH,
    SCHEDULE_DATE_SIXTEENTH,
    SCHEDULE_DATE_SIXTH,
    SCHEDULE_DATE_TENTH,
    SCHEDULE_DATE_THIRD,
    SCHEDULE_DATE_THIRTEENTH,
    SCHEDULE_DATE_THIRTIETH,
    SCHEDULE_DATE_THIRTYFIRST,
    SCHEDULE_DATE_TWELFTH,
    SCHEDULE_DATE_TWENTIETH,
    SCHEDULE_DATE_TWENTYEIGHTH,
    SCHEDULE_DATE_TWENTYFIFTH,
    SCHEDULE_DATE_TWENTYFIRST,
    SCHEDULE_DATE_TWENTYFOURTH,
    SCHEDULE_DATE_TWENTYNINETH,
    SCHEDULE_DATE_TWENTYSECOND,
    SCHEDULE_DATE_TWENTYSEVENTH,
    SCHEDULE_DATE_TWENTYSIXTH,
    SCHEDULE_DATE_TWENTYTHIRD,
    SCHEDULE_DAY_FIRST_FRIDAY,
    SCHEDULE_DAY_FIRST_MONDAY,
    SCHEDULE_DAY_FIRST_SATURDAY,
    SCHEDULE_DAY_FIRST_SUNDAY,
    SCHEDULE_DAY_FIRST_THURSDAY,
    SCHEDULE_DAY_FIRST_TUESDAY,
    SCHEDULE_DAY_FIRST_WEDNESDAY,
    SCHEDULE_DAY_FRIDAY,
    SCHEDULE_DAY_MONDAY,
    SCHEDULE_DAY_SATURDAY,
    SCHEDULE_DAY_SUNDAY,
    SCHEDULE_DAY_THURSDAY,
    SCHEDULE_DAY_TUESDAY,
    SCHEDULE_DAY_WEDNESDAY,
    SCHEDULE_FREQUENCY_BIWEEKLY,
    SCHEDULE_FREQUENCY_EVERY_DAY,
    SCHEDULE_FREQUENCY_EVERY_WEEKDAY,//added by  Nicky
    SCHEDULE_FREQUENCY_MONTHLY_BY_DATE,
    SCHEDULE_FREQUENCY_MONTHLY_BY_DAY_OF_WEEK,
    SCHEDULE_FREQUENCY_MULTIPLE_MONTHS_EVERY_YEAR,
    SCHEDULE_FREQUENCY_MULTIPLE_WEEKS_EVERY_MONTH,
    SCHEDULE_FREQUENCY_WEEKLY,
    SCHEDULE_MONTH_APRIL,
    SCHEDULE_MONTH_AUGUST,
    SCHEDULE_MONTH_DECEMBER,
    SCHEDULE_MONTH_FEBRUARY,
    SCHEDULE_MONTH_JANUARY,
    SCHEDULE_MONTH_JULY,
    SCHEDULE_MONTH_JUNE,
    SCHEDULE_MONTH_MARCH,
    SCHEDULE_MONTH_MAY,
    SCHEDULE_MONTH_NOVEMEBER,
    SCHEDULE_MONTH_OCTOBER,
    SCHEDULE_MONTH_SEPTEMBER,
    SCHEDULE_WEEK_FIRST,
    SCHEDULE_WEEK_FOURTH,
    SCHEDULE_WEEK_SECOND,
    SCHEDULE_WEEK_THIRD,
    SHORT_CLEARED,
    SHORT_RECONCILED,
    SHOW_ACCOUNT_TYPES,
    SHOW_CLEAR_RECONCILE,
    SHOW_CREDIT_LIMIT,
    SHOW_CURRENCY_SYMBOL_AFTER_AMOUNT,
    SHOW_DELETED_ACCOUNTS,
    SHOW_DELETED_CATEGORIES,
    SHOW_INTEREST_RATE,
    SIX_MONTHS,
    SOURCE_TO_FROM,
    STARTING_BALANCE,
    STARTING_ON,
    START_DATE_AFTER_END_DATE,
    START_DATE_IN_THE_PAST,
    START_DATE_IN_THE_PAST_TITLE,
    SUCCESSFUL_BACKUP,
    SUCCESSFUL_EXPORT,
    SUCCESSFUL_OPEN_FILE,
    SUCCESSFUL_RESTORE_FILE,
    SUCCESSFUL_SAVE_FILE,
    SUMMARY,
    THIS_MONTH,
    THIS_QUARTER,
    THIS_WEEK,
    THIS_YEAR,
    TO,
    TODAY,
    TOOLTIP_AMOUNT,
    TOOLTIP_DATE, 
    TOOLTIP_DESC,
    TOOLTIP_FROM,
    TOOLTIP_MEMO,
    TOOLTIP_NUMBER,
    TOOLTIP_SCHEDULED_MESSAGE,
    TOOLTIP_SHOW_ACCOUNT_TYPES,
    TOOLTIP_SHOW_CLEAR_RECONCILE,
    TOOLTIP_SHOW_CREDIT_LIMIT,
    TOOLTIP_SHOW_INTEREST_RATE,
    TOOLTIP_TO,
    TOTAL,
    TO_HTML_SAFE,
    TRANSACTIONS,
    TRANSACTION_CHANGED_INVALID_MESSAGE,
    TRANSACTION_CHANGED_MESSAGE,
    TRANSACTION_CHANGED_TITLE,
    TRANSACTION_FILTER,
    TWO_MONTHS,
    UNDELETE,
    UPDATE,
    UPGRADE_NOTICE,
    UPGRADE_NOTICE_TITLE,
    UTILITIES,
    VERSION,
    VIEW,
    WEEK,
    YEAR,
    YESTERDAY,
    
    LOCALE_COUNTRY_CODE,
    LOCALE_LANGUAGE_CODE
}
