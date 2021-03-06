package com.cosd.greenbuild.calwin.mashup.adobe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.cosd.greenbuild.calwin.adobe.AdobeRESTClient;
import com.cosd.greenbuild.calwin.mashup.MashupInfo;
import com.cosd.greenbuild.calwin.mashup.MashupProcessor;
import com.cosd.greenbuild.calwin.mashup.MashupInfo.MashupDocument;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;
import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants.CONFIG;
import com.documentum.fc.client.IDfPersistentObject;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.common.IDfId;

public class AdobeMashupProcessor extends MashupProcessor implements COSDCalwinConstants {

	private static Logger log = Logger.getLogger(AdobeMashupProcessor.class);

	public AdobeMashupProcessor() {
		super();
	}

	@Override
	public Thread performMashup(MashupInfo info, IDfSession sess) throws DfException {
		this.sess = sess;
		this.mashup = info;
		return super.process();
	}

	@Override
	protected DfId doMashup() throws IOException {
		//System.out.println("In doMashup() method for...");
		String result = null;

		// format the specific strings for passing to adobe.
		// this processing could be done on the adobe side, but
		// is easier done here.
		// passing as deliminated strings is not great, but works
		// arrays or simple object/xml structure would be better
		// but delim is ok for now.

		// find docs by section
/*		Map<Integer, List<MashupDocument>> docsBySection = new TreeMap<Integer, List<MashupDocument>>();
		for (MashupDocument info : mashup.getDocumentsInfo()) {
			List<MashupDocument> docs = docsBySection.get(info.section);
			if (docs == null) {
				docs = new ArrayList<MashupDocument>();
				docsBySection.put(info.section, docs);
			}
			docs.add(info);
		}*/

		// idStr is a pipe separated list of ids for each section
		// includes the Object IDs for documents need to be mashup,
		// The object IDs are pipe delimited and each section name is included after the all object IDs
		// belong to the section.
		// dump ids followed by section
/*		String idStr = "";
		List<Integer> keys = new ArrayList<Integer>(docsBySection.keySet());
		for (int j = 0; j < keys.size(); j++) {
			Integer key = keys.get(j);
			List<MashupDocument> docs = docsBySection.get(key);
			if (!docs.isEmpty())
				idStr += docs.get(0).sectionName.toUpperCase()+"|";
			for (MashupDocument doc : docs)
				idStr += doc.id + "|";
		}
		log.debug("idStr: " + idStr);*/
		
		String idStr = "";
		for (MashupDocument info : mashup.getDocumentsInfo()) {
			idStr += info.id + ",";
		}
		//idStr="0900cb1380003bfb,0900cb1380003bf9,";

		//System.out.println("..." + CONFIG.METHOD_MASHUP + " service.");
		String appName=config.get(CONFIG.APP_WHOLE_CFILES,CONFIG.APP_WHOLE_CFILES_DEFAULT);
		String methodName=config.get(CONFIG.METHOD_MASHUP,CONFIG.METHOD_MASHUP_DEFAULT);
		AdobeRESTClient client = new AdobeRESTClient(appName, methodName, config);
		try {
			String mashupId = mashup.getSourceObject().getObjectId().getId();
			//client.addParameter("cdcr_number", "" + mashup.getCDCRNumber()); //TO-DO: Remove
			// false for normal, variable for advanced
			//client.addParameter("inRemoveBookmarks", mashup.removeBookmarks() ? "Y" : "N"); //TO-DO: Remove
			//client.addParameter("inRemoveComments", mashup.removeComments() ? "Y" : "N"); //TO-DO: Remove
			client.addParameter("inStrDocIDs", idStr);
			//System.out.println("inStrDocIDs: " + idStr);
			// now unused by LC
			//client.addParameter("inStrDocTypeIDs", typeStr);
			//client.addParameter("inStrTotalDocSize", "" + mashup.getTotalLength()); //TO-DO: Remove
			// now unused by LC
			//client.addParameter("inStrUniqueDocTypes", uniqueTypesStr);
			//client.addParameter("mashup_owner", mashup.getOwner()); //TO-DO: Remove
			//client.addParameter("section", mashup.getTitle()); //TO-DO: Remove
			//client.addParameter("strDMAuditType", mashup.getAuditType()); //TO-DO: Remove
			client.addParameter("inStrMashUpID", "" + mashupId);
			//System.out.println("inStrMashUpID: " + mashupId);
			// unused by LC, for possible future use
			//client.addParameter("isRegMashup", "" + mashup.isSimple()); //TO-DO: Remove
			client.addParameter("inStrDocbaseName", DOCBASE_NAME);
			//System.out.println("inStrDocbaseName: " + DOCBASE_NAME);
			result = client.invoke();
			if (log.isDebugEnabled())
				log.debug("Mashup request/response: "+client);

			if (result == null) {
				String emsg = "Invalid response from livecycle server: " + result;
				throw new IOException(emsg);
			}
			return new DfId(result);
		} catch (IOException e) {
			System.err.println("Error during request/response: "+e.getMessage()+ "\n" + client);
			throw e;
		} catch (Exception e) {
			System.err.println("Error calling service:\n" + client);
			e.printStackTrace();
			throw new RuntimeException("Error calling service", e);
		}

	}

	@Override
	protected String doExport(IDfId id, String openPass, String permsPass, IDfSession session) throws IOException { //IDfId
		//System.out.println("In doExport() method with password parameters for...");
		//System.out.println("..." + CONFIG.METHOD_SAVE_MASHUP + " service.");
		String appName=config.get(CONFIG.APP_WHOLE_CFILES_EXPORT,CONFIG.APP_WHOLE_CFILES_EXPORT_DEFAULT);
		String methodName=config.get(CONFIG.METHOD_SAVE_MASHUP,CONFIG.METHOD_SAVE_MASHUP_DEFAULT);
		// this should be over https
		AdobeRESTClient client = new AdobeRESTClient(appName, methodName, config);
		// these chould be crypted
		client.addParameter("inStrOpenPassword", openPass);
		client.addParameter("inStrPermissionPassword", permsPass);
		client.addParameter("inStrObjectId", "" + id);
		client.addParameter("inStrDMRepository", DOCBASE_NAME);
		// returns object id
		String result = client.invoke();
		return result;

/*		if (result == null)
			throw new IOException("Invalid id: " + result);

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.DAY_OF_YEAR, 1);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);

		try {
			IDfPersistentObject obj = session.getObject(new DfId(result));
			obj.setTime("a_retention_date", new DfTime(cal.getTime()));
			obj.setString(MashupInfo.ATTR_PROCESS_STATE, MashupInfo.PROCESS_STATE_FOREGROUND);
			obj.save();

			return obj.getObjectId();
		} catch (DfException e) {
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}*/
	}
	
	
	@Override
	protected String doExport(IDfId id, IDfSession session) throws IOException {
		//System.out.println("In doExport() method for...");
		//System.out.println("..." + CONFIG.METHOD_SAVE_MASHUP + " service.");
		String appName=config.get(CONFIG.APP_WHOLE_CFILES_EXPORT,CONFIG.APP_WHOLE_CFILES_EXPORT_DEFAULT);
		String methodName=config.get(CONFIG.METHOD_SAVE_MASHUP,CONFIG.METHOD_SAVE_MASHUP_DEFAULT);
		// this should be over https
		AdobeRESTClient client = new AdobeRESTClient(appName, methodName, config);
/*		client.addParameter("inStrObjectId", "" + id);
		client.addParameter("inStrDMRepository", DOCBASE_NAME);*/
		client.addParameter("inStrDocIDs", "" + id);
		client.addParameter("inStrDocbaseName", DOCBASE_NAME);
		// returns object id
		String result = client.invoke();
		return result;
	}
	

	private static final String DOCBASE_NAME = "HHSA_CALWIN";
	
}
