/*
 * Created on Aug 4, 2007 by wyatt
 */
package org.homeunix.thecave.buddi.model.swing;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.homeunix.thecave.buddi.i18n.BuddiKeys;
import org.homeunix.thecave.buddi.i18n.keys.BudgetCategoryTypes;
import org.homeunix.thecave.buddi.model.BudgetCategory;
import org.homeunix.thecave.buddi.model.BudgetCategoryType;
import org.homeunix.thecave.buddi.model.Document;
import org.homeunix.thecave.buddi.model.impl.FilteredLists;
import org.homeunix.thecave.buddi.model.impl.ModelFactory;
import org.homeunix.thecave.buddi.model.prefs.PrefsModel;
import org.homeunix.thecave.buddi.plugin.api.exception.InvalidValueException;
import org.homeunix.thecave.buddi.plugin.api.util.TextFormatter;
import org.homeunix.thecave.moss.data.list.FilteredList;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class MyBudgetTreeTableModel extends AbstractTreeTableModel {

	private final Document model;
	private final Object root;
	private Date selectedDate;
	private BudgetCategoryType selectedBudgetPeriodType;

	private final Map<BudgetCategoryType, List<BudgetCategory>> budgetCategoriesByType;
//	private final Map<Object, BudgetCategoryListFilteredByDeleted> childrenMap;
	private List<BudgetCategory> rootChildren;

	//Where the selected month is in relation to the edge of the table.
	private final int monthOffset = 2; 

	public MyBudgetTreeTableModel(Document model) {
		super(new Object());
//		Map<BudgetCategoryType, List<BudgetCategory>> budgetCategoriesByType = new HashMap<BudgetCategoryType, List<BudgetCategory>>();
		budgetCategoriesByType = new HashMap<BudgetCategoryType, List<BudgetCategory>>();
//		this.childrenMap = new HashMap<Object, BudgetCategoryListFilteredByDeleted>();
		
		this.model = model;
		this.root = getRoot();
		this.selectedDate = new Date();
//		this.monthOffset = 2; //This puts the current month in the middle of three columns.
		
		
		for (BudgetCategoryTypes typeEnum : BudgetCategoryTypes.values()) {
			BudgetCategoryType type = ModelFactory.getBudgetCategoryType(typeEnum);
			budgetCategoriesByType.put(type, new FilteredLists.BudgetCategoryListFilteredByPeriodType(model, type));
		}
	}

	public Date getSelectedDate() {
		return selectedDate;
	}

	public void setSelectedDate(Date selectedDate) {
		this.selectedDate = getSelectedBudgetPeriodType().getStartOfBudgetPeriod(selectedDate);
	}

	public BudgetCategoryType getSelectedBudgetPeriodType(){
		if (selectedBudgetPeriodType == null)
			selectedBudgetPeriodType = ModelFactory.getBudgetCategoryType(BudgetCategoryTypes.BUDGET_CATEGORY_TYPE_MONTH);
		return selectedBudgetPeriodType;
	}

	public void setSelectedBudgetPeriodType(BudgetCategoryType periodType){
		this.selectedBudgetPeriodType = periodType;
		this.rootChildren = null;
		
		setSelectedDate(getSelectedDate());
	}
	
	private List<BudgetCategory> getRootChildren(){
		if (rootChildren == null)
			if (PrefsModel.getInstance().isShowFlatBudget())
				rootChildren = new FilteredLists.BudgetCategoryListFilteredByDeleted(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()));
			else
				rootChildren = new FilteredLists.BudgetCategoryListFilteredByDeleted(model, new FilteredLists.BudgetCategoryListFilteredByParent(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()), null));
		return rootChildren;
	}

	public int getColumnCount() {
		return PrefsModel.getInstance().getNumberOfBudgetColumns();
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0)
			return TextFormatter.getTranslation(BuddiKeys.BUDGET_CATEGORY);

		if (column >= 1 && column < getColumnCount())
			return new SimpleDateFormat(getSelectedBudgetPeriodType().getDateFormat()).format(getColumnDate(column));

		return "";
	}

	public Object getValueAt(Object node, int column) {
		if (column == -1)
			return node;
		if (node instanceof BudgetCategory){
			BudgetCategory bc = (BudgetCategory) node;
			if (column == 0)
				return bc;
			if (column >= 1 && column < getColumnCount()){

				//We use an Object[] array to return a bunch of information.  This is quite
				// a complicated setup, due to the complexity of the budget information.
				// The object array contains the following information, by index:
				// 0: The budget category associated with this value
				// 1: The amount for this given column
				// 2: The amount total for all children of this node
				// 3: The depth of the node (used for indenting)
				
				Object[] retValues = new Object[4];
				retValues[0] = bc;
				retValues[1] = bc.getAmount(getColumnDate(column));
				retValues[2] = getChildCount(node) == 0 ? 0 : getChildTotal(node, column);
				retValues[3] = getChildDepth(node);

//				long value = bc.getAmount(getColumnDate(column));
//				return TextFormatter.getHtmlWrapper(TextFormatter.getFormattedCurrency(value, InternalFormatter.isRed(bc, value)));
				return retValues;
			}
		}
		return null;
	}

	private int getChildDepth(Object node){
		if (node instanceof BudgetCategory && !PrefsModel.getInstance().isShowFlatBudget()){
			if (((BudgetCategory) node).getParent() == null)
				return 0;
			return getChildDepth(((BudgetCategory) node).getParent()) + 1;
		}
		return 0;
	}
	
	private long getChildTotal(Object node, int column){
		if (PrefsModel.getInstance().isShowFlatBudget())
			return ((BudgetCategory) node).getAmount(getColumnDate(column));
		if (node instanceof BudgetCategory){
			long childTotal = ((BudgetCategory) node).getAmount(getColumnDate(column));
			for (int i = 0; i < getChildCount(node); i++){
				childTotal += getChildTotal(getChild(node, i), column);
			}
			return childTotal;
		}
		return 0;
	}

	public Object getChild(Object parent, int childIndex) {
		if (parent.equals(root)){
//			if (rootChildren == null)
//				rootChildren = new BudgetCategoryListFilteredByDeleted(model, new BudgetCategoryListFilteredByParent(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()), null)); 
			return getRootChildren().get(childIndex);
		}
		if (parent instanceof BudgetCategory && !PrefsModel.getInstance().isShowFlatBudget()){
//			List<BudgetCategory> budgetCategories = new BudgetCategoryListFilteredByDeleted(model, new BudgetCategoryListFilteredByParent(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()), (BudgetCategory) parent));
//			if (childIndex < budgetCategories.size())
			return ((BudgetCategory) parent).getChildren().get(childIndex);
		}
		return null;
	}

	public int getChildCount(Object parent) {
			if (parent.equals(root)){
//				childrenMap.put(parent, new BudgetCategoryListFilteredByDeleted(model, new BudgetCategoryListFilteredByParent(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()), null)));
//				return new BudgetCategoryListFilteredByDeleted(model, new BudgetCategoryListFilteredByParent(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()), null)).size();
//				return budgetCategories.size();
				return getRootChildren().size();
			}
			if (parent instanceof BudgetCategory && !PrefsModel.getInstance().isShowFlatBudget()){
//				childrenMap.put(parent, new BudgetCategoryListFilteredByDeleted(model, new BudgetCategoryListFilteredByParent(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()), (BudgetCategory) parent)));
//				return new BudgetCategoryListFilteredByDeleted(model, new BudgetCategoryListFilteredByParent(model, budgetCategoriesByType.get(getSelectedBudgetPeriodType()), (BudgetCategory) parent)).size();
//				return budgetCategories.size();
				return ((BudgetCategory) parent).getChildren().size();
			}
//		}

		return 0;
//		return childrenMap.get(parent).size();
	}

	public int getIndexOfChild(Object parent, Object child) {
		if (parent == null || child == null)
			return -1;

		if (parent.equals(root) && child instanceof BudgetCategory){
			return getRootChildren().indexOf(child);
		}
		if (parent instanceof BudgetCategory && child instanceof BudgetCategory && !PrefsModel.getInstance().isShowFlatBudget()){
			return ((BudgetCategory) parent).getChildren().indexOf(child);
		}
		return -1;
	}



	@Override
	public void setValueAt(Object value, Object node, int column) {
		if (column >= 1 && column < getColumnCount()){
			if (node instanceof BudgetCategory){
				BudgetCategory bc = (BudgetCategory) node;
				try {
					long amount = Long.parseLong(value.toString());
					bc.setAmount(getColumnDate(column), amount);
				}
				catch (InvalidValueException ive){} 
				catch (NumberFormatException nfe){}
			}
		}
	}

	@Override
	public boolean isCellEditable(Object node, int column) {
		if (node instanceof BudgetCategory && column >= 1 && column < getColumnCount())
			return true;
		return false;
	}

	private Date getColumnDate(int column){
		return getSelectedBudgetPeriodType().getBudgetPeriodOffset(getSelectedDate(), column - monthOffset);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < getChildCount(root); i++) {
			Object o1 = getChild(root, i);
			sb.append(o1).append(" [");
			for (int j = 0; j < getChildCount(o1); j++){
				Object o2 = getChild(o1, j);
				sb.append(o2);
				if (j < (getChildCount(o1) - 1))
					sb.append(", ");
			}
			sb.append("]  ");
		}

		return sb.toString();
	}

	public void fireStructureChanged(){
		((FilteredList) rootChildren).updateFilteredList();
		for (List<BudgetCategory> list : budgetCategoriesByType.values()) {
			((FilteredList) list).updateFilteredList();	
		}
		modelSupport.fireNewRoot();
	}
}
