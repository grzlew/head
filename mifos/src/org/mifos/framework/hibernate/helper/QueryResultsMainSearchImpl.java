/**
 * 
 */
package org.mifos.framework.hibernate.helper;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.customer.util.valueobjects.CustomerSearch;
import org.mifos.framework.exceptions.HibernateSearchException;

/**
 * @author imtiyazmb
 *
 */
public class QueryResultsMainSearchImpl extends QueryResultSearchDTOImpl {
	
	 public java.util.List get(int position, int noOfObjects) throws HibernateSearchException
	    {	 	
		 		java.util.List returnList = new java.util.ArrayList();	
		 		java.util.List list = new java.util.ArrayList();
		    	Session session = null;		    	
		    	try
		    	{
		    		session=getSession();	    		  
		    		Query query=prepareQuery(session,queryInputs.getQueryStrings()[1]);			    	
			    	query.setFirstResult(position);
			    	query.setMaxResults(noOfObjects);			    	
			    	list=query.list();			    	
			    	this.queryInputs.setTypes(query.getReturnTypes());			    	
			    	dtoBuilder.setInputs(queryInputs);			    	
		    		if(list!=null)
		 		   	{		    			
			    	   for(int i=0;i < list.size(); i++)	  	     
			     	   {  
			    		   	  if(buildDTO)
			    		   	  {	    
					    		  Object record = buildDTO((Object[])list.get(i));
					    		  CustomerSearch cs = ((CustomerSearch)record);
					    		  Integer customerId = cs.getCustomerId();					    		  
					    		  query= session.createQuery("select account.globalAccountNum from Account account where account.customer.customerId=:customerId and account.accountTypeId=:accountTypeId");
					    		  query.setInteger("customerId",customerId).setShort("accountTypeId",(short)1);
					    		  List listOfLoanAccounts = query.list();
					    		  cs.setLoanGlobalAccountNum(listOfLoanAccounts);
					    		  query = session.createQuery("select account.globalAccountNum from Account account where account.customer.customerId=:customerId and account.accountTypeId=:accountTypeId");
					    		  query.setInteger("customerId",customerId).setShort("accountTypeId",(short)2);
					    		  List listofSavingsAccounts = query.list();
					    		  cs.setSavingsGlobalAccountNum(listofSavingsAccounts);
					    		  returnList.add(cs);					    		  
			    		   	  }
			  		 		  else
			  		 		  {
				    			  if(i<noOfObjects)
				 	    		  {		
				    				  returnList.add(list.get(i));
				 	    		  }		  
			  		 		  }		    
			    	   }    	  
		 		   }
		    	   close();
		  	   }
		  	   catch(Exception e)
		  	   {		   
		  		   throw new HibernateSearchException(HibernateConstants.SEARCH_FAILED,e);
		  	   }		  	   
				return returnList;	 	
	    }	
}
