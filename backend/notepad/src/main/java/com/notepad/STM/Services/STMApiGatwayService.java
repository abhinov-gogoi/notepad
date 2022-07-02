package com.notepad.STM.Services;

import com.notepad.STM.util.STMLiteral;

import java.util.HashMap;
import java.util.Map;

public class STMApiGatwayService {
	/**
	 * Hold teh instance
	 */
	private static STMApiGatwayService instance;

	/**
	 * 
	 * @return
	 */
	public static STMApiGatwayService getInstance() {
		/**
		 * Check for the Null
		 */
		if (instance == null) {
			instance = new STMApiGatwayService();
		}
		return instance;
	}

	/**
	 * 
	 * @param api_gateway
	 * @return
	 */
	public Map<String, Object> checkApiGateway(final String api_gateway) {
		Map<String, Object> ret_map = new HashMap<String, Object>(STMLiteral.FOUR_INT);
		try {
			/**
			 * Check null value
			 */
			if (api_gateway == null) {
				ret_map.put(STMLiteral.STATUS, STMLiteral.ERROR);
				ret_map.put(STMLiteral.MESSAGE, STMLiteral.API_GATE_WAY_NULL);
			} else {
				if (api_gateway.equals(STMLiteral.APPLICATION)) {
					ret_map.put(STMLiteral.STATUS, STMLiteral.SUCCESS);
					ret_map.put(STMLiteral.API_GATE_WAY, STMLiteral.APPLICATION);
				} else {
					ret_map.put(STMLiteral.STATUS, STMLiteral.ERROR);
					ret_map.put(STMLiteral.MESSAGE, STMLiteral.WRONG_GATEWAY);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret_map;
	}
}
