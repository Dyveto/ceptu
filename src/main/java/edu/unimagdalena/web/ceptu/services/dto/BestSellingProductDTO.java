package edu.unimagdalena.web.ceptu.services.dto;

import edu.unimagdalena.web.ceptu.entities.Product;

public interface BestSellingProductDTO {
    Product getProduct();
    Long getTotalSold();
}
