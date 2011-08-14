/*
 * File name: CertificateUtils.java
 * Creation date: 13/08/2011 08:21:06
 * Copyright Mindpool
 */
package ar.com.bunge.util;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;

import org.apache.log4j.Logger;

/**
 *
 * @author <a href="mcapurro@gmail.com">Mariano Capurro</a>
 * @version 1.0
 * @since 1.0
 *
 */
public class CertificateUtils {
	private static final Logger LOG = Logger.getLogger(CertificateUtils.class);
	private static final Logger EMAIL_NOTIFIER = Logger.getLogger("email-notifier");
	private static final String KEYSTORE_TYPES[] = {"pkcs12", "jks"};
	/**
	 * 
	 */
	private CertificateUtils() {
	}

	/**
	 * 
	 * @param keyStorePath
	 * @param keyStorePass
	 * @param signer
	 * @param type
	 */
	public static void validateCertificateExpiration(String keyStorePath, String keyStorePass, String signer) {
		if(LOG.isInfoEnabled()) {
			FileInputStream ksFile = null;
			Certificate cert = null;

			try {
				KeyStore ks = loadKeyStore(keyStorePath, keyStorePass);
				
				if(ks != null) {
					for(Enumeration<String> e = ks.aliases(); e.hasMoreElements(); ) {
						String alias = e.nextElement();
						if((signer != null && signer.equalsIgnoreCase(alias)) || signer == null) {
							if(LOG.isDebugEnabled()) {
								LOG.debug("About to validate certificate [" + alias + "]");
							}
							cert = (Certificate) ks.getCertificate(alias);
							validateExpiration(cert, alias, keyStorePath);
						}
					}
				} else {
					LOG.error("Could not validate certificate" + (signer != null ? " [" + signer + "]" : "/s") + " in keyStore [" + keyStorePath + "]");
				}
			} catch(Exception ex) {
				LOG.error("Could not validate certificate" + (signer != null ? " [" + signer + "]" : "/s") + " in keyStore [" + keyStorePath + "]" , ex);
			} finally {
				if(ksFile != null) {
					try {
						ksFile.close();
					} catch(Exception ex) {
						LOG.error("Could not close keyStore file [" + keyStorePath + "]", ex);
					}
				}
			}
		}
	}
	
	private static KeyStore createKeyStore(String keyStorePath, String keyStorePass, String type) throws Exception {
		FileInputStream ksFile = null;

		try {
			KeyStore ks = KeyStore.getInstance(type);
			ksFile = new FileInputStream(keyStorePath);
			ks.load(ksFile, keyStorePass.toCharArray());
			
			return ks;
		} finally {
			if(ksFile != null) {
				try {
					ksFile.close();
				} catch(Exception ex) {
					LOG.error("Could not close keyStore file [" + keyStorePath + "]", ex);
				}
			}
		}
	}
	
	private static KeyStore loadKeyStore(String keyStorePath, String keyStorePass) {
		for(int i = 0; i < KEYSTORE_TYPES.length; i++) {
			try {
				LOG.debug("About to load keyStore [" + keyStorePath + "] as type [" + KEYSTORE_TYPES[i] + "]");
				return createKeyStore(keyStorePath, keyStorePass, KEYSTORE_TYPES[i]);
			} catch(Exception ex) {
				LOG.debug("KeyStore [" + keyStorePath + "] is not of type [" + KEYSTORE_TYPES[i] + "]", ex);
			}
		}
		LOG.error("KeyStore [" + keyStorePath + "] type is not supported");
		return null;
	}
	
	private static void validateExpiration(Certificate cert, String alias, String keyStorePath) {
		final int DAYS_TO_NOTIFY_IN_HS = 5 * 24; 

		if(cert instanceof X509Certificate) {
			X509Certificate x509Cert = (X509Certificate) cert; 
			if(LOG.isInfoEnabled()) {
				LOG.info("The certificate [" + alias + "] in keyStore [" + keyStorePath + "] is valid from [" + x509Cert.getNotBefore() + "] to [" + x509Cert.getNotAfter() + "]");
			}
			
			if(!isValidCertificate(x509Cert.getNotBefore())) {
				LOG.error(Utils.getErrorMessage("error.cert.not_valid", alias, keyStorePath, x509Cert.getNotBefore()));
				EMAIL_NOTIFIER.error(Utils.getErrorMessage("error.cert.not_valid", alias, keyStorePath, x509Cert.getNotBefore()));
			} else {
				long hsToExpire = hoursToExpire(x509Cert.getNotAfter());
				if(hsToExpire <= 0) {
					LOG.error(Utils.getErrorMessage("error.cert.expired", alias, keyStorePath, x509Cert.getNotAfter()));
					EMAIL_NOTIFIER.error(Utils.getErrorMessage("error.cert.expired", alias, keyStorePath, x509Cert.getNotAfter()));
				} else if(hsToExpire <= DAYS_TO_NOTIFY_IN_HS) {
					LOG.error(Utils.getErrorMessage("warning.cert.about_to_expire", alias, keyStorePath, new Long(hsToExpire)));					
					EMAIL_NOTIFIER.error(Utils.getErrorMessage("warning.cert.about_to_expire", alias, keyStorePath, new Long(hsToExpire)));					
				}
			}
		} else {
			LOG.warn("Certificate type [" + cert.getType() + "] is not supported");
		}
	}
	
	private static boolean isValidCertificate(Date start) {
		long t = start != null ? start.getTime() : 0;
		return new Date().getTime() > t;
	}
	
	/**
	 * 
	 * @param expiration
	 * @return
	 */
	private static long hoursToExpire(Date expiration) {
		final long ONE_HOUR_MS = 60 * 60 * 1000L;
		
		long t1 = expiration != null ? expiration.getTime() : 0;
		long t2 = new Date().getTime();
		
		return (t1 - t2) / ONE_HOUR_MS;
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		validateCertificateExpiration("D:\\Development\\Projects\\bunge\\Certificados\\Afip\\bunge_afip.p12", "bunge", "bunge");
		validateCertificateExpiration("D:\\Development\\Projects\\bunge\\Certificados\\Prod\\30700869918.ks", "bunge", null);
	}

}
