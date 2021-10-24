package org.iii.SecBuzzer.template.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;
import org.iii.SecBuzzer.template.dao.SystemLogVariable;
import org.iii.SecBuzzer.template.util.File;
import org.iii.SecBuzzer.template.util.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
	final static Logger logger = LoggerFactory.getLogger(MailService.class);

	@Value("${spring.profiles.active}")
	private String springProfilesActive;

	@Value("${mail.host}")
	private String mailHost;

	@Value("${mail.port}")
	private String mailPort;

	@Value("${mail.auth}")
	private String mailAuth;

	@Value("${mail.starttls.enable}")
	private String mailStartTlsEnable;

	@Value("${mail.ssl.enable}")
	private String mailSslEnable;

	@Value("${mail.username}")
	private String mailUserName;

	@Value("${mail.code}")
	private String mailCode;

	@Value("${mail.signature.enable}")
	private String mailSignatureEnable;

	@Value("${mail.signature.pfx.name}")
	private String mailSignaturePfxName;

	@Value("${mail.signature.pfx.code}")
	private String mailSignaturePfxCode;
	
	@Autowired
	private SystemLogService systemLogService;

	@Autowired
	private ResourceMessageService resourceMessageService;
	
	@Autowired
	private MessageSource messageSource;
	
	/**
	 * 寄送電子郵件(非同步, 同步請使用WebMail)
	 * 
	 * @param debug
	 *            DEBUG模式下在Subject會附加的字串
	 * @param recipients
	 *            接收者電子郵件信箱
	 * @param recipientCcs
	 *            副本接收者電子郵件信箱
	 * @param recipientBccs
	 *            密件副本接收者電子郵件信箱
	 * @param mailSubject
	 *            電子郵件標題
	 * @param mailBody
	 *            電子郵件內文
	 * @param attachements
	 *            電子郵件附件
	 */
	@Async
	public void Send(String debug, String recipients, String recipientCcs,
			String recipientBccs, String mailSubject, String mailBody, List<MailAttachment> attachements) {
		String fromText = messageSource.getMessage("globalSiteName", null, Locale.TRADITIONAL_CHINESE);
		String mailContext = "from: " + fromText + ";recipients: " + recipients + ";recipientCcs: " + recipients +
				";recipientBccs: " + recipientBccs + ";mailSubject: " + mailSubject + ";mailBody: " + mailBody;
		mailBody = mailBody + "<br /><br />" + MessageFormat.format(
				resourceMessageService.get(Locale.TRADITIONAL_CHINESE, "mailFooter"), WebConfig.WEB_SITE_URL);
		systemLogService.insert("Mail", "Send", mailContext,
				SystemLogVariable.Action.SendMail, SystemLogVariable.Status.Success, "", "");
		if (WebConfig.DEBUG_MODE) {
			mailSubject = mailSubject + "[" + debug + "]";
		}
		
		List<String> recipientsList = null;
		if(recipients!=null && recipients.length()!=0) {
			recipientsList = Arrays.asList(recipients);
		}
		
		List<String> recipientCcsList = null;
		if(recipientCcs!=null && recipientCcs.length()!=0) {
			recipientCcsList = Arrays.asList(recipientCcs);
		}
		
		List<String> recipientBccsList = null;
		if(recipientBccs!=null && recipientBccs.length()!=0) {
			recipientBccsList = Arrays.asList(recipientBccs);
		}
		this.SendMail(fromText, recipientsList, recipientCcsList,
				recipientBccsList, mailSubject, mailBody, attachements);
	}
	
	/**
	 * 寄送電子郵件(非同步, 同步請使用WebMail)
	 * 
	 * @param debug
	 *            DEBUG模式下在Subject會附加的字串
	 * @param fromText
	 *            寄送者顯示名稱
	 * @param recipients
	 *            接收者電子郵件信箱
	 * @param recipientCcs
	 *            副本接收者電子郵件信箱
	 * @param recipientBccs
	 *            密件副本接收者電子郵件信箱
	 * @param mailSubject
	 *            電子郵件標題
	 * @param mailBody
	 *            電子郵件內文
	 * @param attachements
	 *            電子郵件附件
	 */
	@Async
	public void Send(String debug, String fromText, String recipients, String recipientCcs,
			String recipientBccs, String mailSubject, String mailBody, List<MailAttachment> attachements) {
		String mailContext = "from: " + fromText + ";recipients: " + recipients + ";recipientCcs: " + recipients + 
				";recipientBccs: " + recipientBccs + ";mailSubject: " + mailSubject + ";mailBody: " + mailBody;
		mailBody = mailBody + "<br /><br />" + MessageFormat.format(
				resourceMessageService.get(Locale.TRADITIONAL_CHINESE, "mailFooter"), WebConfig.WEB_SITE_URL);
		systemLogService.insert("Mail", "Send", mailContext,
				SystemLogVariable.Action.SendMail, SystemLogVariable.Status.Success, "", "");
		if (WebConfig.DEBUG_MODE) {
			mailSubject = mailSubject + "[" + debug + "]";
		}
		
		
		List<String> recipientsList = null;
		if(recipients!=null && recipients.length()!=0) {
			recipientsList = Arrays.asList(recipients);
		}
		
		List<String> recipientCcsList = null;
		if(recipientCcs!=null && recipientCcs.length()!=0) {
			recipientCcsList = Arrays.asList(recipientCcs);
		}
		
		List<String> recipientBccsList = null;
		if(recipientBccs!=null && recipientBccs.length()!=0) {
			recipientBccsList = Arrays.asList(recipientBccs);
		}
		
		this.SendMail(fromText, recipientsList, recipientCcsList,
				recipientBccsList, mailSubject, mailBody, attachements);
	}
	
	@Async("mailTaskExecutor")
	public void Send(String fromText, List<String> recipients, List<String> recipientCcs, List<String> recipientBccs,
			String mailSubject, String mailBody, List<MailAttachment> attachements) {
		switch (springProfilesActive) {
		case "prod":
			break;
		case "dev":
		case "test":
			mailSubject = "[" + springProfilesActive + "]" + mailSubject;
			break;
		default:
			mailSubject = "[" + springProfilesActive + "]" + mailSubject;
			break;
		}
		this.SendMail(fromText, recipients, recipientCcs, recipientBccs, mailSubject, mailBody, attachements);
	}
	
	/**
	 * Send Mail 寄送電子郵件
	 * 
	 * @param fromText      寄送者顯示名稱
	 * @param recipients    接收者電子郵件信箱
	 * @param recipientCcs  副本接收者電子郵件信箱
	 * @param recipientBccs 密件副本接收者電子郵件信箱
	 * @param mailSubject   電子郵件標題
	 * @param mailBody      電子郵件內文
	 * @param attachements  電子郵件附件
	 */
	private void SendMail(String fromText, List<String> recipients, List<String> recipientCcs,
			List<String> recipientBccs, String mailSubject, String mailBody, List<MailAttachment> attachements) {		
		Properties props = new Properties();
		props.put("mail.smtp.host", mailHost);
		props.put("mail.smtp.port", mailPort);
		props.put("mail.smtp.auth", mailAuth);
		if (Boolean.parseBoolean(mailStartTlsEnable)) {
			props.put("mail.smtp.starttls.enable", mailStartTlsEnable);
		} else if (Boolean.parseBoolean(mailSslEnable)) {
			props.put("mail.smtp.ssl.enable", mailSslEnable);
		}
		Authenticator authenticator = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(mailUserName, mailCode);
			}
		};
		Session session;
		if (Boolean.parseBoolean(mailAuth) == true) {
			session = Session.getDefaultInstance(props, authenticator);
		} else {
			session = Session.getDefaultInstance(props);
		}
		try {
			Message message = new MimeMessage(session);
			message.setSentDate(new Date());
			message.setHeader("Content-Type", "text/html; charset=UTF-8");
			if (fromText != null && !fromText.isEmpty()) {
				message.setFrom(new InternetAddress(mailUserName,
						MimeUtility.encodeText(fromText, StandardCharsets.UTF_8.toString(), "B")));
			} else {
				message.setFrom(new InternetAddress(mailUserName));
			}
			if (recipients != null && recipients.size() > 0) {
				for (String recipient : recipients) {
					try {
						message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			} else {
				Exception e = new Exception("No Recipients");
				logger.error(e.getMessage());
				throw new RuntimeException(e);
			}
			if (recipientCcs != null && recipientCcs.size() > 0) {
				for (String recipientCc : recipientCcs) {
					try {
						message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(recipientCc));
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			}
			if (recipientBccs != null && recipientBccs.size() > 0) {
				for (String recipientBcc : recipientBccs) {
					try {
						message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(recipientBcc));
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e.getMessage());
					}
				}
			}
			message.setSubject(MimeUtility.encodeText(mailSubject, StandardCharsets.UTF_8.toString(), "B"));

			MimeBodyPart messageBodyPart = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();

			MimeBodyPart messageBody = new MimeBodyPart();
			messageBody.setContent(mailBody, "text/html; charset=UTF-8");
			multipart.addBodyPart(messageBody);

			int attachementCount = attachements == null ? 0 : attachements.size();
			if (attachementCount > 0) {
				for (int index = 0; index < attachementCount; index++) {
					MimeBodyPart messageAttachment = new MimeBodyPart();
					ByteArrayDataSource bds = new ByteArrayDataSource(attachements.get(index).getAttachmentBody(),
							"application/octet-stream");
					bds.setName(attachements.get(index).getAttachmentName());
					messageAttachment.setDataHandler(new DataHandler(bds));
					messageAttachment.setFileName(attachements.get(index).getAttachmentName());
					multipart.addBodyPart(messageAttachment);
				}
			}
			messageBodyPart.setContent(multipart);

			if (Boolean.parseBoolean(mailSignatureEnable)) {
				Security.addProvider(new BouncyCastleProvider());
				try {
					KeyStore keyStore = KeyStore.getInstance("PKCS12");
					keyStore.load(File.readFileFromResource(mailSignaturePfxName), mailSignaturePfxCode.toCharArray());
					Enumeration<String> e = keyStore.aliases();
					String keyAlias = null;
					while (e.hasMoreElements() && (keyAlias == null)) {
						String alias = (String) e.nextElement();
						keyAlias = keyStore.isKeyEntry(alias) ? alias : null;
					}
					if (keyAlias == null) {
						throw new Exception("Key alias Null");
					}
					PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, mailSignaturePfxCode.toCharArray());
					X509Certificate pkcs12Cert = (X509Certificate) keyStore.getCertificate(keyAlias);
					SMIMESignedGenerator signedGenerator = new SMIMESignedGenerator();
					signedGenerator.addSignerInfoGenerator(
							new JcaSimpleSignerInfoGeneratorBuilder().build("SHA1withRSA", privateKey, pkcs12Cert));
					MimeMultipart mm = signedGenerator.generate(messageBodyPart);
					message.setContent(mm);
					Transport transport = session.getTransport();
					try {
						transport.connect();
						transport.sendMessage(message, message.getAllRecipients());
					} catch (Exception eTransport) {
						eTransport.printStackTrace();
						logger.error(eTransport.getMessage());
						transport.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			} else {
				message.setContent(multipart);
				Transport transport = session.getTransport();
				try {
					transport.connect();
					transport.sendMessage(message, message.getAllRecipients());
				} catch (Exception eTransport) {
					eTransport.printStackTrace();
					logger.error(eTransport.getMessage());
					transport.close();
				}
			}
		} catch (MessagingException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}
}
