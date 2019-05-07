package com.travelex.automation.api.methods;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

public class APIMethods {
	private static final Logger log = LoggerFactory.getLogger(APIMethods.class);

	/**
	 * Returns response of POST API method execution
	 * 
	 * @param headers
	 * @param contentType
	 * @param url
	 * @return Response of POST command
	 */
	public static Response POST(HashMap<String,String> headers, String contentType, String url) {

		Response resp =  RestAssured.given().headers(headers)
				.contentType(contentType).post(url).andReturn();
		log.info("running POST command");
		log.info("URL \n" + url);
		log.info("Header \n" + resp.getHeaders().toString());
		log.info("Response body \n" + resp.getBody().toString());
		return resp;

	}

	/**
	 * Returns response of GET API method execution
	 * 
	 * @param headers
	 * @param contentType
	 * @param url
	 * @return Response of GET command
	 */
	public static Response securedGET(String username, String password, HashMap<String, String> headers, String contentType, String url) {
		Response resp = null;

		try {
			RestAssured.useRelaxedHTTPSValidation();
			resp = RestAssured.given().headers(headers).contentType(contentType).get(url).andReturn();
		} catch (Exception ex) {
			System.out.println("Error while loading keystore >>>>>>>>>");
			ex.printStackTrace();
		}
		log.info("running GET command");
		log.info("Response body \n" + resp.prettyPrint());
		return resp;
	}

	/**
	 * Returns response of GET API method execution
	 * 
	 * @param headers
	 * @param contentType
	 * @param url
	 * @return Response of GET command
	 */
	public static Response securedGET(String username, String password, HashMap<String, String> headers, String contentType, String body, String url) {

		Response resp = RestAssured.given().headers(headers).contentType(contentType).get(url).andReturn();

		log.info("running GET command");
		log.info("Response body \n" + resp.prettyPrint());
		return resp;
	}

	/**
	 * Returns response of GET API method execution
	 * 
	 * @param headers
	 * @param contentType
	 * @param url
	 * @return Response of GET command
	 */
	public static Response GET(HashMap<String, String> headers,	String contentType, String body, String url) {

		Response resp = RestAssured.given().headers(headers).contentType(contentType).body(body).get(url).andReturn();

		log.info("running GET command");
		log.info("Response body \n" + resp.prettyPrint());
		return resp;
	}

	/**
	 * Returns response of POST API method execution
	 * 
	 * @param headerKey
	 * @param headerValue
	 * @param contentTypeJson
	 * @param body
	 * @param url
	 * @return Response of POST command
	 */
	public static Response POST(HashMap<String, String> headers,
			String contentType, String body, String url) {
		Response resp = RestAssured
				.given()
				.headers(headers)
				.contentType(contentType)
				.body(body)
				.post(url)
				.andReturn();		
		return resp;
	}

	/**
	 * Returns response of PUT API method execution
	 * 
	 * @param headers
	 * @param contentType
	 * @param body
	 * @param url
	 * @return Response of PUT command
	 */
	public static Response PUT(HashMap<String, String> headers,	String contentType, String body, String url) {

		Response resp = RestAssured.given().headers(headers)
				.contentType(contentType).body(body).put(url).andReturn();

		log.info("running PUT command");
		log.info("URL \n" + url);
		log.info("Header \n" + resp.getHeaders().toString());
		log.info("Response body \n" + resp.getBody().toString());
		return resp;

	}

	/**
	 * Returns response of DELETE API method execution
	 * 
	 * @param headers
	 * @param contentType
	 * @param body
	 * @param url
	 * @return Response of DELETE command
	 */
	public static Response DELETE(HashMap<String, String> headers,	String contentType, String url) {

		Response resp = RestAssured.given().headers(headers).delete(url);

		log.info("running DELETE command");
		log.info("URL \n" + url);
		log.info("Header \n" + resp.getHeaders().toString());
		log.info("Response body \n" + resp.getBody().toString());
		return resp;
	}

	public static Response POST(HashMap<String, String> headers, Map<String, String> inputData, String url) {

		Response resp = RestAssured.given().headers(headers)
				.formParams(inputData).post(url).andReturn();
		log.info("running POST command");
		log.info("URL \n" + url);
		log.info("Header \n" + resp.getHeaders().toString());
		log.info("Response body \n" + resp.getBody().toString());
		return resp;
	}


}
