package com.notepad.NotePad.Services;

import com.notepad.NotePad.util.Literal;

import java.util.HashMap;
import java.util.Map;

public class ApiGatwayService {
	/**
	 * Hold teh instance
	 */
	private static ApiGatwayService instance;

	/**
	 * 
	 * @return
	 */
	public static ApiGatwayService getInstance() {
		/**
		 * Check for the Null
		 */
		if (instance == null) {
			instance = new ApiGatwayService();
		}
		return instance;
	}

	/**
	 * 
	 * @param api_gateway
	 * @return
	 */
	public Map<String, Object> checkApiGateway(final String api_gateway) {
		Map<String, Object> ret_map = new HashMap<String, Object>(Literal.FOUR_INT);
		try {
			/**
			 * Check null value
			 */
			if (api_gateway == null) {
				ret_map.put(Literal.STATUS, Literal.ERROR);
				ret_map.put(Literal.MESSAGE, Literal.API_GATE_WAY_NULL);
			} else {
				if (api_gateway.equals(Literal.APPLICATION)) {
					ret_map.put(Literal.STATUS, Literal.SUCCESS);
					ret_map.put(Literal.API_GATE_WAY, Literal.APPLICATION);
				} else {
					ret_map.put(Literal.STATUS, Literal.ERROR);
					ret_map.put(Literal.MESSAGE, Literal.WRONG_GATEWAY);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret_map;
	}
}
