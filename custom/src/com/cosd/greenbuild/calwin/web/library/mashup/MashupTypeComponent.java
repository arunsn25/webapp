package com.cosd.greenbuild.calwin.web.library.mashup;


import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.documentum.fc.common.DfLogger;
import com.documentum.web.common.ArgumentList;
import com.documentum.web.formext.component.Component;

public class MashupTypeComponent extends Component implements COSDCalwinConstants {
	private static final long serialVersionUID = 1L;

	@Override
	public void onInit(ArgumentList argList) {
		DfLogger.debug(this, "In MashupTypeComponent onInit", null, null);
		super.onInit(argList);
	}

}