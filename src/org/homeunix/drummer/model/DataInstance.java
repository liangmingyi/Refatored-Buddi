/*
 * Created on May 12, 2006 by wyatt
 */
package org.homeunix.drummer.model;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.homeunix.drummer.Const;
import org.homeunix.drummer.controller.Translate;
import org.homeunix.drummer.controller.TranslateKeys;
import org.homeunix.drummer.model.impl.ModelFactoryImpl;
import org.homeunix.drummer.model.util.AESCryptoCipher;
import org.homeunix.drummer.prefs.PrefsInstance;
import org.homeunix.drummer.util.DateUtil;
import org.homeunix.drummer.util.FileFunctions;
import org.homeunix.drummer.util.Formatter;
import org.homeunix.drummer.util.Log;

@SuppressWarnings("unchecked")
public class DataInstance {
	public static DataInstance getInstance() {
		return SingletonHolder.instance;
	}
	
	private static class SingletonHolder {
		private static DataInstance instance = new DataInstance();		
	}
		
	private DataModel dataModel;
	private ModelFactory dataModelFactory = ModelFactoryImpl.eINSTANCE;
	
	private ResourceSet resourceSet;
	private URIConverter.Cipher cipher;
	
	/**
	 * Creates a new cipher, with the encryption set as defined
	 * @param encrypted
	 */
	public void createNewCipher(boolean encrypted){
		this.cipher = new AESCryptoCipher();
		((AESCryptoCipher) cipher).setEncrypted(encrypted);
	}
	
	private DataInstance(){
		File dataFile = null;
		
		if (PrefsInstance.getInstance().getPrefs().getDataFile() != null){
			dataFile = new File(PrefsInstance.getInstance().getPrefs().getDataFile());
			if (dataFile.exists()) {
				try{
					String backupFileLocation = 
						PrefsInstance.getInstance().getPrefs().getDataFile()
						.replaceAll(Const.DATA_FILE_EXTENSION + "$", "") 
						+ " " + Formatter.getInstance().getFilenameDateFormat().format(new Date())
						+ Const.DATA_FILE_EXTENSION;
					File backupFile = new File(backupFileLocation);
					if (!backupFile.exists()){
						FileFunctions.copyFile(dataFile, backupFile);
						Log.debug("Backing up file to " + backupFile);
					}
				}
				catch(IOException ioe){
					Log.emergency("Failure when attempting to backup when starting program: " + ioe);
				}
			}
		}
		
		loadDataModel(dataFile, false);
	}
	
	public void loadDataModel(File locationFile, boolean forceNewFile){
		// throw away the old cipher (if any) when we load a new data file
		this.cipher = new AESCryptoCipher();
		
		if (!forceNewFile){
			if (locationFile == null || !locationFile.getParentFile().exists()){
				JOptionPane.showMessageDialog(
						null,
						Translate.getInstance().get(TranslateKeys.PROBLEM_READING_DATA_FILE_INTRO)
						+ ( locationFile == null ? locationFile : locationFile.getAbsolutePath() ) 
						+ Translate.getInstance().get(TranslateKeys.PROBLEM_READING_DATA_FILE_DIR_NOT_EXIST),
						Translate.getInstance().get(TranslateKeys.MISSING_DATA_FILE),
						JOptionPane.ERROR_MESSAGE);
				
				String file = PrefsInstance.chooseDataFile();
				if (file != null)
					locationFile = new File(file);
				else
					locationFile = null;
			}
			
			if (locationFile == null){
				System.exit(0);
			}
			
			if (!locationFile.exists() && locationFile.getParentFile().exists()){
				locationFile = new File(locationFile.getParent() + File.separator + Const.DATA_DEFAULT_FILENAME + Const.DATA_FILE_EXTENSION);
			}
		}
		
		try{
			if (forceNewFile){
				throw new Exception();
			}
			
			// Create a resource set.
			resourceSet = new ResourceSetImpl();
			
			// Register the default resource factory -- only needed for stand-alone!
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
					Resource.Factory.Registry.DEFAULT_EXTENSION, new XMLResourceFactoryImpl());
			resourceSet.getLoadOptions().put(Resource.OPTION_CIPHER, this.cipher);
						
			// Register the package
			@SuppressWarnings("unused") 
			ModelPackage modelPackage = ModelPackage.eINSTANCE;
			
			// Get the URI of the model file.
			URI fileURI = URI.createFileURI(locationFile.getAbsolutePath());
			
			Resource resource = resourceSet.getResource(fileURI, true);
			
			// Print the contents of the resource to System.out.
			EList contents = resource.getContents();
			if (contents.size() > 0){
				dataModel = (DataModel) contents.get(0);
			}
			
			//Save the location to the prefs file, in case we changed it.
			PrefsInstance.getInstance().getPrefs().setDataFile(locationFile.getAbsolutePath());
			PrefsInstance.getInstance().savePrefs();
		}
		//If there is a problem opening the file, we will prompt to make a new one
		catch (Exception e){
			if (locationFile == null)
				locationFile = new File(Const.DATA_DEFAULT_FILENAME + Const.DATA_FILE_EXTENSION);
			
			PrefsInstance.getInstance().getPrefs().setDataFile(locationFile.getAbsolutePath());
			PrefsInstance.getInstance().savePrefs();
			
			if (forceNewFile || !locationFile.exists() || JOptionPane.showConfirmDialog(
					null,
					Translate.getInstance().get(TranslateKeys.PROBLEM_READING_DATA_FILE_INTRO) 
					+ locationFile.getAbsolutePath()
					+ Translate.getInstance().get(TranslateKeys.PROBLEM_READING_DATA_FILE_CORRUPTED),
					Translate.getInstance().get(TranslateKeys.CREATE_NEW_DATA_FILE),
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){
				
				if (!locationFile.exists() && !forceNewFile){
					JOptionPane.showMessageDialog(
							null,
							Translate.getInstance().get(TranslateKeys.CREATED_NEW_DATA_FILE_MESSAGE)
							+ locationFile.getAbsolutePath(),
							Translate.getInstance().get(TranslateKeys.CREATED_NEW_DATA_FILE),
							JOptionPane.INFORMATION_MESSAGE
					);
				}
				
				Accounts accounts = getDataModelFactory().createAccounts();
				Transactions transactions = getDataModelFactory().createTransactions();
				Categories categories = getDataModelFactory().createCategories();
				Types types = getDataModelFactory().createTypes();
				
				dataModel = getDataModelFactory().createDataModel();
				
				dataModel.setAllAccounts(accounts);
				dataModel.setAllTransactions(transactions);
				dataModel.setAllCategories(categories);
				dataModel.setAllTypes(types);
				
				//Default starting categories
				TranslateKeys[] expenseNames = {
						TranslateKeys.AUTO, 
						TranslateKeys.ENTERTAINMENT, 
						TranslateKeys.HOUSEHOLD, 
						TranslateKeys.GROCERIES, 
						TranslateKeys.INVESTMENT_EXPENSES, 
						TranslateKeys.MISC_EXPENSES, 
						TranslateKeys.UTILITIES
				};
				TranslateKeys[] incomeNames = {
						TranslateKeys.BONUS, 
						TranslateKeys.SALARY, 
						TranslateKeys.INVESTMENT_INCOME
				};
				
				for (TranslateKeys s : expenseNames){
					Category c = getDataModelFactory().createCategory();
					c.setName(s.toString());
					c.setBudgetedAmount(0);
					c.setIncome(false);
					categories.getCategories().add(c);
				}
				for (TranslateKeys s : incomeNames){
					Category c = getDataModelFactory().createCategory();
					c.setName(s.toString());
					c.setBudgetedAmount(0);
					c.setIncome(true);
					categories.getCategories().add(c);
				}
				
				//Default starting types - debit
				TranslateKeys[] debitNames = {
						TranslateKeys.CASH, 
						TranslateKeys.SAVINGS,
						TranslateKeys.CHEQUING,
						TranslateKeys.INVESTMENT				
				};
				
				for (TranslateKeys s : debitNames){
					addType(s.toString(), false);
				}				

				//Default starting types - credit
				TranslateKeys[] creditNames = {
						TranslateKeys.LIABILITY,
						TranslateKeys.CREDIT_CARD,
						TranslateKeys.PREPAID_ACCOUNT,
						TranslateKeys.LINE_OF_CREDIT,
						TranslateKeys.LOAN
				};
				
				for (TranslateKeys s : creditNames){
					addType(s.toString(), true);
				}				
				
				ResourceSet resourceSet = new ResourceSetImpl();			
				resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
						Resource.Factory.Registry.DEFAULT_EXTENSION, new XMLResourceFactoryImpl());
				
				URI fileURI = URI.createFileURI(locationFile.toString());
				Log.debug("Saving new file to " + locationFile.toString());
				Resource resource = resourceSet.createResource(fileURI);			
				resource.getContents().add(dataModel);
				
				Map options = new HashMap(1);
				options.put(Resource.OPTION_CIPHER, this.cipher);
				
				try{
					resource.save(options);
				}
				catch (IOException ioe){}
			}
			else{
				JOptionPane.showMessageDialog(
						null, 
						Translate.getInstance().get(TranslateKeys.CANNOT_READ_FILE),
						Translate.getInstance().get(TranslateKeys.EXITING_PROGRAM),
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		}
	}
	
	public void saveDataModel(){
		saveDataModel(PrefsInstance.getInstance().getPrefs().getDataFile());
	}
	
	synchronized public void saveDataModel(final String location){
//		new SwingWorker(){
//			@Override
//			public Object construct() {
				File saveLocation = new File(location);
				File backupLocation = new File(saveLocation.getAbsolutePath() + "~");

				try{
					if (saveLocation.exists())
						FileFunctions.copyFile(saveLocation, backupLocation);
					
					URI fileURI = URI.createFileURI(saveLocation.getAbsolutePath());
					
					Log.debug("Data saved to " + location);
					
					Resource resource = resourceSet.createResource(fileURI);
										
					resource.getContents().add(getDataModel());
					
					Map options = new HashMap(1);
					options.put(Resource.OPTION_CIPHER, this.cipher);
					
					resource.save(options);
				}
				catch (IOException ioe){
					Log.critical("Error when saving file: " + ioe);
				}
//				return null;
//			}			
//		}.start();
	}
	
	
	public DataModel getDataModel(){
		return dataModel;
	}
	
	public ModelFactory getDataModelFactory(){
		return dataModelFactory;
	}
	
	public void calculateAllBalances(){
		for (Account a : getAccounts()){
			a.calculateBalance();
		}
	}
	
	public void addAccount(Account a){
		getDataModel().getAllAccounts().getAccounts().add(a);
		saveDataModel();
	}

	public void addCategory(Category c){
		getDataModel().getAllCategories().getCategories().add(c);
		saveDataModel();
	}
	
	public void addType(String name, boolean credit){
		Type t = getDataModelFactory().createType();
		t.setName(name);
		t.setCredit(credit);
		getDataModel().getAllTypes().getTypes().add(t);
		saveDataModel();
	}

	public void addTransaction(Transaction t){
		getDataModel().getAllTransactions().getTransactions().add(t);
		t.calculateBalance();		
		saveDataModel();
	}
	
	
	public void deleteSource(Source s){
		s.setDeleted(true);
		if (s instanceof Category) {
			Category c = (Category) s;
			for (Object o : c.getChildren()) {
				if (o instanceof Category) {
					Category child = (Category) o;
					deleteSource(child);
				}
			}
		}
		saveDataModel();
	}

	public void deleteSourcePermanent(Source s){
		if (s instanceof Category) {
			Category c = (Category) s;
			getDataModel().getAllCategories().getCategories().remove(c);
		}
		else if (s instanceof Account) {
			Account a = (Account) s;
			getDataModel().getAllAccounts().getAccounts().remove(a);
		}
		saveDataModel();
	}
	
	public void deleteTransaction(Transaction t){
		if (getDataModel().getAllTransactions().getTransactions().remove(t)){
			t.calculateBalance();
			saveDataModel();
		}
		else{
			Log.warning("Could not delete transaction: could not find orignal in data store");
		}
	}

	public void undeleteSource(Source s){
		s.setDeleted(false);
		if (s instanceof Category) {
			Category c = (Category) s;
			for (Object o : c.getChildren()) {
				if (o instanceof Category) {
					Category child = (Category) o;
					undeleteSource(child);
				}
			}
		}
		saveDataModel();
	}
		
	public Vector<Account> getAccounts(){
		Vector<Account> v = new Vector<Account>(getDataModel().getAllAccounts().getAccounts());
		Collections.sort(v);
		return v;
	}

	public Vector<Category> getCategories(){
		Vector<Category> v = new Vector<Category>(getDataModel().getAllCategories().getCategories());
		Collections.sort(v);
		return v;
	}
	
	public Vector<Type> getTypes(){
		Vector<Type> v = new Vector<Type>(getDataModel().getAllTypes().getTypes());
		Collections.sort(v);
		return v;
	}

	public Vector<Transaction> getTransactions(){
		Vector<Transaction> transactions = new Vector<Transaction>(getDataModel().getAllTransactions().getTransactions());
		Collections.sort(transactions);
		return transactions;
	}
	
	/**
	 * Returns all transactions which go to or from a given source.
	 * @param source
	 * @return
	 */
	public Vector<Transaction> getTransactions(Source source){
		Vector<Transaction> v = new Vector<Transaction>();
		
		for (Transaction transaction : getTransactions()) {
			if (transaction.getFrom() != null && transaction.getTo() != null && 
					(transaction.getFrom().equals(source) || transaction.getTo().equals(source)))
				v.add(transaction);
		}
		
		Collections.sort(v);		
		return v;
	}
	
	/**
	 * Returns all transactions within a given time frame.  Must match all
	 * time arguments (excluding null arguments)
	 * @param source
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public Vector<Transaction> getTransactions(Source source, Integer year, Integer month, Integer dayOfMonth){
		Vector<Transaction> transactions = getTransactions(source);
		Vector<Transaction> v = new Vector<Transaction>();
		
		for (Transaction t : transactions) {
			Calendar c = Calendar.getInstance();
			c.setTime(t.getDate());
			if ((year == null || year == c.get(Calendar.YEAR))
					&& (month == null || month == c.get(Calendar.MONTH))
					&& (dayOfMonth == null || dayOfMonth == c.get(Calendar.DAY_OF_MONTH))){
				v.add(t);
			}
		}
		
		Collections.sort(v);
		return v;
	}
	
	public Vector<Transaction> getTransactions(String description, Date startDate, Date endDate){
		Vector<Transaction> allTransactions = getTransactions(startDate, endDate);
		Vector<Transaction> transactions = new Vector<Transaction>();
		
		for (Transaction transaction : allTransactions) {
			if (transaction.getDescription().equals(description))
				transactions.add(transaction);
		}
		
		return transactions;
	}
	
	public Vector<Transaction> getTransactions(Boolean isIncome, Date startDate, Date endDate){
		Vector<Transaction> allTransactions = getTransactions(startDate, endDate);
		Vector<Transaction> transactions = new Vector<Transaction>();
		
		for (Transaction transaction : allTransactions) {
			Category c = null;
			if (transaction.getFrom() instanceof Category)
				c = (Category) transaction.getFrom();
			else if (transaction.getTo() instanceof Category)
				c = (Category) transaction.getTo();
			
			if (c != null && c.isIncome() == isIncome){
				transactions.add(transaction);
			}
		}
		
		return transactions;
	}

	//TODO Test boundary conditions: does this overlap dates or not?
	public Vector<Transaction> getTransactions(Source source, Date startDate, Date endDate){
		Vector<Transaction> transactions = getTransactions(source);
		Vector<Transaction> v = new Vector<Transaction>();
		
		for (Transaction t : transactions) {
			if ((t.getDate().after(startDate) || t.getDate().equals(startDate)) 
					&& (t.getDate().before(endDate) || t.getDate().equals(endDate))){
				v.add(t);
			}
		}
		
		Collections.sort(v);
		return v;
	}
	
	//TODO Test boundary conditions: does this overlap dates or not?
	// Update - I think that is should be working....
	public Vector<Transaction> getTransactions(Date startDate, Date endDate){
		Vector<Transaction> transactions = getTransactions();
		Vector<Transaction> v = new Vector<Transaction>();
		
		for (Transaction t : transactions) {
			if ((t.getDate().after(startDate) || t.getDate().equals(startDate)) 
					&& (t.getDate().before(endDate) || t.getDate().equals(endDate))){
				v.add(t);
			}
		}
		
		Collections.sort(v);
		return v;
	}
	
	public Vector<Schedule> getScheduledTransactions(){
		Vector<Schedule> v = new Vector<Schedule>(dataModel.getAllTransactions().getScheduledTransactions());

		return v;
	}
	
	public void addSchedule(String name, Date startDate, Date endDate, String frequencyType, Integer scheduleDay, Transaction transaction){
		Schedule s = dataModelFactory.createSchedule();
		s.setScheduleName(name);
		s.setStartDate(startDate);
		s.setEndDate(endDate);
		s.setFrequencyType(frequencyType);
		s.setScheduleDay(scheduleDay);
		s.setAmount(transaction.getAmount());
		s.setDescription(transaction.getDescription());
		s.setNumber(transaction.getNumber());
		s.setMemo(transaction.getMemo());
		s.setTo(transaction.getTo());
		s.setFrom(transaction.getFrom());
		
		dataModel.getAllTransactions().getScheduledTransactions().add(s);
		saveDataModel();
	}
	
	public void removeSchedule(Schedule s){
		dataModel.getAllTransactions().getScheduledTransactions().remove(s);
	}
	
	public Vector<Schedule> getScheduledTransactionsBeforeToday(){
		Vector<Schedule> v = getScheduledTransactions();
		Vector<Schedule> newV = new Vector<Schedule>();
		
		for (Schedule schedule : v) {
			if (schedule.getStartDate().before(DateUtil.getStartOfDay(new Date()))
					&& (schedule.getLastDateCreated() == null 
							|| schedule.getLastDateCreated().before(DateUtil.getStartOfDay(new Date()))))
				newV.add(schedule);
		}
		
		return newV;
	}
}