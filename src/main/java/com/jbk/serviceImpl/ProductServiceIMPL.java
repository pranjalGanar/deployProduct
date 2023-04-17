package com.jbk.serviceImpl;

//import com.jbk.validation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.jbk.dao.ProductDao;
import com.jbk.entity.Category;
import com.jbk.entity.Product;
import com.jbk.entity.Supplier;
import com.jbk.product.validation.*;
import com.jbk.service.ProductService;

@Service
public class ProductServiceIMPL implements ProductService {

	@Autowired
	private ProductDao dao;

	String excludedRows = "";
	int totalRecordCount = 0;
	Map<String, Object> map = new HashMap<String, Object>();
	Map<String, String> validatedError = new HashMap<String, String>();
	Map<Integer, Map<String, String>> errorMap = new HashMap<Integer, Map<String, String>>();

	@Override
	public boolean saveProduct(Product product) {
		String productId = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS").format(LocalDateTime.now());
		product.setProductId(productId);
		boolean isAdded = dao.saveProduct(product);
		return isAdded;
	}

	@Override
	public Product getProductById(String productId) {

		return dao.getProductById(productId);
	}

	@Override
	public List<Product> getAllProducts() {

		return dao.getAllProducts();
	}

	@Override
	public boolean deleteProductById(String productId) {
		return dao.deleteProductById(productId);
	}

	@Override
	public boolean updateProduct(Product product) {
		return dao.updateProduct(product);
	}

	@Override
	public List<Product> sortProductsById_ASC() {
		return dao.sortProductsById_ASC();
	}

	@Override
	public List<Product> sortProductsByName_DESC() {

		return dao.sortProductsByName_DESC();
	}

	@Override
	public List<Product> getMaxPriceProducts() {
		return dao.getMaxPriceProducts();
	}

	@Override
	public double getMaxPrice() {
		return dao.getMaxPrice();
	}

	@Override
	public double countSumOfProductPrice() {
		return dao.countSumOfProductPrice();
	}

	@Override
	public int getTotalCountOfProducts() {
		return dao.getTotalCountOfProducts();

	}

	public List<Product> readExcelSheet(String path) {
		Workbook workbook = null;
		FileInputStream fis = null;
		List<Product> list = new ArrayList<Product>();
		Product product = null;

		try {
			fis = new FileInputStream(new File(path));
			workbook = new XSSFWorkbook(fis);

			Sheet sheet = workbook.getSheetAt(1);
			totalRecordCount = sheet.getLastRowNum();
			Iterator<Row> rows = sheet.rowIterator();
			int rowCount = 0;

			while (rows.hasNext()) {

				Row row = rows.next();
				if (rowCount == 0) {
					rowCount++;
					continue;
				}
				product = new Product();
				Thread.sleep(1);
				String id = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new java.util.Date());
				product.setProductId(id);
				Iterator<Cell> cells = row.cellIterator();

				while (cells.hasNext()) {
					Cell cell = cells.next();

					int column = cell.getColumnIndex();

					switch (column) {
					case 0: {
						product.setProductName(cell.getStringCellValue());
						break;
					}
					case 1: {
						Supplier supplier = new Supplier();
						supplier.setSupplierId((int) cell.getNumericCellValue());
						product.setSupplier(supplier);
						break;
					}
					case 2: {
						Category category = new Category();
						category.setCategoryId((int) cell.getNumericCellValue());
						product.setCategory(category);

						break;
					}
					case 3: {
						product.setProductQty((int) cell.getNumericCellValue());
						break;
					}
					case 4: {
						product.setProductPrice(cell.getNumericCellValue());
						break;
					}
					}

				}

				validatedError = ValidateObject.validateProduct(product);
				// validatedError = ValidateObject.validateProduct(product);
				if (validatedError == null || validatedError.isEmpty()) {
					list.add(product);

				} else {
					int rowNum = row.getRowNum() + 1;
					// excludedRows = excludedRows + rowNum + ",";

					errorMap.put(rowNum, validatedError);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	 finally {
		try {
			if (workbook != null)
				workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		return list;
	}

	@Override
	public String uploadsheet(MultipartFile myFile) {
		String path = "src/main/resources";
		File file = new File(path);
		String msg = null;
		String absolutePath = file.getAbsolutePath();
		System.out.println(absolutePath);
		try {
			byte[] data = myFile.getBytes();
			FileOutputStream fos = new FileOutputStream(
					new File(absolutePath + File.separator + myFile.getOriginalFilename()));
			fos.write(data);
			List<Product> list = readExcelSheet(absolutePath + File.separator + myFile.getOriginalFilename());
			msg = dao.uploadProducts(list);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	public String generateExcel(HttpServlet response) {
		List<Product> allProduct = dao.getAllProducts();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("productInfo");
		XSSFRow row = sheet.createRow(0);
		row.createCell(0).setCellValue("ProductId");
		row.createCell(1).setCellValue("ProductName");
		row.createCell(2).setCellValue("supplierId");
		row.createCell(3).setCellValue("categoryId");
		row.createCell(4).setCellValue("ProductQty");
		row.createCell(5).setCellValue("ProductPrice");

		int dataRowIndex = 1;
		for (Product product : allProduct) {
			XSSFRow dataRow = sheet.createRow(dataRowIndex);
			dataRow.createCell(0).setCellValue(product.getProductId());
			dataRow.createCell(1).setCellValue(product.getProductName());
			dataRow.createCell(2).setCellValue(product.getSupplier().getSupplierId());
			dataRow.createCell(3).setCellValue(product.getCategory().getCategoryId());
			dataRow.createCell(4).setCellValue(product.getProductQty());
			dataRow.createCell(5).setCellValue(product.getProductPrice());
			dataRowIndex++;
		}

		return null;

	}

	@Override
	public String exportProductsToExcel(String filePath) {
		List<Product> allProduct = getAllProducts();
		String filePath1 = null;
		String[] columns = { "ProductId", "ProductName", "supplierId", "categoryId", "ProductQty", "ProductPrice" };

		try {

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("product");
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.RED.getIndex());
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			Row headerRow = sheet.createRow(0);
			for (int i = 0; i < columns.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns[i]);
				cell.setCellStyle(headerCellStyle);
			}

			int rowNum = 1;
			for (Product product : allProduct) {
				Row row = sheet.createRow(rowNum++);

				row.createCell(0).setCellValue(product.getProductId());

				row.createCell(1).setCellValue(product.getProductName());

				row.createCell(2).setCellValue(product.getSupplier().getSupplierId());

				row.createCell(3).setCellValue(product.getCategory().getCategoryId());

				row.createCell(4).setCellValue(product.getProductQty());
				row.createCell(5).setCellValue(product.getProductPrice());
			}

			for (int i = 0; i < columns.length; i++) {
				sheet.autoSizeColumn(i);
			}
			filePath = System.getProperty("user.home");
			filePath = filePath + "/Downloads";

			FileOutputStream fileOut = new FileOutputStream(filePath + File.separator + "product.xlsx");
			workbook.write(fileOut);
			fileOut.close();

			// Closing the workbook
			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath + File.separator + "product.xlsx";
	}

	@Override
	public Map<String, Object> uploadSheet(CommonsMultipartFile file, HttpSession httpsession) {
		String path=httpsession.getServletContext().getRealPath("/");
		String fileName=file.getOriginalFilename();
		String uploadedCount=null;
		FileOutputStream fos=null;
		byte[] data = file.getBytes();
		try {
			System.out.println(path);
			fos=new FileOutputStream(new File(path+File.separator+fileName));
			fos.write(data);
			List<Product> list=readExcelSheet(path+File.separator+fileName);
			uploadedCount=dao.uploadProducts(list);
			
			map.put("Total Record In Sheet", totalRecordCount);
			map.put("Uploaded Record In DB", uploadedCount);
			map.put("Total Excluded", errorMap.size());
			map.put("Bad Record Row Number", errorMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	

}
