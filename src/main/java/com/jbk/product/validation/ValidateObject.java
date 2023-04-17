package com.jbk.product.validation;

import java.util.HashMap;
import java.util.Map;

import com.jbk.entity.Product;


public class ValidateObject {

	public static Map<String, String> map=null;
	public static Map<String, String> validateProduct( Product product) {
		
		map=new HashMap<>();
		
		if (product.getProductName() == null || product.getProductName().equals("")) {
			
			map.put("productName", "ProductName is required");
		}
		if (product.getSupplier().getSupplierId() <= 0) {
			
			map.put("supplierId", "SupplierId should be greater than 0");
		}
		if (product.getCategory().getCategoryId()<=0) {
			
			map.put("categoryId", "CategoryId should be greater than 0");
		}
		if(product.getProductQty()<=0) {
			map.put("productQty", "productQty should be greater than 0");
		}
		if(product.getProductPrice()<=0) {
			map.put("productPrice", "productProice should be greater than 0");
		}

			return map;

		}
	
}
