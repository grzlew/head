package org.mifos.framework.struts.tags;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.struts.util.MessageResourcesFactory;
import org.apache.struts.util.PropertyMessageResources;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.master.dao.MasterDAO;
import org.mifos.application.master.util.valueobjects.EntityMaster;
import org.mifos.framework.util.helpers.BundleKey;
import org.mifos.framework.util.helpers.MifosSelectHelper;


public class MifosPropertyMessageResources extends PropertyMessageResources {

	private Map dbMap_labels = new ConcurrentHashMap();
	private Map dbMap_Values = new ConcurrentHashMap();
	//private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER);
	private MasterDAO dao = new MasterDAO();
	
	public MifosPropertyMessageResources(MessageResourcesFactory factory,
			String config, boolean returnNull) {
		super(factory, config, returnNull);
	}

	public MifosPropertyMessageResources(MessageResourcesFactory factory,
			String config) {
		super(factory, config);
	}
	

	/**
	 * serial version UID for serailization
	 */
	private static final long serialVersionUID = 75674564734673651L;
	
	public String getMessage(Locale locale, String key) {
		
	
		String returnVal = null;
		
		// try to get the value from the PropertyMessageResources
		returnVal = super.getMessage(locale, key);
		

		//logger.debug("The value from the super call is - " + (returnVal == null ? "null" : returnVal) );
		if(returnVal == null){
			//try to get from the local hashmap
			try{
			returnVal =MifosConfiguration.getInstance().getLabel(key,locale);
			}catch( ConfigurationException ce){
				//eat it
				//TODO we may log it
			}
			
			//logger.debug("The value from the local hash map is - " + (returnVal == null ? "null" : returnVal) );
		}

		if ( returnVal==null)returnVal = (String) dbMap_labels.get(new BundleKey(locale, key));

		if (returnVal == null) {
			// try to get it from the database	
			try {
				
				
				EntityMaster entity = getEntity(locale, key);
				returnVal = entity.getEntityLabel();
				//put it into hash map for further use
				dbMap_labels.put(new BundleKey(locale, key), returnVal);
				
				
			} catch (Exception e) {
				//logger.error(e.getMessage());
			} 
		}
		
		//logger.debug("The final value is - " + (returnVal == null ? "null" : returnVal) );
		
		return returnVal;
	}
	
	public Collection getLookupValues(Locale locale, String key, String mappingKey) {

		Collection returnVal = null;
		//try to get from the local hashmap
		returnVal = (Collection) dbMap_Values.get(new BundleKey(locale, key));

		if (returnVal == null) {
			// try to get it from the database
			try {
				short locale_id = dao.getLocaleId(locale);
				EntityMaster entity = null;
				if(mappingKey == null || mappingKey.equals(""))
					entity = dao.getLookUpEntity(key, locale_id);
				else{
					String[] mappingValues = MifosSelectHelper.getInstance().getValue(mappingKey);
					if(mappingValues != null && mappingValues.length == 2)
						entity = dao.getLookUpEntity(key, locale_id, mappingValues[0],mappingValues[1]);
				}
				if(entity != null){
					returnVal = entity.getLookUpValues();
					dbMap_Values.put(new BundleKey(locale, key), returnVal);
				}
				
			} catch (Exception e) {
				//logger.error(e.getMessage());
				e.printStackTrace();
			} 
		
	}
		return returnVal;
	}

	
	public EntityMaster getEntity(Locale locale, String key){
		EntityMaster entity = null;
		try{
			short locale_id = dao.getLocaleId(locale);
			entity = dao.getLookUpEntity(key, locale_id);
		}catch(Exception e){
			//logger.error(e.getMessage());
		}
		
		return entity;
		
	}
}
