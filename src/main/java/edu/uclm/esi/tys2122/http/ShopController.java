package edu.uclm.esi.tys2122.http;

import java.io.IOException;

import org.apache.http.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.uclm.esi.tys2122.shop.ShopProxy;

@RestController
@RequestMapping("shop")
public class ShopController extends CookiesController {

	/* Attributes */

	/* Routes */

	@GetMapping(value = "/getProducts", produces = "application/json")
	public String getProducts() {
		try {
			return ShopProxy.get().getProducts();
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

	}

	/* Functions */

}