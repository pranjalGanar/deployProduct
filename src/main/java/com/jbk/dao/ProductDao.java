package com.jbk.dao;

import java.util.List;

import com.jbk.entity.Product;
 
public interface ProductDao {
	
	public boolean saveProduct(Product product);
	
	public Product getProductById(String productId);
	
	public List<Product> getAllProducts();
	
	public boolean deleteProductById(String productId);
	
	public boolean updateProduct(Product product);
	
	public List<Product> sortProductsById_ASC();

	public List<Product> sortProductsByName_DESC();

	public List<Product> getMaxPriceProducts();
	
	public int getTotalCountOfProducts();

	public double getMaxPrice();

	public double countSumOfProductPrice();
  
	public String uploadProducts(List<Product> list);



	

}
  