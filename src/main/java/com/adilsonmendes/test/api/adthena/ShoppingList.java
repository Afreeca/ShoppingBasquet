package com.adilsonmendes.test.api.adthena;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.adilsonmendes.test.api.adthena.model.Product;
import com.adilsonmendes.test.api.adthena.repository.ProductRepository;

@Component
public class ShoppingList {
  
  @Autowired
  private ProductRepository productRepo;
   
  public void Process(ArrayList<Product> list) {
    double subTotalPrice = calculateSubTotal(list);
    double totalPrice = calculateTotalPrice(list);
    
    System.out.println(String.format("Subtotal: %.2f", subTotalPrice));
    
    for (Product product : list) {
      if(product.getEspecialOffer() != null)
        System.out.println(generateSpecialOfferText(product));
    }
  }
  
  private double calculateSubTotal(ArrayList<Product> list) {
    double subTotal = 0;
    for (Product product : list) {
       subTotal +=product.getPrice();
    }
    return subTotal;
  }
  
  private String generateSpecialOfferText(Product product) {
    if (product.getEspecialOffer().getDiscount() != 0)
        return String.format("%s have a %d%% discount off their normal price this week.", product.getName(), product.getEspecialOffer().getDiscount());
    
    String productOf =  product.getEspecialOffer().getCondition().getOnProduct();
    int amount = product.getEspecialOffer().getCondition().getAmount();
    String discount = getDiscountText(product.getEspecialOffer().getCondition().getDiscountOf());
    String plural = amount > 1 ? "s" : "";
    String packaging = product.getDescription().concat(plural);
    String packagingOf = productRepo.getProduct(productOf).getDescription();
    return String.format("Buy %d %s of %s and get a %s of %s for %s.", amount, packaging, product.getName(), packagingOf, productOf, discount);
  }
  
  private double calculateTotalPrice(ArrayList<Product> list) {
    double subTotal = 0;
    for (Product product : list)
       subTotal +=product.getPrice();
    return subTotal;
  }
  
  private String getDiscountText(int discountOf){
    switch (discountOf) {
      case 50 :
        return "half price";
      case 100 :
        return "free";
      default:
        return String.format("%d%% discount", discountOf);
    }
  }

  protected boolean isValidItemList(String[] list) {
    for (String product : list) 
      if(!productRepo.isProductExists(product))
      {
        System.out.println("\n * Validation Error -> Unknow item found in the shopping list");
        return false;
      } 
    return true;
  }
  
  protected boolean isValidateShoppingCommand(String[] list) {
     if(!list[0].equalsIgnoreCase("PriceBasket")){
       System.out.println("\n * Validation Error -> Shopping list format unknown");
       return false;
     }
     return true;
  }
}
