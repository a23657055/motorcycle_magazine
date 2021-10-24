package org.iii.SecBuzzer.template.dao;

/**
 * System Log Variable
 */
public class SystemLogVariable {
	/**
	 * The System Log Status
	 */
	public static enum Status {
		/**
		 * 成功
		 */
		Success,
		/**
		 * 失敗
		 */
		Fail,
		/**
		 * 權限不足
		 */
		Deny
	}

	/**
	 * The System Log Action
	 */
	public static enum Action {
		/**
		 * 登入
		 */
		Login,
		/**
		 * 新增
		 */
		Create,
		/**
		 * 讀取
		 */
		Read,
		/**
		 * 修改
		 */
		Update,
		/**
		 * 刪除
		 */
		Delete,
		/**
		 * 頁面存取
		 */
		PageLoad,
		/**
		 * 發信
		 */
		SendMail,
		/**
		 * 簡訊
		 */
		Sms
	}
}