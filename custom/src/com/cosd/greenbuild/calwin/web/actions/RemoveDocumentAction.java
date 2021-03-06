package com.cosd.greenbuild.calwin.web.actions;

import java.util.Map;

import com.cosd.greenbuild.calwin.web.library.search.CalwinSearchFrame;
import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfACL;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.nls.NlsResourceBundle;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.common.LocaleService;
import com.documentum.web.common.SessionState;
import com.documentum.web.common.WrapperRuntimeException;
import com.documentum.web.formext.action.IActionExecution;
import com.documentum.web.formext.action.IActionPrecondition;
import com.documentum.web.formext.component.Component;
import com.documentum.web.formext.config.Context;
import com.documentum.web.formext.config.IConfigElement;
import com.documentum.web.formext.docbase.ObjectCacheUtil;
import com.documentum.webcomponent.library.messages.MessageService;

/**
 * 
 * ******************************************************************************************
 * File Name: RemoveDocumentAction.java 
 * Description: 
 * Author 					Arun Shankar - HP
 * Creation Date: 			04-April-2013 
 * ******************************************************************************************
 */

public class RemoveDocumentAction implements IActionPrecondition
{
	private static final long serialVersionUID = 1L;

	
	private static final String params[] = new String[] {"objectId"}; 

	public String[] getRequiredParams() 
	{
		return params;
	}

	public boolean queryExecute(String actionName, IConfigElement configElement,
			ArgumentList argumentList, Context context, Component component) 
	{
		boolean enable = true;
		//String strCurrSrcView = (String) SessionState.getAttribute("CurrentSearchView");
		String m_strDocbaseObjectId = argumentList.get("objectId");

		/*		String objectID = argumentList.get("objectId");
		IDfSysObject sysObj;
		try {
			//sysObj = (IDfSysObject) component.getDfSession().getObject(new DfId(objectID));
			sysObj = (IDfSysObject) ObjectCacheUtil.getObject(component.getDfSession(), objectID);

			boolean isRemoved = sysObj.getBoolean("soft_delete");
			if(isRemoved) // sysObj.isCheckedOut() ||
			{
				enable = false;
			}
		} catch (DfException e) {
			e.printStackTrace();
		}*/
		

		IDfSession dfSession = component.getDfSession();
		int iPermitValueOnObject = 0;
		
		if (m_strDocbaseObjectId != null) { // && CalwinSearchFrame.strNLSSimpleSearch.equals(strCurrSrcView)) {
			try {
				//Try to get the object
				IDfSysObject sysObject = (IDfSysObject) ObjectCacheUtil.getObject(dfSession, m_strDocbaseObjectId);
				//Get the object's permissions
				iPermitValueOnObject = sysObject.getPermit();
				boolean isRemoved = sysObject.getBoolean("soft_delete");
				//If the permissions are not Write permission or higher
				if ((iPermitValueOnObject < IDfACL.DF_PERMIT_WRITE) || isRemoved){
					enable=false;
				}
/*				if (sysObject.isCheckedOut())
					enable=false;*/
			}
			catch (DfObjectNotFoundException e) {
				DfLogger.error(this, "Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, null, e);
				throw new WrapperRuntimeException("Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, e);
			}
			catch (DfException e) {
				DfLogger.error(this, "Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, null, e);
				throw new WrapperRuntimeException("Unable to determine the user permissions for Object ID: " + m_strDocbaseObjectId, e);
			}
		}
		
	// Enable this action only for Advanced Search views. Disable for Simple Search.
/*		System.out.println("In action precondition. strCurrSrcView is : " + strCurrSrcView);
		if (CalwinSearchFrame.strNLSAdvSearch.equals(strCurrSrcView)) {
			enable=false;
		}
		
		System.out.println("In action precondition Remove Document. enable Value is : " + enable);*/
		return enable;
	}

}
