package com.tanerdiler.ecommerce.basketapi.builder;

import com.tanerdiler.ecommerce.basketapi.model.Campaign;
import com.tanerdiler.ecommerce.basketapi.model.Discount;
import com.tanerdiler.ecommerce.basketapi.model.DiscountWithPrice;
import com.tanerdiler.ecommerce.basketapi.model.DiscountWithRate;
import com.tanerdiler.ecommerce.basketapi.validation.BeanValidationException;
import com.tanerdiler.ecommerce.basketapi.validation.BeanValidator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.tanerdiler.ecommerce.basketapi.model.DiscountTargetType.CATEGORY;
import static com.tanerdiler.ecommerce.basketapi.model.DiscountTargetType.PRODUCT;
import static com.tanerdiler.ecommerce.basketapi.model.DiscountType.AMOUNT;
import static com.tanerdiler.ecommerce.basketapi.model.DiscountType.RATE;
import static org.assertj.core.api.Assertions.assertThat;

public class CampaignBuilderTest {

    private String name = "Mavi Tişörtte indirim";

    private BeanValidator validator;

    @Before
    public void init()
    {
        validator = new BeanValidator();
    }

    @After
    public void terminate()
    {
        validator.close();
    }

    @Test
    public void should_create_campaign()
    {
        Campaign campaign = Campaign.aNew().withId(12).withName(name).get();
        assertThat(campaign.getId()).isEqualTo(12);
        assertThat(campaign.getName()).isEqualTo(name);
    }

    @Test
    public void should_create_campaign_with_limited_rate_discount()
    {
        Campaign campaign = Campaign.aNew()
                .withName(name)
                .withDiscount(Discount.withRate(10d).withLimit(100d).get())
                .get();

        assertThat(campaign.getName()).isSameAs(name);

        DiscountWithRate discountWithRate = (DiscountWithRate) campaign.getDiscount();
        assertThat(discountWithRate.getRate()).isEqualTo(10d);
        assertThat(discountWithRate.getLimit()).isEqualTo(100d);
        assertThat(discountWithRate.getType()).isEqualTo(RATE);
    }


    @Test
    public void should_create_campaign_with_amount_discount()
    {
        Campaign campaign = Campaign.aNew()
                .withName(name)
                .withDiscount(Discount.withPrice(100d).get())
                .get();

        assertThat(campaign.getName()).isSameAs(name);

        DiscountWithPrice discountWithPrice = (DiscountWithPrice) campaign.getDiscount();
        assertThat(discountWithPrice.getPrice()).isEqualTo(100d);
        assertThat(discountWithPrice.getType()).isEqualTo(AMOUNT);
    }

    @Test
    public void should_create_campaign_targeted_to_product()
    {
        Campaign campaign = Campaign.aNew()
                .withName(name)
                .target(10, PRODUCT)
                .get();

        assertThat(campaign.getName()).isSameAs(name);
        assertThat(campaign.getTargetId()).isEqualTo(10);
        assertThat(campaign.getTargetType()).isEqualTo(PRODUCT);
    }

    @Test
    public void should_create_campaign_targeted_to_category()
    {
        Campaign campaign = Campaign.aNew()
                .withName(name)
                .target(5, CATEGORY)
                .get();

        assertThat(campaign.getName()).isSameAs(name);
        assertThat(campaign.getTargetId()).isEqualTo(5);
        assertThat(campaign.getTargetType()).isEqualTo(CATEGORY);
    }

    @Test(expected = BeanValidationException.class)
    public void should_throw_BeanValidationException_when_discount_is_null()
            throws BeanValidationException
    {
        Campaign.aNew()
                .withName(name)
                .target(5, CATEGORY)
                .validateAndGet(validator);
    }

    @Test(expected = BeanValidationException.class)
    public void should_throw_BeanValidationException_when_rate_discount_is_invalid()
            throws BeanValidationException
    {
        Campaign.aNew()
                .withName(name)
                .withDiscount(Discount.withRate(-10d).withLimit(100d).get())
                .target(5, CATEGORY)
                .validateAndGet(validator);
    }

    @Test(expected = BeanValidationException.class)
    public void should_throw_BeanValidationException_when_price_discount_is_invalid()
            throws BeanValidationException
    {
        Campaign.aNew()
                .withName(name)
                .withDiscount(Discount.withPrice(-100d).get())
                .target(5, CATEGORY)
                .validateAndGet(validator);
    }
}
