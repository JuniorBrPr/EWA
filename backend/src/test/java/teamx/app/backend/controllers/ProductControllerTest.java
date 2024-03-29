package teamx.app.backend.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import teamx.app.backend.models.Product;
import teamx.app.backend.services.ProductService;
import teamx.app.backend.utils.DTO.ProductDTO;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product product1, product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Test Product 1", "Description 1", 100.0, null, null);
        product2 = new Product(2L, "Test Product 2", "Description 2", 200.0, null, null);

        List<Product> products = Arrays.asList(product1, product2);

        when(productService.findAll()).thenReturn(products);
        when(productService.findById(1L)).thenReturn(product1);
        when(productService.add(any(ProductDTO.class))).thenReturn(product1);
        when(productService.update(eq(1L), any(ProductDTO.class))).thenReturn(product1);
    }

    @Test
    void getAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(product1.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(product1.getName())))
                .andExpect(jsonPath("$[1].id", is(product2.getId().intValue())))
                .andExpect(jsonPath("$[1].name", is(product2.getName())));
    }

    @Test
    void getProductById() throws Exception {
        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(product1.getId().intValue())))
                .andExpect(jsonPath("$.name", is(product1.getName())));
    }

    @Test
    void addProduct() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Product\",\"price\":150.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Test Product 1")));
    }

    @Test
    void updateProduct() throws Exception {
        mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Product\",\"price\":200.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product 1")));
    }

    @Test
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk());
        verify(productService).delete(1L);
    }

    @Test
    void deleteNonExistingProduct() throws Exception {
        doThrow(new IllegalArgumentException("Product not found")).when(productService).delete(999L);
        mockMvc.perform(delete("/products/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductByIdNotFound() throws Exception {
        when(productService.findById(999L)).thenReturn(null);
        mockMvc.perform(get("/products/999"))
                .andExpect(status().isInternalServerError());
    }
}