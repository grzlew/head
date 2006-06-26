package org.mifos.framework.components.customTableTag;

import java.util.*;
import java.lang.reflect.*;

import org.mifos.framework.exceptions.TableTagParseException;

public class Row {

	private String totWidth = null;

	private Column[] column = null;

	private String bottomLineRequired = null;

	public void setTotWidth(String totWidth) {
		this.totWidth = totWidth;
	}

	public String getTotWidth() {
		return totWidth;
	}

	public void setColumn(Column[] column) {
		this.column = column;
	}

	public Column[] getColumn() {
		return column;
	}

	public String getBottomLineRequired() {
		return bottomLineRequired;
	}

	public void setBottomLineRequired(String bottomLineRequired) {
		this.bottomLineRequired = bottomLineRequired;
	}

	public void getRowHeader(StringBuilder tableInfo) {
		Column[] column = getColumn();
		for (int i = 0; i < column.length; i++) {
			column[i].getColumnHeader(tableInfo);
		}
	}

	public void generateTableRows(StringBuilder tableInfo, List obj,
			Locale locale,Locale prefferedLocale,Locale mfiLocale) throws TableTagParseException {
		Iterator it = obj.iterator();
		Column[] column = getColumn();
		while (it.hasNext()) {
			tableInfo.append("<tr>");
			Object objValue = it.next();
			for (int i = 0; i < column.length; i++) {
				column[i].generateTableColumn(tableInfo, objValue, locale,prefferedLocale,mfiLocale);
			}
			tableInfo.append("</tr>");
		}
	}

}

