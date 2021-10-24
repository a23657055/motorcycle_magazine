package org.iii.SecBuzzer.template.dao;

/**
 * Org Variable
 */
public class OrgVariable {
	/**
	 * 組織類別
	 */
	public static enum OrgType {
		/**
		 * 所有類別
		 */
		All(""),
		/**
		 * Admin
		 */
		Admin("0"),
		/**
		 * HISAC
		 */
		Hisac("1"),
		/**
		 * 權責單位
		 */
		Authority("2"),
		/**
		 * 會員單位
		 */
		Member("3");

		private String value;

		private OrgType(String value) {
			this.value = value;
		}

		/**
		 * 取得組織類別編號
		 * 
		 * @return 組織類別編號
		 */
		public String getValue() {
			return this.value;
		}
	}

	/**
	 * 權責機關類型
	 */
	public static enum AuthType {
		/**
		 * 所有類型
		 */
		All(""),
		/**
		 * 非權責機關
		 */
		None("0"),
		/**
		 * 業務主管機關
		 */
		Local("1"),
		/**
		 * 隸屬主管機關
		 */
		Supervise("2");

		private String value;

		private AuthType(String value) {
			this.value = value;
		}

		/**
		 * 取得權責機關類型編號
		 * 
		 * @return 權責機關類型編號
		 */
		public String getValue() {
			return this.value;
		}
	}
}