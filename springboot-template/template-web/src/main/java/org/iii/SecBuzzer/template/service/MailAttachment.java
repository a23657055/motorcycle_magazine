package org.iii.SecBuzzer.template.service;

import lombok.Data;

/**
 * 電子郵件夾檔
 */
@Data
public class MailAttachment {
	private String AttachmentName;
	private byte[] AttachmentBody;
}