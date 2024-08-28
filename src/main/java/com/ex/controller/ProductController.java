package com.ex.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ex.entity.Product;
import com.ex.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping
	public List<Product> getAllProducts() {
		return productService.getAllProducts();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		return productService.getProductById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Product createProduct(@RequestParam("name") String name, @RequestParam("price") Double price,
			@RequestParam("image") MultipartFile image) throws IOException {

		Product product = new Product();
		product.setName(name);
		product.setPrice(price);
		product.setImage(image.getBytes());

		return productService.saveProduct(product);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "price", required = false) Double price,
			@RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

		Optional<Product> optionalProduct = productService.getProductById(id);

		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();

			// Update only the provided fields
			if (name != null) {
				product.setName(name);
			}
			if (price != null) {
				product.setPrice(price);
			}
			if (image != null && !image.isEmpty()) {
				product.setImage(image.getBytes());
			}

			Product updatedProduct = productService.saveProduct(product);
			return ResponseEntity.ok(updatedProduct);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}
	
	
	 @GetMapping("/search")
	    public ResponseEntity<List<Product>> findProductsByName(@RequestParam String name) {
	        List<Product> products = productService.findProductsByName(name);

	        if (products.isEmpty()) {
	            return ResponseEntity.notFound().build();
	        } else {
	            return ResponseEntity.ok(products);
	        }
	    }
	

	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
		Optional<Product> product = productService.getProductById(id);

		if (product.isPresent() && product.get().getImage() != null) {
			byte[] imageBytes = product.get().getImage();

			// Set the appropriate content type based on the image type (jpeg, png, etc.)
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG); // Change to IMAGE_PNG or other if needed

			return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}
