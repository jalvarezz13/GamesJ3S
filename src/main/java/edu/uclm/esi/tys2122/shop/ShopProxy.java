package edu.uclm.esi.tys2122.shop;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ShopProxy {
	/* Attributes */

	private String key, url;

	/* Constructors */

	private ShopProxy() {
		this.key = "ETSPCE2P8W953V1XIPEIGWIVCNSPF1A4";
		this.url = "pshop02.esi.uclm.es/prestashop/api";

	}

	/* Singleton */

	private static class ShopHolder {
		static ShopProxy singleton = new ShopProxy();
	}

	public static ShopProxy get() {
		return ShopHolder.singleton;
	}

	/* Functions */

	public String getProducts() throws ParseException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://" + this.key + "@" + this.url + "/products?output_format=JSON");
		CloseableHttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		client.close();
		return result;
	}

}
