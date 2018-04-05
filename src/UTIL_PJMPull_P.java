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

package com.enb.utilities.power;

import com.enb.libraries.IEnbridgeScript;
import com.olf.openjvs.OException;
import com.olf.openjvs.Table;
import com.olf.openjvs.enums.COL_TYPE_ENUM;

public class UTIL_PJMPull_P  extends IEnbridgeScript
{
	public static final int XML_SOURCE_FILE = 1;
	public static final int JSON_SOURCE_FILE = 2;

	@Override
	public void main(Table p_tblArguments, Table p_tblReturn) throws OException
	{
		Table tblParams = Table.tableNew();

		String strSavePath = "C:\\Users\\edwardk3\\PortableApps\\LivITy\\.babun\\cygwin\\home\\edwardk3\\workspace\\jiso-tool\\data";
		String strCaKeystoreType = KeyStore.getDefaultType();
		String strCaKeystorePath = "C:\\java\\jdk1.8.0_152\\jre\\lib\\security\\cacerts";
		String strCaKeystorePass = "changeit";

		String strClientKeystoreType = "PKCS12";
		String strClientKeystorePath = "C:\\Users\\edwardk3\\PortableApps\\LivITy\\.babun\\cygwin\\home\\edwardk3\\workspace\\jiso-tool\\src\\mrm-oati-cert.pfx";
		String strClientKeystorePass = "MRMiso2018";

		String strRequestURL = "https://api.pjm.com/api/v1/rt_hrl_lmps";
		String strContentType = "application/xml";

		UTIL_PJMPull_M.createArgumentsTable(p_tblArguments);
		p_tblArguments.addRow();

		tblParams.addCol("param_name", COL_TYPE_ENUM.COL_STRING);
		tblParams.addCol("param_value", COL_TYPE_ENUM.COL_STRING);

		p_tblArguments.setString("save_path", 1, strSavePath);
		p_tblArguments.setString("ca_keystore_type", 1, strCaKeystoreType);
		p_tblArguments.setString("ca_keystore_path", 1, strCaKeystorePath);
		p_tblArguments.setString("ca_keystore_pass", 1, strCaKeystorePass);
		p_tblArguments.setString("client_keystore_type", 1, strClientKeystoreType);
		p_tblArguments.setString("client_keystore_path", 1, strClientKeystorePath);
		p_tblArguments.setString("client_keystore_pass", 1, strClientKeystorePass);
		p_tblArguments.setString("content_type", 1, strContentType);
		p_tblArguments.setInt("source_file_format", 1, XML_SOURCE_FILE);
	}
}
