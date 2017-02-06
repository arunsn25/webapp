package com.cosd.greenbuild.calwin.web.library.remove;

/**
 ******************************************************************************************
 *
 * Confidential Property of COSD.
 * (c) Copyright COSD, 2013.
 * All Rights reserved.
 * May not be used without prior written agreement
 *
 *******************************************************************************************
 *
 * Project		  CERMS
 * Description    CERMS Greenbuild
 * Created by     Arun Shankar
 * Created on     March 22, 2013
 ********************************************************************************************
 */

import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.component.Component;
import com.documentum.web.form.Control;

public class UnRemoveDocumentComponent extends Component
{

	
	public void onInit(ArgumentList argList)
	{
		DfLogger.debug(this,"In UnRemoveDocumentComponent onInit",null,null);
		super.onInit(argList);
		//objectID = (String) argList.get("objectId");
		vals = argList.getValues("objectId");
	}

	
    public boolean onCommitChanges()
    {
    	DfLogger.debug(this,"UnRemoveDocumentComponent onCommitChanges()-----start",null,null);    	
    	
		try
		{
			for (String item:vals){
				//System.out.println(item);
				IDfSysObject sysObj = (IDfSysObject) getDfSession().getObject(new DfId(item));
				sysObj.setBoolean("soft_delete", false);
				sysObj.save();
				DfLogger.debug(this,"UnRemoveDocumentComponent onCommitChanges() - Selected object ["+ item + "]: is removed from soft delete",null,null);
			}
			
		}
		catch(DfException dfe)
		{
			DfLogger.error(this, "DfException raised in onCommitChanges of UnRemoveDocumentComponent: "+dfe.getMessage(), null, null);
		}
		return true;
        	
    }
    
	
	private static final long serialVersionUID = 1L;
	//private String objectID = null;
	private String vals[];


}
