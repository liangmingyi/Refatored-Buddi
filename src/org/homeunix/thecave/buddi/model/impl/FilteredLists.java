/*
 * Created on Aug 6, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.model.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.homeunix.thecave.buddi.i18n.keys.TransactionClearedFilterKeys;
import org.homeunix.thecave.buddi.i18n.keys.TransactionDateFilterKeys;
import org.homeunix.thecave.buddi.i18n.keys.TransactionReconciledFilterKeys;
import org.homeunix.thecave.buddi.model.Account;
import org.homeunix.thecave.buddi.model.AccountType;
import org.homeunix.thecave.buddi.model.BudgetCategory;
import org.homeunix.thecave.buddi.model.BudgetCategoryType;
import org.homeunix.thecave.buddi.model.Document;
import org.homeunix.thecave.buddi.model.ScheduledTransaction;
import org.homeunix.thecave.buddi.model.Source;
import org.homeunix.thecave.buddi.model.Transaction;
import org.homeunix.thecave.buddi.model.prefs.PrefsModel;
import org.homeunix.thecave.buddi.plugin.api.util.TextFormatter;
import org.homeunix.thecave.moss.data.list.FilteredList;
import org.homeunix.thecave.moss.model.DocumentChangeEvent;
import org.homeunix.thecave.moss.model.DocumentChangeListener;
import org.homeunix.thecave.moss.util.DateFunctions;
import org.homeunix.thecave.moss.util.Formatter;
import org.homeunix.thecave.moss.util.Log;

public class FilteredLists {
	private FilteredLists() {}

	/**
	 * Abstract class used as a base for Buddi filtered lists.  Registers the data
	 * model for change events, and updates the filtered list accordingly. 
	 * @author wyatt
	 *
	 */
	private static abstract class BuddiFilteredList<T> extends FilteredList<T> {
		public BuddiFilteredList(Document model, List<T> source) {
			super(source);
			model.addDocumentChangeListener(new DocumentChangeListener(){
				public void documentChange(DocumentChangeEvent event) {
					updateFilteredList();
				}
			});
		}
	}

	/**
	 * Returns a list of all transactions which are associated with a given source
	 * @author wyatt
	 *
	 */
	private final static Map<String, List<Transaction>> transactionsBySourceMap = new HashMap<String, List<Transaction>>();
	public static List<Transaction> getTransactionsBySource(Document model, List<Transaction> transactions, Source source) {
		String key = model.getUid() + transactions.hashCode() + source.getUid();
		if (transactionsBySourceMap.get(key) == null){
			transactionsBySourceMap.put(key, new TransactionListFilteredBySource(model, transactions, source));
		}
		
		return transactionsBySourceMap.get(key);
	}
	private static class TransactionListFilteredBySource extends BuddiFilteredList<Transaction> {
		private final Source source;

		public TransactionListFilteredBySource(Document model, List<Transaction> transactions, Source source){
			super(model, transactions);
			this.source = source;
		}

		@Override
		public boolean isIncluded(Transaction t) {
			if (t.getTo().equals(source)
					|| t.getFrom().equals(source)){
				return true;
			}

			return false;
		}
	}
	
	/**
	 * Returns all scheduled transactions who meet the following criteria: 
	 * 
	 * 1) Start date is before today
	 * 2) Last date created is before today (or is not yet set).
	 * 3) End date is after today (or is not defined)
	 * 
	 * This is used at startup when determining which scheduled transactions
	 * we need to check and possibly add.
	 * @author wyatt
	 *
	 */
	private final static Map<String, List<ScheduledTransaction>> scheduledTransactionsBeforeTodayMap = new HashMap<String, List<ScheduledTransaction>>();
	public static List<ScheduledTransaction> getScheduledTransactionsBeforeToday(Document model, List<ScheduledTransaction> transactions) {
		String key = model.getUid() + transactions.hashCode();
		if (scheduledTransactionsBeforeTodayMap.get(key) == null){
			scheduledTransactionsBeforeTodayMap.put(key, new ScheduledTransactionListFilteredByBeforeToday(model, transactions));
		}
		
		return scheduledTransactionsBeforeTodayMap.get(key);
	}
	private static class ScheduledTransactionListFilteredByBeforeToday extends BuddiFilteredList<ScheduledTransaction> {
		private Date today = DateFunctions.getStartOfDay(new Date());
		
		public ScheduledTransactionListFilteredByBeforeToday(Document model, List<ScheduledTransaction> transactions){
			super(model, transactions);
		}

		@Override
		public boolean isIncluded(ScheduledTransaction st) {
			if ((DateFunctions.getDaysBetween(st.getStartDate(), today, false) == 0
					|| DateFunctions.getStartOfDay(st.getStartDate()).before(today))
					&& (st.getLastDayCreated() == null 
							|| DateFunctions.getStartOfDay(st.getLastDayCreated()).before(today))
							&& (st.getEndDate() == null 
									|| st.getEndDate().after(today))
									|| st.getLastDayCreated() == null
									|| (DateFunctions.getDaysBetween(st.getEndDate(), st.getLastDayCreated(), false) == 0)
									|| st.getEndDate().after(st.getLastDayCreated()))
				return true;
			return false;
		}
	}
	
	/**
	 * Returns a list of all transactions which fall between startDate and endDate
	 * @author wyatt
	 *
	 */
	private final static Map<String, List<Transaction>> transactionsByDateMap = new HashMap<String, List<Transaction>>();
	public static List<Transaction> getTransactionsByDate(Document model, List<Transaction> transactions, Date startDate, Date endDate) {
		String key = model.getUid() + transactions.hashCode() + startDate + endDate;
		if (transactionsByDateMap.get(key) == null){
			transactionsByDateMap.put(key, new TransactionListFilteredByDate(model, transactions, startDate, endDate));
		}
		
		return transactionsByDateMap.get(key);
	}
	private static class TransactionListFilteredByDate extends BuddiFilteredList<Transaction> {
		private final Date startDate;
		private final Date endDate;

		public TransactionListFilteredByDate(Document model, List<Transaction> transactions, Date startDate, Date endDate){
			super(model, transactions);
			this.startDate = startDate;
			this.endDate = endDate;
		}

		@Override
		public boolean isIncluded(Transaction t) {
			if ((t.getDate().after(startDate) || t.getDate().equals(startDate)) 
					&& (t.getDate().before(endDate) || t.getDate().equals(endDate))){
				return true;
			}

			return false;
		}
	}

	/**
	 * Returns a list of all transactions which match the query text and the pulldowns in 
	 * the TransactionsFrame.
	 * @author wyatt
	 *
	 */
	private final static Map<String, List<Transaction>> transactionsBySearchMap = new HashMap<String, List<Transaction>>();
	public static List<Transaction> getTransactionsBySearch(Document model, Source associatedSource, List<Transaction> transactions) {
		String key = model.getUid() + transactions.hashCode() + associatedSource.getUid();
		System.out.println(key);
		if (transactionsBySearchMap.get(key) == null){
			transactionsBySearchMap.put(key, new TransactionListFilteredBySearch(model, associatedSource, transactions));
		}
		
		return transactionsBySearchMap.get(key);
	}
	public static class TransactionListFilteredBySearch extends BuddiFilteredList<Transaction> {
		private final Document model;
		private String searchText;
		private TransactionDateFilterKeys dateFilter;
		private TransactionClearedFilterKeys clearedFilter;
		private TransactionReconciledFilterKeys reconciledFilter;
		private final Source associatedSource;

		private TransactionListFilteredBySearch(Document model, Source associatedSource, List<Transaction> transactions){
			super(model, transactions);
			this.model = model;
			this.associatedSource = associatedSource;
		}
		public void setDateFilter(TransactionDateFilterKeys dateFilter) {
			this.dateFilter = dateFilter;
		}
		public void setClearedFilter(TransactionClearedFilterKeys clearedFilter){
			this.clearedFilter = clearedFilter;
		}
		public void setReconciledFilter(TransactionReconciledFilterKeys reconciledFilter){
			this.reconciledFilter = reconciledFilter;
		}
		public void setSearchText(String searchText) {
			this.searchText = searchText;
		}
		
		/**
		 * Is the current list filtered?  This is a relatively expensive operation, as
		 * we need to get the list of transactions associated with the given source
		 * from the model, and compare the size of it to the size of the current list.
		 * Try to avoid using this in loops, or other frequently called code.
		 * @return
		 */
		public boolean isFiltered(){
			return model.getTransactions(associatedSource).size() != this.size();
		}
//		public TransactionClearedFilterKeys getClearedFilter() {
//			return clearedFilter;
//		}
//		public TransactionDateFilterKeys getDateFilter() {
//			return dateFilter;
//		}
//		public TransactionReconciledFilterKeys getReconciledFilter() {
//			return reconciledFilter;
//		}
//		public String getSearchText() {
//			return searchText;
//		}
		
		@Override
		public boolean isIncluded(Transaction t) {
			if (t == null || t.getTo() == null | t.getFrom() == null){
				return false;		
			}

			return acceptDate(t) && acceptText(t) && acceptCleared(t) && acceptReconciled(t);
		}
		
		private boolean acceptCleared(Transaction t){
			if (clearedFilter == null || TransactionClearedFilterKeys.TRANSACTION_FILTER_ALL_CLEARED.equals(clearedFilter))
				return true;
			
			if (associatedSource == null){
				//No concept of cleared when source is null.  Return true.  We should probably not even show the dropdown in this situation.
				return true;
			}
			else if (t.getFrom().equals(associatedSource)){
				if (TransactionClearedFilterKeys.TRANSACTION_FILTER_CLEARED.equals(clearedFilter)){
					return t.isClearedFrom();
				}	
				else if (TransactionClearedFilterKeys.TRANSACTION_FILTER_NOT_CLEARED.equals(clearedFilter)){
					return !t.isClearedFrom();
				}
				else {
					Log.emergency("Unknown value in Transaction Cleared Filter Keys: " + clearedFilter);
				}
			}
			else if (associatedSource.equals(t.getTo())){
				if (TransactionClearedFilterKeys.TRANSACTION_FILTER_CLEARED.equals(clearedFilter)){
					return t.isClearedTo();
				}	
				else if (TransactionClearedFilterKeys.TRANSACTION_FILTER_NOT_CLEARED.equals(clearedFilter)){
					return !t.isClearedTo();
				}
				else {
					Log.emergency("Unknown value in Transaction Cleared Filter Keys: " + clearedFilter);
				}
			}
				
			//If in doubt, we return true.  Is this correct behaviour?
			return true;
		}
		
		private boolean acceptReconciled(Transaction t){
			if (reconciledFilter == null || TransactionReconciledFilterKeys.TRANSACTION_FILTER_ALL_RECONCILED.equals(reconciledFilter))
				return true;
			
			if (associatedSource == null){
				//No concept of reconciled when source is null.  Return true.  We should probably not even show the dropdown in this situation.
				return true;
			}
			else if (t.getFrom().equals(associatedSource)){
				if (TransactionReconciledFilterKeys.TRANSACTION_FILTER_RECONCILED.equals(reconciledFilter)){
					return t.isReconciledFrom();
				}	
				else if (TransactionReconciledFilterKeys.TRANSACTION_FILTER_NOT_RECONCILED.equals(reconciledFilter)){
					return !t.isReconciledFrom();
				}
				else {
					Log.emergency("Unknown value in Transaction Cleared Filter Keys: " + reconciledFilter);
				}
			}
			else if (associatedSource.equals(t.getTo())){
				if (TransactionReconciledFilterKeys.TRANSACTION_FILTER_RECONCILED.equals(reconciledFilter)){
					return t.isReconciledTo();
				}	
				else if (TransactionReconciledFilterKeys.TRANSACTION_FILTER_NOT_RECONCILED.equals(reconciledFilter)){
					return !t.isReconciledTo();
				}
				else {
					Log.emergency("Unknown value in Transaction Reconciled Filter Keys: " + reconciledFilter);
				}
			}
				
			//If in doubt, we return true.  Is this correct behaviour?
			return true;
		}

		private boolean acceptDate(Transaction t) {
			if (null == dateFilter || TransactionDateFilterKeys.TRANSACTION_FILTER_ALL_DATES == dateFilter) {
				return true;
			}

			Date today = new Date();

			if (TransactionDateFilterKeys.TRANSACTION_FILTER_TODAY == dateFilter) {
				return DateFunctions.isSameDay(today, t.getDate());
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_YESTERDAY == dateFilter) {
				return DateFunctions.isSameDay(DateFunctions.addDays(today, -1), t.getDate());
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_THIS_WEEK == dateFilter) {
				return DateFunctions.getStartOfDay(DateFunctions.addDays(today, -7)).before(t.getDate());
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_THIS_MONTH == dateFilter) {
				return DateFunctions.isSameMonth(today, t.getDate());
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_LAST_MONTH == dateFilter) {
				return DateFunctions.isSameMonth(DateFunctions.addMonths(today, -1), t.getDate());
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_THIS_QUARTER == dateFilter) {
				return DateFunctions.getStartOfDay(DateFunctions.getStartOfQuarter(today)).before(t.getDate());
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_LAST_QUARTER == dateFilter) {
				return DateFunctions.isSameDay(DateFunctions.getStartOfQuarter(DateFunctions.addQuarters(today, -1)), DateFunctions.getStartOfQuarter(t.getDate()));
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_THIS_YEAR == dateFilter) {
				return DateFunctions.getStartOfYear(today).before(t.getDate());				
			}
			else if (TransactionDateFilterKeys.TRANSACTION_FILTER_LAST_YEAR == dateFilter) {
				Date startOfLastYear = DateFunctions.getStartOfYear(DateFunctions.addYears(today, -1));
				Date endOfLastYear = DateFunctions.getEndOfYear(startOfLastYear);
				return startOfLastYear.before(t.getDate()) && endOfLastYear.after(t.getDate()); 
			}
			else {
				Log.error("Unknown filter pulldown: " + dateFilter);
				return false;
			}
		}

		private boolean acceptText(Transaction t) {
			if (searchText == null || searchText.length() == 0) {
				return true;
			}
			String decimal = Formatter.getDecimalFormat().format(100).replaceAll("\\d", "");
			return (searchText == null
					|| searchText.length() == 0
					|| t.getDescription().toLowerCase().contains(searchText.toLowerCase())
					|| (t.getNumber() != null && t.getNumber().toLowerCase().contains(searchText.toLowerCase()))
					|| (t.getMemo() != null && t.getMemo().toLowerCase().contains(searchText.toLowerCase()))
					|| t.getFrom().getName().toLowerCase().contains(searchText.toLowerCase())
					|| t.getTo().getName().toLowerCase().contains(searchText.toLowerCase())
					|| TextFormatter.getFormattedCurrency(t.getAmount()).replaceAll("[^\\d" + decimal + "]", "").contains(searchText.toLowerCase()))
					|| TextFormatter.getDateFormat().format(t.getDate()).toLowerCase().contains(searchText.toLowerCase());

		}	
	}

	/**
	 * Returns a list of all the Accounts associated with the given type. 
	 * @author wyatt
	 */
	private final static Map<String, List<Account>> accountsByTypeMap = new HashMap<String, List<Account>>();
	public static List<Account> getAccountsByType(Document model, List<Account> accounts, AccountType type) {
		String key = model.getUid() + type.getName() + accounts.hashCode();
		if (accountsByTypeMap.get(key) == null){
			accountsByTypeMap.put(key, new AccountListFilteredByType(model, accounts, type));
		}
		
		return accountsByTypeMap.get(key);
	}
	private static class AccountListFilteredByType extends BuddiFilteredList<Account> {
		private final AccountType type;

		public AccountListFilteredByType(Document model, List<Account> accounts, AccountType type) {
			super(model, accounts);
			this.type = type;
		}

		@Override
		public boolean isIncluded(Account object) {
			if (object != null && object.getAccountType() != null){
				return object.getAccountType().equals(type);
			}
			return false;
		}
	}
	
	/**
	 * Returns a list of all accounts included in the constructor, with deleted 
	 * ones removed if the Preferences state that you should do it.
	 * @author wyatt
	 *
	 */
	private final static Map<String, List<Account>> accountsByDeletedMap = new HashMap<String, List<Account>>();
	public static List<Account> getAccountsByDeleted(Document model, List<Account> accounts) {
		String key = model.getUid() + accounts.hashCode();
		if (accountsByDeletedMap.get(key) == null){
			accountsByDeletedMap.put(key, new AccountListFilteredByDeleted(model, accounts));
		}
		
		return accountsByDeletedMap.get(key);
	}
	private static class AccountListFilteredByDeleted extends BuddiFilteredList<Account> {
		public AccountListFilteredByDeleted(Document model, List<Account> accounts) {
			super(model, accounts);
		}

		@Override
		public boolean isIncluded(Account object) {
			if (object != null){
				return !object.isDeleted() || PrefsModel.getInstance().isShowDeleted();
			}
			return false;
		}
	}

	/**
	 * Returns a list of all the type objects which have Accounts associated with them.
	 * @author wyatt
	 */
	private final static Map<String, List<AccountType>> typesByAccountsMap = new HashMap<String, List<AccountType>>();
	public static List<AccountType> getTypesByAccounts(Document model) {
		String key = model.getUid();
		if (typesByAccountsMap.get(key) == null){
			typesByAccountsMap.put(key, new TypeListFilteredByAccounts(model));
		}
		
		return typesByAccountsMap.get(key);
	}
	private static class TypeListFilteredByAccounts extends BuddiFilteredList<AccountType> {
		private final Document model;

		public TypeListFilteredByAccounts(Document model) {
			super(model, model.getAccountTypes());
			this.model = model;
		}

		@Override
		public boolean isIncluded(AccountType object) {
			if (object != null){
				for (Account a : model.getAccounts()) {
					if (a.getAccountType().equals(object))
						return true;
				}
			}
			return false;
		}
	}

	private final static Map<String, List<BudgetCategory>> categoriesByChildrenMap = new HashMap<String, List<BudgetCategory>>();
	public static List<BudgetCategory> getBudgetCategoriesByChildren(Document model, List<BudgetCategory> budgetCategories, BudgetCategory parent) {
		String key = model.getUid() + budgetCategories.hashCode() + parent.getUid();
		if (categoriesByChildrenMap.get(key) == null){
			categoriesByChildrenMap.put(key, new BudgetCategoryListFilteredByChildren(model, budgetCategories, parent));
		}
		
		return categoriesByChildrenMap.get(key);
	}
	private static class BudgetCategoryListFilteredByChildren extends BuddiFilteredList<BudgetCategory> {
		private final BudgetCategory parent;

		public BudgetCategoryListFilteredByChildren(Document model, List<BudgetCategory> budgetCategories, BudgetCategory parent) {
			super(model, budgetCategories);
			this.parent = parent;
		}

		@Override
		public boolean isIncluded(BudgetCategory object) {
			//We check the parental hierarchy of each object, and if it the 
			// parent object is included, this node is included in the list.
			BudgetCategory temp = object;
			while (temp.getParent() != null){
				temp = temp.getParent();
				if (temp.equals(parent))
					return true;
			}
			return false;
		}
	}

	/**
	 * Returns a list of categories which are children of the given parent.
	 * @author wyatt
	 */
	private final static Map<String, List<BudgetCategory>> categoriesByParentMap = new HashMap<String, List<BudgetCategory>>();
	public static List<BudgetCategory> getBudgetCategoriesByParent(Document model, List<BudgetCategory> budgetCategories, BudgetCategory parent) {
		String key = model.getUid() + budgetCategories.hashCode() + (parent == null ? null : parent.getUid());
		if (categoriesByParentMap.get(key) == null){
			categoriesByParentMap.put(key, new BudgetCategoryListFilteredByParent(model, budgetCategories, parent));
		}
		
		return categoriesByParentMap.get(key);
	}
	private static class BudgetCategoryListFilteredByParent extends BuddiFilteredList<BudgetCategory> {
		private final BudgetCategory parent;

		public BudgetCategoryListFilteredByParent(Document model, List<BudgetCategory> budgetCategories, BudgetCategory parent) {
			super(model, budgetCategories);
			this.parent = parent;
		}

		@Override
		public boolean isIncluded(BudgetCategory object) {
			if (object != null){
				if (parent == null)
					return object.getParent() == null;
				else if (object.getParent() != null)
					return object.getParent().equals(parent);
			}
			return false;
		}
	}
	
	/**
	 * Returns a list of categories, filtered by deleted status if the preferences state such.
	 * @author wyatt
	 */
	private final static Map<String, List<BudgetCategory>> categoriesByDeletedMap = new HashMap<String, List<BudgetCategory>>();
	public static List<BudgetCategory> getBudgetCategoriesByDeleted(Document model, List<BudgetCategory> budgetCategories) {
		String key = model.getUid() + budgetCategories.hashCode();
		if (categoriesByDeletedMap.get(key) == null){
			categoriesByDeletedMap.put(key, new BudgetCategoryListFilteredByDeleted(model, budgetCategories));
		}
		
		return categoriesByDeletedMap.get(key);
	}
	private static class BudgetCategoryListFilteredByDeleted extends BuddiFilteredList<BudgetCategory> {
		public BudgetCategoryListFilteredByDeleted(Document model, List<BudgetCategory> budgetCategories) {
			super(model, budgetCategories);
		}

		@Override
		public boolean isIncluded(BudgetCategory object) {
			if (object != null){
				return !object.isDeleted() || PrefsModel.getInstance().isShowDeleted();
			}
			return false;
		}
	}
	
	/**
	 * Returns a list of categories which are children of the given parent.
	 * @author wyatt
	 */
	private final static Map<String, List<BudgetCategory>> categoriesByTypeMap = new HashMap<String, List<BudgetCategory>>();
	public static List<BudgetCategory> getBudgetCategoriesByPeriodType(Document model, BudgetCategoryType type) {
		String key = model.getUid() + type.getName();
		if (categoriesByTypeMap.get(key) == null){
			categoriesByTypeMap.put(key, new BudgetCategoryListFilteredByPeriodType(model, type));
		}
		
		return categoriesByTypeMap.get(key);
	}	
	private static class BudgetCategoryListFilteredByPeriodType extends BuddiFilteredList<BudgetCategory> {
		private final BudgetCategoryType type;

		public BudgetCategoryListFilteredByPeriodType(Document model, BudgetCategoryType type) {
			super(model, model.getBudgetCategories());
			this.type = type;
		}

		@Override
		public boolean isIncluded(BudgetCategory object) {
			if (object != null){
				if (object.getBudgetPeriodType() != null)
					return object.getBudgetPeriodType().equals(type);
			}
			return false;
		}
	}
}
