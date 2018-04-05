
/***********************************************************************************************************************************************
 *$URL$
 *$Id$
 *
  * Description: 
 *
 *  REVISION HISTORY:
 * ---Date---    ---Author--   ---Revision Details----------------------------------------------------------------------------------------------
 * 12-Mar-2018   Shormany      Issue#00000: Initial creation;
 * 
 * 
 ***********************************************************************************************************************************************
 */

package com.enb.utilities.end_of_day;

import com.enb.libraries.IEnbridgeScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;

public class UTIL_PullPJMPowerHourly_P  extends IEnbridgeScript
{
	public static final int XML_SOURCE_FILE = 1;
	public static final int JSON_SOURCE_FILE = 2;
	

	@Override
	public void main(Table p_tblArguments, Table p_tblReturn) throws OException
	{
		Table tblParams = Table.tableNew();
		
		String strRequestURL = "https://api.pjm.com/api/v1/rt_hrl_lmps";		
		String strFilePath = "M:\\Temp\\Dev\\fromJSON.xls";
		String strSubscriptionKey = "104f7be5ffcb4ac1b9af9231cd3ca697";
		String strContentType = "application/xml";
		
		UTIL_PullPJMPowerHourly_M.createArgumentsTable(p_tblArguments);		
		p_tblArguments.addRow();
		
		tblParams.addCol("param_name", COL_TYPE_ENUM.COL_STRING);
		tblParams.addCol("param_value", COL_TYPE_ENUM.COL_STRING);
		
		p_tblArguments.setString("file_path", 1, strFilePath);
		p_tblArguments.setString("request_url", 1, strRequestURL);
		p_tblArguments.setString("subscription_key", 1,strSubscriptionKey);
		p_tblArguments.setString("content_type", 1, strContentType);
		p_tblArguments.setInt("source_file_format", 1, XML_SOURCE_FILE);
		
	}

}
