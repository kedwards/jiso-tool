/***********************************************************************************************************************************************
 * Filename: UTIL_PullPJMIso_P.java
 * SVN: JVS\Utilities
 *
 * Description: Param Script for PJM ISO File downloads.
 *
 *  REVISION HISTORY:
 * ---Date---    ---Author--   ---Revision Details----------------------------------------------------------------------------------------------
 * 12-Mar-2018   Shormany      Issue#00000: Initial creation;
 *
 ***********************************************************************************************************************************************
 */
package com.enb.utilities;

import java.security.KeyStore;
import com.enb.libraries.IEnbridgeWebServices;
import com.olf.openjvs.OException;
import com.olf.openjvs.SystemUtil;
import com.olf.openjvs.Table;

public class UTIL_PullPJMIso_P extends IEnbridgeWebServices
{
	private final String strPJMConfigFile = "pjmConfig.csv";
	private final String strSavePath = "\\main\\utilities\\ISO\\data\\PJM";
	private final String strConfig = "\\main\\utilities\\ISO\\config\\" + strPJMConfigFile;
			
	private final String strCaKeystoreType = KeyStore.getDefaultType();
	private final String strCaKeystorePath = "\\main\\utilities\\ISO\\cert\\cacerts";
	private final String strCaKeystorePass = "changeit";

	private final String strClientKeystoreType = "PKCS12";
	private final String strClientKeystorePath = "\\main\\utilities\\ISO\\cert\\mrm-oati-cert.pfx";
	private final String strClientKeystorePass = "MRMiso2018";
	
	
	private final String strRootUri = "https://msrs.pjm.com/msrs/browserless.do?";
	private final String strVersion = "L";
	private final String strFormat = "C";
	private final String strUser = "loucksd";
	private final String strPass = "Enbpower3!";
	
	@Override
	public void main(Table p_tblArguments, Table p_tblReturn) throws OException 
	{	
		try {
			UTIL_PullPowerIso_M.createArgumentsTable(p_tblArguments);	
		} catch(Exception e) {
			throw new OException(e.getClass().getSimpleName() + " - " + e.getMessage());
		}
		
		p_tblArguments.addRow();
		
		p_tblArguments.setString("config_file", 1, SystemUtil.getEnvVariable("AB_ENDUR_DIR") + strConfig);
		
		p_tblArguments.setInt("operation", 1, IEnbridgeWebServices.PJM_SOURCE);
		p_tblArguments.setString("iso_source", 1, "PJM");
		p_tblArguments.setString("save_path", 1, SystemUtil.getEnvVariable("AB_ENDUR_DIR") + strSavePath);
		
		p_tblArguments.setString("ca_keystore_type", 1, strCaKeystoreType);
		p_tblArguments.setString("ca_keystore_path", 1, SystemUtil.getEnvVariable("AB_ENDUR_DIR") + strCaKeystorePath);
		p_tblArguments.setString("ca_keystore_pass", 1, strCaKeystorePass);
		
		p_tblArguments.setString("client_keystore_type", 1, strClientKeystoreType);
		p_tblArguments.setString("client_keystore_path", 1, SystemUtil.getEnvVariable("AB_ENDUR_DIR") + strClientKeystorePath);
		p_tblArguments.setString("client_keystore_pass", 1, strClientKeystorePass);
				
		p_tblArguments.setString("root_uri", 1, strRootUri);
		p_tblArguments.setString("version", 1, strVersion);		
		p_tblArguments.setString("format", 1, strFormat);
		p_tblArguments.setString("user", 1, strUser);
		p_tblArguments.setString("pass", 1, strPass);
	}
}
