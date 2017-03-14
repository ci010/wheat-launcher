package net.launcher.model;

import org.to2mbn.jmccc.auth.Authenticator;
import org.to2mbn.jmccc.auth.yggdrasil.core.ProfileService;

import java.util.List;

/**
 * @author ci010
 */
public interface Authorize
{
	String getId();

	void setAccount(String account);

	String getAccount();

	void updatePassword(String password);

	List<String> getAccountHistory();

	Authenticator buildAuthenticator();

	ProfileService createProfileService();
}
