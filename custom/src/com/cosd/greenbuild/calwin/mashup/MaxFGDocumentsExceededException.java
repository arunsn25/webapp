package com.cosd.greenbuild.calwin.mashup;

import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;

/**
 * Thrown when the maximum number of has been exceeded for a mashup.
 * Maximum total documents is the number documents in a mashup, 
 * and is configured in the mashup configuration object (currently the
 * /CDCR-Config/Config/erms_adobe_asembler_ws object)
 * 
 * @see CDCRConstants#GET_ADOBE_ASEMBLER_CONFIG_QRY
 * @author Andy.Taylor
 *
 */
public class MaxFGDocumentsExceededException extends Exception {

	private static final long serialVersionUID = 1L;

	public MaxFGDocumentsExceededException(String msg) {
		super(msg);
	}

}
